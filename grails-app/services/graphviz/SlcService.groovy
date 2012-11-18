package graphviz

import org.springframework.web.context.request.RequestContextHolder
import javax.servlet.http.HttpSession
import uk.co.desirableobjects.oauth.scribe.OauthService
import org.scribe.model.Token
import grails.converters.JSON
import graphviz.oauth.OAuth20ServiceBearerAdapter

class SlcService {

    final String API_BASE_URL = 'https://api.sandbox.slcedu.org/api/rest/v1'
    final boolean DEBUG=false
    
    OauthService oauthService
    Map resourceMappings = null
    boolean bearerEnabled=false // part of authorization hack
    
    //  loading entity metadata for methodMissing
    public SlcService() {
      resourceMappings = JSON.parse(new File("json/SlcResourceMappings.js").text)
    }
    
    // all session access must got through here
    // NOTE: Not a great idea to access session in service
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
        return ['error' : 'slc connection lost']
      }

      // start hack to enable bearer header for authorization
      if(!bearerEnabled) {
        oauthService.services['slc'].service = new OAuth20ServiceBearerAdapter(oauthService.services['slc'].service)
	    bearerEnabled=true
      }
      // end hack to enable bearer header for authorization

      def response = null
      
      try {
        response = oauthService."${verb}SlcResource"(accessToken,API_BASE_URL + url)
      }
      catch(Throwable t) {
        println "failed call to ${url}: ${t}"
        return [ 'error' : 'failed slc lookup']
      }
      
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
       
       // methodMissing will provide all the wanted find impls
       // cleanseView will make sure all values are populated
       cleanseView(searchId, "find${entityName}"(*entityArgs))
    }
    
    // just a safety net
    // the app can't handle nulls
    def cleanseView(searchId, view) {
    
      if(view==null || view.containsKey('error')) {
	    println searchId + ' not found: ' + view
	    view = [:]
	  }
	   
	  if(!view.containsKey('id') || view.id==null || view.id=='') { 
        view['id'] = searchId
      }
      
      if(!view.containsKey('label') || view.label==null || view.label =='') {
        view['label'] = 'N/A'
      }
      
      if(!view.containsKey('title') || view.title==null || view.title == '') {
	    view['title'] = 'N/A'
	  }
	  
	  if(!view.containsKey('summary') || view.summary==null || view.summary == '') {
	    view['summary'] = "Missing ID # ${searchId}"
	  }
	  
	  if(!view.containsKey('content') || view.content==null || view.content == '') {
	    view['content'] = 'N/A'
	  }
	  
	  if(!view.containsKey('imageUrl') || view.imageUrl==null || view.imageUrl=='') {
	    view['imageUrl'] = null
	  }
	  
	  if(!view.containsKey('relations') || view.relations==null) {
	    view['relations'] = []
	  }
	  
	  return view
    }
    
    
    // the center of everything
    def findSLC() {
    
      // TODO - need to make this method user centric
      // base impl on user's role
      // use session check?
      def slcResources = resourceMappings['SLC']
      def slcView = [:]  
      slcView['id'] = 'slc'
      slcView['label'] = 'My SLC'
      slcView['title'] = 'Shared Learning Collaborative'
	  slcView['summary'] = ''
	  slcView['content'] = ''
	  slcView['imageUrl'] = null
	  
	  def slcData = call('get', slcResources.link )
	  slcData.links.each { println it.rel }
	  def slcLinks = slcData['links'].findAll { it.rel =~ /^get.+/ && resourceMappings.containsKey(it.rel - 'get') }
		 
	  slcView['relations'] = slcLinks.collect { 'All' + it.rel - 'get' }
	    
	  return slcView
    } 
        
    // provide a generic impl for all known entities
    // provide a blank impl for all non-implemented entities
    def methodMissing(String name, args) {

     // process any method call starting with find
     if(name =~ /^find.+/) {
     if(DEBUG) println "calling " + name + " with " + args
         def entityName = name - 'find'
           
         // an entity starting with and Underscore implies called internally
         def isInternal = false
         if(entityName.startsWith('_')) { // internal method
           entityName -= '_'
           isInternal = true
         }
         
         // an entity starting with 'All' implies a grouping
         def isAllGroup = false
         if(entityName =~ /^All.+/) {
           isAllGroup = true
           entityName -= 'All'
         }
         
         // need to know later if params exists
         def paramsPresent = false
         if(args.size()>0) {
           paramsPresent = true
         }
         
         def entityNameWithSpaces = spaceCamelCase(entityName)
         
         def allViews = []
         if(resourceMappings.containsKey(entityName)) {
           def mapping = resourceMappings[entityName]
           
           // substitute blanks for all args when none provided
           // may cause problems. depending on urls... wait and see...
           if(!paramsPresent) {
             args=[]
            if(isInternal || isAllGroup) {
              mapping.allLink.count('%s').times { args.add('') }
            }
            else {
	          mapping.link.count('%s').times { args.add('') }
	        }
	       }
	       
	       // retrieve data from slc
           def allData = call('get',String.format(isAllGroup?mapping.allLink:mapping.link,*args))
       
           // wrap single element in List
           // NOTE: assume no params = expect multiple/list
	       if(!(allData instanceof List)) {
	         allData = [allData]
	       }
	      
	       // process each data object
	       allData.each { data ->
	       
	         // no sense in processing what already errored out
	         if(entityName != 'SLC' && data.containsKey('error') || !data.containsKey('id')) {
	           println name + args + 'omitting data because of error with data: ' + data
	           return
	         }
	         
	         def view = [:]
	         
	         // extract the entity relations
	         def allLinks = null
			 if(!data.containsKey('links')) {
			   allLinks=[]
			 }
			 else {
			   allLinks=data['links'].findAll { it.rel =~ /^get.+/ && resourceMappings.containsKey(it.rel - 'get') }
		     }
		     
		     // determine the unique entity relation types
		     def relationTypes = allLinks.collect { it.rel -'get' }.toSet() 
		     
		     // create group if not internal call and is 'All' entity
		     if(!isInternal && isAllGroup) {
		       view['id'] = 'All' + entityName +  (paramsPresent?'_'+args.join('_'):'')           
			   view['label'] = entityNameWithSpaces
			   view['title'] = entityNameWithSpaces
			   view['summary'] = ''
			   view['content'] = "This is a grouping of all ${entityName}"
			   view['imageUrl'] = Eval.me('data',data,mapping.groupImageUrl)
		     }
		     else {
		     
	           view['id'] = entityName + '_' + ((!isInternal && paramsPresent)? args.join('_'):data.id)           
			   view['label'] = Eval.me('data',data,mapping.label)
			   view['title'] = Eval.me('data',data,mapping.title)
			   view['summary'] = Eval.me('data',data,mapping.summary)
			   
			   // use provided mapping if present
			   if(mapping.containsKey('content')) {
			     Eval.me('data',data,mapping.content)
			   } 
			   else { // otherwise, use just extract what is there
			     view['content'] =  extractContent(data) 
			   }
			   
			   view['imageUrl'] = Eval.me('data',data,mapping.imageUrl)
			 }  
			 
			
	        // infinite loop will occur from getting relations for relations...
		    // BE CAREFUL when using internal!
		    if(isInternal) {
		      if(DEBUG) println "a " + name
		      view['relations'] = []
		    }
			// multiple relations types should be grouped
			// only group when not in a group
			else if(!isAllGroup && relationTypes.size()>1) { 
			   if(DEBUG) println "b " + name
			   view['relations'] = relationTypes.collect { 'All' + it + '_' + Eval.me('data',data,mapping.relationParams)  }			 
			}
			else if(relationTypes.size()==1) {
		      def relType = relationTypes.iterator().next()
		      if(DEBUG) println "d - " + name + " : " + relType
    	      def relationData = null // "find_All${relType}"(*args)
		      if(relationData != null) {
			     if(! (relationData instanceof List)) {
			       relationData = [relationData]
			     }
			     // START TESTING
			     if(DEBUG) relationData.each {println "d - " + name + " : " + relType + " :: " + it.id } //
			     //view['relations']=[]
			     // END TESTING
			     view['relations'] = relationData.collect { it.id } 
		       }
		       else {
		         view['relations'] = []
		       }
	        }
		    else if(isAllGroup) {
		      if(DEBUG) println "c - " + name
		      def relationData = "find_All${entityName}"(*args)
		      if(relationData != null) {
			    if(! (relationData instanceof List)) {
			      relationData = [relationData]
			    }
			    // START TESTING
			    if(DEBUG) relationData.each { println  "c - " + name + " :: " + it.id }
			    //view['relations']=[]
			    // END TESTING
			    view['relations'] = relationData.collect { it.id } 
		      }
		      else {
		        view['relations'] = []
		      }
		    }
	       else {
	         if(DEBUG) println "e"
	         view['relations'] = []
	       }
       
		   if(DEBUG) println name + " with " + args + " - relations " + view.relations	
		   allViews.add(view)
	     }
	   }
	       
	   if(isInternal) {
	     return allViews
	   }
	   else if(allViews.size()>0) {
	     return allViews[0] 
	   }
       else { 
         def view = [:]
         
         view['id'] = entityName + '_' + args.join('_')
         // All groups need prefix
         // NOTE: should never fail to find these...
         if(isAllGroup) {
           println "Failed to find an all group for ${entityName}!"
           view['id'] = 'All' + view['id']
         }
         
		 view['label'] = entityNameWithSpaces
		 view['title'] = entityNameWithSpaces
		 view['summary'] = 'TODO'
		 view['content'] = 'Not implemented yet'
		 view['imageUrl'] = null
		 view['relations'] = []
		   
	    println "missing " + view['id'] 
	    return view
	   }	  
	 }    

     // there was no method impl found
     throw new MissingMethodException(name, delegate, args)
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
