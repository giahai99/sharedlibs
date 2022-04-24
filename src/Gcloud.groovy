#!/usr/bin/env groovy

def authenticate(Map config = [:]) {
    container('claranet') {
//        sh 'cat key1.json'
        String key = config.key
//        writeFile(file: 'zorg.txt', text: data)
        writeFile(file: 'key.json', text: config.key)
//        sh 'set +x ;echo $key > key.json'
        sh "gcloud auth activate-service-account $config.serviceAccount --key-file=key.json --project=$config.project"
    }
}

def getClusterCredentials(Map config = [:]) {
    container('claranet') {
        sh "gcloud container clusters get-credentials $config.clusterName --zone $config.zone --project $config.project"
    }
}
