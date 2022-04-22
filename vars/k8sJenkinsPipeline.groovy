#!/usr/bin/env groovy

def call() {
    PodTemplate podTemplate = new PodTemplate()
    Git git = new Git()
    Kaniko kaniko = new Kaniko()
    Kubectl kubectl = new Kubectl()
    Gcloud gcloud = new Gcloud()
    StageOperator stageOperator = new StageOperator()

    pipeline {
        agent {
            kubernetes {
                yaml podTemplate.addClaranetBuilder()
            }
        }

        stages {
            script {
                stageOperator.createDockerHubSecret(clusterName: "cluster-1", username: "giahai99", namespace: "devops-tools")
            }
            stage('Checkout and Build With Kaniko') {
                agent { 
                kubernetes {
                    yaml podTemplate.addKanikoBuilder()
                    }
                }
                steps {
                        script{
                            git.checkOut(branch: "main", url: "https://github.com/giahai99/devops-first-prj.git")
                            kaniko.buildAndPushImage(dockerImage: "giahai99/javaapp", tag: BUILD_NUMBER)
                    }
                }
            }
            

        // Running Docker container, make sure port 8080 is opened in
            stage('Deploy App to Kubernetes') {
                steps {
                        script{
                            withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/mysql', secretValues: [[vaultKey: 'username'], [vaultKey: 'password']]]
                            , [path: 'kv/github-token', secretValues: [[vaultKey: 'token']]]]) {

                                git.pull(token: token, organization: "giahai99", resporitory: "devops-first-prj.git")
                                
                                kubectl.createGenericSecret(secretName: "db-user-pass" , username: username, password: password)

                                kubectl.applyFiles(nameSpace:"devops-tools",fileList:["my-app-service.yml","mysql-config.yml","my-app-deployment.yml"], directory:"devops-first-prj")

                                kubectl.setDeploymentImage(nameSpace:"devops-tools",deploymentName:"book-deployment",containerName:"my-book-management",dockerImage:"giahai99/javaapp",tag:BUILD_NUMBER)

                        }
                    }
                }
            }
        }
        
        
        post { 
            cleanup {
                    script{

                        kubectl.deleteSecretAfterRun(nameSpace:"devops-tools", secrets:["db-user-pass","docker-credentials"])

                }
            }
        }
    }
}
