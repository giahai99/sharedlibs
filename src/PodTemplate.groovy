
class PodTemplate {

    String volumesField = """
  volumes:"""

    def getTemplate(def addContainers = [""], def addVolumes = [""]) {
        return (containersField + volumesField)
    }

    String containersField = """kind: Pod
spec:
  containers:
  - name: jnlp
    image: 'jenkins/inbound-agent:4.7-1'"""

    def addClaranetBuilder() {

        containersField += """
  - name: claranet
    image: claranet/gcloud-kubectl-docker:latest
    imagePullPolicy: Always
    command:
    - cat
    tty: true"""
    }

    def addKanikoBuilder() {

        containersField += """
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    imagePullPolicy: Always
    command:
    - sleep
    args:
    - 9999999
    volumeMounts:
    - name: jenkins-docker-cfg
      mountPath: /kaniko/.docker"""

        volumesField += """
  - name: jenkins-docker-cfg
    projected:
      sources:
      - secret:
          name: docker-credentials
          items:
            - key: .dockerconfigjson
              path: config.json  """
    }
}



