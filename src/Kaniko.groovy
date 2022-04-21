def buildAndPushImage(String dockerImage, String tag) {
    container(name: 'kaniko', shell: '/busybox/sh') {
        sh """#!/busybox/sh
       /kaniko/executor --context `pwd` --destination $dockerImage:$tag
       """
    }
}