<%@include file="/html/checkout/init.jsp" %>

<portlet:actionURL var="confirmCheckoutURL" name="confirmOrder"/>

<% 
	double grandTotal = 0;
	long userId = themeDisplay.getUserId();
	int itemscount = CommonUtil.getItemsCount(userId, themeDisplay, session);
	List<ShoppingBean> shoppingList = new ArrayList<ShoppingBean>();
	
	PortletURL detailsURL = renderResponse.createRenderURL();
	detailsURL.setWindowState(LiferayWindowState.POP_UP);
	detailsURL.setParameter("jspPage", "/html/shoppinglist/details.jsp");
	
	String currentCurrencyid = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	long currencyId2 = (Validator.isNull(currentCurrencyid)) ?  0 : Long.valueOf(currentCurrencyid);
	String currencySymbol = CommonUtil.getCurrencySymbol(Long.valueOf(currencyId2));
	double currentRate = CommonUtil.getCurrentRate(Long.valueOf(currencyId2));
	
	if (themeDisplay.isSignedIn()) {
		shoppingList = CommonUtil.getSignedInUserItems(userId);
	} else {
		shoppingList = CommonUtil.getGuestUserList(session);
	}
	
	UserInfo _userInfo = UserInfoLocalServiceUtil.getUserInfoByUserIdAndIsDelivery(userId, true);
	Address address = CommonUtil.getAddressByAddressId(_userInfo.getShippingAddressId());
	String zip = (Validator.isNotNull(address.getZip())) ? address.getZip() : StringPool.BLANK;
	String firstName = Validator.isNotNull(_userInfo.getFirstName()) ? _userInfo.getFirstName() : StringPool.BLANK;
	String lastName = Validator.isNotNull(_userInfo.getLastName()) ? _userInfo.getLastName() : StringPool.BLANK;
%>

<aui:form id="checkout-step5" name="confirmOrder" method="post" action="${confirmCheckoutURL}" inlineLabels="<%= true %>"> 
	<div id="order-detail-content">
		<table class="cart-summary">
			<thead>
				<tr>
					<th><liferay-ui:message key="checkout-label-product"/></th>
					<th><liferay-ui:message key="checkout-label-description"/></th>
					<th><liferay-ui:message key="checkout-label-availability"/></th>
					<th><liferay-ui:message key="checkout-label-unit-price"/></th>
					<th><liferay-ui:message key="checkout-label-quantity"/></th>
					<th><liferay-ui:message key="checkout-label-total"/></th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="<%= Validator.isNotNull(shoppingList) && shoppingList.size() > 0 %>">
						<% for (ShoppingBean _shpcart : shoppingList) {
							boolean inStock = true;
							long itemId = _shpcart.getItemId();
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
										<span><%= _shpcart.getQuantity() %></span>
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
			<tfoot>
				<tr>
					<td colspan="4"><span><liferay-ui:message key="checkout-label-Total"/></span></td>
					<td colspan="2"><div class="checkout-totalprice"><%= CommonUtil.getPriceInNumberFormat(grandTotal, currencySymbol) %></div></td>  
				</tr>
			</tfoot>		
		</table>
	</div>
	
	<div class="row-fluid address-payment-div">
		<div class="span6">
			<ul class="address first_item">
				<li>
					<h1 class="page-heading">
						<liferay-ui:message key="checkout-label-your-shipping-address"/>
					</h1>
				</li>
				<li><span class="address_name"> <%= firstName + StringPool.SPACE + lastName %> </span></li>
				<li><span class="address_street"><%= Validator.isNotNull(address.getStreet1()) ? address.getStreet1() : StringPool.BLANK %></span></li>
				<li><span class="address_city"><%= Validator.isNotNull(address.getCity()) ? address.getCity() : StringPool.BLANK %></span></li>
				<li><span class=""><%= Validator.isNotNull(address.getRegionId()) ? CommonUtil.getState(address.getRegionId()) : StringPool.BLANK %></span></li>
				<li><span class="address_phone"><%= Validator.isNotNull(address.getCountryId()) ? CommonUtil.getCountry(address.getCountryId()) : StringPool.BLANK  + ", " + zip  %> </span></li>
				<li><span class="address_phone_mobile"><%= Validator.isNotNull(_userInfo.getMobileNumber()) ?  _userInfo.getMobileNumber(): StringPool.BLANK %></span></li>
				<li><span class="address_alt_mobile"><%= Validator.isNotNull(_userInfo.getAltNumber()) ? _userInfo.getAltNumber() : StringPool.BLANK %></span></li>
			</ul>
		</div>
		<div class="span6 payment-methods">
			<div class="payment-methods-label"> 
				<h1 class="page-heading"><liferay-ui:message key="checkout-payment-methods"/></h1>
			</div>
			<div class="paymentOptions">
				<aui:input name="paymentMethod" type="radio" label="checkout-label-debitcard" value="debit" disabled="true">
					<aui:validator name="required"/>
				</aui:input>
				<aui:input name="paymentMethod" type="radio" label="checkout-label-creditcard" value="credit" disabled="true">
					<aui:validator name="required"/>
				</aui:input>
				<aui:input name="paymentMethod" type="radio" label="checkout-label-cod" value="cod">
					<aui:validator name="required"/>
				</aui:input>
			</div>
		</div>	
	</div>

	<aui:button-row>
		<aui:button cssClass="pull-right" type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "step5") %>'/>
	</aui:button-row>
</aui:form>

<script type="text/javascript"> 
$(function(){
	$(".checkoutProductImage").click(function(){
		var $elem = $(this), url = $elem.attr("data-url");
		showPageInPopup(url, 800, 1200,'Product Details');
	});
});
</script>