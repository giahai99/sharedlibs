#!/usr/bin/env groovy

def call() {
    PodTemplate podTemplate = new PodTemplate()
    StageOperator stageOperator = new StageOperator()

    node {



        stage('Create secret for docker hub') {
            agent {
                kubernetes {
                    yaml podTemplate.addClaranetBuilder()
                }
            }

                    withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
                                                                                                                                             [path: 'kv/dockerhub-password', secretValues: [[vaultKey: 'password']]]]) {

                        stageOperator.createDockerHubSecret(serviceAccountKey: key,clusterName: "cluster-1", username: "giahai99", password: password, namespace: "devops-tools")
                    }


        }


        stage('Checkout and Build With Kaniko') {
            agent {
            kubernetes {
                yaml podTemplate.addKanikoBuilder()
                }
            }


                        stageOperator.checkoutBuildAndPushImage(branch: "main", url: "https://github.com/giahai99/devops-first-prj.git", dockerImage: "giahai99/javaapp")


        }


    // Running Docker container, make sure port 8080 is opened in
        stage('Deploy App to Kubernetes') {
            agent {
                kubernetes {
                    yaml podTemplate.getDefaultTemplate()
                }
            }

                        withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/mysql', secretValues: [[vaultKey: 'username'], [vaultKey: 'password']]]
                                                                                                                                                 , [path: 'kv/github-token', secretValues: [[vaultKey: 'token']]]]) {

                        stageOperator.deployAppToKubernetes(organization: "giahai99", resporitory: "devops-first-prj.git", secretName: "db-user-pass",
                                secrets: [username: username, password: password], namespace: "devops-tools")


            }
        }

        
        
        post { 
            cleanup {


                        stageOperator.deleteSecretAfterRun(namespace: "devops-tools", secrets: ["db-user-pass","docker-credentials"])


            }
        }
    }
}
