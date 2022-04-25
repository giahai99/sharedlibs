#!/usr/bin/env groovy

def call() {
    StageOperator stageOperator = new StageOperator()

    PodTemplate podTemp = new PodTemplate()
    podTemp.addClaranetBuilder()

    podTemplate(yaml: podTemp.getTemplate()) {
        node(POD_LABEL) {
                stageOperator.createDockerHubSecret(clusterName: "cluster-1", username: "giahai99", namespace: "devops-tools")
        }
    }

    podTemp.addKanikoBuilder()

    podTemplate(yaml: podTemp.getTemplate()) {
        node(POD_LABEL) {
            try {
                stageOperator.checkoutBuildAndPushImage(branch: "main", url: "https://github.com/giahai99/devops-first-prj.git", dockerImage: "giahai99/javaapp")

                stageOperator.deployAppToKubernetes(organization: "giahai99", resporitory: "devops-first-prj.git", clusterName: "cluster-1",
                        secretName: "db-user-pass", namespace: "devops-tools")
            }
            finally {
                stageOperator.deleteSecretAfterRun(namespace: "devops-tools", secrets: ["db-user-pass", "docker-credentials"], clusterName: "cluster-1")
            }
        }
    }
}
