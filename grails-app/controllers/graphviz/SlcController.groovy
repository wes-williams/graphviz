package graphviz

import grails.converters.JSON;
import graphviz.slc.*;

class SlcController {

  def slcService
  def stickyNoteService

  def index = { } // enables login page


  def api = {
    def slcData = null

    // lookup post it with sticky note  service
    if(params.id!=null && params.id.startsWith(stickyNoteService.STICKY_NOTE_PREFIX)) {
      slcData = stickyNoteService.lookupStickyNote(params.id)
    }
    else { // lookup everything else with slc
      slcData = slcService.lookupData(params.id)
      // lookup sticky notes attached to slc data

      def stickyNoteList = stickyNoteService.lookupRelatedStickyNotes(slcData.id)
      if(stickyNoteList.size()>0) {
        // want the sticky notes to be front and center 
        // NOTE : only one note currently, so this may be silly impl if not more in future
        def newRelations = []
        newRelations.addAll(stickyNoteList)
        newRelations.addAll(slcData.relations)
        slcData.relations = newRelations
      }
    }

    if(params.callback==null) {
      render slcData  as JSON
    }
    else {
      render (
        text: "${params.callback}(${slcData as JSON})",
        contentType: "text/javascript",
        encoding: "UTF-8"
       )
    }
  }
}
