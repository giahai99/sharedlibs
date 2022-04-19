#!/usr/bin/env groovy

def call() {
    sh """- name: claranet
            image: claranet/gcloud-kubectl-docker:latest
            imagePullPolicy: Always
            command:
            - cat
            tty: true"""
}
