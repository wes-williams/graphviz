<!doctype html>
<html>
  <head>
    <title>HTML5 Canvas!</title>
    <link rel="stylesheet" type="text/css" href="css/graph.css" />
    <script type="text/javascript" src="js/remoteData.js"></script>
    <script type="text/javascript" src="js/graph.js"></script>
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
  </body>
</html>
<script type="text/javascript">
// make sure a refresh starts over
document.location.hash='#loadingDialog';

// control data
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
</script>
