#!/usr/bin/env groovy


def authenticate(String key, String serviceAccount, String project) {
    container('claranet') {
        println("hello1")
        sh 'set +x ;echo $key > key.json'
        println("hello2")
        sh "gcloud auth activate-service-account $serviceAccount --key-file=key.json --project=$project"
        println("hello3")
    }
}

def getClusterCredentials(String clusterName, String zone, String project) {
    container('claranet') {
        sh "gcloud container clusters get-credentials ${clusterName} --zone asia-southeast1-b --project primal-catfish-346210"
    }
}
