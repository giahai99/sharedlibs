#!/usr/bin/env groovy


PodTemplate podTemplate = new PodTemplate()
Git git = new Git()
Kaniko kaniko = new Kaniko()
Kubectl kubectl = new Kubectl()
Gcloud gcloud = new Gcloud()

def clusterNameMaps = [[clusterName: "cluster-1", serviceAccount: "truonggiahai-newaccount-primal@primal-catfish-346210.iam.gserviceaccount.com",
                      project: "primal-catfish-346210", key: "", zone: "asia-southeast1-b"]]
def userNameMaps = [username: "giahai99", password: "", dockerEmail: "Haidepzai_kut3@yahoo.com"]

def createDockerHubSecret(Map config = [:]) {
    steps {
        script {
            withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
                                                                                                                                     [path: 'kv/dockerhub-password', secretValues: [[vaultKey: 'password']]]]) {
                // authenticate with service account and get kubectl config file
                for (int i = 0; i < clusterNameMaps.size(); i++) {
                    if (config.clusterName == clusterNameMaps[i].clusterName) {
                        clusterNameMaps[i].key = key

                        gcloud.authenticate(key: clusterNameMaps[i].key, serviceAccount: clusterNameMaps[i].serviceAccount,
                                project: clusterNameMaps[i].project)

                        gcloud.getClusterCredentials(clusterName: clusterNameMaps[i].clusterName, zone: clusterNameMaps[i].zone, project: clusterNameMaps[i].project)
                    }
                }

                // create secret for docker hub
                for (int i = 0; i < userNameMaps.size(); i++) {
                    if (config.username == userNameMaps[i].username) {
                        userNameMaps[i].password = password

                        kubectl.createDockerRegistrySecret(username: userNameMaps[i].username, password: userNameMaps[i].password, dockerEmail: userNameMaps[i].dockerEmail, namespace: config.namespace)
                    }
                }
            }
        }
    }
}

def other(Map config = [:]) {

    stage('Checkout and Build With Kaniko') {
        agent {
            kubernetes {
                yaml podTemplate.addKanikoBuilder()
            }
        }
        steps {
            script {
                git.checkOut(branch: "main", url: "https://github.com/giahai99/devops-first-prj.git")
                kaniko.buildAndPushImage(dockerImage: "giahai99/javaapp", tag: BUILD_NUMBER)
            }
        }
    }


    // Running Docker container, make sure port 8080 is opened in
    stage('Deploy App to Kubernetes') {
        steps {
            script {
                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/mysql', secretValues: [[vaultKey: 'username'], [vaultKey: 'password']]]
                                                                                                                                         , [path: 'kv/github-token', secretValues: [[vaultKey: 'token']]]]) {

                    git.pull(token: token, organization: "giahai99", resporitory: "devops-first-prj.git")

                    kubectl.createGenericSecret(secretName: "db-user-pass", username: username, password: password)

                    kubectl.applyFiles(nameSpace: "devops-tools", fileList: ["my-app-service.yml", "mysql-config.yml", "my-app-deployment.yml"], directory: "devops-first-prj")

                    kubectl.setDeploymentImage(nameSpace: "devops-tools", deploymentName: "book-deployment", containerName: "my-book-management", dockerImage: "giahai99/javaapp", tag: BUILD_NUMBER)

                }
            }
        }
    }
}