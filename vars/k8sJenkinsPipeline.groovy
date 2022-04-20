#!/usr/bin/env groovy

def call() {
    ClaranetContainerTemplate claranet = new ClaranetContainerTemplate()
    Gcloud gcloud = new Gcloud()
    KanikoContainerTemplate kaniko = new KanikoContainerTemplate()
    Kubectl kubectl = new Kubectl()
    GitCheckingOut gitCheckingOut = new GitCheckingOut()
    Git git = new Git()
    Kaniko kaniko = new Kaniko()
  
    pipeline {
        agent {
            kubernetes {
                yaml claranet.addPod()
            }
        }

        stages {
            stage('Create secret for docker hub') {
                steps {
                    container('claranet') {
                        withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
                                [path: 'kv/dockerhub-password', secretValues: [[vaultKey: 'password']]]]) {
                            
                            gcloud.authenticate(key)
                            
                            gcloud.getClusterCredentials()
                        
                            kubectl.createDockerRegistrySecret(password)

                        }
                    }
                }
            }
        
            
            stage('Checkout and Build With Kaniko') {
                agent { 
                kubernetes {
                    yaml kaniko.addPod()
                    }
                }
                steps {
                    container(name: 'kaniko', shell: '/busybox/sh') {
                        gitCheckingOut.checkOut()
                        kaniko.buildAndPushImage()
                    }
                }
            }
            

        // Running Docker container, make sure port 8080 is opened in
            stage('Deploy App to Kubernetes') {
                steps {
                    container('claranet') {
                        withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/mysql', secretValues: [[vaultKey: 'username'], [vaultKey: 'password']]]
                        , [path: 'kv/github-token', secretValues: [[vaultKey: 'token']]]]) {
            
                            git.pull()
                            
                            kubectl.createGenericSecret(username:username, password:password)
                            
                            kubectl.applyFiles(["my-app-service.yml","mysql-config.yml","my-app-deployment.yml"])

                            kubectl.setDeploymentImage()
                
                        }
                    }
                }
            }
        }
        
        
        post { 
            cleanup { 
                container('claranet') {

                        kubectl.deleteSecretAfterRun() 
                        
                }
            }
        }
    }
}
