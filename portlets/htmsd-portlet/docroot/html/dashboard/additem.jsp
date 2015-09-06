<%@include file="/html/dashboard/init.jsp" %>

<portlet:actionURL name="addItem" var="addItemURL" >
	<portlet:param name="tab1" value='<%=ParamUtil.getString(renderRequest, "tab1") %>'/>
</portlet:actionURL>
<portlet:resourceURL  var="getTagsURL"/>

<%
	List<Category> categories = CategoryLocalServiceUtil.getCategories(-1, -1);
%>

<liferay-ui:header title="add-item" backLabel="go-back" backURL='<%=ParamUtil.getString(renderRequest, "backURL") %>'/>

<aui:fieldset>
	<aui:form action="<%=addItemURL %>" enctype="multipart/form-data" method="POST" name="addItemForm">
		<aui:input name="<%=HConstants.NAME%>" required="true" />
		<aui:input name="<%=HConstants.PRODUCT_CODE %>" />
		<aui:select name="<%=HConstants.CATEGORY_ID %>"   required="true" showEmptyOption="true">
			<c:forEach items="<%=categories %>" var="category">   
				<aui:option label="${category.name}"  value="${category.categoryId}"/>
			</c:forEach>
		</aui:select>
		<liferay-ui:input-editor />
		<aui:input name="<%=HConstants.DESCRIPTION %>" value=""  type="hidden" />
		<aui:layout>
			<c:forEach var="i" begin="1" end="<%=HConstants.IMAGES_UPLOAD_LIMIT %>">
				<aui:column columnWidth="60" >
					<aui:input name='image${i}'  type="file" label="" onChange="readURL(this,${i});">
						<aui:validator name="acceptFiles" errorMessage="please-upload-the-jpg,png,gif">'jpg,png,gif'</aui:validator>
						<c:if test="${i==1}">
							<aui:validator name="required" errorMessage="please-upload-atleast-one-image" />
						</c:if>	
					</aui:input>
				</aui:column>
				
				<aui:column columnWidth="25">
					<img id="image_upload_preview${i}" src=""  height="50px" width="50px" />
				</aui:column>
				
				<aui:column columnWidth="15">
					<aui:a href="#" onclick="deleteUpload(${i});">Delete</aui:a>
				</aui:column>
			</c:forEach>
		</aui:layout>
		
		<aui:input name="<%=HConstants.VEDIO_URL %>" >
			<aui:validator name="url" />
		</aui:input>
		
		<aui:input name="<%=HConstants.PRICE %>" required="true">
			<aui:validator name="number" />
		</aui:input>
		<aui:layout>
			<aui:column columnWidth="25">
				<aui:input name="<%=HConstants.QUANTITY %>">
					<aui:validator name="number" />
				</aui:input>
			</aui:column>
			
			<aui:column columnWidth="25">
				<aui:input name="<%=HConstants.UNILIMITED_QUANTITY %>" type="checkbox" label="unlimited-quantity" />
			</aui:column>
		</aui:layout>
		
		<aui:input name="<%=HConstants.TAG %>" />
		<aui:input name="<%=HConstants.TAG_ID %>" type="hidden" />
		
		<aui:input name="terms"  type="checkbox" required="true" label="" inlineField="true" />
		<aui:a href="http://www.google.com">Terms of user</aui:a>
		And
		<aui:a href="http://www.google.com">Posting Policies</aui:a>
		
		<aui:button type="submit" value="add-item" onClick="extractCodeFromEditor();"/>
		<aui:button type="reset" value="reset" />
	</aui:form>
</aui:fieldset>


<script>
	
	$( document ).ready(function() {
		$("img[src='']").hide();
	});
	
	function readURL(input,id) {
	    if (input.files && input.files[0]) {
	        var reader = new FileReader();
	
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
	    control.focus();
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
		 A.one("#<portlet:namespace /><%=HConstants.UNILIMITED_QUANTITY%>Checkbox").on('change',function(e){
			 var quantity =  A.one("#<portlet:namespace /><%=HConstants.QUANTITY%>");
			if(e.currentTarget.get('checked')) {
				quantity.set('disabled', true);
		 }else{
			 quantity.set('disabled', false);
		 }
		});
	});

	function extractCodeFromEditor() {
        var x = document.<portlet:namespace />addItemForm.<portlet:namespace /><%=HConstants.DESCRIPTION%>.value = window.<portlet:namespace />editor.getHTML();
	}
</script>
