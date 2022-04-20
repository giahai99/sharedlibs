#!/usr/bin/env groovy

def apply(def files) {
  for(file in files) {
    sh "kubectl --namespace=devops-tools apply -f ${file}"
  }
}


def createDockerRegistrySecret(String password) {
  sh 'kubectl create secret docker-registry docker-credentials --docker-username=giahai99 --docker-password=${password} --docker-email=Haidepzai_kut3@yahoo.com  --namespace=devops-tools'
}
