<!doctype html>
<html>
  <head>
    <title>EduViz!</title>
    <link rel="stylesheet" type="text/css" href="css/graph.css" />
    <script type="text/javascript" src="js/remoteData.js"></script>
    <script type="text/javascript" src="js/graph.js"></script>
    <script type="text/javascript" src="js/stickyNote.js"></script>
  </head>
  <body>
    <div align="center">
      <span><canvas id="crumbs" width="120" height="450" class="crumbsCanvas" /></span>
      <span><canvas id="graph" width="580" height="450" class="graphCanvas" /></span>
    </div>
    <div id="modalDialog" class="divModalDialog">
       <div>
          <nav>
            <img id="image" src="" />
            <ul id="relations">
                <a href="#"><li>relation</li></a>
            </ul>
          </nav>
          <span class="closeDialog"><a href="#">&nbsp;X&nbsp;</a></span>
          <article>
            <h1 id="title">title</h1>            
            <summary id="summary">summary</summary>
            <section id="content">content</section>
          </article>
       </div>
    </div>
   <div id="loadingDialog" class="divModalDialog">
       <div style="position: relative">
          <h1 style="position:absolute;top:0;bottom:0;left:0;right:0;width:50%;height:40%;margin:auto;">Please Wait...<br /><br /><img src="images/nav/ajax-loader.gif" /></h1>
       </div>
   </div>
   <div id="dataSourceDialog" class="divModalDialog">
       <div style="position: relative">
         <nav>
          <h1 style="position:absolute;top:0;bottom:0;left:0;right:0;width:70%;height:50%;margin:auto;">Select a data source for EduViz:<br /><br />
           <ul>
	    <oauth:connect provider="slc"><li><h2>Shared Learning Collaborative</h2></li></oauth:connect>
	    <!--<a href="./resume"><li><h2>Wes Williams' Resume</h2></li></a>-->
	   </ul>
	  </h1>
	 </nav>
       </div>
   </div>
    <div id="stickyNoteDialog" class="divModalDialog">
       <div>
         <span class="closeDialog"><a href="#">&nbsp;X&nbsp;</a></span>
	 <br />
	 <h1>Leave a Message</h1>
	 <div  style="height:60%" align="center">
	  <form id="stickyNoteForm" name="stickyNoteForm">
	    <table>
	      <tr><td>Title:</td><td align="left"><input type="text" id="noteTitle" name="noteTitle" maxlength="20" size="20" /></td></tr>
              <tr><td>Summary:</td><td align="left"><input type="text" id="noteSummary" name="noteSummary" maxlength="50" size="50" /></td></tr>
	      <tr><td valign="top">Message:</td><td><textarea id="noteMessage" name="noteMessage" cols="60" rows="10"></textarea></td></tr>
	      <tr>
	        <td><input type="hidden" id="noteId" name="noteId" value="" /></td>
	        <td align="right"><input type="button" id="noteSubmit" name="noteSubmit" value="Submit" onClick="submitStickyNote()" /></td>
	      </tr>
	     </table>
	  </form>
	 </div>
       </div>
   </div>
   <g:if test="${!'resume'.equals(params.ds)}">
    <div align="center"><a style="color:white;text-decoration:none" href="./resume">Developed by Wes Williams</a></div>
   </g:if>
   <g:else>
    <div align="center"><a style="color:white;text-decoration:none" href="#dataSourceDialog">Choose a Data Source</a></div>
   </g:else>
  </body>
</html>
<script type="text/javascript">
// make sure a refresh starts over
document.location.hash='#loadingDialog';

<g:if test="${params.ds!=null}">
// control data
var serverUrl = '${grailsApplication.config.grails.serverURL}';
var dataSource = '${params.ds}';
var crumbs = [];
var selectables = [];
var dataIndex = new Object();
var allData = [];

// canvas objects
var graphCanvas = document.getElementById('graph');
var graphContext = graphCanvas.getContext('2d');
var crumbsCanvas = document.getElementById('crumbs');
var crumbsContext = crumbsCanvas.getContext('2d');

//canvas events
graphCanvas.addEventListener("click", showInformation, false);
graphCanvas.addEventListener("mousemove", showLabel, false);
crumbsCanvas.addEventListener("click", goToCrumb, false);

//window events
if(screen.availHeight<graphCanvas.height) { // || screen.availWidth<(graphCanvas.width+crumbsCanvas.width)) {
  var adjustY=(graphCanvas.height-screen.availHeight)/8;
  var adjustX=0; //((graphCanvas.width+crumbsCanvas.width)-screen.availWidth)/8;
  var adjustFunction = function() {setTimeout(function(){window.scrollTo(adjustX, adjustY);},10);}
  window.addEventListener("hashchange", adjustFunction);
  window.addEventListener("orientationchange", adjustFunction);
  adjustFunction();
}

// get started
showData();
</g:if>
<g:else>
document.location.hash='#dataSourceDialog';
</g:else>
</script>
