def KanikoContainerTemplate() {
    return """
kind: Pod
spec:
  containers:
  - name: claranet
    image: claranet/gcloud-kubectl-docker:latest
    imagePullPolicy: Always
    command:
    - cat
    tty: true
"""
}
