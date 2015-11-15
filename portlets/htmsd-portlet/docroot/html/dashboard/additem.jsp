<%@include file="/html/dashboard/init.jsp" %>

<portlet:resourceURL  id="getCategoryId"  var="getCategoryURL"/>
<portlet:resourceURL id="getBusinessAddr" var="getBusinessAddrURL" />
<portlet:actionURL var="saveBusinessURl" name="saveBusiness"/>


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
	<aui:form action="<%=addItemURL %>" enctype="multipart/form-data" method="POST" name="addItemForm" >
		<aui:layout>
			<aui:col width="25">
				<aui:input name="<%=HConstants.NAME%>">
					<aui:validator name="required" errorMessage="item-name-required"></aui:validator>
				</aui:input>
			</aui:col>
			
			<aui:col width="25">
				<aui:input name="<%=HConstants.PRODUCT_CODE %>" />
			</aui:col>
			
			<aui:col width="25">
				<aui:select name="<%=HConstants.PARENT_CATEGORY_ID %>" label="category"  required="true" showEmptyOption="true">
					<c:forEach items="<%=parentCategories %>" var="parentCategory">   
						<aui:option label="${parentCategory.name}"  value="${parentCategory.categoryId}"/>
					</c:forEach>
				</aui:select>
			</aui:col>
			
			<aui:col width="25">
				<aui:select name="<%=HConstants.CATEGORY_ID %>" label="sub-category"  required="true" showEmptyOption="true">
				</aui:select>
			</aui:col>
		</aui:layout>
		
		
		<liferay-ui:message key="description" />
		<liferay-ui:input-editor cssClass="editor_padding"/>
		<aui:input name="<%=HConstants.DESCRIPTION %>" value=""  type="hidden" />
		<aui:layout>
			<c:forEach var="i" begin="1" end="<%=HConstants.IMAGES_UPLOAD_LIMIT %>">
				<aui:column columnWidth="60" >
					<aui:input name='image${i}'  type="file" label="" onChange="readURL(this,${i});">
						<aui:validator name="acceptFiles" errorMessage="please-upload-image">'jpg,png,gif,jpeg,tif,tiff,bmp'</aui:validator>
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
				<aui:input name="<%=HConstants.TAX %>">
					<aui:validator  name="custom"  errorMessage="Please enter valid Percentage" >
						function (val, fieldNode, ruleValue) {
							var result = false;
							if (val.match(/^(?:\d*\.\d{1,2}|\d+)$/) || val.length == 0 ) {
								result = true;
							}
							return result;
						}
					</aui:validator>
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
			<aui:input name="<%=HConstants.WHOLESALE_DISCOUNT %>" type="checkbox"  inlineLabel="true" />
			<div id="wholeSaleDiv" style="display:none;">
				<%
					for(int i = 1 ; i <= HConstants.WHOLESALE_LIMIT ; i ++) {
						%>
							<div class="wholesaleclass" id='<%="wholeSaleDiv" + i %>' style="display:none;">
								<aui:row>
									<aui:col width="20">
										<aui:input name="<%=HConstants.WHOLESALE_QUANTITY + i %>" >
											<aui:validator name="number" />
										</aui:input>	
									</aui:col>
									<aui:col width="20">
										<aui:input name="<%=HConstants.WHOLESALE_PRICE + i%>" >
											<aui:validator name="number" />
										</aui:input>
									</aui:col>
									<aui:col width="20">
										<aui:button-row cssClass="add-btn-padding">
											<%	if(i!=HConstants.WHOLESALE_LIMIT) {
													%>
												  		<aui:button cssClass="addclass" name='<%="addbtn" + i %>' value="add"  />
												 	<%
												}
											%>
											<%	if(i!=1) {
													%>
														<aui:button cssClass="removeclass" name='<%="removebtn" + i %>'  value="remove" />
													<%
												}
											%>
										</aui:button-row>
									</aui:col>
								</aui:row>
								<hr/>
							</div>
						<%
					}
				%>
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
			<aui:button type="button" value="add-item"  onClick="onAddItem();" />
			<aui:button type="reset" value="reset" />
		</aui:button-row>
	</aui:form>
</aui:fieldset>

<%@include file="/html/dashboard/businessinfo.jspf" %>

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
	
	AUI().use('aui-base' ,'aui-io-request', "aui-node" ,function (A) {	
		 
		A.one("#<portlet:namespace /><%=HConstants.UNILIMITED_QUANTITY%>Checkbox").on('change',function(e){
			 var quantity =  A.one("#<portlet:namespace /><%=HConstants.QUANTITY%>");
			if(e.currentTarget.get('checked')) {
				quantity.set('disabled', true);
		 }else{
			 quantity.set('disabled', false);
		 }
		});
		 
		 A.one("#<portlet:namespace /><%=HConstants.WHOLESALE_DISCOUNT%>Checkbox").on('change',function(e){
			var wholeSaleDiv = document.getElementById("wholeSaleDiv1");
			var fullwholeSaleDiv = document.getElementById("wholeSaleDiv");

			if(e.currentTarget.get('checked')) {
				wholeSaleDiv.style.display = "block";
				fullwholeSaleDiv.style.display = "block";
			 }else{
				wholeSaleDiv.style.display = "none";	
			 	fullwholeSaleDiv.style.display = "none";
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
		
	$('.addclass').click(function(){
		
		var id = $(this).attr('id');
		var counter = parseInt(id.substr(id.length-1, id.length))+1;
		$('#wholeSaleDiv'+counter).show();
		$('#<portlet:namespace/>addbtn'+(counter-1)).hide();
		$('#<portlet:namespace/>removebtn'+(counter-1)).hide();
		$('#<portlet:namespace/>addbtn'+counter).show();
		$('#<portlet:namespace/>removebtn'+counter).show();
	});
	
	
	$('.removeclass').click(function(){
		
		var id = $(this).attr('id');
		var counter = parseInt(id.substr(id.length-1, id.length));
		$('#wholeSaleDiv'+counter).hide();
		$('#<portlet:namespace/>removebtn'+counter).hide();
		$('#<portlet:namespace/>addbtn'+(counter-1)).show();
		$('#<portlet:namespace/>removebtn'+(counter-1)).show();
		$('#<portlet:namespace/><%=HConstants.WHOLESALE_QUANTITY%>'+counter).val('');
		$('#<portlet:namespace/><%=HConstants.WHOLESALE_PRICE%>'+counter).val('');
	});
	
	function getBusinessAddress() {
		var A = AUI();
		var cat = A.one('#<portlet:namespace/><%=HConstants.PARENT_CATEGORY_ID %>').attr('value');
		
		A.io.request('<%=getBusinessAddrURL%>', {
			dataType: 'json',
			data: {
				'<portlet:namespace/>categoryId' : cat
			   },
			  on: {
			   success: function() {
					if(this.get('responseData')[0] == 'false'){
						AUI().use('aui-modal', function(A) {
					        Liferay.Util.openWindow({
					            dialog: {
					                centered: true,
					                modal: true,
					                bodyContent : document.getElementById('businessDiv').innerHTML ,
					                
					                toolbars: {
		    	   	 			          body: [
		    	   	 			            {
		    	   	 			              icon: 'glyphicon glyphicon-file',
		    	   	 			              label: 'Ok',
		    	   	 			         	  centered: true,
			    	   	 			          on: {
			    	   	 			            click: function() {
			    	   	 			            	
			    	   	 			            	var validForm = false;
			    	   	 			            	 
			    	   	 			            	validForm = validateBusinessForm(document.getElementById('<portlet:namespace/>businessForm').elements);
			    	   	 			            	if(validForm) {
			    	   	 			            	   AUI().use('aui-base','aui-io-request', 'aui-form-validator', 'aui-overlay-context-panel', function(A){
					    	   	 			         		
					    	   	 			            	A.io.request('<%=saveBusinessURl%>',{
						    	   	 			           	 sync : true,
						    	   	 			                dataType: 'json',
						    	   	 			                form: { 
						    	   	 			               	 	  id: '<portlet:namespace/>businessForm'
						    	   	 			                }, 
						    	   	 			                on: {
						    	   	 			                success: function() {
							    	   	 			                if(validForm) {
							    	   	 			              	  var updateBusinessDialog=Liferay.Util.Window.getById('updateBusiness');
							    	   	 			           	 		updateBusinessDialog.destroy();	
							    	   	 			                }
					    	   	 			                   		}
						    	   	 			                }
						    	   	 			            });
					    	   	 			       		});
			    	   	 			            	}
				    	   	 			         
			    	   	 			            }
			    	   	 			          }
			    	   	 			              
		    	   	 			            },
			    	   	 			        {
			    	   	 			              icon: 'glyphicon glyphicon-file',
			    	   	 			              label: 'Cancel',
			    	   	 			         	  centered: true,
				    	   	 			          on: {
				    	   	 			            click: function() {
				    	   	 			                var updateBusinessDialog=Liferay.Util.Window.getById('updateBusiness');
				    	   	 			           	 	updateBusinessDialog.destroy();	
				    	   	 			            }
				    	   	 			          }
				    	   	 			    }
		    	   	 			          ]
		    	   	 			        }
					            },
					            id: 'updateBusiness',
					            title: Liferay.Language.get('Business Information'),
					        });
					       
					    }); 
						
					} else{
						document.getElementById("<portlet:namespace/>addItemForm").submit();	  
					}
			   }
			  }
			});
		
	}
	
	function onAddItem() {
		extractCodeFromEditor();
		getBusinessAddress();
	}
	
	function validateBusinessForm(form) {
		
		for(var i = 0 ; i <form.length; i++){
			var name = 	form[i].name;
			var value = form[i].value;
			switch(name) {
			
				case "<portlet:namespace/>street1" : if(value.length >0 )  {
														document.getElementById(name+'Err').style.display = "none";
														break;
													 }else{
														document.getElementById(name+'Err').style.display = "block";
														return false;
													}
				
				case "<portlet:namespace/>companyName" : if(value.length >0 )  {
														document.getElementById(name+'Err').style.display = "none";
														break;
													 }else{
														document.getElementById(name+'Err').style.display = "block";
														return false;
													}
				
				
				case "<portlet:namespace/>zip" : if(value.match(/^[0-9]{6}/))  {
													document.getElementById(name+'Err').style.display = "none";
													break;
												}else{
													document.getElementById(name+'Err').style.display = "block";
													return false;
												}
				case "<portlet:namespace/>city" : 	if(value.match(/^[a-zA-Z]+$/))  {
														document.getElementById(name+'Err').style.display = "none";
														break;
												}else{
													document.getElementById(name+'Err').style.display = "block";
													return false;
												}
				
				case "<portlet:namespace/>tin" : 	if(value.match(/^[0-9]{9}/))  {
														document.getElementById(name+'Err').style.display = "none";
														break;
													}else{
														document.getElementById(name+'Err').style.display = "block";
														return false;
													}
					
			}
		}
		return true;
	}
	function  <portlet:namespace />initEditor() {
	    return "";
	}
</script>
