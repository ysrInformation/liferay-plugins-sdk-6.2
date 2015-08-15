<%@page import="java.text.DecimalFormat"%>
<%@include file="/html/dashboard/init.jsp" %>
<portlet:resourceURL  var="getTagsURL"/>

<%
	boolean isAdmin = permissionChecker.isOmniadmin();
	long itemId = ParamUtil.getLong(renderRequest, "itemId");
	ShoppingItem item = ShoppingItemLocalServiceUtil.fetchShoppingItem(itemId);
	DLFileEntry documentFileEntry = null;
	DecimalFormat decimalFormat = new DecimalFormat("#.00");
	if(Validator.isNotNull(item)) {
		List<Category> categories = CategoryLocalServiceUtil.getCategories(-1, -1);
		String backURL = ParamUtil.getString(renderRequest, "backURL");
		PortletURL updateItemURL = renderResponse.createActionURL();
		updateItemURL.setParameter(ActionRequest.ACTION_NAME, "addItem");
		updateItemURL.setParameter(HConstants.ITEM_ID, String.valueOf(itemId));
		updateItemURL.setParameter("userId", String.valueOf(item.getUserId()));
		updateItemURL.setParameter("backURL", backURL);
		updateItemURL.setParameter("tab1", ParamUtil.getString(renderRequest, "tab1"));
		List<Tag> tagsList = TagLocalServiceUtil.getTagByItemId(itemId);
		String tagName = StringPool.BLANK;
		long tagId = 0l;
		if(tagsList.size()>=1) {
			tagName = tagsList.get(0).getName();
			tagId = tagsList.get(0).getTagId();
		}
		List<Category> itemCategory = CategoryLocalServiceUtil.getCategoryByItemId(itemId);
		long categoryId = 0l;
		if(itemCategory.size()>=1) {
			categoryId = itemCategory.get(0).getCategoryId();
		}
		int status = item.getStatus();
		String itemStatus = StringPool.BLANK;
		if(status == HConstants.APPROVE) {
			itemStatus = "item-approve";
		}else if(status == HConstants.REJECT){
			itemStatus = "item-reject";
		}else{
			itemStatus = "item-new";
		}
		%>
	
		<liferay-ui:header title='<%=isAdmin ? "update-item" : "view-item" %>' backLabel="go-back" backURL='<%=backURL%>'/>
		<liferay-ui:header title='<%=itemStatus%>'/>
		<aui:fieldset>
			<aui:form action="<%=updateItemURL %>" enctype="multipart/form-data" method="POST" name="fm">
				<aui:input name="<%=HConstants.NAME%>" required="true" value="<%= item.getName()%>" />
				<aui:select name="<%=HConstants.CATEGORY_ID %>"   required="true" showEmptyOption="true">
					<%
						for(Category category : categories) {
							boolean showcategory = category.getCategoryId()== categoryId ? true : false;
							%>
								<aui:option label="<%=category.getName() %>"  value="<%=category.getCategoryId() %>" selected="<%=showcategory %>" />
							<%
						}
					%>   
					
				</aui:select>
				<aui:input name="<%=HConstants.DESCRIPTION %>" type="textarea" required="true" value="<%=item.getDescription()%>" />
				<aui:layout>
					<%
						String imagesList [] =  StringUtil.split(item.getImageIds(), StringPool.COMMA) ;
						int i = 1;
						for(String imageEntryId : imagesList) {
							try {
								documentFileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(Long.valueOf(imageEntryId));	
							}catch(Exception e) {
								
							}
							if(Validator.isNull(documentFileEntry)) continue;
							String readURL = "readURL(this,"+i+");";
							String deleteURL = "deleteUpload("+i+");";
							String image = HConstants.IMAGE+i;
							String imageId = HConstants.IMAGE_ID+i;
							String image_upload_preview = HConstants.IMAGE_UPLOAD_PREVIEW+i;
							String image_del = "image_del"+i;
							String deleteImage = HConstants.DELETE_IMAGE+i;
							%>
							<aui:column columnWidth="60" >
								<aui:input name='<%=image%>'  type="file" label="" onChange="<%=readURL %>">
								</aui:input>
								<aui:input name="<%=imageId %>" type="hidden"  value="<%=imageEntryId%>"/>
								<aui:input name="<%=deleteImage %>"  type="hidden"  value="false"/>
							</aui:column>
							
							<aui:column columnWidth="25" >
								<img id="<%=image_upload_preview %>"  src="<%= "/documents/" + documentFileEntry.getGroupId() + "/" + documentFileEntry.getFolderId() + "/" + documentFileEntry.getTitle()+"/"+documentFileEntry.getUuid()%>"  width="50px" height="50px"/>
							</aui:column>
							<c:if test="<%=isAdmin %>">
								<aui:column columnWidth="15">
									<aui:a href="#" id="<%=image_del %>" onclick="<%=deleteURL%>">Delete</aui:a>
								</aui:column>
							</c:if>
							<%
								i++;
						}
					%>
				</aui:layout>
				
				<aui:input name="<%=HConstants.PRICE %>" required="true" value="<%=decimalFormat.format(item.getSellerPrice()) %>">
					<aui:validator name="number" />
				</aui:input>
				<c:if test="<%=isAdmin%>">
					<aui:input name="<%=HConstants.TOTAL_PRICE %>" required="true" value="<%=decimalFormat.format(item.getTotalPrice()) %>">
						<aui:validator name="number" />
					</aui:input>
				</c:if>
				<aui:input name="<%=HConstants.TAG %>"  value="<%=tagName %>"/>
				<aui:input type="hidden" name="<%=HConstants.TAG_ID %>" value="<%=tagId%>"/>
				
				<c:if test="<%=isAdmin %>">
					<aui:select name="<%=HConstants.status %>" showEmptyOption="true" required="true">
						<aui:option value="<%=HConstants.APPROVE%>" label="approve" />
						<aui:option value="<%=HConstants.REJECT %>" label="reject" />
					</aui:select>
				
					<aui:button type="submit" value="update" onClick="return confirmSubmit();"/>
					<aui:button type="button" href="<%=backURL %>" value="cancel" />
				</c:if>
			</aui:form>
		</aui:fieldset>

<script>
	$(document).ready(function(){
		if(!<%=isAdmin%>) {
			$('#<portlet:namespace/>fm input,input[type=textarea],select ').attr("disabled", true);	
		}
		
	});
	function readURL(input,id) {
	    if (input.files && input.files[0]) {
	        var reader = new FileReader();
	        $('#<portlet:namespace/>deleteImage'+id).val('false');
	        reader.onload = function (e) {
	            $('#image_upload_preview'+id).attr('src', e.target.result);
	            $('#image_upload_preview'+id).show();
	        }
	        reader.readAsDataURL(input.files[0]);
	    }
	}
	
	function deleteUpload(id) {
		var control = $('#<portlet:namespace/>image'+id);
	    control.replaceWith( control = control.clone( true ) );
	    $('#image_upload_preview'+id).attr("src","");
	    $('#image_upload_preview'+id).hide();
	    $('#<portlet:namespace/>deleteImage'+id).val('true');
	}
	
	function confirmSubmit(){
		return confirm("Are you sure you want to update ?");
	}
	
	AUI().use('autocomplete-list','aui-base','aui-io-request','autocomplete-filters','autocomplete-highlighters',function (A) {
		var tagAuto
		A.io.request('<%=getTagsURL%>',{
			dataType: 'json',
			method: 'GET',
			on: {
				success: function() {	
					tags=this.get('responseData');
					//console.log(tags);
					tagAuto = new A.AutoCompleteList({
								allowBrowserAutocomplete: 'false',
								activateFirstItem: 'true',
								inputNode: '#<portlet:namespace /><%=HConstants.TAG %>',
								resultTextLocator: 'tagName',
								render: 'true',
								resultHighlighter: 'charMatch',
								resultFilters:['phraseMatch'],
								source:this.get('responseData'),
							 })
					
					tagAuto.on('select',function(e)
						     {
						    var selected_key = e.result.raw.tagId;
						  	//console.log(selected_key);
						    A.one("#<portlet:namespace /><%=HConstants.TAG_ID%>").set("value",selected_key);
						     }); 
				}
			}
		});
	});
</script>
		<%
	}else {
		%>
		<h2>Item Not Exist</h2>
		<%
	}
%>
