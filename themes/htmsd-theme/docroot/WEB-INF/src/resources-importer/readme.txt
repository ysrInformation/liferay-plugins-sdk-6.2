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

<div class="row-fluid">
    
	<#list entries as entry>
		
		<#assign entry = entry />
		<#assign assetRenderer = entry.getAssetRenderer() />
		<#assign Uuid = assetRenderer.getUuid() >
		<#assign VideoURL = assetRenderer.getURLDownload(themeDisplay) >
		
    	
    	
    	<div class="span4" width="400px" height="400px">
    		<span rel=".${Uuid}"  vidURL="${VideoURL}" ed="${Uuid}" classpk="${entry.getClassPK()}"  classname="${entry.getClassName()}" 
    		class="vedioInfoId"  title="${entry.getTitle(locale)}">
    			<img alt="" src="${assetRenderer.getURLImagePreview(renderRequest)}" class="vedioThum" width="200px" height="200px" style="cursor:pointer"/>
    		</span>
            <div><strong>${entry.getTitle(locale)}</strong></div>
			${entry.getCreateDate()?string("EEEE, MMMM dd, yyyy")}
			<br/>
				Views : <strong>${entry.getViewCount()}</strong>
		  	 </div>
		
		<#if entry_index%3==2>
                    </div>
                    <div class="row-fluid">
         </#if>
    </#list>

</div>


<div id="videoBoxId"  style="display:none; ">
    	 <video width="450" height="200" controls>
             <source src='' type="video/mp4">
             <source src='' type="video/ogg">
             Your browser does not support the video tag.
        </video>
                        	 
</div>


<script>
$(document).ready(function(){
  
    $('.vedioInfoId').click(function(event){
      
        var vedioBox = document.getElementById('videoBoxId');
        var vidURL=$(this).attr('vidURL');
        var vidID=$(this).attr('ed');
        var title = $(this).attr('title');
        var classname=$(this).attr('classname');
        var classpk=$(this).attr('classpk');
        $("source").attr('src',vidURL);
         
		createDialog(vedioBox.innerHTML, title);
		incrementCount(classname, classpk);
      
    });
	
});

function createDialog(content, title) {
 
	AUI().ready(function(A) {
			AUI().use('aui-base','liferay-util-window', function(A) {
				Liferay.Util.Window.getWindow({
					title : title,
					dialog: {
						destroyOnHide: true,
						cache: false,
						resizable : false,
						draggable : false,
						modal: true,
						width:500,
						height:300,
						centered: true,
						cssClass: "videoModal",
						bodyContent: content
					}
				});
			});
		});
}

function incrementCount(classname, classpk) {
	  Liferay.Service(
      '/assetentry/increment-view-counter',
      {
        className: classname,
        classPK: classpk
      },
      function(obj) {
        //console.log(obj);
      }
    );
}

</script>