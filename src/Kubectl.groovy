#!/usr/bin/env groovy

def applyFiles(def files) {
  for(file in files) {
    sh "kubectl --namespace=devops-tools apply -f ${file}"
  }
}


def createDockerRegistrySecret(String password) {
   sh 'kubectl create secret docker-registry docker-credentials --docker-username=giahai99 --docker-password=${password} --docker-email=Haidepzai_kut3@yahoo.com  --namespace=devops-tools'
}

def createGenericSecret(Map config = [:]){
   sh "kubectl --namespace=devops-tools create secret generic db-user-pass --from-literal=username=${config.username} --from-literal=password=${config.password}"
}

def setDeploymentImage() {
  sh 'kubectl --namespace=devops-tools set image deployment/book-deployment my-book-management=giahai99/javaapp:${BUILD_NUMBER}'  
}
