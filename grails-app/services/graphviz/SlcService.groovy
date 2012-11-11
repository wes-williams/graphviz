package graphviz

import org.springframework.web.context.request.RequestContextHolder
import javax.servlet.http.HttpSession
import uk.co.desirableobjects.oauth.scribe.OauthService
import org.scribe.model.Token
import grails.converters.JSON
import graphviz.oauth.OAuth20ServiceBearerAdapter

class SlcService {

    final String API_BASE_URL = 'https://api.sandbox.slcedu.org/api/rest/v1'
    final String SCHOOL_KEY_PREFIX = 'Schools_'

    OauthService oauthService
    boolean bearerEnabled=false // part of authorization hack
    
    def retrieveFromSession(key) {
      // need the session available here
      HttpSession session = RequestContextHolder.currentRequestAttributes().getSession()
      session[key]
    }
        
    def call(verb,url) {
    
      Token accessToken = retrieveFromSession(oauthService.findSessionKeyForAccessToken('slc'))    
      // token should be available
      if(accessToken==null) {
        println "Lost Session to SLC"
        // TODO : re-establish connection
        return [:]
      }

      // start hack to enable bearer header for authorization
      if(!bearerEnabled) {
        oauthService.services['slc'].service = new OAuth20ServiceBearerAdapter(oauthService.services['slc'].service)
	    bearerEnabled=true
      }
      // end hack to enable bearer header for authorization

      def response = oauthService."${verb}SlcResource"(accessToken,url)

      JSON.parse(response.body)
    }
    
    def lookupData(searchId) {
    
      println "API lookup for ${searchId}"
      
      if(searchId==null) {
        searchId = 'SLC'    
      }
           
       def idParts = searchId.tokenize('_')
       def entityName = idParts[0]
       def entityArgs = idParts.drop(1)
       
       "find${entityName}"(*entityArgs)
    }
    
    def findSLC() {
    
      def slcView = [:]  
      slcView['id'] = 'slc'
      slcView['label'] = 'SLC'
      slcView['title'] = 'Shared Learning Collaborative'
	  slcView['summary'] = ''
	  slcView['content'] = ''
	  slcView['imageUrl'] = null
	  slcView['relations'] = getAllSchools().collect { it.id } // this is wasteful. TODO : cache?
	    
	  return slcView
    }
    
    def findSchools(schoolId) {
      getAllSchools(schoolId)[0]
    }
   
    def getAllSchools(schoolId) {
    
      def schoolUrl = "${API_BASE_URL}/schools/"
      
      if(schoolId !=null) {
        schoolUrl += schoolId
      }

      def schoolList = []      
      def schoolData = call('get',schoolUrl)
      
      if(schoolId !=null) {
         schoolData = [schoolData]
      }
      
      schoolData.each { school ->
        def schoolView = [:]

	    def selfLink = null
	    def getLinks = null
	    
	    if(school['links']==null) {
	      selfLinks=[]
	      getLinks=[]
	    }
	    else {
	      selfLink=school['links'].findAll { it.rel == 'self' }
	      getLinks=school['links'].findAll { it.rel =~ /^get.+/ }
        }
        
        if(schoolId!=null) {
          schoolView['id'] = SCHOOL_KEY_PREFIX + schoolId
        }
        else {
          schoolView['id'] = SCHOOL_KEY_PREFIX + (selfLink[0].href - schoolUrl.toString())
        }
        
        schoolView['label'] = school.educationOrgIdentificationCode[0].ID
        schoolView['title'] = school.educationOrgIdentificationCode[0].ID
	    schoolView['summary'] = ''
	    schoolView['content'] = ''
	    schoolView['imageUrl'] = null
	    schoolView['relations'] = getLinks.collect { (it.rel - 'get') + '_' + (schoolView.id - SCHOOL_KEY_PREFIX)  }

	    schoolList.add(schoolView)
      }

      return schoolList
    }
    
    
     def methodMissing(String name, args) {
      
       if(name =~ /^find.+/) {
         def missingView = [:]  
         def entityName = name - 'find'
         def entityNameWithSpaces = entityName.replaceAll(/([A-Z][a-z]*)/, '$1 ').trim()
	     missingView['id'] = entityName + '_' + args.join('_')
		 missingView['label'] = entityNameWithSpaces
		 missingView['title'] = entityNameWithSpaces
	     missingView['summary'] = ''
	     missingView['content'] = ''
	     missingView['imageUrl'] = null
	     missingView['relations'] = []
	     
	     println "missing " + missingView['id'] 
	     
	     return missingView
       }
       else throw new MissingMethodException(name, delegate, args)
   }
} 
