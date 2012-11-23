package graphviz

import grails.converters.JSON;

class ResumeController {

  def resumeService

  def index = {
   redirect(uri : "/?ds=resume")
  }

  def api = {

    if(params.callback==null) {
      render resumeService.lookupData(params.id) as JSON
    }
    else {
      render (
        text: "${params.callback}(${resumeService.lookupData(params.id) as JSON})",
        contentType: "text/javascript",
        encoding: "UTF-8"
      )
    }
  }

}

