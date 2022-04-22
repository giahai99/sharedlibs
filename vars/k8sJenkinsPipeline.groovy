#!/usr/bin/env groovy

def call() {
    PodTemplate podTemplate = new PodTemplate()
    StageOperator stageOperator = new StageOperator()

//    def containers = [
//            containerTemplate(name: 'node', image: 'node', ttyEnabled: true),
//            containerTemplate(name: 'docker', image: 'docker', ttyEnabled: true),
//            containerTemplate(name: 'jnlp', image: 'jenkins/jnlp-slave:3.35-5-alpine', ttyEnabled: true)
//    ]
//    def REGISTRY_CONFIG = [
//            url: "https://hub.docker.com/repository/docker/marceloschirbel/jsl-medium",
//            credentials: "90f8072d-4194-4c7e-807b-90e4a4135093"
//    ]
//    def name = args.name
//    def label = "job-${name}-${UUID.randomUUID().toString()}".take(15)
//    def tag = "${UUID.randomUUID().toString()}".take(5)
//    def imageName = "${registryRepository}:${tag}"

    podTemplate(label: label, containers: containers, serviceAccount: 'jenkins') {
        node(label) {
            stage('Checkout') {
                checkout scm
            }
            container('node') {
                stage('Install Dependencies') {
                    sh 'npm install'
                }
                stage('Build and publish image to Registry') {
                    container('docker') {
                        docker.withRegistry(REGISTRY_CONFIG.url, REGISTRY_CONFIG.credentials) {
                            docker.build(imageName).push()
                        }
                    }
                }
            }
        }
    }

//    podTemplate(yaml: podTemplate.addKanikoBuilder()) {
////        kubernetes {
//
////        }
//        node(podTemplate.addKanikoBuilder()) {
//            stage('Create secret for docker hub') {
//
//
//                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
//                                                                                                                                         [path: 'kv/dockerhub-password', secretValues: [[vaultKey: 'password']]]]) {
//
//                    stageOperator.createDockerHubSecret(serviceAccountKey: key, clusterName: "cluster-1", username: "giahai99", password: password, namespace: "devops-tools")
//                }
//
//
//            }
//        }
//    }




//        node(
//            yaml podTemplate.addClaranetBuilder()
//        ) {
//
//
//            stage('Create secret for docker hub') {
//
//
//                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
//                                                                                                                                         [path: 'kv/dockerhub-password', secretValues: [[vaultKey: 'password']]]]) {
//
//                    stageOperator.createDockerHubSecret(serviceAccountKey: key, clusterName: "cluster-1", username: "giahai99", password: password, namespace: "devops-tools")
//                }
//
//
//            }
//        }
//
//
//    agent {
//        kubernetes {
//            yaml podTemplate.addKanikoBuilder()
//        }
//
//        node {
//            stage('Checkout and Build With Kaniko') {
//
//
//                stageOperator.checkoutBuildAndPushImage(branch: "main", url: "https://github.com/giahai99/devops-first-prj.git", dockerImage: "giahai99/javaapp")
//            }
//        }
//    }
//    agent {
//        kubernetes {
//            yaml podTemplate.getDefaultTemplate()
//        }
//
//        node {
//            // Running Docker container, make sure port 8080 is opened in
//            stage('Deploy App to Kubernetes') {
//
//
//                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/mysql', secretValues: [[vaultKey: 'username'], [vaultKey: 'password']]]
//                                                                                                                                         , [path: 'kv/github-token', secretValues: [[vaultKey: 'token']]]]) {
//
//                    stageOperator.deployAppToKubernetes(organization: "giahai99", resporitory: "devops-first-prj.git", secretName: "db-user-pass",
//                            secrets: [username: username, password: password], namespace: "devops-tools")
//
//
//                }
//            }
//        }
//    }
//
//
//
//    agent {
//        kubernetes {
//            yaml podTemplate.getDefaultTemplate()
//        }
//        node {
//            stage('Clean up after run') {
//                stageOperator.deleteSecretAfterRun(namespace: "devops-tools", secrets: ["db-user-pass", "docker-credentials"])
//            }
//        }
//    }
}
