<%@page import="com.htmsd.slayer.service.ItemTypeLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.model.ItemType"%>
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
			<%-- <c:if test="<%=isAdmin || isStaff%>"> --%>
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
			<%-- </c:if> --%>
		</aui:layout>
		<h4><liferay-ui:message key="allowed-files" /></h4>
		<aui:layout >
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
		
		<liferay-ui:message key="description" />
		<liferay-ui:input-editor cssClass="editor_padding"/>
		<aui:input name="<%=HConstants.DESCRIPTION %>" value=""  type="hidden" />
		
		<aui:layout>
			<aui:column>
				<aui:select name="itemType" label="Type" required="true" showEmptyOption="true" helpMessage="item-type-help-message">
					<%
						for (ItemType itemType : ItemTypeLocalServiceUtil.getItemTypes(-1, -1)) {
							%>
								<aui:option value="<%=itemType.getItemTypeId() %>" label="<%=itemType.getName() %>"/>		 
							<%
						}
					%>
				</aui:select>
			</aui:column>
			<aui:column>
				<aui:input name="itemTypeDocument" label="Document" type="file" required="true">
					<aui:validator name="acceptFiles" errorMessage="please-upload-image-document">'jpg,png,gif,jpeg,tif,tiff,bmp,docx,pdf'</aui:validator>
				</aui:input>
			</aui:column>
		</aui:layout>
		
		<aui:fieldset label="product-dimensions">
			<aui:layout>
				<aui:column>
					<aui:input name="itemWeight" required="true" suffix="Kgs">
						<aui:validator name="number" />
						<aui:validator name="custom" errorMessage="Please enter a valid Weight">
							function (val, fieldNode, ruleValue) {
								var result = true;
								if (val.length == 0 || val <= 0) {
									result = false;
								}
								return result;
							}
						</aui:validator>
					</aui:input>
				</aui:column>
				<%-- <aui:column>
					<aui:input name="itemLength" required="true" suffix="cm">
						<aui:validator name="number" />
						<aui:validator name="custom" errorMessage="Please enter a valid Length">
							function (val, fieldNode, ruleValue) {
								var result = true;
								if (val.length == 0 || val <= 0) {
									result = false;
								}
								return result;
							}
						</aui:validator>
					</aui:input>
				</aui:column>
				<aui:column>
					<aui:input name="itemWidth" required="true" suffix="cm">
						<aui:validator name="number" />
						<aui:validator name="custom" errorMessage="Please enter a valid Width">
							function (val, fieldNode, ruleValue) {
								var result = true;
								if (val.length == 0 || val <= 0) {
									result = false;
								}
								return result;
							}
						</aui:validator>
					</aui:input>
				</aui:column>
				<aui:column>
					<aui:input name="itemHeight" required="true" suffix="cm">
						<aui:validator name="number" />
						<aui:validator name="custom" errorMessage="Please enter a valid Height">
							function (val, fieldNode, ruleValue) {
								var result = true;
								if (val.length == 0 || val <= 0) {
									result = false;
								}
								return result;
							}
						</aui:validator>
					</aui:input>
				</aui:column> --%>
			</aui:layout>
		</aui:fieldset>
		<aui:fieldset label="product-pricing">
			<h4><liferay-ui:message key="comission" /></h4>
			<aui:layout>
				<aui:column>
					<aui:input name="MRP" label="MRP" required="true" prefix="<%= currSym %>">
						<aui:validator name="number" />
						<aui:validator name="custom" errorMessage="Please enter a valid price">
							function (val, fieldNode, ruleValue) {
								var result = true;
								if (val.length == 0 || val <= 0) {
									result = false;
								}
								return result;
							}
						</aui:validator>
					</aui:input>
				</aui:column>
				<aui:column>
					<aui:input name="<%=HConstants.TAX %>" label="Tax" suffix="%" onkeyup="calculateSellerEarning(this)">
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
				<c:if test="<%=isAdmin || isStaff%>">
					<aui:column>
						<aui:input name="deliveryCharges" prefix="<%= currSym %>" required="true">
							<aui:validator name="number" />
							<aui:validator name="custom" errorMessage="Please enter a valid price">
								function (val, fieldNode, ruleValue) {
									var result = true;
									if (val.length == 0 || val <= 0) {
										result = false;
									}
									return result;
								}
							</aui:validator>
						</aui:input>
					</aui:column>
				</c:if>
			</aui:layout>
			<aui:layout>	
				<aui:column>
					<aui:input name="<%=HConstants.PRICE %>" required="true" onkeyup="calculateSellerEarning(this);" prefix="<%= currSym %>"> 
						<aui:validator name="number" />
						<aui:validator name="custom" errorMessage="Please enter a valid price">
							function (val, fieldNode, ruleValue) {
								var result = true;
								if (val.length == 0 || val <= 0) {
									result = false;
								}
								return result;
							}
						</aui:validator>
					</aui:input>
				</aui:column>
				<aui:column>
					<aui:input name="commission" cssClass="commission" readonly="readonly" suffix="%"/>
				</aui:column>
			</aui:layout>
			<aui:layout>
				<aui:column>
					<aui:input name="earned" disabled="true" cssClass="earned" prefix="<%= currSym %>"/>
				</aui:column>
			</aui:layout>
			<aui:layout>
				<aui:input name="<%=HConstants.WHOLESALE_DISCOUNT %>" type="checkbox"  inlineLabel="true" />
				<div id="wholeSaleDiv" style="display:none;">
					<%
						for(int i = 1 ; i <= HConstants.WHOLESALE_LIMIT ; i ++) {
							%>
								<div class="wholesaleclass" id='<%="wholeSaleDiv" + i %>' style="display:none;">
									<aui:layout>
										<aui:column>
											<aui:input name="<%=HConstants.WHOLESALE_QUANTITY + i %>">
												<aui:validator name="number" />
											</aui:input>	
										</aui:column>
										<aui:column>
											<aui:input name="<%=HConstants.WHOLESALE_PRICE + i%>" data-index = "<%= i %>" onkeyup="calculateSellerEarning(this);">
												<aui:validator name="number" />
											</aui:input>
										</aui:column>
										<aui:column>
											<aui:input name='<%= "commission"+ i%>' cssClass="commission" label="commission" disabled="true" suffix="%"/>
										</aui:column>
									</aui:layout>	
									<aui:layout>
										<aui:column columnWidth="60">
											<aui:input name='<%= "earned"+ i%>' cssClass="earned" label="earned" disabled="true" prefix="<%= currSym %>"/>
										</aui:column>
										<aui:column>
											<aui:button-row cssClass="add-btn-padding">
												<%	
													if(i!=HConstants.WHOLESALE_LIMIT) {
														%>
													  		<aui:button cssClass="addclass" name='<%="addbtn" + i %>' value="add"  />
													 	<%
													}
												%>
												<%	
													if(i!=1) {
														%>
															<aui:button cssClass="removeclass" name='<%="removebtn" + i %>'  value="remove" />
														<%
													}
												%>
											</aui:button-row>
										</aui:column>
									</aui:layout>
									<hr/>
								</div>
							<%
						}
					%>
				</div>
			</aui:layout>
		</aui:fieldset>
		
		<aui:fieldset label="product-quantity">
			<aui:layout>	
				<aui:column>
					<aui:input name="<%=HConstants.QUANTITY %>">
						<aui:validator name="number" />
						<aui:validator name="custom" errorMessage="Please enter a valid quantity">
							function (val, fieldNode, ruleValue) {
								var result = true;
								if (val.length == 0 || val <= 0) {
									result = false;
								}
								return result;
							}
						</aui:validator>
					</aui:input>
				</aui:column>
				<aui:column>
					<aui:input name="<%=HConstants.UNILIMITED_QUANTITY %>" type="checkbox" label="unlimited-quantity" />
				</aui:column>
			</aui:layout>
		</aui:fieldset>
		<aui:input name="terms"  type="checkbox" required="true" label="" inlineField="true" />
		<aui:a href="/terms-and-conditions">Terms of use</aui:a>
		And
		<aui:a href="/posting-policy">Posting Policies</aui:a>
		<aui:button-row>
			<aui:button type="submit" value="add-item" onClick="onAddItem();"/>
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
		A.Node.create('<option value=""> -- Select -- </option>').appendTo(categoryElem);
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

	function onAddItem() {
		extractCodeFromEditor();
	}
	
	function  <portlet:namespace />initEditor() {
	    return "";
	}
	
	function calculateSellerEarning(elem) {
		var index = $(elem).data("index");
		var price = $(elem).val();
		var tax = $('#<portlet:namespace/>tax').val();
		var commissionPercent = ((parseFloat($(".commission").val()) + parseFloat(tax)) / 100);
		console.info(commissionPercent);
		var dispStr = price+" - (" +price+" * ( ("+ $(".commission").val() +" + "+tax+") / 100) = " + (price - (price * commissionPercent));
		if (index === undefined) {
			$('#<portlet:namespace/>earned').val(dispStr);
		} else {
			$('#<portlet:namespace/>earned'+index).val(dispStr);
		}
	}
	
	$(function() {
		var commissionPercent = 10;
		$('#<portlet:namespace/>categoryId').on('change', function() {
			Liferay.Service('/htmsd-portlet.commission/get-commission-percent-by-category', {
				categoryId: $('#<portlet:namespace/>categoryId').val()
			}, function(obj) {
				$('.commission').val(obj);				
			});
		});
	})
</script>