def call(String filePath, Closure closure) {
    def request = libraryResource 'globalconfig.yml'
    println "$request"
    def map = readYaml text: request
    println map
  
  if (!filePath.isEmpty()) {
     def file_exist = findFiles (glob: filePath)
     if (file_exist.length == 1) {
         def map1 = readYaml file: filePath
          println map1
          map.putAll(map1)
     }
  }
  
  MavenBuilder builder = new MavenBuilder(map_var: map)
  closure.delegate = builder
  closure.resolveStrategy = Closure.DELEGATE_FISRT
  closure()
  
}

def call(Closure closure) {
    call('', closure) 
}
