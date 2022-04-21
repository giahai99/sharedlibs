#!/usr/bin/env groovy


def authenticate(String key, String serviceAccount, String project,String clusterName, String zone) {
    container('claranet') {
        sh 'echo ${clusterName} > hai.txt'
        sh 'echo ${zone} > hai2.txt'
        sh 'echo ${key} > key.json'
        sh 'gcloud auth activate-service-account ${serviceAccount} --key-file=key.json --project=${project}'
    }
}

def getClusterCredentials(String clusterName, String zone, String project) {
    container('claranet') {
        sh 'echo ${clusterName} > hai.txt'
        sh 'echo ${zone} > hai.txt'
        sh 'gcloud container clusters get-credentials ${clusterName} --zone asia-southeast1-b --project primal-catfish-346210'
    }
}
