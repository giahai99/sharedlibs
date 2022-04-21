def pull(Map config = [:]) {
    container('claranet') {

        sh "git clone https://$config.token@github.com/$config.organization/$config.resporitory"

    }
}

def checkOut(Map config = [:]) {
    container(name: 'kaniko', shell: '/busybox/sh') {
        checkout([$class: 'GitSCM', branches: [[name: "*/$config.branch"]], extensions: [], userRemoteConfigs: [[url: config.url]]])
    }
}
