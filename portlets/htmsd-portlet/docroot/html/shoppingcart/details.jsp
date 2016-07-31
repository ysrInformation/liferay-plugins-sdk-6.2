<%@ include file="/html/shoppingcart/init.jsp"%>

<%
	double totalPrice = 0;
	
	PortletURL detailsURL = renderResponse.createRenderURL();
	detailsURL.setWindowState(LiferayWindowState.POP_UP);
	detailsURL.setParameter("jspPage", "/html/shoppinglist/details.jsp");
	
	PortletURL checkoutURL = renderResponse.createRenderURL();
	checkoutURL.setParameter("jspPage", "/html/shoppingcart/checkout.jsp");

	String currentCurrencyid = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	long currencyId2 = (Validator.isNull(currentCurrencyid)) ?  0 : Long.valueOf(currentCurrencyid);
	String currencySymbol = CommonUtil.getCurrencySymbol(Long.valueOf(currencyId2));
%>

<portlet:resourceURL var="removeItemURL"> 
	<portlet:param name="<%= Constants.CMD %>" value="remove-item"/> 
</portlet:resourceURL>

<portlet:resourceURL var="getTotalPriceURL"> 
	<portlet:param name="<%= Constants.CMD %>" value="total-price"/> 
</portlet:resourceURL>

<liferay-portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="loginURL" portletName="<%= PortletKeys.LOGIN %>">
	<portlet:param name="struts_action" value="/login/login" />
	<portlet:param name="redirect" value="<%= checkoutURL.toString() %>" />
	<portlet:param name="portletResource" value="<%= portletDisplay.getId() %>" />
	<portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>" />
</liferay-portlet:renderURL>

<% 
	boolean itemExist = false;
	String checkOutURL = StringPool.BLANK;
	List<ShoppingBean> shoppingItemsList = new ArrayList<ShoppingBean>();
	if (themeDisplay.isSignedIn()) {
		checkOutURL = checkoutURL.toString();
		shoppingItemsList = CommonUtil.getSignedInUserItems(themeDisplay.getUserId());;
	} else {
		checkOutURL = loginURL;
		shoppingItemsList = CommonUtil.getGuestUserList(session);
	}
	String continueShoppingURL = themeDisplay.getPortalURL()+"/web/guest/exotic-pet-birds";
	String tdstyle = (themeDisplay.isSignedIn()) ? "style='width:40%'" : "style='width:60%'"; 
%>

<div class="cart-details-page">
	<c:choose>
		<c:when test='<%= (Validator.isNotNull(shoppingItemsList) && shoppingItemsList.size() > 0) %>'>
			<table width="100%" class="cart-table">
				<thead class="cart-head">
					<tr class="cart-head-row">
						<td <%= tdstyle %>><liferay-ui:message key="shopping-item"/></td> 
						<td style="width:20%"><liferay-ui:message key="shopping-qty"/></td>
						<td style="width:20%"><liferay-ui:message key="shopping-price"/></td>
						<c:if test='<%= themeDisplay.isSignedIn() %>'> 
							<td style="width:20%"></td>
						</c:if>
					</tr>
				</thead>
				<% 
				for (ShoppingBean shpCtItem:shoppingItemsList) { 
					long itemId = shpCtItem.getItemId();
					long cartId = shpCtItem.getCartId();
					ShoppingItem shoppingItem = CommonUtil.getShoppingItem(itemId);
					if (Validator.isNotNull(shoppingItem)) {
						itemExist = true;
						int quantity[] = CommonUtil.getItemsQuantity(itemId);
						int qtyLength = quantity.length;
						double currentRate = CommonUtil.getCurrentRate(Long.valueOf(currencyId2));
						double price = (currentRate == 0) ? shpCtItem.getTotalPrice() : shpCtItem.getTotalPrice() / currentRate;
						totalPrice += price;
						String updatetotalPrice = "javascript:updatetotalPrice(this.value,'"+shpCtItem.getCartItemId()+"','"+getTotalPriceURL+"','"+shoppingItem.getTotalPrice()+"','"+itemId+"');";
						String removeCartItem = "javascript:removeItems('"+shoppingItem.getItemId()+"','"+removeItemURL+"');";
						String confirmCheckOut = "javascript:confirmCheckout('"+checkOutURL+"','"+shpCtItem.getCartItemId()+"')"; 
						%>
						<tbody>
							<tr>
								<td class="product-info" <%= tdstyle %>> 
									<div >
										<div class="row-fluid">
											<div class="span4">
												<c:choose>
													<c:when test="<%= (shpCtItem.getImageId() > 0) %>">
														<%
														boolean imageExist = false;
														String image_upload_preview = HConstants.IMAGE_UPLOAD_PREVIEW+1;
														String imageURL = CommonUtil.getThumbnailpath(shpCtItem.getImageId(), themeDisplay.getScopeGroupId(), false);
														imageExist = (!imageURL.isEmpty())?true:false;
														detailsURL.setParameter(HConstants.ITEM_ID, String.valueOf(itemId));
														detailsURL.setParameter(Constants.CMD, "itemsDetails");
														String showInPopup = "javascript:showPopup('"+detailsURL.toString()+"','800','1200','Product Details');";
														%>
														<a href="<%= showInPopup %>">
															<img width="100" height="100" src="<%= imageURL %>" style="margin-top:5%;">
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
								<td style="width:20%;">
									<div >
										<aui:select name="quantity"  label="" cssClass="cart-qty" onChange="<%= updatetotalPrice  %>">
											<% for (int j=0;j<qtyLength;j++) { %>
												<aui:option value="<%= quantity[j] %>" label="<%= quantity[j] %>"
													selected='<%= (Validator.isNotNull(shpCtItem) && shpCtItem.getQuantity() == quantity[j] ) %>'/>
											<% } %>
										</aui:select> 
									</div>
								</td>
								<td style="width:20%">
									<div>
										<h5><%= CommonUtil.getPriceFormat(price, currencyId2) %></h5>
									</div>
								</td>
								<c:if test='<%= themeDisplay.isSignedIn() %>'> 
									<td style="width:20%">
										<div>
											<aui:button type="button" cssClass="btn-primary" value='<%= LanguageUtil.get(portletConfig, locale, "place-order") %>' onClick="<%= confirmCheckOut %>"/>					
										</div>
									</td>
								</c:if>
							</tr>
						</tbody><% 
					}
				} 
				%>
				<c:if test='<%= !itemExist %>'>
					<tr><td colspan="<%= (themeDisplay.isSignedIn() ? 4 : 3) %>"><liferay-ui:message key="no-items-in-cart"/></td></tr>
				</c:if>
				<tfoot class="cart-footer">
					<tr>
						<td style="width:20%" colspan="<%= (themeDisplay.isSignedIn() ? 4 : 3) %>">
							<div class="pull-right price-div">
								<span class="price-text"><liferay-ui:message key="amount-payable"/>:</span> 
								<span class="price"><%= CommonUtil.getPriceInNumberFormat(totalPrice, currencySymbol) %></span> 
							</div>
						</td>
					</tr>
				</tfoot>
			</table>
			<c:if test='<%= itemExist %>'>
				<aui:button-row cssClass="pull-right">
					<aui:button type="button" value='<%= LanguageUtil.get(portletConfig, locale, "continue-shopping") %>' href="<%= continueShoppingURL %>"/>
					<aui:button type="button" cssClass="btn-primary" value='<%= LanguageUtil.get(portletConfig, locale, "place-order") %>' href="<%= checkOutURL %>"/>
				</aui:button-row>
			</c:if>
		</c:when>
		<c:otherwise>
			<div class="mycart">
				<img src="<%=request.getContextPath()%>/images/cart-tray.png" style="width: 100px; height: auto"/>
			</div>
			<p class="mycart"><liferay-ui:message key="no-items-in-cart"/></p> 
		</c:otherwise>
	</c:choose>
	
	<div id="loader-icon" style="display:none;"> 
		<img src="<%=request.getContextPath()%>/images/loader.gif" style="width: 100px; height: 100px"/>
	</div>
</div>

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

function updatetotalPrice(quantity, Id, url, price, itemid) {
	
	$.ajax({
		url : url,
		type : "GET",
		data : {
			<portlet:namespace/>quantity:quantity,
			<portlet:namespace/>id:Id,
			<portlet:namespace/>itemPrice:price,
			<portlet:namespace/>itemId:itemid
		},
		beforeSend : function() {
			$('#loader-icon').show();
		},
		complete : function() {
			$('#loader-icon').hide();
		},
		success : function(data) {
			Liferay.Portlet.refresh('#p_p_id_4_WAR_htmsdportlet_');
		},
		error : function() {
			alert("Something went wrong !!Please try again..");
		}
	});
}

function confirmCheckout(url, id) {	
	window.location.href = 	url + '&<portlet:namespace/>shoppingCartItemId='+id+ '&<portlet:namespace/>singleCheckout='+true; 
}
</aui:script>