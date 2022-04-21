#!/usr/bin/env groovy

def applyFiles(String nameSpace, def fileList, String diretory) {
    container('claranet') {
        for (file in fileList) {

            sh "cd ./$directory && kubectl --namespace=$nameSpace apply -f ${file}"

        }
    }
}

def createDockerRegistrySecret(String username, String password,String dockerEmail, String nameSpace) {
    container('claranet') {
        sh "kubectl create secret docker-registry docker-credentials --docker-username=${username} --docker-password=${password} --docker-email=${dockerEmail}  --namespace=${nameSpace}"
    }
}

def createGenericSecret(Map config = [:]) {
    container('claranet') {
        sh "kubectl --namespace=devops-tools create secret generic $config.secretName --from-literal=username=${config.username} --from-literal=password=${config.password}"
    }
}

def setDeploymentImage(String nameSpace, String deploymentName, String $containerName, String dockerImage, String tag) {
    container('claranet') {
        sh "kubectl --namespace=$nameSpace set image deployment/$deploymentName $containerName=$dockerImage:$config.tag"
    }
}

def deleteSecretAfterRun(String nameSpace, def secrets) {
    container('claranet') {
        for (secret in secrets) {

            sh "kubectl --namespace=$nameSpace delete secret $secret --ignore-not-found=true"

        }
    }
}

