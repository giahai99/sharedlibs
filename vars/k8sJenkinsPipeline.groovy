#!/usr/bin/env groovy

def call() {
    StageOperator stageOperator = new StageOperator()

    PodTemplate podTemp = new PodTemplate()
    podTemp.addClaranetBuilder()

    podTemplate(yaml: podTemp.getTemplate()) {
        node(POD_LABEL) {
            stage('Create a secret for docker hub') {
                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
                                                                                                                                         [path: 'kv/dockerhub-password', secretValues: [[vaultKey: 'password']]]]) {
                    stageOperator.createDockerHubSecret(serviceAccountKey: key, clusterName: "cluster-1", username: "giahai99", password: password, namespace: "devops-tools")
                }
            }
        }
    }

    podTemp.addKanikoBuilder()

    podTemplate(yaml: podTemp.getTemplate()) {
        node(POD_LABEL) {
            try {
                stage('Checkout and Build With Kaniko') {
                    stageOperator.checkoutBuildAndPushImage(branch: "main", url: "https://github.com/giahai99/devops-first-prj.git", dockerImage: "giahai99/javaapp")
                }

                stage('Create a secret for MySQL') {
                    withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/github-token', secretValues: [[vaultKey: 'token']]],
                                                                                                                                             [path: 'kv/mysql', secretValues: [[vaultKey: 'username'], [vaultKey: 'password']]], [path: 'kv/service-account', secretValues: [[vaultKey: 'key']]]]) {

                        stageOperator.deployAppToKubernetes(organization: "giahai99", token: token, resporitory: "devops-first-prj.git", clusterName: "cluster-1", serviceAccountKey: key,
                                secretName: "db-user-pass", secrets: [username: username, password: password], namespace: "devops-tools")
                    }
                }
            }
            finally {
                stage('Clean up after run') {
                    withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/mysql', secretValues: [[vaultKey: 'username'], [vaultKey: 'password']]], [path: 'kv/service-account', secretValues: [[vaultKey: 'key']]]]) {

                        stageOperator.deleteSecretAfterRun(namespace: "devops-tools", secrets: ["db-user-pass", "docker-credentials"], clusterName: "cluster-1", serviceAccountKey: key)
                    }
                }
            }
        }
    }
}
