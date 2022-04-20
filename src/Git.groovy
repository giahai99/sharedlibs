def pull() {
  
    sh 'git init'
  
    sh 'git pull https://$token@github.com/giahai99/devops-first-prj.git'
}
