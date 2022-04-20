#!/usr/bin/env groovy

def call() {
  ClaranetContainerTemplate claranet = new ClaranetContainerTemplate()
  GcloudAuthentication gcloud = new GcloudAuthentication()
  KanikoContainerTemplate kaniko = new KanikoContainerTemplate()
  KubectlAppliation kubectlAppliation = new KubectlAppliation()
  
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
                  script{
                    withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]]]) {
                        
                      gcloud.authenticate(key)
                        
                        sh 'gcloud container clusters get-credentials cluster-1 --zone asia-southeast1-b --project primal-catfish-346210'
                    
                        sh 'kubectl create secret docker-registry docker-credentials --docker-username=giahai99 --docker-password=84cf7c40-ee9d-4948-bd6a-b2f088afd595 --docker-email=Haidepzai_kut3@yahoo.com  --namespace=devops-tools'
                    }
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
                    checkout([$class: 'GitSCM', branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/giahai99/devops-first-prj.git']]])
                       sh '''#!/busybox/sh
                    /kaniko/executor --context `pwd` --destination giahai99/javaapp:${BUILD_NUMBER}
                  '''
                }
            }
        }
        

    // Running Docker container, make sure port 8080 is opened in
        stage('Deploy App to Kubernetes') {
            steps {
                container('claranet') {
                  script{
                    withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/mysql', secretValues: [[vaultKey: 'username'], [vaultKey: 'password']]]
                    , [path: 'kv/github-token', secretValues: [[vaultKey: 'token']]]]) {
        
                        sh 'git init'
                        
                        sh 'git pull https://$token@github.com/giahai99/devops-first-prj.git'
                        
                        sh 'ls -l'
                        
                        sh "kubectl --namespace=devops-tools create secret generic db-user-pass --from-literal=username=$username --from-literal=password=$password"
                        
                        kubectlAppliation.apply(["my-app-service.yml","mysql-config.yml","my-app-deployment.yml"])
                                   
                        sh 'kubectl --namespace=devops-tools set image deployment/book-deployment my-book-management=giahai99/javaapp:${BUILD_NUMBER}'
                    }
                    }
                }
            }
        }
    }
    
    
    post { 
        cleanup { 
            container('claranet') {
                    sh 'kubectl --namespace=devops-tools delete secret db-user-pass --ignore-not-found=true'
                    sh 'kubectl delete secret docker-credentials -n devops-tools --ignore-not-found=true'
            }
        }
    }
}
}
