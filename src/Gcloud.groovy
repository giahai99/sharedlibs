#!/usr/bin/env groovy


def authenticate(Map config = [:]) {
    container('claranet') {
        String key = config.key
        sh 'set +x ;echo $key > key.json'
        println("hello2")
        sh "gcloud auth activate-service-account $config.serviceAccount --key-file=key.json --project=$config.project"
        println("hello3")
    }
}

def getClusterCredentials(Map config = [:]) {
    container('claranet') {
        sh "gcloud container clusters get-credentials $config.clusterName --zone $config.zone --project $config.project"
    }
}
