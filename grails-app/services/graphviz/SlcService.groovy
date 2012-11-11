package graphviz

import org.springframework.web.context.request.RequestContextHolder
import javax.servlet.http.HttpSession
import uk.co.desirableobjects.oauth.scribe.OauthService
import org.scribe.model.Token
import grails.converters.JSON
import graphviz.oauth.OAuth20ServiceBearerAdapter

class SlcService {

    final String API_BASE_URL = 'https://api.sandbox.slcedu.org/api/rest/v1'

    OauthService oauthService
    boolean bearerEnabled=false
   
    def call(verb,url) {


      HttpSession session = RequestContextHolder.currentRequestAttributes().getSession()
      Token accessToken = session[oauthService.findSessionKeyForAccessToken('slc')]    

      if(accessToken==null) {
        println "Lost Session to SLC"
        return [:]
      }

      // start hack to enable bearer header
      if(!bearerEnabled) {
        oauthService.services['slc'].service = new OAuth20ServiceBearerAdapter(oauthService.services['slc'].service)
	bearerEnabled=true
      }
      // end hack to enable bearer header

      def response = oauthService."${verb}SlcResource"(accessToken,url)

      JSON.parse(response.body)
    }

    def findSchools() {

      def schoolUrl = "${API_BASE_URL}/schools/"
      def schoolData = call('get',schoolUrl)

      def schoolList = []

      schoolData.each { school ->
        def schoolView = [:]

	def selfLink = school.links.findAll { it.rel == 'self' }
	def getLinks = school.links.findAll { it.rel =~ /^get.+/ }

        schoolView['id'] = 'school_' + (selfLink[0].href - schoolUrl.toString())
        schoolView['label'] = school.educationOrgIdentificationCode[0].ID
        schoolView['title'] = school.educationOrgIdentificationCode[0].ID
	schoolView['summary'] = ''
	schoolView['content'] = ''
	schoolView['imageUrl'] = null
	schoolView['relations'] = getLinks.collect { schoolView.id + '_' + (it.rel - 'get') }

	schoolList.add(schoolView)
      }

      return schoolList
    }
} 
