def buildAndPushImage(Map config = [:]) {
    container(name: 'kaniko', shell: '/busybox/sh') {
        sh """#!/busybox/sh
       /kaniko/executor --context `pwd` --destination $config.dockerImage:$config.tag
       """
    }
}