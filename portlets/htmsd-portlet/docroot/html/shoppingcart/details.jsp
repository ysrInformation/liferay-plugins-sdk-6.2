<%@ include file="/html/shoppingcart/init.jsp"%>

<%
	double totalPrice = 0;

	PortletURL backURL = renderResponse.createRenderURL();
	backURL.setParameter("jspPage", "/html/shoppingcart/view.jsp");
	
	PortletURL checkoutURL = renderResponse.createRenderURL();
	checkoutURL.setParameter("jspPage", "/html/shoppingcart/checkout.jsp");
	
	List<ShoppingItem_Cart> shoppingItem_Carts = CommonUtil.getUserCartItems(themeDisplay.getUserId());
%>

<portlet:resourceURL var="removeItemURL"> 
	<portlet:param name="<%= Constants.CMD %>" value="remove-item"/> 
</portlet:resourceURL>

<liferay-ui:header title="product-details" backURL="<%= backURL.toString() %>"/> 

<% 
if (Validator.isNotNull(shoppingItem_Carts) && shoppingItem_Carts.size() > 0) {
	for (ShoppingItem_Cart shoppingItem_Cart : shoppingItem_Carts) {
		long itemId = shoppingItem_Cart.getItemId();
		ShoppingItem shoppingItem = CommonUtil.getShoppingItem(itemId);
		totalPrice += shoppingItem.getTotalPrice();
		String removeCartItem = "javascript:removeItems('"+shoppingItem.getItemId()+"');";
		String[] imageIdsList = Validator.isNotNull(shoppingItem) ? shoppingItem.getImageIds().split(StringPool.COMMA):new String[0]; 
		%> 
		<div class="itemDetails"> 
			<liferay-ui:panel-container accordion="false" extended="true"> 
				<liferay-ui:panel title="<%= shoppingItem.getName() %>" defaultState="collapsed">
					<aui:column columnWidth="40">
						<%
						int j=1;
						boolean imageExist = false;
						if (Validator.isNotNull(imageIdsList) && imageIdsList.length > 0) {
							for (String imageEntryId :imageIdsList){
								String image_upload_preview = HConstants.IMAGE_UPLOAD_PREVIEW+j;
								String imageURL = CommonUtil.getThumbnailpath(Long.parseLong(imageEntryId), themeDisplay.getScopeGroupId(), false);
								imageExist = (!imageURL.isEmpty())?true:false;
								%><div class="images-disp"><img id="<%= image_upload_preview %>" src="<%= imageURL %>" /></div><%
								j++;
							} 
						}
						%>
						<c:if test='<%= !imageExist %>'>
							<strong><liferay-ui:message key="no-image"/></strong>
						</c:if>
					</aui:column>
					<aui:column columnWidth="60">  
						<div><%= (Validator.isNotNull(shoppingItem))? shoppingItem.getName():"NA" %></div>
						<div><%= (Validator.isNotNull(shoppingItem))?shoppingItem.getDescription():"NA" %></div>
						<div><strong><%= (Validator.isNotNull(shoppingItem))? CommonUtil.getPriceFormat(shoppingItem.getTotalPrice()):0L %></strong></div>
						<div><aui:a href="<%= removeCartItem %>"><liferay-ui:message key="remove"/></aui:a></div>
					</aui:column>
				</liferay-ui:panel>
			</liferay-ui:panel-container>
		</div><%
	} 
}
%>
<aui:fieldset>
	<div class="product-total">
		<div><strong><liferay-ui:message key="total"/></strong></div>
		<div><strong><%= CommonUtil.getPriceFormat(totalPrice) %></strong></div>
	</div>
</aui:fieldset>

<aui:button-row>
	<aui:button type="button" value='<%= LanguageUtil.get(portletConfig, locale, "checkout") %>' href="<%= checkoutURL.toString() %>"/>
	<aui:button type="button" value='<%= LanguageUtil.get(portletConfig, locale, "cancel") %>' href="<%= backURL.toString() %>"/>
</aui:button-row>
	
<style>
.product-total div {
	display: inline-block;
	font-family:serif;
	font-size: large;
}
.images-disp {
	padding: 10px;
	width: 200px;
	height: auto;
}
</style>

<aui:script>
function removeItems(itemId) {
	
	AUI().use('aui-base','aui-io-request', function(A){
		A.io.request('<%= removeItemURL %>',{
			dataType: 'json',
			method: 'POST',
			data: { 
				 itemId :itemId
			},
			on: {
				success: function(obj) {
					window.location.reload();
				},
				failure:function(){
					alert('Internal Error Occured');
				}
			}
		});
	});
}
</aui:script>
