#!/usr/bin/env groovy

def call() {
    PodTemplate podTemp = new PodTemplate()
    StageOperator stageOperator = new StageOperator()

    def containerNames = [podTemp.getClaranetBuilder()[0]]
    def volumeNames = [podTemp.getClaranetBuilder()[1]]

    podTemplate(yaml: podTemp.getDefaultTemplate(containerNames, volumeNames)) {
        node(POD_LABEL) {
            stage('Create secret for docker hub') {
                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
                                                                                                                                         [path: 'kv/dockerhub-password', secretValues: [[vaultKey: 'password']]]]) {

                    stageOperator.createDockerHubSecret(serviceAccountKey: key, clusterName: "cluster-1", username: "giahai99", password: password, namespace: "devops-tools")
                }
            }
        }
    }

    containerNames.add(podTemp.getKanikoBuilder()[0])
    volumeNames.add(podTemp.getKanikoBuilder()[1])

    podTemplate(yaml: podTemp.getDefaultTemplate(containerNames, volumeNames)) {
        node(POD_LABEL) {
            stage('Checkout and Build With Kaniko') {
                stageOperator.checkoutBuildAndPushImage(branch: "main", url: "https://github.com/giahai99/devops-first-prj.git", dockerImage: "giahai99/javaapp")
                }
            }
        }

    containerNames.remove(podTemp.size()-1)
    volumeNames.remove(podTemp.size()-1)

    podTemplate(yaml: podTemp.getDefaultTemplate(containerNames, volumeNames)) {
        node(POD_LABEL) {
            stage('Create secret for docker hub') {
                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/github-token', secretValues: [[vaultKey: 'token']]],
                                                                                                                                         [path: 'kv/mysql', secretValues: [[vaultKey: 'username'], [vaultKey: 'password']]]]) {
                    stageOperator.deployAppToKubernetes(organization: "giahai99", token: token, resporitory: "devops-first-prj.git", secretName: "db-user-pass",
                            secrets: [username: username, password: password], namespace: "devops-tools")
                }
            }

            stage('Clean up after run') {
                stageOperator.deleteSecretAfterRun(namespace: "devops-tools", secrets: ["db-user-pass", "docker-credentials"])
            }

        }
    }
}
