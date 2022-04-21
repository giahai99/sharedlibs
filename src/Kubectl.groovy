#!/usr/bin/env groovy



class Kubectl {

    Kubectl() {


            sh "echo hello 2"


    }
    def sayHello() {


        sh "echo hello 2"


    }

    Kubectl(String key,String serviceAccount,String project, String clusterName, String zone) {
        container('claranet') {

            sh "echo hello 2"
            Gcloud gcloud = new Gcloud(key,serviceAccount,project,clusterName,zone)

        }
    }

    def applyFiles(String nameSpace="default" ,def files,String directory) {
        container('claranet') {
            for (file in files) {

                sh "cd ./$directory && kubectl --namespace=$nameSpace apply -f ${file}"

            }
        }
    }

    def createDockerRegistrySecret(String username,String password, String dockerEmail, String nameSpace="default") {
        container('claranet') {
            sh 'kubectl create secret docker-registry docker-credentials --docker-username=$username --docker-password=${password} --docker-email=$dockerEmail  --namespace=$nameSpace'
        }
    }

    def createGenericSecret(Map config = [:]) {
        container('claranet') {
            sh "kubectl --namespace=devops-tools create secret generic $config.secretName --from-literal=username=${config.username} --from-literal=password=${config.password}"
        }
    }

    def setDeploymentImage(String nameSpace="default", String deploymentName, String containerName, String dockerImage, String tag) {
        container('claranet') {
            sh 'kubectl --namespace=$nameSpace set image deployment/$deploymentName $containerName=$dockerImage:$tag'
        }
    }

    def deleteSecretAfterRun(String nameSpace="default", def secrets) {
        container('claranet') {
            for (secret in secrets) {

                sh 'kubectl --namespace=$nameSpace delete secret $secret --ignore-not-found=true'

            }
        }
    }
}
