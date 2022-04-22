#!/usr/bin/env groovy

def call() {
    PodTemplate podTemp = new PodTemplate()
    StageOperator stageOperator = new StageOperator()

//    def containerNames = [podTemp.getClaranetBuilder()[0], podTemp.getKanikoBuilder()[0]]
//    def volumeNames = [podTemp.getClaranetBuilder()[1] ,podTemp.getKanikoBuilder()[1]]



    podTemplate(yaml: """
kind: Pod
spec:
  containers:
  - name: jnlp
    image: 'jenkins/inbound-agent:4.7-1'
  - name: claranet
    image: claranet/gcloud-kubectl-docker:latest
    imagePullPolicy: Always
    command:
    - cat
    tty: true""") {
        node(POD_LABEL) {
            stage('Create secret for docker hub') {
                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
                                                                                                                                         [path: 'kv/dockerhub-password', secretValues: [[vaultKey: 'password']]]]) {

                    stageOperator.createDockerHubSecret(serviceAccountKey: key, clusterName: "cluster-1", username: "giahai99", password: password, namespace: "devops-tools")
                }
            }
        }
    }

    podTemplate(yaml: """
kind: Pod
spec:
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    imagePullPolicy: Always
    command:
    - sleep
    args:
    - 9999999
    volumeMounts:
    - name: jenkins-docker-cfg
      mountPath: /kaniko/.docker  
  volumes:
  - name: jenkins-docker-cfg
    projected:
      sources:
      - secret:
          name: docker-credentials
          items:
            - key: .dockerconfigjson
              path: config.json""") {
        node(POD_LABEL) {
            stage('Checkout and Build With Kaniko') {
                stageOperator.checkoutBuildAndPushImage(branch: "main", url: "https://github.com/giahai99/devops-first-prj.git", dockerImage: "giahai99/javaapp")
                }
            }
        }

    podTemplate(yaml: """
kind: Pod
spec:
  containers:
  - name: jnlp
    image: 'jenkins/inbound-agent:4.7-1'
  - name: claranet
    image: claranet/gcloud-kubectl-docker:latest
    imagePullPolicy: Always
    command:
    - cat
    tty: true""") {
        node(POD_LABEL) {
            stage('Create secret for docker hub') {
                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]],
                                                                                                                                         [path: 'kv/mysql', secretValues: [[vaultKey: 'username'], [vaultKey: 'password']]]]) {
                    stageOperator.deployAppToKubernetes(organization: "giahai99", resporitory: "devops-first-prj.git", secretName: "db-user-pass",
                            secrets: [username: username, password: password], namespace: "devops-tools")
                }
            }

            stage('Clean up after run') {
                stageOperator.deleteSecretAfterRun(namespace: "devops-tools", secrets: ["db-user-pass", "docker-credentials"])
            }

        }
    }



}
