{
    
      'SLC' :  {
        'link' : '/home'          
      },
      
    'Schools' :  {
        'allLink' : '/schools/%s?limit=5',
        'link' : '/schools/%s',
        'label' : 'data.educationOrgIdentificationCode?.getAt(0)?.ID',
        'title' : 'data.educationOrgIdentificationCode?.getAt(0)?.ID',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.id'          
      },
      
    'Courses' : {
        'allLink' : '/courses?schoolId=%s&limit=5',
        'link' : '/courses/%s',
        'label' : 'data.courseTitle',
        'title' : 'data.courseTitle',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.id'           
      },
      
    'Students' : {
        'allLink' : '/schools/%s/studentSchoolAssociations/students?limit=10',
        'link' : '/students/%s',
        'label' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
        'title' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.id'     
      },
          
    'Teachers' : {
        'allLink' : '/schools/%s/teacherSchoolAssociations/teachers?limit=5',
        'link' : '/teachers/%s',
        'label' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
        'title' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.id'
      },
      
     '_TeacherSectionAssociations' : {
        'allLink' : '/teacherSectionAssociations?limit=5',
        'link' : '/teacherSectionAssociations/%s/sections?limit=5',
        'label' : 'data.uniqueSectionCode',
        'title' : 'data.uniqueSectionCode',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.teacherId'
      },
      
     'Sections' : {
        'allLink' : '/sections?limit=5',
        'link' : '/sections/%s?limit=5',
        'label' : 'data.uniqueSectionCode',
        'title' : 'data.uniqueSectionCode',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.id'
      }, 

     'DisciplineActions' : {
        'allLink' : '/disciplineActions?limit=5',
        'link' : '/disciplineActions/%s?limit=5',
        'label' : 'data.disciplineActionIdentifier',
        'title' : 'data.disciplineActionIdentifier',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.id'
      }, 

     'DisciplineIncidents' : {
        'allLink' : '/disciplineIncidents?limit=5',
        'link' : '/disciplineIncidents/%s?limit=5',
        'label' : 'data.incidentIdentifier',
        'title' : 'data.incidentIdentifier',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.id'
      } 
  }
   
