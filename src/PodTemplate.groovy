
def getDefaultTemplate(def addContainers=[""],def addVolumes=[""]) {
    String containerName = """kind: Pod
spec:
  containers:
  - name: jnlp
    image: 'jenkins/inbound-agent:4.7-1'
  """
    for(int i=0; i<addContainers.size(); i++)
    {
        containerName += addContainers[i]
    }
     String volumeName = """
  volumes:
  """
    for(int i=0; i < addContainers.size(); i++)
    {
        volumeName += addVolumes[i]
    }
    return (containerName + volumeName)
}

def getClaranetBuilder() {
    String claranetBuilder = """- name: claranet
    image: claranet/gcloud-kubectl-docker:latest
    imagePullPolicy: Always
    command:
    - cat
    tty: true
  """
    return [claranetBuilder , ""]
}

def getKanikoBuilder() {
    String kanikoBuilder = """- name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    imagePullPolicy: Always
    command:
    - sleep
    args:
    - 9999999
    volumeMounts:
    - name: jenkins-docker-cfg
      mountPath: /kaniko/.docker  """
    String KanikoVolume = """- name: jenkins-docker-cfg
    projected:
      sources:
      - secret:
          name: docker-credentials
          items:
            - key: .dockerconfigjson
              path: config.json  """
    return [kanikoBuilder , KanikoVolume]
}




