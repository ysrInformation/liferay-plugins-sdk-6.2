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
---------------------------------------------------------------------------------s