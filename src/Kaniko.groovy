def buildAndPushImage() {
    sh '''#!/busybox/sh
       /kaniko/executor --context `pwd` --destination giahai99/javaapp:${BUILD_NUMBER}
       '''  
}
