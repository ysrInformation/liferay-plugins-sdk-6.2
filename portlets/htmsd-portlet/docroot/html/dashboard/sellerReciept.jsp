<%@page import="com.htmsd.slayer.service.CategoryLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.model.Category"%>
<%@page import="com.htmsd.slayer.service.CommissionLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.service.SellerLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.model.Seller"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@ include file="/html/orderpanel/init.jsp"%>

<head>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script src='<%= request.getContextPath() + "/js/jquery-barcode.min.js"%>' ></script>
	<script src='<%= request.getContextPath() + "/js/jquery-barcode.js"%>' ></script>
</head>

<%
	int slNo = 1;
	long orderId = ParamUtil.getLong(request, "orderId");
	
	ShoppingOrder shoppingOrder = null;
	try {
		if (Validator.isNotNull(orderId)) {
			shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);	
		}
	} catch (Exception e){
		System.out.println("No order exist"+e);
	}
	
	Seller seller = SellerLocalServiceUtil.getSellerByUserId(themeDisplay.getUserId());
	String[] billingAddressArray = CommonUtil.getUserAddress(shoppingOrder.getSellerId());
	String shippingAddress = CommonUtil.getAddressBuilder(Long.parseLong(shoppingOrder.getShippingState()), Long.parseLong(shoppingOrder.getShippingCountry()), 
			shoppingOrder.getShippingStreet(), shoppingOrder.getShippingCity(), shoppingOrder.getShippingZip());
	String billingAddress = CommonUtil.getAddressBuilder(Long.parseLong(billingAddressArray[4]), Long.parseLong(billingAddressArray[3]), 
			billingAddressArray[0], billingAddressArray[1], billingAddressArray[2]);

	String emailAddress = shoppingOrder.getShippingEmailAddress();
	String fullName = shoppingOrder.getShippingFirstName().concat(StringPool.SPACE).concat(shoppingOrder.getShippingLastName());
	String altNumber = (Validator.isNotNull(shoppingOrder.getShippingAltMoble()) ? shoppingOrder.getShippingAltMoble():"N/A");
	String mobileNumber = (Validator.isNotNull(shoppingOrder.getShippingMoble()) ? shoppingOrder.getShippingMoble():"N/A");

	DecimalFormat decimalFormat = new DecimalFormat("0.00");
	ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId());
	
	String val2 = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	long currencyId1 = (Validator.isNull(val2)) ?  0 : Long.valueOf(val2);
	String currencySymbol = CommonUtil.getCurrencySymbol(Long.valueOf(currencyId1));
	
	double deliveryCharges = 0.0;
	double currentRate = CommonUtil.getCurrentRate(Long.valueOf(currencyId1));
	int quantity = shoppingOrder.getQuantity();
	double unitPrice = CommonUtil.getSellerPriceByCategory(shoppingItem.getSellingPrice(), shoppingItem.getItemId()); 
	double unitPricetax = (unitPrice * shoppingItem.getTax()) / 100;
	double subTotalPrice = (currentRate == 0) ? (unitPrice * quantity) : (unitPrice * quantity) / currentRate;
	double tax = (subTotalPrice * shoppingItem.getTax()) / 100;
	double subTotal = subTotalPrice - tax;
	double totalPrice = CommonUtil.getSellerPriceByCategory(shoppingOrder.getTotalPrice(), shoppingItem.getItemId()); 
	tax = (currentRate == 0) ? tax : tax / currentRate;
	subTotal = (currentRate == 0) ? subTotal : subTotal / currentRate;
	String productCode = Validator.isNotNull(shoppingItem) ? shoppingItem.getProductCode() : "N/A";
	String itemName = Validator.isNotNull(shoppingItem) ? shoppingItem.getName() : "N/A"; 
	String productDetails = productCode +"<br/>"+ itemName;
%>

<c:choose>
	<c:when test='<%= Validator.isNotNull(seller) && Validator.isNotNull(shoppingOrder)  %>'> 
		<div id="print-area">
			<aui:input id="barCode" name="barCode" type="hidden" value="<%= productCode %>"/> 
			<div class="print-btn">
				<liferay-ui:icon-menu>
					<liferay-ui:icon image="print" url="javascript:printArticle();" message="print"  />
				</liferay-ui:icon-menu>	
			</div>
			
			<div class="company-details text-center">
				<div>
					<h2><%= seller.getName() %></h2>
				</div>
				<div class="company-address">
					<span><%= billingAddress %></span> 
				</div>
				<div class="company-tin">
					<span><liferay-ui:message key="tin"/> : <%= seller.getTIN() %></span>
				</div>
			</div>
			<div class="row-fluid txtmargin">
				<div class="span6">
					<strong><liferay-ui:message key="to"/></strong>
					<div>
						<h2><liferay-ui:message key="a2zali-company"/></h2>
					</div>
					<div class="company-address">
						<span><liferay-ui:message key="company-address"/></span> 
					</div>
				</div>
				<div class="span6 text-right">
					<div>
						<span><liferay-ui:message key="bill-no"/> : <span style="visibility: hidden;"><%= CommonUtil.getBillNumber(shoppingOrder.getOrderId()) %></span></span> 
					</div>
					<div>
						<span><liferay-ui:message key="date"/> : <%= HConstants.DATE_FORMAT.format(shoppingOrder.getCreateDate()) %></span>
					</div>
				</div>
			</div>

			<div class="order-details"> 
				<table class="orderTable">
					<thead>
						<tr>
							<th><liferay-ui:message key="no"/></th>
							<th><liferay-ui:message key="particulars"/></th>
							<th><liferay-ui:message key="checkout-label-unit-price"/></th>
							<th><liferay-ui:message key="quantity"/></th>
							<th><liferay-ui:message key="total"/></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><%= slNo %></td>
							<td>
								<%= productDetails %>
								<c:if test='<%= Validator.isNotNull(shoppingItem) && shoppingItem.getSmallImage() > 0 %>'> 
									<div class="orderdetails-image">
										<img  src="<%= CommonUtil.getThumbnailpath(shoppingItem.getSmallImage(), themeDisplay.getScopeGroupId(), false) %>">
									</div>
								</c:if>
							</td>
							<td class="text-center"><%= decimalFormat.format(unitPrice - unitPricetax) %></td>
							<td class="text-center"><%= quantity %></td>
							<td class="text-center"><%= decimalFormat.format(subTotal) %></td>
						</tr>
						<tr>
							<td class="noBorder" colspan="3"></td>
							<td class="text-right heading"><liferay-ui:message key="sub-total"/></td>
							<td class="text-right"><%= decimalFormat.format(subTotal) %></td>
						</tr>
						<tr>
							<td class="noBorder" colspan="3"></td>
							<td class="text-right heading"><liferay-ui:message key="tax"/></td>
							<td class="text-right"><%= decimalFormat.format(tax) %></td>
						</tr>
						<tr>
							<td class="noBorder" colspan="3"></td>
							<td class="text-right heading"><liferay-ui:message key="total"/></td>
							<td class="heading text-right"><%= CommonUtil.getPriceInNumberFormat(totalPrice, currencySymbol) %></td>
						</tr>
					</tbody>	
				</table>
			</div>
		</div>
		<aui:script>
			$(document).ready(function(){
				var barCodeNumber = $("#<portlet:namespace/>barCode").val();
				<c:if test="<%= (productCode.length() == HConstants.BAR_CODE_LENGTH) %>">
					$("#bcTarget").barcode(barCodeNumber, "ean13");
				</c:if>
			});
			
			function printArticle() {
				print();
			}
		</aui:script>
	</c:when>
	<c:otherwise>
		<div class="text-center">
			<h2><liferay-ui:message key="item-not-found"/></h2>
		</div>
	</c:otherwise>
</c:choose>