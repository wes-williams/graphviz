package graphviz

import grails.converters.JSON

class ResumeService {

  static transactional = true

  def resume = null;

  public ResumeService() {
    resume = JSON.parse(new File("json/Resume.js").text)
  }

  def lookupData(searchId) {

    if(searchId==null) {
      searchId = 'me'
    }

    resume.find { it.id == searchId }
  }
}

