#!/usr/bin/env groovy


def authenticate(String key, String serviceAccount, String project) {
    container('claranet') {
        sh 'echo ${key} > key.json'
        sh 'gcloud auth activate-service-account ${serviceAccount} --key-file=key.json --project=${project}'
    }
}

def getClusterCredentials(String serviceAccount, String zone, String project) {
    container('claranet') {
        sh 'echo ${serviceAccount} > hai.txt'
        sh 'echo ${zone} > hai.txt'
        sh 'gcloud container clusters get-credentials ${serviceAccount} --zone asia-southeast1-b --project primal-catfish-346210'
    }
}
