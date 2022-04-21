#!/usr/bin/env groovy


def authenticate(String key,String serviceAccount, String project) {
    container('claranet') {
        sh 'echo hello1'
        sh 'set +x; echo $key > key.json'
        sh 'echo hello2'
        sh 'gcloud auth activate-service-account ${serviceAccount} --key-file=key.json --project=${project}'
    }
}

def getClusterCredentials(Map config = [:]) {
    container('claranet') {
        sh 'gcloud container clusters get-credentials ${config.clusterName} --zone ${config.zone} --project ${config.project}'
    }
}

