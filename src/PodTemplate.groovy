#!/usr/bin/env groovy

def getDefaultTemplate(String addContainer="",String addVolumes="") {
    return """
kind: Pod
spec:
  containers:
  - name: jnlp
    image: 'jenkins/inbound-agent:4.7-1'
  ${addContainer}
  volumes:
  ${addVolumes}
"""
}

def addClaranetBuilder() {
   String claranetBuilder = """
  - name: claranet
    image: claranet/gcloud-kubectl-docker:latest
    imagePullPolicy: Always
    command:
    - cat
    tty: true
"""
    return getDefaultTemplate(claranetBuilder)
}

def addKanikoBuilder() {
    String kanikoBuilder = """
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
    String KanikoVolume = """
  volumes:
  - name: jenkins-docker-cfg
    projected:
      sources:
      - secret:
          name: docker-credentials
          items:
            - key: .dockerconfigjson
              path: config.json"""
    return getDefaultTemplate(kanikoBuilder,KanikoVolume)
}