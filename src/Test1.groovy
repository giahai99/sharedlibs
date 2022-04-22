
PodTemplate podTemplate = new PodTemplate()

def containerNames = [podTemplate.getClaranetBuilder()[0], podTemplate.getKanikoBuilder()[0]]
def volumeNames = [podTemplate.getClaranetBuilder()[1] ,podTemplate.getKanikoBuilder()[1]]

println (podTemplate.getDefaultTemplate(containerName, volumeName))