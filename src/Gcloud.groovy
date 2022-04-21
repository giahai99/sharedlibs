#!/usr/bin/env groovy

class Gcloud {

    def authenticate(String key, String serviceAccount, String project) {
        sh 'set +x; echo ${key} > key.json'
        sh 'gcloud auth activate-service-account ${serviceAccount} --key-file=key.json --project=${project}'
    }

    def getClusterCredentials(Map config = [:]) {
        sh 'gcloud container clusters get-credentials ${config.clusterName} --zone ${config.zone} --project ${config.project}'
    }

}