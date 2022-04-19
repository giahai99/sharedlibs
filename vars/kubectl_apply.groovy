#!/usr/bin/env groovy

def call(Map config = [:]) {
  sh "kubectl --namespace=${config.namespace} apply -f ${config.filename}"
}
