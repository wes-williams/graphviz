package graphviz.slc

class StickyNoteService {

	final String STICKY_NOTE_PREFIX = "STICKYNOTE_"
	final String STICKY_NOTE_GROUP_PREFIX = "${STICKY_NOTE_PREFIX}ALL_"
	final String dummyTestId = "Schools_2012vq-abc8995f-1f19-11e2-ba40-0247754674f3"
	
    def lookupRelatedStickyNotes(searchId) {
     
		println "Lookup Sticky Note for : ${searchId}"
		
		def stickyNoteGroupId = "${STICKY_NOTE_GROUP_PREFIX}${searchId}"
		
		// quick dummy test
		if(searchId==dummyTestId) {
		  return [stickyNoteGroupId]
		}
		
		return []
    }
	
	def lookupStickyNote(searchId) {
		
		println "Processing Sticky Note : ${searchId}"
		
		// quick dummy test
		if(searchId=="${STICKY_NOTE_GROUP_PREFIX}${dummyTestId}") {
			def view = [:]
			view['id'] = searchId
		    view['label'] = 'Daily Message'
			view['title'] =  'Daily Message'
			view['summary'] = "Message last updated on ${new Date()}"
			view['content'] = 'Remember the food drive is today. Leave your donations at the front.<br /><br /> '
			view['content'] += 'Lunch: Meat Loaf with mashed potatoes and vegatables'
			view['imageUrl'] = null
 		    view['relations'] = ["${STICKY_NOTE_PREFIX}${dummyTestId}_1","${STICKY_NOTE_PREFIX}${dummyTestId}_2"]
			return view
	     }
	
	     if(searchId=="${STICKY_NOTE_PREFIX}${dummyTestId}_1") {
			def view = [:]
			view['id'] = searchId
			view['label'] = 'Volunteers Needed'
			view['title'] =  'Volunteers Needed'
			view['summary'] = "Message last updated on ${new Date()}"
			view['content'] = 'The drama department is putting on a play this weekend.<br /><br />Volunteers are needed for concessions!'
			view['imageUrl'] = null
			view['relations'] = []
			return view
		}
		
		 if(searchId=="${STICKY_NOTE_PREFIX}${dummyTestId}_2") {
			 def view = [:]
			 view['id'] = searchId
			 view['label'] = 'Best School!'
			 view['title'] =  'Best School!'
			 view['summary'] = 'I love my school!'
			 view['content'] = 'My administrators and team are awesome!<br />Thank you guys!<br />- Mr Smith'
			 view['imageUrl'] = null
			 view['relations'] = []
			 return view
		 }
		 
		return null
	}
}
