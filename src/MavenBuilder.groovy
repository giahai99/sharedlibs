class MavenBuilder{
Map map_var
  def mavenBuild(){
    "mvn "+map_var.builder.maven.goal 
  }
}
