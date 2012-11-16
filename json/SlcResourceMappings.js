{
    
    'Schools' :  {
        'link' : '/schools/%s',
        'label' : 'data.educationOrgIdentificationCode?.getAt(0)?.ID',
        'title' : 'data.educationOrgIdentificationCode?.getAt(0)?.ID',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.id'          
      },
      
    'Courses' : {
        'link' : '/courses?schoolId=%s',
        'label' : 'data.courseTitle',
        'title' : 'data.courseTitle',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.id'           
      },
      
    'Students' : {
        'link' : '/schools/%s/studentSchoolAssociations/students',
        'label' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
        'title' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.id'     
      },
          
    'Teachers' : {
        'link' : '/schools/%s/teacherSchoolAssociations/teachers',
        'label' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
        'title' : '"${data.name?.firstName} ${data.name?.lastSurname}"',
	    'summary' : '"SLC Identity # ${data.id}"',
	    'groupImageUrl' : 'null',
	    'imageUrl' : 'null',
	    'relationParams' : 'data.id'
      }   
  }
   