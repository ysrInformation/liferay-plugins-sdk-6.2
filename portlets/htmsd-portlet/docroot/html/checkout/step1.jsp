<%@include file="/html/checkout/init.jsp" %>

<% 
	double grandTotal = 0;
	long userId = themeDisplay.getUserId();
	int itemscount = CommonUtil.getItemsCount(userId, themeDisplay, session);
	List<ShoppingBean> shoppingList = new ArrayList<ShoppingBean>();
	
	String checkOut = StringPool.BLANK;
	PortletURL checkoutURL = renderResponse.createRenderURL();
	checkoutURL.setParameter("jspPage", "/html/checkout/view.jsp");
	checkoutURL.setParameter("order_step", "step3");
	
	PortletURL detailsURL = renderResponse.createRenderURL();
	detailsURL.setWindowState(LiferayWindowState.POP_UP);
	detailsURL.setParameter("jspPage", "/html/shoppinglist/details.jsp");
	
	String currentCurrencyid = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	long currencyId2 = (Validator.isNull(currentCurrencyid)) ?  0 : Long.valueOf(currentCurrencyid);
	String currencySymbol = CommonUtil.getCurrencySymbol(Long.valueOf(currencyId2));
	double currentRate = CommonUtil.getCurrentRate(Long.valueOf(currencyId2));
%>

<portlet:resourceURL var="updateTotalPriceURL"> 
	<portlet:param name="<%= Constants.CMD %>" value="total-price"/> 
</portlet:resourceURL>

<portlet:resourceURL var="removeItemURL"> 
	<portlet:param name="<%= Constants.CMD %>" value="remove-item"/> 
</portlet:resourceURL>

<liferay-portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="loginURL" portletName="<%= PortletKeys.LOGIN %>">
	<portlet:param name="struts_action" value="/login/login" />
	<portlet:param name="redirect" value="<%= checkoutURL.toString() %>" />
	<portlet:param name="portletResource" value="<%= portletDisplay.getId() %>" />
	<portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>" />
</liferay-portlet:renderURL>

<%
	if (themeDisplay.isSignedIn()) {
		checkOut = checkoutURL.toString();
		shoppingList = CommonUtil.getSignedInUserItems(userId);
	} else {
		checkOut = loginURL;
		shoppingList = CommonUtil.getGuestUserList(session);
	}
%>

<c:choose>
	<c:when test='<%= itemscount > 0 %>'>
		<p>
			<liferay-ui:message key="your-shopping-cart-contains"/>
			<span id="checkout-item-count"><%= itemscount %> <liferay-ui:message key="checkout-product"/></span>
		</p>
		<div id="order-detail-content">
			<table class="cart-summary">
				<thead>
					<tr>
						<th><liferay-ui:message key="checkout-label-product"/></th>
						<th><liferay-ui:message key="checkout-label-description"/></th>
						<th><liferay-ui:message key="checkout-label-availability"/></th>
						<th><liferay-ui:message key="checkout-label-unit-price"/></th>
						<th><liferay-ui:message key="checkout-label-quantity"/></th>
						<th></th>
						<th><liferay-ui:message key="checkout-label-total"/></th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="<%= Validator.isNotNull(shoppingList) && shoppingList.size() > 0 %>">
							<% for (ShoppingBean _shpcart : shoppingList) {
								boolean inStock = true;
								long itemId = _shpcart.getItemId();
								int quantity[] = CommonUtil.getItemsQuantity(itemId);
								double unitPrice = (currentRate == 0) ? _shpcart.getUnitPrice() : _shpcart.getUnitPrice() / currentRate;
								double totalPrice = (currentRate == 0) ? _shpcart.getTotalPrice() : _shpcart.getTotalPrice() / currentRate;
								grandTotal += totalPrice;
								ShoppingItem shpItem = CommonUtil.getShoppingItem(_shpcart.getItemId());
								%>
								<tr>
									<td>
										<div class="checkout-product-img">
											<c:choose>
												<c:when test='<%= Validator.isNotNull(shpItem) && _shpcart.getImageId() > 0 %>'>
													<a class="checkoutProductImage" href="#" data-url='<%= detailsURL.toString()+"&_8_WAR_htmsdportlet_cmd=itemsDetails&_8_WAR_htmsdportlet_itemId="+itemId %>'>
														<img width="45" height="51" src='<%= CommonUtil.getThumbnailpath(_shpcart.getImageId(), themeDisplay.getScopeGroupId(), false) %>' />
													</a>												
												</c:when>
												<c:otherwise>
													<p><liferay-ui:message key="no-image"/></p>
												</c:otherwise>
											</c:choose>
										</div>
									</td>
									<td>
										<div class="checkout-item-description">
											<p class="checkout-prdName"><%= _shpcart.getProductName() %></p>
											<div class="checkout-item-code"> 
												<p>Product Code : <span><%= _shpcart.getProductCode() %></span></p>
											</div>
										</div>
									</td>
									<td>
										<div class="checkout-availability">
											<c:if test='<%= inStock %>'><liferay-ui:message key="checkout-in-stock"/></c:if>
										</div>
									</td>
									<td>
										<div class="checkout-unitprice">
											<span><%= CommonUtil.getPriceFormat(unitPrice, currencyId2) %></span> 
										</div>
									</td>
									<td>
										<div class="checkout-qty">
											<input type="hidden" id="quantityData<%=_shpcart.getItemId() %>" value="<%= _shpcart.getCartItemId() %>" data-price="<%= shpItem.getTotalPrice() %>"/>  
											<select class="cart-qty checkout-quantity" name="productQuantity" data-id="<%= _shpcart.getItemId()%>">
												<% for (int i=0; i<quantity.length; i++) { 
													boolean isSelected = (Validator.isNotNull(_shpcart) && _shpcart.getQuantity() == quantity[i]);
													%> 
													<aui:option value="<%= quantity[i] %>" selected="<%= isSelected %>" label="<%= quantity[i] %>"/>
												<% } %>
											</select>
										</div>
									</td>
									<td>
										<div class="checkout-cancel-item">
											<a class="cancelItemLink" href="#" data-id="<%= shpItem.getItemId() %>"><i class="icon-cancel icon-small">X</i></a>
										</div>
									</td>
									<td>
										<div class="checkout-totalPrice">
											<span><%= CommonUtil.getPriceFormat(totalPrice, currencyId2) %></span> 
										</div>
									</td>
								</tr>
							<% } %>
						</c:when>
						<c:otherwise>
							<tr><liferay-ui:message key="checkout-no-items-in-your-cart"/></tr>
						</c:otherwise>
					</c:choose>
				</tbody>		
			</table>	
			<div class="clearfix"> 
				<table id="cart-summary-total">
					<tbody>
						<tr>
							<td><span><liferay-ui:message key="checkout-label-Total"/></span></td>
							<td><div class="checkout-totalprice"><%= CommonUtil.getPriceInNumberFormat(grandTotal, currencySymbol) %></div></td>
						</tr>
					</tbody>
				</table>
			</div>
			<aui:button-row>
				<aui:button cssClass="pull-left" type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "continue-shopping") %>' href="#"/>
				<aui:button cssClass="pull-right" type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "step1") %>' href="<%= checkOut %>"/>
			</aui:button-row>
		</div>
	</c:when>
	<c:otherwise>
		<p class="newcart-alert newcartalert-warning"><liferay-ui:message key="your-shopping-cart-is-empty"/></p>
	</c:otherwise>
</c:choose>

<script>
$(function(){
	$(".checkoutProductImage").click(function(){
		var $elem = $(this), url = $elem.attr("data-url");
		showPageInPopup(url, 800, 1200,'Product Details');
	});
	
	$(".cancelItemLink").click(function(){
		var url = '<%= removeItemURL %>';
		var itemId = $(this).attr("data-id");
		$.ajax({
			url : url,
			type : "GET",
			data : {
				<portlet:namespace/>itemId:itemId,
			},
			success : function(data) {
				Liferay.Portlet.refresh('#p_p_id_8_WAR_htmsdportlet_');
			},
			error : function() {
				alert("Something went wrong !!Please try again..");
			}
		});
	});
	
	$(".checkout-quantity").change(function(){
		var $elem = $(this);
		var quantity = $elem.val(), itemid = $elem.attr("data-id");
		var qtyData = $("#quantityData"+itemid);
		var Id = qtyData.val(), price = qtyData.attr("data-price"), url = '<%= updateTotalPriceURL %>';
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
				Liferay.Portlet.refresh('#p_p_id_8_WAR_htmsdportlet_');
			},
			error : function() {
				alert("Something went wrong !!Please try again..");
			}
		});
	});
});
</script>