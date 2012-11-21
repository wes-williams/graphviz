{
    
    'SLC' :  {
       'link' : '/home'          
      },
      
    'Schools' :  {
        'allLink' : '/schools?limit=5',
        'link' : '/schools/%s',
        'label' : 'data.educationOrgIdentificationCode?.getAt(0)?.ID',
        'title' : 'data.educationOrgIdentificationCode?.getAt(0)?.ID',
        'summary' : '"SLC Identity # ${data.id}"',
        'groupImageUrl' : 'null',
        'imageUrl' : 'null',
        'relationParams' : 'data.id'          
      },
      
    'Courses' : {
        'allLink' : '/courses?limit=5',
        'allLinkBySchools' : '/courses?schoolId=%s&limit=5',
        'allLinkBySessions' : '/sessions/%s/courseOfferings/courses?schoolId=%s&limit=5',
	'allLinkByStudents' : '/students/%s/courseTranscripts/courses?limit=5',
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
        'allLinkBySchools' : '/schools/%s/studentSchoolAssociations/students?limit=10',
        'allLinkByCourses' : '/courses/%s/courseTranscripts/students?limit=10',
        'allLinkByCourseTrascripts' : '/courseTranscripts/%s/students?limit=10',
        'allLinkByDisciplineIncidents' : '/disciplineIncidents/%s/studentDisciplineIncidentAssociations/students?limit=10',
	'allLinkBySections' : '/sections/%s/studentSectionAssociations/students?limit=10',
        'allLinkByPrograms' : '/programs/%s/studentProgramAssociations/students?limit=10',
        'link' : '/students/%s',
        'label' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
        'title' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
        'summary' : '"SLC Identity # ${data.id}"',
        'groupImageUrl' : 'null',
        'imageUrl' : 'null',
        'relationParams' : 'data.id'     
      },
          
     'Teachers' : {
        'allLink' : '/schools?limit=5',
        'allLinkBySchools' : '/schools/%s/teacherSchoolAssociations/teachers?limit=5',
	'allLinkBySections' : '/sections/%s/teacherSectionAssociations/teachers?limit=5',
        'link' : '/teachers/%s',
        'label' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
        'title' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
        'summary' : '"SLC Identity # ${data.id}"',
        'groupImageUrl' : 'null',
        'imageUrl' : 'null',
        'relationParams' : 'data.id'
      },
      
     'Sections' : {
        'allLink' : '/sections?limit=5',
        'allLinkBySchools' : '/schools/%s/sections?limit=5',
	'allLinkByStudents' : '/students/%s/studentSectionAssociations/sections?limit=5',
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
        'allLinkByStudents' : '/disciplineActions?studentId=%s&limit=5',
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
        'allLinkByStudents' : '/students/%s/studentDisciplineIncidentAssociations/disciplineIncidents?limit=5',
        'link' : '/disciplineIncidents/%s?limit=5',
        'label' : 'data.incidentIdentifier',
        'title' : 'data.incidentIdentifier',
        'summary' : '"SLC Identity # ${data.id}"',
        'groupImageUrl' : 'null',
        'imageUrl' : 'null',
        'relationParams' : 'data.id'
      } 
  }
