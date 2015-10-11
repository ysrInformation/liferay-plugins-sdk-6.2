<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@page import="com.liferay.portlet.asset.service.AssetTagLocalServiceUtil"%>
<%@page import="com.liferay.portlet.asset.service.persistence.AssetTagUtil"%>
<%@page import="com.liferay.portlet.asset.model.AssetTag"%>
<%@include file="/html/dashboard/init.jsp" %>

<portlet:resourceURL  id="getCategoryId"  var="getCategoryURL"/>

<portlet:actionURL name="addItem" var="addItemURL" >
	<portlet:param name="tab1" value='<%=ParamUtil.getString(renderRequest, "tab1") %>'/>
</portlet:actionURL>
<%
	List<AssetTag> assetTags = AssetTagLocalServiceUtil.getTags(themeDisplay.getScopeGroupId(), PortalUtil.getClassNameId(ShoppingItem.class), null, -1, -1);
	List<Category> parentCategories = CategoryLocalServiceUtil.getByParent(0);
	List<Category> categories = CategoryLocalServiceUtil.getByChild(0);
%>

<liferay-ui:header title="add-item" backLabel="go-back" backURL='<%=ParamUtil.getString(renderRequest, "backURL") %>'/>

<aui:fieldset>
	<aui:form action="<%=addItemURL %>" enctype="multipart/form-data" method="POST" name="addItemForm">
		<aui:layout>
			<aui:column columnWidth="25">
				<aui:input name="<%=HConstants.NAME%>">
					<aui:validator name="required" errorMessage="item-name-required"></aui:validator>
				</aui:input>
			</aui:column>
			
			<aui:column columnWidth="25">
				<aui:input name="<%=HConstants.PRODUCT_CODE %>" />
			</aui:column>
			
			<aui:column columnWidth="25">
				<aui:select name="<%=HConstants.PARENT_CATEGORY_ID %>"   required="true" showEmptyOption="true">
					<c:forEach items="<%=parentCategories %>" var="parentCategory">   
						<aui:option label="${parentCategory.name}"  value="${parentCategory.categoryId}"/>
					</c:forEach>
				</aui:select>
			</aui:column>
			
			<aui:column columnWidth="25">
				<aui:select name="<%=HConstants.CATEGORY_ID %>"   required="true" showEmptyOption="true">
				</aui:select>
			</aui:column>
		</aui:layout>
		
		
		<liferay-ui:message key="description" />
		<liferay-ui:input-editor cssClass="editor_padding"/>
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
		
		<aui:layout>
			<aui:column>
				<aui:input name="<%=HConstants.PRICE %>" required="true">
					<aui:validator name="number" />
				</aui:input>
			</aui:column>
			<aui:column>
				<aui:input name="<%=HConstants.QUANTITY %>">
					<aui:validator name="number" />
				</aui:input>
			</aui:column>	
			<aui:column >
				<aui:input name="<%=HConstants.UNILIMITED_QUANTITY %>" type="checkbox" label="unlimited-quantity" />
			</aui:column>
		</aui:layout>
		
		<aui:layout>
			<aui:column columnWidth="30">
				<aui:input name="<%=HConstants.WHOLESALE_DISCOUNT %>" type="checkbox"  inlineLabel="true" />
			</aui:column>	
			<div id="wholeSaleDiv" style="display:none;">
				<aui:column columnWidth="30">
					<aui:input name="<%=HConstants.WHOLESALE_QUANTITY %>" />	
				</aui:column>
				<aui:column columnWidth="30">
					<aui:input name="<%=HConstants.WHOLESALE_PRICE %>" />
				</aui:column>
			</div>
		</aui:layout>
		
		<div id="tags">
			<liferay-ui:message key="tags" />
			<liferay-ui:asset-tags-selector className="<%=ShoppingItem.class.getName() %>" />
		</div>
		<aui:input name="terms"  type="checkbox" required="true" label="" inlineField="true" />
		<aui:a href="http://www.google.com">Terms of user</aui:a>
		And
		<aui:a href="http://www.google.com">Posting Policies</aui:a>
		<aui:button-row>
			<aui:button type="submit" value="add-item" onClick="extractCodeFromEditor();"/>
			<aui:button type="reset" value="reset" />
		</aui:button-row>
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
	
	AUI().use('aui-base' ,'aui-io-request',function (A) {	
		 
		A.one("#<portlet:namespace /><%=HConstants.UNILIMITED_QUANTITY%>Checkbox").on('change',function(e){
			 var quantity =  A.one("#<portlet:namespace /><%=HConstants.QUANTITY%>");
			if(e.currentTarget.get('checked')) {
				quantity.set('disabled', true);
		 }else{
			 quantity.set('disabled', false);
		 }
		});
		 
		 A.one("#<portlet:namespace /><%=HConstants.WHOLESALE_DISCOUNT%>Checkbox").on('change',function(e){
			var wholeSaleDiv = document.getElementById("wholeSaleDiv");

			if(e.currentTarget.get('checked')) {
				wholeSaleDiv.style.display = "block";
		 }else{
			 wholeSaleDiv.style.display = "none";	
		 }
		}); 
		 
		A.one("#<portlet:namespace /><%=HConstants.PARENT_CATEGORY_ID%>").on('change',function(e){
			var parentCategoryId =  A.one("#<portlet:namespace /><%=HConstants.PARENT_CATEGORY_ID%>").val();
			A.io.request('<%=getCategoryURL%>', {
				dataType: 'json',
				data: {
					'<portlet:namespace/>parentCategoryId' : parentCategoryId
				   },
				  on: {
				   success: function() {
					   setCategory(this.get('responseData'));
				   }
				  }
				});

		});
	});
	function extractCodeFromEditor() {
        var x = document.<portlet:namespace />addItemForm.<portlet:namespace /><%=HConstants.DESCRIPTION%>.value = window.<portlet:namespace />editor.getHTML();
	}
	function setCategory(data) {
		var A = new AUI();
		var categoryElem = A.one("#<portlet:namespace /><%=HConstants.CATEGORY_ID%>");
		categoryElem.get('children').remove();
		for(x in data) {
			A.Node.create('<option value='+data[x].categoryId+'>'+data[x].categoryName+'</option>').appendTo(categoryElem);
		}
	}
</script>
