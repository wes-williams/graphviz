package graphviz

import grails.converters.JSON;

class SlcController {

    def slcService
    

    def index() { }

    def api() { 
      render slcService.findSchools() as JSON 
    }
}
