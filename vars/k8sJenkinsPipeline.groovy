#!/usr/bin/env groovy

def call() {
    PodTemplate podTemplate = new PodTemplate()
    Git git = new Git()
    Kaniko kaniko = new Kaniko()
    Kubectl kubectl = new Kubectl()
    Gcloud gcloud = new Gcloud()

    pipeline {
        agent {
            kubernetes {
                yaml podTemplate.addClaranetBuilder()
            }
        }

        stages {
            stage('Create secret for docker hub') {
                steps {
                        script {
                            withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
                                                                                                                                                     [path: 'kv/dockerhub-password', secretValues: [[vaultKey: 'password']]]]) {

                                gcloud.authenticate(key,"truonggiahai-newaccount-primal@primal-catfish-346210.iam.gserviceaccount.com","primal-catfish-346210")

                                gcloud.getClusterCredentials("cluster-1","asia-southeast1-b","primal-catfish-346210")

                                kubectl.createDockerRegistrySecret("giahai99",password,"Haidepzai_kut3@yahoo.com","devops-tools")

                            }
                        }
                }
            }
            
            stage('Checkout and Build With Kaniko') {
                agent { 
                kubernetes {
                    yaml podTemplate.addKanikoBuilder()
                    }
                }
                steps {
                        script{
                            git.checkOut("main", "https://github.com/giahai99/devops-first-prj.git")
                            kaniko.buildAndPushImage("giahai99/javaapp","${BUILD_NUMBER}")
                    }
                }
            }
            

        // Running Docker container, make sure port 8080 is opened in
            stage('Deploy App to Kubernetes') {
                steps {
                        script{
                            withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/mysql', secretValues: [[vaultKey: 'username'], [vaultKey: 'password']]]
                            , [path: 'kv/github-token', secretValues: [[vaultKey: 'token']]]]) {
                
                                git.pull(token, "giahai99", "devops-first-prj.git")
                                
                                kubectl.createGenericSecret(secretName:"db-user-pass" ,username:username, password:password)
                                
                                kubectl.applyFiles("devops-tools",["my-app-service.yml","mysql-config.yml","my-app-deployment.yml"], "devops-first-prj")

                                kubectl.setDeploymentImage("devops-tools","book-deployment","my-book-management","giahai99/javaapp","${BUILD_NUMBER}")

                        }
                    }
                }
            }
        }
        
        
        post { 
            cleanup {
                    script{

                        kubectl.deleteSecretAfterRun("devops-tools", ["db-user-pass","docker-credentials"])

                }
            }
        }
    }
}
