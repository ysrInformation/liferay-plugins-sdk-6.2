<%@ include file="/html/orderpanel/init.jsp"%>

<%@page import="com.htmsd.slayer.model.Category"%>
<%@page import="com.htmsd.slayer.service.ShoppingItemLocalServiceUtil"%>

<head>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script src='<%= request.getContextPath() + "/js/jquery-barcode.min.js"%>' ></script>
	<script src='<%= request.getContextPath() + "/js/jquery-barcode.js"%>' ></script>
</head>
	
<%
	long orderId = ParamUtil.getLong(request, "orderId");
	
	ShoppingOrder shoppingOrder = null;
	try {
		if (Validator.isNotNull(orderId)) {
			shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);	
		}
	} catch (Exception e){
		System.out.println("No order exist"+e);
	}
	
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
	Category parentCategory = CommonUtil.getShoppingItemParentCategory(shoppingOrder.getShoppingItemId());
	Category category = ShoppingItemLocalServiceUtil.getShoppingItemCategory(shoppingOrder.getShoppingItemId());
	String val2 = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	long currencyId1 = (Validator.isNull(val2)) ?  0 : Long.valueOf(val2);
	String currencySymbol = CommonUtil.getCurrencySymbol(Long.valueOf(currencyId1));
	
	double currentRate = CommonUtil.getCurrentRate(Long.valueOf(currencyId1));
	boolean isLiveCategory = Validator.isNotNull(parentCategory) && parentCategory.getName().equals("Live");
	/* int colspan = (isLiveCategory) ? 4 : 6; */
	int colspan = 6;
	int quantity = shoppingOrder.getQuantity();
	double totalPrice = (currentRate == 0) ? shoppingOrder.getTotalPrice() :shoppingOrder.getTotalPrice() / currentRate;
	double tax = CommonUtil.calculateVat(totalPrice, (Validator.isNotNull(shoppingItem)) ? shoppingItem.getTax() : 0);
	//double subTotal = shoppingOrder.getTotalPrice() - tax;
	double subTotal = Validator.isNotNull(shoppingItem) ? shoppingItem.getTotalPrice() : 0;
	tax = (currentRate == 0) ? tax : tax / currentRate;
	subTotal = (currentRate == 0) ? subTotal : subTotal / currentRate;
	String productCode = Validator.isNotNull(shoppingItem) ? shoppingItem.getProductCode() : "N/A";
	String itemName = Validator.isNotNull(shoppingItem) ? shoppingItem.getName() : "N/A"; 
	String productDetails = productCode +"<br/>"+ itemName;
	String productType = (Validator.isNotNull(category.getName())) ? category.getName() : "N/A"; 
	String tin = CommonUtil.getSellerCompanyDetails(shoppingItem.getUserId(), HConstants.TIN);
	String companyName = CommonUtil.getSellerCompanyDetails(shoppingItem.getUserId(), HConstants.COMPANY_NAME);
	String status = " ( Status : <i class='"+CommonUtil.getOrderStatus(shoppingOrder.getOrderStatus())+"'>"+CommonUtil.getOrderStatus(shoppingOrder.getOrderStatus())+"</i>)";
%>

<c:choose>
	<c:when test='<%= Validator.isNotNull(shoppingOrder)  %>'>
		<div id="print-area">
			<aui:input id="barCode" name="barCode" type="hidden" value="<%= productCode %>"/> 
			<div class="print-btn">
				<liferay-ui:icon-menu>
					<liferay-ui:icon image="print" url="javascript:printArticle();" message="print"  />
				</liferay-ui:icon-menu>	
			</div>
			
			<div class="text-center">
				<liferay-ui:journal-article articleId="BILL_LOGO" groupId="<%= themeDisplay.getScopeGroupId() %>"/>
			</div><hr/>
			<div class="row-fluid txtmargin">
				<div class="span4">
					<strong><liferay-ui:message key="name"/></strong>
					<label><%= fullName %></label>
				</div>
				<div class="span4">
					<strong><liferay-ui:message key="email"/></strong>
					<label><%= emailAddress %></label>
				</div>
				<div class="span4">
					<strong><liferay-ui:message key="payment-method"/></strong>
					<label><%= HConstants.CASH_ON_DELIVERY %></label>
				</div>
			</div>
			<div class="row-fluid txtmargin">
				<div class="span4">
					<strong><liferay-ui:message key="mobile-number"/></strong>
					<label><%= mobileNumber %></label>
				</div>
				<div class="span4">
					<strong><liferay-ui:message key="alternative-number"/></strong>
					<label><%= altNumber %></label>		
				</div>
				<div class="span4">
					<strong><liferay-ui:message key="tin"/></strong>
					<label><%= tin.isEmpty() ? "N/A" : tin %></label>		
				</div>
			</div>
			<div class="row-fluid txtmargin">
				<div class="span4">
					<strong><liferay-ui:message key="bill-no"/></strong>
					<label><%= CommonUtil.getBillNumber(orderId) + " "+status %></label>
				</div>
				<div class="span4">
					<strong><liferay-ui:message key="order-date"/></strong>
					<label><%= HConstants.DATE_FORMAT.format(shoppingOrder.getCreateDate()) %></label> 
				</div>
				<div class="span4">
					<strong><liferay-ui:message key="company-name"/></strong>
					<label><%= companyName.isEmpty() ? "N/A" : companyName %></label>
				</div>
			</div>
			<div class="row-fluid txtmargin">
				<div class="span4">
					<strong><liferay-ui:message key="billing-address"/></strong>
					<label><%= billingAddress %></label>
				</div>
				<div class="span4">
					<strong><liferay-ui:message key="shipping-address"/></strong>
					<label><%= shippingAddress %></label>
				</div>
				<div class="span4">
					<div id="bcTarget"></div>
				</div>
			</div>
			<div class="order-details"> 
				<table style="width:100%;height: auto;">
					<thead>
						<tr>
							<th><liferay-ui:message key="no"/></th>
							<th><liferay-ui:message key="product-type"/></th>
							<th><liferay-ui:message key="product-details"/></th>
							<th><liferay-ui:message key="quantity"/></th>
							<%-- <c:if test="<%= !isLiveCategory %>"> --%>  
								<th><liferay-ui:message key="price"/></th>
								<th><liferay-ui:message key="label-commission"/><%= " ("+shoppingItem.getTax()+" %)" %></th>
							<%-- </c:if> --%>
							<th><liferay-ui:message key="total"/></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><%= 1 %></td>
							<td><%= productType %></td>
							<td>
								<%= productDetails %>
								<c:if test='<%= Validator.isNotNull(shoppingItem) && shoppingItem.getSmallImage() > 0 %>'> 
									<div class="orderdetails-image">
										<img  src="<%= CommonUtil.getThumbnailpath(shoppingItem.getSmallImage(), themeDisplay.getScopeGroupId(), false) %>">
									</div>
								</c:if>
							</td>
							<td><%= quantity %></td>
							<%-- <c:if test="<%= !isLiveCategory %>"> --%>
								<td><%= decimalFormat.format(subTotal) %></td>
								<td><%= decimalFormat.format( totalPrice * (shoppingItem.getTax() / 100)) %></td>
							<%-- </c:if> --%>
							<td><%= decimalFormat.format(totalPrice)  %></td>
						</tr>
						<tr>
							<td colspan="<%= colspan %>" class="heading text-center"><liferay-ui:message key="amount-payable"/></td>
							<td class="heading text-center"><%= CommonUtil.getPriceInNumberFormat(totalPrice, currencySymbol) %></td>
						</tr>
					</tbody>	
				</table>
			</div>
			<div class="text-center">
				<liferay-ui:journal-article articleId="RECIEPT_INFO" groupId="<%= themeDisplay.getScopeGroupId() %>"/>
			</div>
		</div>
		
		<aui:script>
			$(document).ready(function(){
				console.log("ready");
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