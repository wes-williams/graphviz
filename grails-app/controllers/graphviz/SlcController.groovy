package graphviz

import grails.converters.JSON;

class SlcController {

    def slcService
   
    def index() { } // enables login page
	
	def api = {

	   if(params.callback==null) {
		   render slcService.lookupData(params.id) as JSON
	   }
	   else {
		 render (
			text: "${params.callback}(${slcService.lookupData(params.id) as JSON})",
			contentType: "text/javascript",
			encoding: "UTF-8"
		 )
	   }
	}
}
