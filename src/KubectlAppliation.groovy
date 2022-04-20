#!/usr/bin/env groovy

def apply(def files) {
  for(file in files) {
    sh "kubectl --namespace=devops-tools apply -f ${file}"
  }
}
