<%@ include file="/html/shoppingcart/init.jsp"%>

<%
	int i=0;
	double totalPrice = 0;
	
	PortletURL detailsURL = renderResponse.createRenderURL();
	detailsURL.setWindowState(LiferayWindowState.POP_UP);
	detailsURL.setParameter("jspPage", "/html/shoppinglist/details.jsp");
	
	PortletURL checkoutURL = renderResponse.createRenderURL();
	checkoutURL.setParameter("jspPage", "/html/shoppingcart/checkout.jsp");
	
	List<ShoppingItem_Cart> shoppingItem_Carts = CommonUtil.getUserCartItems(themeDisplay.getUserId());
%>

<portlet:resourceURL var="removeItemURL"> 
	<portlet:param name="<%= Constants.CMD %>" value="remove-item"/> 
</portlet:resourceURL>

<liferay-portlet:renderURL  var="loginURL" portletName="<%= PortletKeys.LOGIN %>">
	<portlet:param name="struts_action" value="/login/login" />
	<portlet:param name="redirect" value="<%= checkoutURL.toString() %>" />
	<portlet:param name="portletResource" value="<%= portletDisplay.getId() %>" />
	<portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>" />
</liferay-portlet:renderURL>

<% 
	String checkOutURL = (themeDisplay.isSignedIn()) ? checkoutURL.toString():loginURL;
%>

<c:choose>
	<c:when test='<%= (Validator.isNotNull(shoppingItem_Carts) && shoppingItem_Carts.size() > 0) %>'>
		<table width="100%" class="cart-table">
			<thead class="cart-head">
				<tr class="cart-head-row">
					<td style="width 60%"><liferay-ui:message key="shopping-item"/></td>
					<td style="width 20%"><liferay-ui:message key="shopping-qty"/></td>
					<td style="width 20%"><liferay-ui:message key="shopping-price"/></td>
				</tr>
			</thead>
			<% 
			for (ShoppingItem_Cart shpCtItem:shoppingItem_Carts) { 
				long itemId = shpCtItem.getItemId();
				ShoppingItem shoppingItem = CommonUtil.getShoppingItem(itemId);
				double price = shoppingItem.getTotalPrice();
				totalPrice += price;
				String removeCartItem = "javascript:removeItems('"+shoppingItem.getItemId()+"','"+removeItemURL+"');";
				String[] imageIdsList = Validator.isNotNull(shoppingItem) ? shoppingItem.getImageIds().split(StringPool.COMMA):new String[]{}; 
				%>
				<tbody>
					<tr>
						<td class="product-info" style="width 60%">
							<div >
								<div class="row-fluid">
									<div class="span4">
										<c:choose>
											<c:when test="<%= (Validator.isNotNull(imageIdsList) && imageIdsList.length > 0) %>">
												<%
												boolean imageExist = false;
												String image_upload_preview = HConstants.IMAGE_UPLOAD_PREVIEW+1;
												String imageURL = CommonUtil.getThumbnailpath(Long.parseLong(imageIdsList[0]), themeDisplay.getScopeGroupId(), false);
												imageExist = (!imageURL.isEmpty())?true:false;
												detailsURL.setParameter(HConstants.ITEM_ID, String.valueOf(itemId));
												detailsURL.setParameter(Constants.CMD, "itemsDetails");
												String showInPopup = "javascript:showPopup('"+detailsURL.toString()+"','800','1200','Product Details');";
												%>
												<a href="<%= showInPopup %>">
													<img class="product-image" width="100" height="100" src="<%= imageURL %>">
												</a>
												<c:if test="<%= !imageExist %>"><liferay-ui:message key="no-image"/></c:if>
											</c:when>
											<c:otherwise>
												<h2><liferay-ui:message key="no-image"/></h2>
											</c:otherwise>
										</c:choose>
									</div>
									<div class="span8 product-description">
										<label><%= shoppingItem.getProductCode()+"<br/>"+shoppingItem.getName() %></label>
									</div>
								</div>	
								<div class="pull-right">
									<div class="remove-item">
										<a  href="<%= removeCartItem %>"><liferay-ui:message key="remove-item"/></a>
									</div>
								</div>
							</div>
						</td>
						<td style="width 20%">
							<div >
								<aui:input name="quantity"  label="" value="<%= shpCtItem.getQuantity() %>" cssClass="cart-qty"/> 
							</div>
						</td>
						<td style="width 20%">
							<div >
								<h5><%= CommonUtil.getPriceFormat(price) %></h5>
							</div>
						</td>
					</tr>
				</tbody><% 
			} 
			%>
			<tfoot class="cart-footer">
				<tr>
					<td style="width 20%" colspan="3">
						<div class="pull-right price-div">
							<span class="price-text"><liferay-ui:message key="amount-payable"/>:</span> 
							<span class="price"><%= CommonUtil.getPriceFormat(totalPrice) %></span>
						</div>
					</td>
				</tr>
			</tfoot>
		</table>
		<aui:button-row cssClass="pull-right">
			<aui:button type="button" value='<%= LanguageUtil.get(portletConfig, locale, "place-order") %>' href="<%= checkOutURL %>"/>
		</aui:button-row>
	</c:when>
	<c:otherwise>
		<h2><liferay-ui:message key="no-items-to-display"/></h2>
	</c:otherwise>
</c:choose>

<aui:script>
function removeItems(itemId, url) {
	
	var A = AUI();
	A.use('aui-io-request', function(A) {
		A.io.request(url, {
			method : 'POST',
			dataType : 'html',
			data : {
				<portlet:namespace/>itemId:itemId,
			},
			sync : false,
			on : {
	         	success : function() {
	         		location.reload();
				},
	       		failure : function() {
	        		alert("Something went wrong !!Please try again..");
				}
			}
	 	});
	});
}

function showPopup(url, height, width, title){
	
	AUI().use('aui-base','liferay-util-window','aui-io-plugin-deprecated',function(A){
		  
    	var popup = Liferay.Util.Window.getWindow(
                {
                    dialog: {
                        centered: true,
                        constrain2view: true,
                        modal: true,
                        resizable: false,
                        width: width
                    }
                }).plug(A.Plugin.DialogIframe,
                     {
                     autoLoad: true,
                     iframeCssClass: 'dialog-iframe',
                     uri:url
                }).render();
    		popup.show();
    		popup.titleNode.html(title);
	});
}
</aui:script>
