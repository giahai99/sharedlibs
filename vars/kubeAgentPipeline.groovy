def call() {
    pipeline {
 agent {
    kubernetes {
      yaml '''
        apiVersion: v1
        kind: Pod
        spec:
          containers:
          - name: claranet
            image: claranet/gcloud-kubectl-docker:latest
            imagePullPolicy: Always
            command:
            - cat
            tty: true
        '''
    }
  }
  
  stages {
    stage('Run docker') {
      steps {
          container('claranet') {
              sh 'kubectl version'
              sh 'gcloud version'
     
    withVault(configuration: [timeout: 60, vaultCredentialId: 'vault', vaultUrl: 'http://34.125.10.91:8200'], vaultSecrets: [[path: 'kv/service-account', secretValues: [[vaultKey: 'key']]]]) {

            sh 'echo $key'
            sh 'echo $key > key.json'
            sh 'cat key.json'
            sh 'gcloud auth activate-service-account truonggiahai-newaccount-primal@primal-catfish-346210.iam.gserviceaccount.com --key-file=key.json --project=primal-catfish-346210'
            sh 'gcloud container clusters get-credentials cluster-1 --zone asia-southeast1-b --project primal-catfish-346210'
            sh 'kubectl get pods'
            
            
            }
        
          }
        }
      }
    }
}

}
