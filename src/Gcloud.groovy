#!/usr/bin/env groovy

class Gcloud {

    def authenticate(Map config = [:]) {
        sh 'set +x; echo $config.key > key.json'
        sh 'gcloud auth activate-service-account $config.serviceAccount --key-file=key.json --project=$config.project'
    }

    def getClusterCredentials(Map config = [:]) {
        sh 'gcloud container clusters get-credentials $config.clusterName --zone $config.zone --project $config.project'
    }

}