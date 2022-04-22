#!/usr/bin/env groovy

def call() {
    PodTemplate podTemp = new PodTemplate()
    StageOperator stageOperator = new StageOperator()

    podTemplate(yaml: podTemp.addClaranetBuilder()) {
        node(POD_LABEL) {
            stage('Create secret for docker hub') {
                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
                                                                                                                                         [path: 'kv/dockerhub-password', secretValues: [[vaultKey: 'password']]]]) {

                    stageOperator.createDockerHubSecret(serviceAccountKey: key, clusterName: "cluster-1", username: "giahai99", password: password, namespace: "devops-tools")
                }
            }
        }
    }

    podTemplate(yaml: podTemp.addKanikoBuilder()) {
        node(POD_LABEL) {
            stage('Checkout and Build With Kaniko') {
                stageOperator.checkoutBuildAndPushImage(branch: "main", url: "https://github.com/giahai99/devops-first-prj.git", dockerImage: "giahai99/javaapp")
                }
            }
        }

    podTemplate(yaml: podTemp.addClaranetBuilder()) {
        node(POD_LABEL) {
            stage('Create secret for docker hub') {
                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
                                                                                                                                         [path: 'kv/dockerhub-password', secretValues: [[vaultKey: 'password']]]]) {
                    stageOperator.createDockerHubSecret(serviceAccountKey: key, clusterName: "cluster-1", username: "giahai99", password: password, namespace: "devops-tools")
                }
            }
        }
    }

        podTemplate(yaml: podTemp.addClaranetBuilder()) {
            node() {
            stage('Clean up after run') {
                stageOperator.deleteSecretAfterRun(namespace: "devops-tools", secrets: ["db-user-pass", "docker-credentials"])
            }
        }
    }
}
