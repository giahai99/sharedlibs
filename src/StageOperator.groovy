#!/usr/bin/env groovy

def createDockerHubSecret(Map config = [:]) {
    Kubectl kubectl = new Kubectl()
    Gcloud gcloud = new Gcloud()

    def userNameMaps = [ [ username : "giahai99", dockerEmail : "Haidepzai_kut3@yahoo.com" ] ]

        // authenticate with service account and get kubectl config file

            if ( checkClusterName(config.clusterName) != null) {

                def clusterNameMap = checkClusterName(config.clusterName)
                println(config.serviceAccountKey+"aaaaa")
                gcloud.authenticate(key: config.serviceAccountKey, serviceAccount: clusterNameMap.serviceAccount,
                        project: clusterNameMap.project)

                gcloud.getClusterCredentials(clusterName: clusterNameMap.clusterName, zone: clusterNameMap.zone, project: clusterNameMap.project)

        }

        // create secret for docker hub
        for (int i = 0; i < userNameMaps.size(); i++) {
            if (config.username == userNameMaps[i].username) {

                kubectl.createDockerRegistrySecret(username: userNameMaps[i].username, password: config.password, dockerEmail: userNameMaps[i].dockerEmail, namespace: config.namespace)

        }
    }
}

def checkoutBuildAndPushImage(Map config = [:]) {
    Git git = new Git()
    Kaniko kaniko = new Kaniko()
    git.checkOut(branch: config.branch, url: config.url)
    kaniko.buildAndPushImage(dockerImage: config.dockerImage, tag: BUILD_NUMBER)
    }


def deployAppToKubernetes(Map config = [:]) {
            Git git = new Git()
            Kubectl kubectl = new Kubectl()
            Gcloud gcloud = new Gcloud()

            def organizationMap = [[token: "", organization: "giahai99"]]

            def respositoryMap = [[organization  : "giahai99", resporitory: "devops-first-prj.git", filesDeployment: ["my-app-service.yml", "mysql-config.yml", "my-app-deployment.yml"],
                                   deploymentName: "book-deployment", containerName: "my-book-management", dockerImage: "giahai99/javaapp"]]

            for (int i = 0; i < organizationMap.size(); i++) {
                if (config.organization == organizationMap[i].organization) {
                    organizationMap[i].token = token

                    git.pull(token: organizationMap[i].token, organization: organizationMap[i].organization, resporitory: config.resporitory)
                }
            }

            if ( checkClusterName(config.clusterName) != null) {

                def clusterNameMap = checkClusterName(config.clusterName)

                gcloud.authenticate(key: config.key, serviceAccount: clusterNameMap.serviceAccount,
                        project: clusterNameMap.project)

                gcloud.getClusterCredentials(clusterName: clusterNameMap.clusterName, zone: clusterNameMap.zone, project: clusterNameMap.project)

            }

            kubectl.createK8sSecret(secretName: config.secretName, secrets: config.secrets, namespace: config.namespace)

            for (int i = 0; i < respositoryMap.size(); i++) {
                if (config.resporitory == respositoryMap[i].resporitory && config.organization == respositoryMap[i].organization) {

                    String directory = config.resporitory.minus(".git")

                    kubectl.applyFiles(namespace: config.namespace, filesDeployment: respositoryMap[i].filesDeployment, directory: directory)

                    kubectl.setDeploymentImage(namespace: config.namespace, deploymentName: respositoryMap[i].deploymentName, containerName: respositoryMap[i].containerName, dockerImage: respositoryMap[i].dockerImage, tag: BUILD_NUMBER)


        }
    }
}

def deleteSecretAfterRun(Map config = [:]) {

    Kubectl kubectl = new Kubectl()

    kubectl.deleteSecretAfterRun(namespace: config.namespace, secrets: config.secrets)
}

private checkClusterName (String clusterName) {
    def clusterNameMaps = [[clusterName: "cluster-1", serviceAccount:
            "truonggiahai-newaccount-primal@primal-catfish-346210.iam.gserviceaccount.com",
                            project    : "primal-catfish-346210", zone: "asia-southeast1-b"]]

    for (int i = 0; i < clusterNameMaps.size(); i++) {
        if (clusterName == clusterNameMaps[i].clusterName) {
            return clusterNameMaps[i]
        }
        return null
    }
}



