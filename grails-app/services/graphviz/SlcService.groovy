package graphviz

import org.springframework.web.context.request.RequestContextHolder
import javax.servlet.http.HttpSession
import uk.co.desirableobjects.oauth.scribe.OauthService
import org.scribe.model.Token
import grails.converters.JSON
import graphviz.oauth.OAuth20ServiceBearerAdapter

class SlcService {

    // start constants
    final String API_BASE_URL = 'https://api.sandbox.slcedu.org/api/rest/v1'
    final String SCHOOL_KEY_PREFIX = 'Schools_'
    // end constants
    
    OauthService oauthService
    boolean bearerEnabled=false // part of authorization hack
    
    def retrieveFromSession(key) {
      // need the session available here
      HttpSession session = RequestContextHolder.currentRequestAttributes().getSession()
      session[key]
    }
        
    // all rest calls must go through here
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
    
    // entry point for controller
    def lookupData(searchId) {
    
      println "API lookup for ${searchId}"
      
      if(searchId==null) {
        searchId = 'SLC'    
      }
           
       def idParts = searchId.tokenize('_')
       def entityName = idParts[0]        // first token is entiry
       def entityArgs = idParts.drop(1)   // everything is argument
       
       // find method should exist for each Entity
       // If not, methodMissing provides dummy impl for now
       "find${entityName}"(*entityArgs)
    }
    
    // the center of everything
    def findSLC() {
    
      // TODO - need to make this method user centric
      // base impl on user's role
      
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
    
    // find a certain school
    def findSchools(schoolId) {
      getAllSchools(schoolId)[0]
    }
   
    // find all or certain school
    // NOTE: schoolId is optional
    def getAllSchools(schoolId) {
    
      def schoolUrl = "${API_BASE_URL}/schools/"
      
      if(schoolId !=null) {
        schoolUrl += schoolId
      }

      def schoolList = []      
      def schoolData = call('get',schoolUrl)
      
      // wrap single school in List
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
          schoolView['id'] = SCHOOL_KEY_PREFIX + school.id
        }
        
        schoolView['label'] = school.educationOrgIdentificationCode[0].ID
        schoolView['title'] = school.educationOrgIdentificationCode[0].ID
	    schoolView['summary'] = "SLC Identity # ${school.id}"
	    schoolView['content'] = extractContent(school)
	    schoolView['imageUrl'] = null
	    schoolView['relations'] = getLinks.collect { (it.rel - 'get') + '_' + school.id  }

	    schoolList.add(schoolView)
      }

      return schoolList
    }
    
    // provide a temp impl for all non-implemented entities
    def methodMissing(String name, args) {
      
       if(name =~ /^find.+/) {
         def missingView = [:]  
         def entityName = name - 'find'
         def entityNameWithSpaces = spaceCamelCase(entityName)
	     missingView['id'] = entityName + '_' + args.join('_')
		 missingView['label'] = entityNameWithSpaces
		 missingView['title'] = entityNameWithSpaces
	     missingView['summary'] = 'TODO'
	     missingView['content'] = 'Not implemented yet'
	     missingView['imageUrl'] = null
	     missingView['relations'] = []
	     
	     println "missing " + missingView['id'] 
	     
	     return missingView
       }
       else throw new MissingMethodException(name, delegate, args)
   }
   
   // start collection of string utils
  
   def eliminateRepeats(str,repeatedString) {
     str.replaceAll(~"(${repeatedString}){2,}",repeatedString)
   }
   
   def spaceCamelCase(str) {
     str.capitalize().replaceAll(/([A-Z][a-z]*)/, '$1 ').trim()
   }
    
   def extractContent(data) {
      
      def content = extractContent(data,null).toString()
      content = eliminateRepeats(content,"<br />")
      content = eliminateRepeats(content,"<hr />")
      
      return "<div style='width:420px;height:300px;overflow:scroll;white-space:nowrap;'>${content}</div>"
    }
    
    def extractContent(data,contentIn) {
        
      StringBuilder content = contentIn!=null?contentIn:new StringBuilder()
     
       if(data instanceof List) {

         if(data.size() > 0 ) {
           data.each { 
             extractContent(it,content)  
           }
         }
       }
       else if(data instanceof Map) {
         data.each { k , v ->
           if(k != "links") {
             if(!(v instanceof List<Map> && v.size() == 0)) {      
               if(v instanceof Map || v instanceof List) {
                 content.append("<hr />")
                 content.append("<b>${spaceCamelCase(k).toUpperCase()}:</b> ")
                 content.append("<hr /><i>")
               }
               else {
                 if(k==k.toUpperCase()) {
                   content.append("<b>${k}:</b> ")
                 }
                 else {
                   content.append("<b>${spaceCamelCase(k)}:</b> ")
                 }
               }
               extractContent(v,content)
               content.append("<br />")
               if(v instanceof Map || v instanceof List) {
                 content.append("</i><hr />")
               }
             } 
           } 
         }
       }
       else if(data!=null) {
         content.append(data)
         content.append("<br />")
       }
       
       return content
    }
    // end collection of string utils
} 
