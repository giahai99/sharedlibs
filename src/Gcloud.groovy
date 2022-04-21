#!/usr/bin/env groovy


def authenticate(String key,Map config = [:]) {
    sh 'echo hello1'
    sh 'set +x; echo $key > key.json'
    sh 'echo hello2'
    sh 'gcloud auth activate-service-account ${config.serviceAccount} --key-file=key.json --project=${config.project}'
}

def getClusterCredentials(Map config = [:]) {
    sh 'gcloud container clusters get-credentials ${config.clusterName} --zone ${config.zone} --project ${config.project}'
}

