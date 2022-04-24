#!/usr/bin/env groovy

def authenticate(Map config = [:]) {
    container('claranet') {
        writeFile(file: "key.json", text: config.key)
        sh "gcloud auth activate-service-account $config.serviceAccount --key-file=key.json --project=$config.project"
    }
}

def getClusterCredentials(Map config = [:]) {
    container('claranet') {
        sh "gcloud container clusters get-credentials $config.clusterName --zone $config.zone --project $config.project"
    }
}
