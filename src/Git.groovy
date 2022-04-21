def pull(String token, String organization, String resporitory) {
    container('claranet') {

        sh 'git clone https://$token@github.com/$organization/$resporitory'

    }
}


def checkOut(String branch, String url) {
    container(name: 'kaniko', shell: '/busybox/sh') {
        checkout([$class: 'GitSCM', branches: [[name: '*/$branch']], extensions: [], userRemoteConfigs: [[url: url]]])
    }
}
