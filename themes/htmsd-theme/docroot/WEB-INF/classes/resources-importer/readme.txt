ADT Gallery Image
<style>
.crop img {
 height: 300px;
 width: 100%;
}
</style>
<div class="row-fluid">
    <#list entries as entry>

    	<#assign assetRenderer = entry.getAssetRenderer() />
    	<#assign Uuid = assetRenderer.getUuid() >
    	
    	<#assign imageThumURL = assetRenderer.getThumbnailPath(renderRequest) >
    	<#assign imageURL = assetRenderer.getURLDownload(themeDisplay) >
       
        <div class="span4" id="myGallery" >
            <div class="crop">
              <a href="${imageURL}" class="imgDiv" title="${entry.getTitle(locale)}">
                <img class="thumbnail" src="${imageThumURL}"/>
              </a>
            </div>  
        </div>
      
        <#if entry_index%3==2>
                    </div>
                    <div class="row-fluid">
        </#if>
    </#list>
</div>

<script>
AUI().use(
  'aui-image-viewer-gallery',
  function(A) {
    new A.ImageGallery(
      {
        caption: 'HTMSD Gallery',
        delay: 2000,
        links: '#myGallery a',
        pagination: {
          total: 5
        },
        cssClass : 'dialogClass'
      }
    ).render();
  }
);
</script>
---------------------------------------------------------------------------------
ADT Video Gallery 

<head>
<style>
#lightGallery.row {
    width:100%;
   
}
#lightGallery.row li {
 list-style-type:none;
    width:33%;
    float:left;
}

#lightGallery img{
margin-top: 90px;
}
</style>
</head>

<div class="row-fluid">
	<ul id="lightGallery" class="row" style="margin-left:auto;"> 
        <#if entries?has_content>
        	<#list entries as entry>
               
               <#assign videoURL = entry.getDescriptionCurrentValue()>
                <#assign index = videoURL?last_index_of("v=")+2>
                <#if index == 1>
                     <#assign index = videoURL?last_index_of("/")+1>
                </#if>
              <#assign videoThumnail = "http://img.youtube.com/vi/"+videoURL?substring(index)+"/default.jpg" >
               
               
                    <li data-src = "${videoURL}">
                        <a href="#">
                            
                             <img width="300px" height="300px" src="${videoThumnail}"></img>
                        </a>
                        <div><strong>${entry.getTitle(locale)}</strong></div>
            		 </li>
    		 
            </#list>
        </#if>		      						
	</ul>	  
</div>	


<script>
$(document).ready(function() {
    $("#lightGallery").lightGallery({
    	mode : 'fade',
    	thumbnail : false
    });
});
</script>