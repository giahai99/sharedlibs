
//PodTemplate podTemplate = new PodTemplate()
//
//def containerNames = [podTemplate.getClaranetBuilder()[0], podTemplate.getKanikoBuilder()[0]]
//def volumeNames = [podTemplate.getClaranetBuilder()[1] ,podTemplate.getKanikoBuilder()[1]]
//
////def containerNames = [podTemplate.getClaranetBuilder()[0]]
////def volumeNames = [podTemplate.getClaranetBuilder()[1]]
//
//println (podTemplate.getDefaultTemplate(containerNames, volumeNames))


PodTemplate podTemp = new PodTemplate()

def containerNames = [podTemp.getClaranetBuilder()[0], podTemp.getKanikoBuilder()[0]]
def volumeNames = [podTemp.getClaranetBuilder()[1] ,podTemp.getKanikoBuilder()[1]]

String template = (podTemp.getDefaultTemplate(containerNames, volumeNames))

println (template)