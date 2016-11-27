<%@page import="com.htmsd.slayer.service.CommissionLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.model.Commission"%>
<%@page import="com.htmsd.slayer.service.ItemTypeLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.model.ItemType"%>
<%@page import="com.htmsd.slayer.model.Seller"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLAppHelperLocalServiceUtil"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.repository.model.FileEntry"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@page import="com.liferay.portal.model.Address"%>
<%@page import="com.htmsd.util.CommonUtil"%>
<%@page import="com.liferay.portal.kernel.workflow.WorkflowConstants"%>
<%@include file="/html/dashboard/init.jsp" %>

<portlet:resourceURL  id="getCategoryId"  var="getCategoryURL"/>

<head>

  <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
  <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
  
  <style>
	.ui-widget-content {
		border: 0;
		background: transparent;
	}
	
	.ui-widget-header {
		background: transparent;
		border: 0;
	}
	
	.ui-button-icon-only .ui-button-text, .ui-button-icons-only .ui-button-text
		{
		padding: 0;
	}
	
	.ui-widget-overlay, .ui-dialog-content {
		cursor: zoom-out;
	}
	.seller-details {
		border: 1px solid;
		border-radius: 5px;
		padding: 10px;
		margin-bottom: 20px;
	}
</style>
 </head> 
<%
 	String backURL = ParamUtil.getString(renderRequest, "backURL");
 	long itemId = ParamUtil.getLong(renderRequest, "itemId");
 	ShoppingItem item = ShoppingItemLocalServiceUtil.fetchShoppingItem(itemId);
 	DLFileEntry documentFileEntry = null;
 	DecimalFormat decimalFormat = new DecimalFormat("#.00");
 	if (Validator.isNotNull(item)) {
 		List<Category> parentCategories = CategoryLocalServiceUtil.getByParent(0);
 		PortletURL updateItemURL = renderResponse.createActionURL();
 		updateItemURL.setParameter(ActionRequest.ACTION_NAME, "addItem");
 		updateItemURL.setParameter(HConstants.ITEM_ID, String.valueOf(itemId));
 		updateItemURL.setParameter("userId", String.valueOf(item.getUserId()));
 		updateItemURL.setParameter("backURL", backURL);
 		updateItemURL.setParameter("redirect", themeDisplay.getURLCurrent());
 		updateItemURL.setParameter("tab1", ParamUtil.getString(renderRequest, "tab1"));
 		if (!isAdmin) {
 			updateItemURL.setParameter(HConstants.status, String.valueOf(item.getStatus()));
 			updateItemURL.setParameter(HConstants.QUANTITY, String.valueOf(item.getQuantity()));
 		}
 		long quantity = item.getQuantity();
 		List<Category> itemCategory = CategoryLocalServiceUtil.getCategoryByItemId(itemId);
 		List<WholeSale> wholeSales = WholeSaleLocalServiceUtil.getWholeSaleItem(itemId);
 		long wholeSaleSize = wholeSales.size();
 		long categoryId = 0l;
 		long parentCategoryId = 0l;
 		if (itemCategory.size() >= 1) {
 			categoryId = itemCategory.get(0).getCategoryId();
 			parentCategoryId = itemCategory.get(0).getParentCategoryId();
 		}
 		int status = item.getStatus();

 		User sellerUser = UserLocalServiceUtil.fetchUser(item.getUserId());
 		List<Address> addressList = sellerUser.getAddresses();
 		Seller seller = SellerLocalServiceUtil.getSellerByUserId(item.getUserId());
 		
 		String companyName = seller.getName();
 		String cst = seller.getCST();
 		String tin = seller.getTIN();
 		String bankName = seller.getBankName();
 		String ifscCode = seller.getIfscCode();
 		long bankAcountNumber = seller.getBankAccountNumber();
 		double commission = CommonUtil.getShoppingItemCommission(item.getItemId());
 %>
		<div class="seller-details">
			<aui:fieldset label="seller-details">
				<div class="seller-company ">
					<div class="row-fluid">
						<div class="span6">
							<div class="seller-name">
								<strong><liferay-ui:message key="name" />: </strong><%=sellerUser.getFullName()%>
							</div>
							<div class="bank-name">
								<strong><liferay-ui:message key="bank-name" />: </strong><%=bankName%>
							</div>
							<div class="bank-account-number">
								<strong><liferay-ui:message key="bank-account-number" />: </strong><%=bankAcountNumber%>
							</div>
							<div class="ifsc-code">
								<strong><liferay-ui:message key="ifsc-code" />: </strong><%=ifscCode%>
							</div>
						</div>
						<div class="span6">
							<div class="phone-number">
								<strong><liferay-ui:message key="phone-number" />: </strong><%=sellerUser.getScreenName()%>
							</div>
							<div class="company-name">
								<strong><liferay-ui:message key="company-name"/>: </strong><%= companyName %>
							</div>
							<div class="tin">
								<strong><liferay-ui:message key="tin"/>: </strong><%= tin %>
							</div>
							<div class="cst">
								<strong><liferay-ui:message key="cst"/>: </strong><%= cst %>
							</div>
						</div>
					</div>
				</div>
			</aui:fieldset>
			<aui:fieldset label="seller-addresses">
				<div class="seller-addresses">
					<div class="address row-fluid">
						<%
							for (Address address : addressList) {
								String primaryCss = address.isPrimary() ? "primary-address" : StringPool.BLANK;
								%>
									<div class="span3 <%=primaryCss%>">
										<div class="street1">
											<strong><liferay-ui:message key="street1"/>:</strong><%=address.getStreet1() %>
										</div>
										<%
											if (Validator.isNotNull(address.getStreet2()) && !address.getStreet2().isEmpty()) {
												%>
													<div class="street2">
														<strong><liferay-ui:message key="street2"/>:</strong><%=address.getStreet2() %>
													</div>
												<%
											}
										%>	
										<%
											if (Validator.isNotNull(address.getStreet3()) && !address.getStreet3().isEmpty()) {
												%>
													<div class="street3">
														<strong><liferay-ui:message key="street3"/>:</strong><%=address.getStreet3() %>
													</div>
												<%
											}
										%>
										<div class="city">
											<strong><liferay-ui:message key="city"/>:</strong><%=address.getCity() %>
										</div>
										<div class="zip">
											<strong><liferay-ui:message key="postal-code"/>:</strong><%=address.getZip() %>
										</div>
										<div class="country">
											<strong><liferay-ui:message key="country"/>:</strong><%=address.getCountry().getName() %>
										</div>
									</div>
								<%
							}
						%>
					</div>
				</div>
			</aui:fieldset>
		</div>
	
		<liferay-ui:header title='<%=isAdmin ? "update-item" : "view-item" %>' backLabel="go-back" backURL='<%=backURL%>'/>
		<aui:fieldset>
			<aui:form action="<%=updateItemURL %>" enctype="multipart/form-data" method="POST" name="itemDetailForm">
				<aui:input name="userId" type="hidden" value="<%=item.getUserId() %>" />
				<aui:input name="userName" type="hidden" value="<%=item.getUserName() %>" />
				<aui:input name="emailId" type="hidden" value="<%=item.getUserEmail() %>" />
				<aui:input name="<%=HConstants.SMALL_IMAGE %>" type="hidden" value="<%=item.getSmallImage() %>" />
				
				<aui:layout>
					<aui:col width="25">
						<aui:input name="<%=HConstants.NAME%>" required="true" value="<%= item.getName()%>" cssClass="mark-readonly"/>
					</aui:col>
					
					<aui:col width="25">
						<aui:input name="<%=HConstants.PRODUCT_CODE %>" value="<%=item.getProductCode() %>" cssClass="mark-readonly"/>
					</aui:col>
					
					<aui:col width="25">
						<aui:select name="<%=HConstants.PARENT_CATEGORY_ID %>" label="category"  required="true" showEmptyOption="true" disabled="<%=!(isAdmin || isStaff || isApprover)%>" cssClass="mark-readonly">
							<%
								
								for(Category parentCategory : parentCategories) {
									boolean showoption = (parentCategory.getCategoryId() == parentCategoryId);
									%>
										<aui:option label="<%=parentCategory.getName() %>"  value="<%=parentCategory.getCategoryId() %>" selected="<%=showoption %>" />
									<%
								}
							%>   
						</aui:select>
					</aui:col>
					<aui:col width="25">
						<aui:select name="<%=HConstants.CATEGORY_ID %>" label="sub-category" showEmptyOption="true" required="true" disabled="<%=!(isAdmin || isStaff || isApprover)%>" cssClass="mark-readonly"></aui:select>
					</aui:col>
				</aui:layout>
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
							String imgSrc = "/documents/"
											+ documentFileEntry.getGroupId()
											+ StringPool.FORWARD_SLASH
											+ documentFileEntry.getFolderId()
											+ StringPool.FORWARD_SLASH
											+ documentFileEntry.getTitle()
											+ StringPool.FORWARD_SLASH 
											+ documentFileEntry.getUuid();
							%>
							<aui:column columnWidth="60" >
								<aui:input name='<%=image%>'  type="file" label="" onChange="<%=readURL %>" cssClass="mark-readonly">
								</aui:input>
								<aui:input name="<%=imageId %>" type="hidden"  value="<%=imageEntryId%>"/>
								<aui:input name="<%=deleteImage %>"  type="hidden"  value="false"/>
							</aui:column>
							
							<aui:column columnWidth="25"  >
								<img  id="<%=image_upload_preview%>" src="<%=imgSrc%>" width="60px" height="60px"  onMouseOver="this.style.cursor='zoom-in'" />
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
				
				<div>
					<div>
						<aui:input name="<%=HConstants.VEDIO_URL %>" value="<%=item.getVedioURL() %>" cssClass="mark-readonly">
							<aui:validator name="url" />
						</aui:input>
					</div>
					<div>
						<%
							String videoURL = item.getVedioURL();
							String videoId = StringPool.BLANK; 
							if (Validator.isNotNull(videoURL) && !videoURL.isEmpty()) {
								videoId = videoURL.substring(videoURL.indexOf("v=")+2, videoURL.length());
								%>
									<iframe src="<%="//www.youtube.com/embed/"+videoId%>" width="500px" height="300px"></iframe>
								<%
							}
						%>	
					</div>
				</div>
				
				<liferay-ui:message key="description" />
				<liferay-ui:input-editor cssClass="editor_padding" />
				<aui:input name="<%=HConstants.DESCRIPTION %>" value="<%=item.getDescription()%>"  type="hidden" />

				<aui:layout>
					<aui:column>
						<aui:select name="itemType" label="Type" required="true" showEmptyOption="true" helpMessage="item-type-help-message" cssClass="mark-readonly" >
							<%
								for (ItemType itemType : ItemTypeLocalServiceUtil.getItemTypes(-1, -1)) {
									%>
										<aui:option value="<%=itemType.getItemTypeId() %>" label="<%=itemType.getName() %>" selected="<%= item.getItemTypeId() == itemType.getItemTypeId() ? true : false %>"/>		 
									<%
								}
							%>
						</aui:select>
					</aui:column>
					<aui:column>
						<aui:input name="itemTypeDocumentId" type="hidden" value="<%= item.getItemTypeDocumentId() %>"/>
						<aui:input name="itemTypeDocument" label="Document" type="file" cssClass="mark-readonly"
							required='<c:choose><c:when test="<%=item.getItemTypeDocumentId() > 0%>">false</c:when><c:otherwise>true</c:otherwise></c:choose>'>
							<aui:validator name="acceptFiles"
								errorMessage="please-upload-image-document">'jpg,png,gif,jpeg,tif,tiff,bmp,docx,pdf'</aui:validator>
						</aui:input>
					</aui:column>
					<c:if test="<%=item.getItemTypeDocumentId() > 0 %>">
						<aui:column>
							<%
								String downloadURL = CommonUtil.getThumbnailpath(item.getItemTypeDocumentId(), item.getGroupId(), false);
								FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(item.getItemTypeDocumentId());
							%>
							<c:choose>
								<c:when test='<%=fileEntry.getMimeType().contains("image") %>'>
									<img src="<%=downloadURL %>" width="100px" height="100px"/>
								</c:when>
								<c:otherwise>
									<aui:a href="<%=downloadURL %>" label="Type Document"/>
								</c:otherwise>
							</c:choose>
						</aui:column>
					</c:if>
				</aui:layout>
			
				<aui:fieldset label="product-dimensions">
					<aui:layout>
						<%-- hidding this field as per client's comment
						<aui:column>
							<aui:input name="itemWeight" required="true" suffix="Kgs" value="<%= item.getItemWeight() %>" >
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
						--%>
						<aui:column>
							<aui:input name="itemShortDescription" helpMessage="item-short-help-message" value='<%= (Validator.isNotNull(item.getShortDescription()) ? item.getShortDescription() : StringPool.BLANK) %>' />
						</aui:column>
						<%-- <aui:column>
							<aui:input name="itemLength" required="true" suffix="cm" value="<%= item.getItemLength() %>">
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
							<aui:input name="itemWidth" required="true" suffix="cm" value="<%= item.getItemWidth() %>">
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
							<aui:input name="itemHeight" required="true" suffix="cm" value="<%= item.getItemHeight() %>">
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
							<aui:input name="MRP" label="MRP" required="true" prefix="<%= currSym %>" value="<%=item.getMRP() %>">
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
							<aui:input name="<%=HConstants.TAX %>" label="Tax" suffix="%" value="<%=item.getTax() %>" onkeyup="calculateSellerEarning(this)">
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
						<%-- hidding this field as per client's comment
						<c:if test="<%=isAdmin || isStaff%>">
							<aui:column>
								<aui:input name="deliveryCharges" prefix="<%= currSym %>" required="true" value="<%=item.getDeliveryChanges() %>" cssClass="mark-readonly">
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
						--%>
					</aui:layout>
					<aui:layout>	
						<aui:column>
							<aui:input name="<%=HConstants.PRICE %>" required="true" onkeyup="calculateSellerEarning(this);" prefix="<%= currSym %>" value="<%=item.getSellingPrice() %>"> 
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
							<aui:input name="commission" cssClass="commission" readonly="readonly" suffix="%" value="<%= commission  %>"/>
						</aui:column> 
					</aui:layout>
					<aui:layout>
						<aui:column>
							<%
								double price = item.getSellingPrice();
								double commissionPercent = ((commission + item.getTax()) / 100);
								//String dispStr = price+" - (" +price+" * ( ("+ commission +" + "+item.getTax()+" ) / 100) = " + (price - (price * commissionPercent));
								String dispStr = String.valueOf((price - (price * commissionPercent)));
							%>
							<aui:input name="earned" disabled="true" prefix="<%= currSym %>" value="<%=dispStr %>" cssClass="earned" helpMessage="earning-message"/>
						</aui:column>
					</aui:layout>
					<aui:layout>
						<aui:input name="<%=HConstants.WHOLESALE_DISCOUNT %>" type="checkbox"  checked="<%= wholeSaleSize > 0 ? true :  false %>" inlineLabel="true" />
						<div id="wholeSaleDiv" style='<%= wholeSaleSize > 0 ? "display:block" :  "display:block" %>'>
							<%
								for(int i = 1 ; i <= HConstants.WHOLESALE_LIMIT ; i ++) {
									String whlDispStr = StringPool.BLANK;
									if (wholeSaleSize >=  i) {
										WholeSale wholeSale = wholeSales.get(i-1);
										double whlPrice = wholeSale.getPrice();
										double whlCommissionPercent = ((commission + item.getTax())/ 100);
										//whlDispStr = whlPrice+" - (" +whlPrice+" * ("+ commission +" + "+item.getTax()+" ) / 100) = " + (whlPrice - (whlPrice * whlCommissionPercent));
										whlDispStr = String.valueOf((whlPrice - (whlPrice * whlCommissionPercent)));
									}
									%>
										<div class="wholesaleclass" id='<%="wholeSaleDiv" + i %>' style='<%=wholeSaleSize >= i ? "display:block;" : "display:none;"%> '>
											<aui:layout>
												<aui:column>
													<aui:input name="<%=HConstants.WHOLESALE_QUANTITY + i %>" value="<%=wholeSaleSize >= i  ? wholeSales.get(i-1).getQuantity() : StringPool.BLANK%>" >
														<aui:validator name="number" />
													</aui:input>	
												</aui:column>
												<aui:column>
													<aui:input name="<%=HConstants.WHOLESALE_PRICE + i%>"  value="<%=wholeSaleSize >= i  ? wholeSales.get(i-1).getPrice() : StringPool.BLANK%>" data-index = "<%= i %>" onkeyup="calculateSellerEarning(this);">
														<aui:validator name="number" />
													</aui:input>
												</aui:column>
												<aui:column>
													<aui:input name='<%= "commission"+ i%>' cssClass="commission" label="commission" disabled="true" suffix="%" value="<%= commission %>"/>
												</aui:column>
											</aui:layout>
											<aui:layout>
												<aui:column columnWidth="60">
													<aui:input name='<%= "earned"+ i%>' cssClass="earned"  label="earned" disabled="true" prefix="<%= currSym %>" value="<%=whlDispStr %>" helpMessage="earning-message"/>
												</aui:column>
												<aui:column>
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
												</aui:column>
											</aui:layout>
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
							<aui:input name="<%=HConstants.QUANTITY %>" value="<%= quantity == -1 ? StringPool.BLANK : quantity%>" disabled="<%=( quantity == -1 ) %>">
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
							<aui:input name="<%=HConstants.UNILIMITED_QUANTITY %>" type="checkbox" label="unlimited-quantity" checked="<%= quantity == -1 ? true : false %>"/>
						</aui:column>
					</aui:layout>
				</aui:fieldset>
				<c:choose>
					<c:when test="<%=isAdmin || isStaff || isApprover %>">
						<aui:input name="<%=HConstants.STAFF_REMARKS %>" type="textarea" value="<%=item.getRemark()%>" required="true"/>
					</c:when>
					<c:otherwise>
						<aui:input name="<%=HConstants.STAFF_REMARKS %>" type="hidden" value='<%="Seller Update:"+user.getFullName()%>' />
					</c:otherwise>
				</c:choose>
				<aui:button type="submit" value="update" onClick="return confirmSubmit();"/>
				<aui:button type="button" href="<%=backURL %>" value="cancel" />
				</aui:form>
			</aui:fieldset>
			
			<!-- Image for Modal -->
	    	<img id="zoomImg" src="" width="50px" height="50px" style="display:none;"/> 

			<script>
				$(document).ready(function(){
					fetchCategories('<%=categoryId%>');
					if(<%=!isAdmin && !isApprover%>) {
						$('#<portlet:namespace/>itemDetailForm .mark-readonly').attr("readonly", true);	
					}
				});
				$("img").on("click",function(){
					$("#zoomImg").attr('src',$(this).attr('src'));
					popupImage();
				});
				function popupImage(){
					 $(function() {
						    $( "#zoomImg" ).dialog({
						    	modal: true,
						    	width : 500,
						    	height : 500,
						    	draggable: false,
						    	resizable: false,
						    	closeOnEscape : true ,
								show: {
									effect: "fade",
									duration: 1000
								},
								hide: {
									effect: "explode",
									duration: 1000
								},
							   open: function(){
						              jQuery('.ui-widget-overlay,.ui-dialog-content').bind('click',function(){
						                jQuery('#zoomImg').dialog('close');
						             })
						        }
						    });
						  });
				}
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
					extractCodeFromEditor();
					return confirm("Are you sure you want to update ?");
				}
				
				AUI().use('aui-base',function (A) {
					 
				 A.one("#<portlet:namespace /><%=HConstants.PARENT_CATEGORY_ID%>").on('change',function(e){
					 fetchCategories(0);
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
				 
					 <c:if test='<%=isAdmin %>'>
						A.one("#<portlet:namespace /><%=HConstants.UNILIMITED_QUANTITY%>Checkbox").on('change',function(e){
							var quantity =  A.one("#<portlet:namespace /><%=HConstants.QUANTITY%>");
							if(e.currentTarget.get('checked')) {
								quantity.set('disabled', true);
							 }else{
							 	quantity.set('disabled', false);	
							 }
						});
					 </c:if>
				});
				
				function  <portlet:namespace />initEditor() {
				    return document.getElementById('<portlet:namespace /><%=HConstants.DESCRIPTION%>').value;
				}
				function showWholeDiv(){
					 A.one("#<portlet:namespace /><%=HConstants.WHOLESALE_DISCOUNT%>Checkbox").on('change',function(e){
						 if(e.currentTarget.get('checked')) {
							 wholeSaleDiv.style.display = 'none';						 
						 }
					 });
				}
				function extractCodeFromEditor() {
				 	 document.<portlet:namespace />itemDetailForm.<portlet:namespace /><%=HConstants.DESCRIPTION%>.value = window.<portlet:namespace />editor.getHTML();
				}
				
				function setCategory(data,categoryId) {
					var A = new AUI();
					var categoryElem = A.one("#<portlet:namespace /><%=HConstants.CATEGORY_ID%>");
					categoryElem.get('children').remove();
					for(x in data) {
						var showSelected = (categoryId > 0) &&  (categoryId == data[x].categoryId) ? 'selected' : '';
						A.Node.create('<option value='+ data[x].categoryId + ' '+ showSelected + '>'+ data[x].categoryName +'</option>').appendTo(categoryElem);
					}
				}
				function fetchCategories(categoryId) {
					AUI().use('aui-base','aui-io-request',function (A) {
						var parentCategoryId =  A.one("#<portlet:namespace /><%=HConstants.PARENT_CATEGORY_ID%>").val();
						A.io.request('<%=getCategoryURL%>', {
							dataType: 'json',
							data: {
								'<portlet:namespace/>parentCategoryId' : parentCategoryId
							   },
							  on: {
							   success: function() {
								   setCategory(this.get('responseData'),categoryId);
							   }
							  }
						});
					});	
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
				
				function calculateSellerEarning(elem) {
					var index = $(elem).data("index");
					var price = $(elem).val();
					var tax = $('#<portlet:namespace/>tax').val();
					var commissionPercent = ((parseFloat($(".commission").val()) + parseFloat(tax)) / 100);
					//var dispStr = price+" - (" +price+" * ( ("+ $(".commission").val() +" + "+tax+") / 100) = " + (price - (price * commissionPercent));
					var dispStr = (price - (price * commissionPercent));
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
			<%
		}else {
			%>
			<liferay-ui:header title='back'  backLabel="go-back" backURL='<%=backURL%>'/>
			<h2>Item Not Exist</h2>
			<%
	}
%>