#!/usr/bin/env groovy

def applyFiles(Map config = [:]) {
    container('claranet') {
        for (file in config.fileList) {

            sh "cd $config.directory && kubectl --namespace=$config.nameSpace apply -f $file"

        }
    }
}

def createDockerRegistrySecret(Map config = [:]) {
    container('claranet') {
        sh "kubectl create secret docker-registry docker-credentials --docker-username=$config.username --docker-password=$config.password --docker-email=$config.dockerEmail  --namespace=$config.namespace"
    }
}

def createK8sSecret(Map config = [:]) {
    def cmd = "kubectl --namespace=$config.namespace create secret ${config.secretName}"
    config.secrets.each { k, v ->
        cmd += "--from-literak=${k}=${v}"
    }
}


//def createGenericSecret(Map config = [:]) {
//    container('claranet') {
//        sh "kubectl --namespace=devops-tools create secret generic $config.secretName --from-literal=username=$config.username --from-literal=password=$config.password"
//    }
//}




def setDeploymentImage(Map config = [:]) {
    container('claranet') {
        sh "kubectl --namespace=$config.nameSpace set image deployment/$config.deploymentName $config.containerName=$config.dockerImage:$config.tag"
    }
}

def deleteSecretAfterRun(Map config = [:]) {
    container('claranet') {
        for (secret in config.secrets) {

            sh "kubectl --namespace=$config.nameSpace delete secret $secret --ignore-not-found=true"

        }
    }
}

