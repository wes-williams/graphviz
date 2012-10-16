function findNodeById(aId) {
  if(dataIndex[aId]!=undefined) {
    return allData[dataIndex[aId]];
  }

  return null;
}

function getMaxNodesPerPage() {
 return 8;
}

function findXYOn(event, currentElement) {
  var totalOffsetX = 0;
  var totalOffsetY = 0;
  var graphCanvasY = 0;
  do{
      totalOffsetX += currentElement.offsetLeft;
      totalOffsetY += currentElement.offsetTop;
  }
  while(currentElement = currentElement.offsetParent)

  graphCanvasX = event.pageX - totalOffsetX;
  graphCanvasY = event.pageY - totalOffsetY;

  return {x:graphCanvasX, y:graphCanvasY}
}

function findOnGraphCanvas(event){
  return findXYOn(event, graphCanvas);
}

function findOnCrumbsCanvas(event){
  return findXYOn(event, crumbsCanvas);
}

function goToCrumb(e) {
  var crumb = findCrumbAtXY(findOnCrumbsCanvas(e))

  if(crumb==null) return;

  removeCrumbsUntil(crumb);
  loadData(crumb,0,0,0,crumb.currentPage)
}

function addCrumb(crumb,img) {
  if(crumbs.indexOf(crumb)!=-1) return;
  
  crumbs.push(crumb);
  crumb.crumbX = (crumbsCanvas.width-crumb.width)/2;
  crumb.crumbY =  10 + (crumbs.length==1?0:crumbs[crumbs.length-2].crumbY+ crumbs[crumbs.length-2].height);
  
  if(crumb.imageUrl==null) {
    drawTextAsImage(crumbsCanvas,crumbsContext,null,crumb.label,crumb.crumbX,crumb.crumbY,crumb.width,crumb.height);
  } else {
    drawImage(crumbsCanvas,crumbsContext,img,null,crumb.crumbX,crumb.crumbY,crumbs.length);
  }
}

function removeCrumbsUntil(crumb) {

  var borderWidth=2;
  var crumbIndex = crumbs.indexOf(crumb);
  for(var i=crumbs.length-1;i>crumbIndex;i--) {
    crumbsContext.clearRect(crumbs[i].crumbX-borderWidth,crumbs[i].crumbY-borderWidth,crumbsCanvas.width+(borderWidth*2),crumbsCanvas.height+(borderWidth*2));
    crumbs.pop();
  }
}

function defineLoadRelations(aData) {          

  aData.loadRelations = function() { 
    for(var i=0;i<this.relations.length;i++) {
      if(dataIndex[this.relations[i]]==undefined) {
       findData(this.relations[i]);
      }
    }
  }
}

function loadRelations(aData) {
  if(aData.loadRelations != null) {
    //alert('loading relations for ' + aData.id)
    aData.loadRelations();
    aData.loadRelations=null;
  }
}

function loadData(aData,startX,startY,relativePosition,pageNum,textLabelOverride, screenLoadedAfter) {

  if(relativePosition==0 && !aData.loadedRelations) {
    
    for(var i=0;i<aData.relations.length;i++) {
       if(dataIndex[aData.relations[i]]==undefined) {
           
          if(document.location.hash!='#loadingDialog') {
            document.location.hash='#loadingDialog';
          }

          setTimeout(function(){loadData(aData,startX,startY,relativePosition,pageNum,textLabelOverride,screenLoadedAfter);},500);
          return;
       }
    }

    aData.loadedRelations=true;
  }

  doLoadData(aData,startX,startY,relativePosition,pageNum,textLabelOverride);
  loadRelations(aData);

  if(screenLoadedAfter!=undefined && document.location.hash=='#loadingDialog') {
    document.location.hash=screenLoadedAfter;
  }

}

function doLoadData(aData,startX,startY,relativePosition,pageNum,textLabelOverride) {
  if(pageNum==undefined) { pageNum=1; }
  if(textLabelOverride==undefined) { textLabelOverride=false; }

  var img = document.createElement('IMG');
  
  img.onload = function() {
    
    if(relativePosition==0) {
      if(selectables.length !=0) {
        graphContext.clearRect(0,0,graphCanvas.width,graphCanvas.height);
      }
    }  
    
    if(aData.imageUrl==null || textLabelOverride) {
      if(textLabelOverride) {
        drawTextAsImage(graphCanvas,graphContext,null,aData.label,aData.x,aData.y,aData.width,aData.height);
      } 
      else {
        if(relativePosition==0) {
          drawTextAsImage(graphCanvas,graphContext,aData,aData.label);
        } 
        else {
          drawTextAsImage(graphCanvas,graphContext,aData,aData.label,startX,startY);
        }
      }

      aData.textLabelVisible=true;
      if(textLabelOverride){
        aData.textLabelOverride=true;
      } 
    } else {
      aData.textLabelVisible=false;
      aData.textLabelOverride=false;
      drawImage(graphCanvas,graphContext,img,aData,startX,startY,relativePosition); 
    }
    
    aData.currentPage=pageNum;

    if(relativePosition>=0) {
      aData.relativePosition = relativePosition;
    }

    // don't show relations if this is a relation being shown...
    if(relativePosition!=0) {
      if(aData.relations.length > 0) {   
        var relationX=aData.x;
        var relationY=aData.y;

        if(relationX > graphCanvas.width/2) {
          relationX += aData.width;
        }

        if(relationY > graphCanvas.height/2) {
          relationY += aData.height;
        }

        if(aData.relativePosition!=0) {
          drawTextInCircle(graphContext,aData.relations.length.toString(),relationX,relationY);
        }
      }

      return;
    }

    addCrumb(aData,img);

    selectables = [aData];

    var startPicX = aData.x;
    var startPicY = aData.y;
    var distBetweenNodes= 100; //aData.width;
    var percentTurn = .3;
    var percentTurnPlus = 1 + percentTurn;
    // currently limited to only 8
    for(var r=(aData.currentPage-1)*getMaxNodesPerPage();r<(aData.currentPage*getMaxNodesPerPage()) && r<aData.relations.length;r++) {
      
      var relationData = findNodeById(aData.relations[r]);

      if(relationData==null) {
        continue;
      }
   
			switch(r%getMaxNodesPerPage())
      {
         case 0:
           startPicY -= distBetweenNodes*(percentTurn+percentTurnPlus);
           break;
         case 1:
           startPicX += distBetweenNodes*percentTurnPlus;
           startPicY += distBetweenNodes*percentTurn;
           break;
         case 2:
           startPicX += distBetweenNodes*percentTurn;
           startPicY += distBetweenNodes*percentTurnPlus;
           break;
         case 3:
           startPicX -= distBetweenNodes*percentTurn;
           startPicY += distBetweenNodes*percentTurnPlus;
           break;
         case 4:
           startPicX -= distBetweenNodes*percentTurnPlus;
           startPicY += distBetweenNodes*percentTurn;
           break;
         case 5:
           startPicX -= distBetweenNodes*percentTurnPlus;
           startPicY -= distBetweenNodes*percentTurn;
           break;
         case 6:
           startPicX -= distBetweenNodes*percentTurn;
           startPicY -= distBetweenNodes*percentTurnPlus;
           break;
         case 7:
           startPicX += distBetweenNodes*percentTurn;
           startPicY -= distBetweenNodes*percentTurnPlus;
           break;
       }
  
       selectables.push(relationData);
       drawLine(aData.x+(aData.width/2),aData.y+(aData.height/2),startPicX+(aData.width/2),startPicY+(aData.height/2));

       loadData(relationData,startPicX,startPicY,r+1);  
    } 

    // redraw the image again if there have been lines drawn
    if(aData.relations.length>0) {
      if(aData.imageUrl==null) {
        drawTextAsImage(graphCanvas,graphContext,null,aData.label,aData.x,aData.y,aData.width,aData.height);
      } else {
        drawImage(graphCanvas,graphContext,img,aData,startX,startY,relativePosition);
      }
    }

    if(aData.relations.length>pageNum*getMaxNodesPerPage()) {
      showNextButton();
    }

    if(pageNum>1) {
     showPreviousButton();
    }
  } 
  img.src = (aData.imageUrl==null)?'images/nav/blank.jpg':aData.imageUrl;
}

function drawLine(startX,startY,endX,endY) {
  graphContext.beginPath();
  graphContext.strokeStyle = 'black';
  graphContext.lineWidth = 2;
  graphContext.moveTo(startX,startY);
  graphContext.lineTo(endX,endY);
  graphContext.stroke();
}

function showInformation(e) {
  var clickXY = findOnGraphCanvas(e);
  
  if(isClickOnNextButton(clickXY)) {
    crumbs[crumbs.length-1].currentPage++;
    loadData(crumbs[crumbs.length-1],0,0,0,crumbs[crumbs.length-1].currentPage);
  }
  else if(isClickOnPreviousButton(clickXY)) {
    crumbs[crumbs.length-1].currentPage--;
    loadData(crumbs[crumbs.length-1],0,0,0,crumbs[crumbs.length-1].currentPage);
  }
  else {
   showInformationForData(findSelectableAtXY(clickXY));
  }
}

function showLabel(e) {
  var aData = findSelectableAtXY(findOnGraphCanvas(e));
  if(aData!=null) {
    if(!aData.textLabelVisible) {
     showLabelForData(aData);
    } 
  }

  for(var i=0;i<selectables.length;i++) {
    if(selectables[i] != aData && selectables[i].textLabelOverride) {
      loadData(selectables[i],selectables[i].x,selectables[i].y,-1,selectables[i].currentPage);
    }
  }
}

function findSelectableAtXY(clickXY) {

  for(var i=0;i<selectables.length;i++) {
    if(clickXY.x >= selectables[i].x && 
      clickXY.x <= (selectables[i].x+selectables[i].width) &&
      clickXY.y >= selectables[i].y && 
      clickXY.y <= (selectables[i].y+selectables[i].height)) 
    {
      return selectables[i];
    }
  }

  return null;
}

function findCrumbAtXY(clickXY) {

  for(var i=0;i<crumbs.length;i++) {
    if(clickXY.x >= crumbs[i].crumbX && 
      clickXY.x <= (crumbs[i].crumbX+crumbs[i].width) &&
      clickXY.y >= crumbs[i].crumbY && 
      clickXY.y <= (crumbs[i].crumbY+crumbs[i].height)) 
    {
      return crumbs[i];
    }
  }

  return null;
}

function getSelectable(i) {
  return selectables[i];
}

function getNewSelectable(i,parentSelectableIndex) {
  if(parentSelectableIndex != null) {
    loadData(allData[parentSelectableIndex],0,0,0);
  }

  return allData[i];
}

function showLabelForData(aData) {
  if(aData==null || aData.imageUrl==null) return;

  loadData(aData,aData.x,aData.y,-1,aData.currentPage,true);
  loadRelations(aData);
}

function showInformationForData(aData,ignoreTextLabel) {
  if(aData==null) return;

  if(!aData.loadedRelations) {    
    for(var i=0;i<aData.relations.length;i++) {
       if(dataIndex[aData.relations[i]]==undefined) {
          //alert(aData.relations[i] + " relation is not loaded");

          if(document.location.hash!='#loadingDialog') {
            document.location.hash='#loadingDialog';
          }

          setTimeout(function(){showInformationForData(aData,ignoreTextLabel);},500);
          return;
       }
    }

    // load the relations of these relations
    for(var i=0;i<aData.relations.length;i++) {
        loadRelations(findNodeById(aData.relations[i]));
    }

    aData.loadedRelations=true;
  }

  doShowInformationForData(aData,ignoreTextLabel); 
}

function doShowInformationForData(aData,ignoreTextLabel) {
  if(aData==null) return;
  if(ignoreTextLabel==undefined) { ignoreTextLabel=false; };

  if(!ignoreTextLabel && aData.imageUrl!=null) {
    if(!aData.textLabelOverride) {
      showLabelForData(aData);
      return;
    }
    else {
      loadData(aData,aData.x,aData.y,-1,aData.currentPage,false);
    }
  }

  var isAtCenter = aData.id == selectables[0].id;

  document.getElementById('title').innerHTML = aData.title;
  document.getElementById('summary').innerHTML = aData.summary;
  document.getElementById('content').innerHTML = aData.content;
  document.getElementById('image').src = aData.imageUrl==null?'images/nav/unknown.jpg':aData.imageUrl;

  var relationContent = '';
  var startNav=aData.currentPage==undefined?0:((aData.currentPage-1)*getMaxNodesPerPage());
  var endNav = aData.currentPage==undefined?aData.relations.length:(aData.currentPage*getMaxNodesPerPage());
  for(var j=startNav;j<endNav &&j<aData.relations.length;j++) {
    
    if(isAtCenter) {
      relationContent += '<a href="javascript:showInformationForData(getSelectable('+selectables.indexOf(findNodeById(aData.relations[j]))+'),true);"><li>' + findNodeById(aData.relations[j]).label + '</li></a>';
    }
    else 
    {
      relationContent += '<a href="javascript:showInformationForData(getNewSelectable('+dataIndex[aData.relations[j]]+','+dataIndex[aData.id]+'),true);"><li>' + findNodeById(aData.relations[j]).label + '</li></a>';
    }
  }
  document.getElementById('relations').innerHTML = relationContent;

  window.location.hash = 'modalDialog';
}

function showData() {

  if(allData.length==0) {
    findData();
    setTimeout(function(){showData();},500);
    return;
  }

  doShowData(allData);  
}

function doShowData(data) {
  var aData = data[0];
  defineLoadRelations(aData);
  loadRelations(aData);
  loadData(aData,0,0,0,undefined,undefined,'#');
}


function drawImage(canvas,context,img,aData,startX,startY,relativePosition) {

    var canvasWidth = canvas.width;
    var canvasHeight = canvas.height;
    var imageWidth = img.width;
    var imageHeight = img.height;

    var forcedImageWidth=100;
    var scalingAdjustment = imageWidth>forcedImageWidth?forcedImageWidth/imageWidth:1+((forcedImageWidth-imageWidth)/forcedImageWidth); 

    if(scalingAdjustment!=1) { 
      imageWidth *= scalingAdjustment;
      imageHeight *= scalingAdjustment;
    }

    if(imageHeight>imageWidth) {
      scalingAdjustment = imageWidth/imageHeight; 

      if(scalingAdjustment!=1) { 
        imageWidth *= scalingAdjustment;
        imageHeight *= scalingAdjustment;
      }
    }

    if(canvas == graphCanvas && relativePosition > 0) {
      if(selectables[0].width != imageWidth) {
        startX -= (imageWidth-selectables[0].width)/2;
      }

      if(selectables[0].height != imageHeight) {
          startY -= (imageHeight-selectables[0].height)/2;
      }
    }

    var startPicX = relativePosition!=0?startX:((canvasWidth/2)-(imageWidth/2));
    var endPicX = startPicX + imageWidth;
    var startPicY = relativePosition!=0?startY:((canvasHeight/2)-(imageHeight/2));
    var endPicY = startPicY + imageHeight;
    var borderWidth = 2;

    if(aData!=null) {
		  aData.x=startPicX;
		  aData.y=startPicY;
		  aData.width=imageWidth;
		  aData.height=imageHeight;
		  aData.image = img;
    }

    // draw rectangle to border image
    context.save();
    clipRoundedRegion(context,startPicX-borderWidth,startPicY-borderWidth,endPicX+(borderWidth),endPicY+(borderWidth))
    context.fillStyle   = '#000000';
    context.fillRect(startPicX-borderWidth,startPicY-borderWidth,imageWidth+(borderWidth*2),imageHeight+(borderWidth*2));
    context.restore();

    context.save();
    clipRoundedRegion(context,startPicX,startPicY,endPicX,endPicY)
    context.drawImage(img,startPicX,startPicY,imageWidth,imageHeight);
    context.restore();

}

function clipRoundedRegion(context,startPicX,startPicY,endPicX,endPicY) {
    var cornerRadius = 20;

    context.beginPath();
    context.moveTo(startPicX+cornerRadius, startPicY);
    context.lineTo(endPicX-cornerRadius, startPicY); 
    context.quadraticCurveTo(endPicX,startPicY,endPicX,startPicY+cornerRadius);
    context.lineTo(endPicX,endPicY-cornerRadius);
		context.quadraticCurveTo(endPicX,endPicY,endPicX-cornerRadius,endPicY);
    context.lineTo(startPicX+cornerRadius,endPicY);
		context.quadraticCurveTo(startPicX,endPicY,startPicX,endPicY-cornerRadius);
    context.lineTo(startPicX,startPicY+cornerRadius);
    context.quadraticCurveTo(startPicX,startPicY, startPicX+cornerRadius, startPicY);
    context.clip();
}

function drawTextAsImage(canvas,context,aData,text,x,y,width,height) {

  if(width==undefined) { width=100; }
  if(height==undefined) { height=100; }
  if(x==undefined) { x=(canvas.width/2)-(width/2); }
  if(y==undefined) { y=(canvas.height/2)-(height/2); }

  var fontSize = 15;
  context.textAlign = "center";
  context.textBaseline = 'middle';
  context.fillStyle = 'yellow';
  context.font = 'bold '+fontSize+'px sans-serif';

 
  var centerX = x+(width/2);
  var centerY = y+(height/2);

  var textParts = text.split(' '); 

  if(textParts.length > 1) {
   centerY -= fontSize * (textParts.length/4);
  } 

   if(aData!=null) {
	   aData.x=x;
		 aData.y=y;
		 aData.width=width;
		 aData.height=height;
   }

   context.save();
   clipRoundedRegion(context,x,y,x+width,y+height);
   context.fillStyle   = '#000000';
   context.fillRect(x,y,width,height);
   context.restore();

  for(var i=0;i<textParts.length;i++) { 
    context.fillText(textParts[i], centerX, centerY+(i*fontSize));
  }
}

function drawTextInCircle(context,text,x,y) {
  context.beginPath();
  context.arc(x, y, 15,0,2*Math.PI);
  context.fillStyle = 'yellow';
  context.fill();

  context.beginPath();
  context.fillStyle = 'red';
  context.font = 'italic bold 15px sans-serif';
  context.textAlign='center';
  context.textBaseline = 'middle';
  context.fillText(text,x,y);
}

function isClickOnNextButton(clickXY) {
  var nextButtonX=graphCanvas.width-55;
  var nextButtonY=graphCanvas.height-55;
  var nextButtonWidth=50;

  if(crumbs[crumbs.length-1].relations.length<=crumbs[crumbs.length-1].currentPage*8) {
    return false;
  }

  if(nextButtonX <= clickXY.x && nextButtonY <= clickXY.y) {
    
    if(nextButtonX+nextButtonWidth>clickXY.x && nextButtonY+nextButtonWidth>clickXY.y) {
      return true;
    }
  }

  return false;
}

function showNextButton() {
  var nextButtonX=graphCanvas.width-30;
  var nextButtonY=graphCanvas.height-30;

  graphContext.beginPath();
  graphContext.arc(nextButtonX, nextButtonY, 25,0,2*Math.PI);
  graphContext.strokeStyle = 'black';
  graphContext.stroke();

  graphContext.beginPath();
  graphContext.fillStyle = 'black';
  graphContext.font = 'bold 25px courier';
  graphContext.textAlign='center';
  graphContext.textBaseline = 'middle';
  graphContext.fillText('>>',nextButtonX,nextButtonY);
}

function isClickOnPreviousButton(clickXY) {
  var prevButtonX=5;
  var prevButtonY=graphCanvas.height-55;
  var prevButtonWidth=50;

  if(crumbs[crumbs.length-1].currentPage==1) {
    return false;   
  }

  if(prevButtonX <= clickXY.x && prevButtonY <= clickXY.y) {
    
    if(prevButtonX+prevButtonWidth>clickXY.x && prevButtonY+prevButtonWidth>clickXY.y) {
      return true;
    }
  }

  return false;
}

function showPreviousButton() {
  var prevButtonX=30;
  var prevButtonY=graphCanvas.height-30;

  graphContext.beginPath();
  graphContext.arc(prevButtonX, prevButtonY, 25,0,2*Math.PI);
  graphContext.strokeStyle = 'black';
  graphContext.stroke();

  graphContext.beginPath();
  graphContext.fillStyle = 'black';
  graphContext.font = 'italic bold 25px courier';
  graphContext.textAlign='center';
  graphContext.textBaseline = 'middle';
  graphContext.fillText('<<',prevButtonX,prevButtonY);
}
