#!/usr/bin/env groovy

class Gcloud {
    public Gcloud(String key,String serviceAccount,String project, String clusterName, String zone) {

        this.authenticate(key, serviceAccount, project)
        this.getClusterCredentials(clusterName, zone, project)

    }

    def authenticate(String key, String serviceAccount,String project) {
        sh 'set +x; echo ${key} > key.json'
        sh 'gcloud auth activate-service-account $serviceAccount --key-file=key.json --project=$project'
    }

    def getClusterCredentials(String clusterName,String zone, String project) {
        sh 'gcloud container clusters get-credentials $clusterName --zone $zone --project $project'
    }

}