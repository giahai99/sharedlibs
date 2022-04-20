#!/usr/bin/env groovy

def call(String key) {
    sh 'set +x; echo ${key} > key.json'
    sh 'gcloud auth activate-service-account truonggiahai-newaccount-primal@primal-catfish-346210.iam.gserviceaccount.com --key-file=key.json --project=primal-catfish-346210'
}
