#!/usr/bin/env groovy

def authenticate(Map config = [:]) {
    container('claranet') {
        def key = config.key
        println(key)
        writeFile(file: 'key.json', text: key)
        sh 'cat key.json'
//        sh 'set +x ;echo $key > key.json'
        sh "gcloud auth activate-service-account $config.serviceAccount --key-file=key.json --project=$config.project"
    }
}

def getClusterCredentials(Map config = [:]) {
    container('claranet') {
        sh "gcloud container clusters get-credentials $config.clusterName --zone $config.zone --project $config.project"
    }
}
