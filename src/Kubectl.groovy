#!/usr/bin/env groovy
class Kubectl {

    public Kubectl(String key,String serviceAccount,String project, String clusterName, String zone) {
        container('claranet') {

            Gcloud gcloud = new Gcloud(key,serviceAccount,project)

        }
    }

    def applyFiles(String nameSpace ,def files,String directory) {
        container('claranet') {
            for (file in files) {

                sh "cd ./$directory && kubectl --namespace=$nameSpace apply -f ${file}"

            }
        }
    }

    def createDockerRegistrySecret(String username,String password, String dockerEmail, String nameSpace) {
        container('claranet') {
            sh 'kubectl create secret docker-registry docker-credentials --docker-username=$username --docker-password=${password} --docker-email=$dockerEmail  --namespace=$nameSpace'
        }
    }

    def createGenericSecret(Map config = [:]) {
        container('claranet') {
            sh "kubectl --namespace=devops-tools create secret generic $config.secretName --from-literal=username=${config.username} --from-literal=password=${config.password}"
        }
    }

    def setDeploymentImage(String nameSpace, String deploymentName, String containerName, String dockerImage, String tag) {
        container('claranet') {
            sh 'kubectl --namespace=$nameSpace set image deployment/$deploymentName $containerName=$dockerImage:$tag'
        }
    }

    def deleteSecretAfterRun() {
        container('claranet') {
            sh 'kubectl --namespace=devops-tools delete secret db-user-pass --ignore-not-found=true'
            sh 'kubectl delete secret docker-credentials -n devops-tools --ignore-not-found=true'
        }
    }
}
