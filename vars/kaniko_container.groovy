#!/usr/bin/env groovy

def call() {
    sh """- name: kaniko
          image: gcr.io/kaniko-project/executor:debug
          imagePullPolicy: Always
          command:
          - sleep
          args:
          - 9999999
          volumeMounts:
            - name: jenkins-docker-cfg
              mountPath: /kaniko/.docker"""  
}
