

function findData(aId) {
  if(dataIndex[aId] == null) {
    var params = aId==null?"?callback=loadAsync":aId+"?callback=loadAsync";
    var tag = document.createElement("script");
    tag.src = "https://127.0.0.1:8443/graphviz/"+dataSource+"/api/"+params;
    document.getElementsByTagName("head")[0].appendChild(tag);
  }
}

function loadAsync(data)
{
   if(dataIndex[data.id] == null) {
       allData.push(data);
       dataIndex[data.id] = allData.indexOf(data);
       defineLoadRelations(data);
   } 
}

