<%@include file="/html/dashboard/init.jsp" %>

<portlet:actionURL name="addItem" var="addItemURL" />
<%
	List<Category> categories = CategoryLocalServiceUtil.getCategories(-1, -1);
%>
<liferay-ui:header title="back"  backURL="<%=ParamUtil.getString(renderRequest, "backURL")  %>" />
<aui:fieldset>
	<aui:form action="<%=addItemURL %>" enctype="multipart/form-data" method="POST">
		<aui:input name="<%=HConstants.NAME%>" required="true" />
		<aui:select name="<%=HConstants.CATEGORY_ID %>"   required="true" showEmptyOption="true">
			<c:forEach items="<%=categories %>" var="category">   
				<aui:option label="${category.name}"  value="${category.categoryId}"/>
			</c:forEach>
		</aui:select>
		<aui:input name="<%=HConstants.DESCRIPTION %>" type="textarea" required="true" />
		<aui:layout>
			<c:forEach var="i" begin="1" end="<%=HConstants.IMAGES_UPLOAD_LIMIT %>">
					<aui:column columnWidth="60" >
						<aui:input name='image${i}'  type="file" label="" onChange="readURL(this,${i});">
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
		
		<aui:input name="<%=HConstants.PRICE %>" required="true">
			<aui:validator name="number" />
		</aui:input>
		<aui:input name="<%=HConstants.TAG %>" />
		<aui:button type="submit" value="add-item" />
		<aui:button type="reset" value="reset" />
	</aui:form>
</aui:fieldset>


<script>
	function readURL(input,id) {
	    if (input.files && input.files[0]) {
	        var reader = new FileReader();
	
	        reader.onload = function (e) {
	            $('#image_upload_preview'+id).attr('src', e.target.result);
	        }
	
	        reader.readAsDataURL(input.files[0]);
	    }
	}
	
	function deleteUpload(id) {
		var control = $('#<portlet:namespace/>image'+id);
	    control.replaceWith( control = control.clone( true ) );
	    $('#image_upload_preview'+id).attr("src","");
	}

</script>