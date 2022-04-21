#!/usr/bin/env groovy


def authenticate(String key, String serviceAccount, String project) {
    container('claranet') {
        sh "set +x ;echo ${key} > key.json"
        sh "gcloud auth activate-service-account ${serviceAccount} --key-file=key.json --project=$project"
    }
}

def getClusterCredentials(String clusterName, String zone, String project) {
    container('claranet') {
        sh "gcloud container clusters get-credentials ${clusterName} --zone asia-southeast1-b --project primal-catfish-346210"
    }
}
