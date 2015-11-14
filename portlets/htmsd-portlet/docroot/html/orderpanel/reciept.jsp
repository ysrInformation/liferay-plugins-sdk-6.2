<%@ include file="/html/orderpanel/init.jsp"%>

<head>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()+"/css/main.css" %>"> 
	<script src='<%=request.getContextPath() + "/js/jquery-barcode.min.js"%>' ></script>
	<script src='<%=request.getContextPath() + "/js/jquery-barcode.js"%>' ></script>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
</head>
	
<%
	int slno = 1;
	int orderStatus = 0;
	double totalPrice = 0;
	double tax = 0;
	long orderId = ParamUtil.getLong(request, "orderId");
	long orderItemId = ParamUtil.getLong(request, "orderItemId");
	
	ShoppingOrder shoppingOrder = null;
	try {
		if (Validator.isNotNull(orderId)) {
			shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);	
			orderStatus = shoppingOrder.getOrderStatus();
		}
	} catch (Exception e){
		System.out.println("No order exist"+e);
	}
	
	StringBuilder sb = new StringBuilder();
	sb.append(shoppingOrder.getShippingStreet());
	sb.append(StringPool.COMMA_AND_SPACE);
	sb.append(shoppingOrder.getShippingCity());
	sb.append(StringPool.COMMA_AND_SPACE);
	sb.append(CommonUtil.getState(Long.parseLong(shoppingOrder.getShippingState()))); 
	sb.append(StringPool.SPACE);
	sb.append(CommonUtil.getCountry(Long.parseLong(shoppingOrder.getShippingCountry()))); 
	sb.append(StringPool.COMMA_AND_SPACE);
	sb.append(shoppingOrder.getShippingZip());
	
	String fullName = shoppingOrder.getShippingFirstName().concat(StringPool.SPACE)
			.concat(shoppingOrder.getShippingLastName());
	
	String emailAddress = shoppingOrder.getShippingEmailAddress();
	String altNumber = (Validator.isNotNull(shoppingOrder.getShippingAltMoble()) ? shoppingOrder.getShippingAltMoble():"N/A");
	String mobileNumber = (Validator.isNotNull(shoppingOrder.getShippingMoble()) ? shoppingOrder.getShippingMoble():"N/A");

	ShoppingOrderItem shoppingOrderItem = ShoppingOrderItemLocalServiceUtil.fetchShoppingOrderItem(orderItemId);
	DecimalFormat decimalFormat = new DecimalFormat("#.00");
	CommonUtil.isCategoryLive(shoppingOrderItem.getShoppingItemId());
	ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingOrderItem.getShoppingItemId());
%>

<div id="print-area">
	<div class="print-btn">
		<liferay-ui:icon-menu>
			<liferay-ui:icon image="print" url="javascript:printArticle();" message="print"  />
		</liferay-ui:icon-menu>	
	</div>
	
	<div class="text-center">
		<liferay-ui:journal-article articleId="BILL_LOGO" groupId="<%= themeDisplay.getScopeGroupId() %>"/>
	</div><hr/>
	<div class="row-fluid txtmargin">
		<div class="span5">
			<strong><liferay-ui:message key="name"/></strong>
			<label><%= fullName %></label>
		</div>
		<div class="span5">
			<strong><liferay-ui:message key="email"/></strong>
			<label><%= emailAddress %></label>
		</div>
		<div class="span2">
			<div id="bcTarget"></div>
		</div>
	</div>
	<div class="row-fluid txtmargin">
		<div class="span5">
			<strong><liferay-ui:message key="mobile-number"/></strong>
			<label><%= mobileNumber %></label>
		</div>
		<div class="span5">
			<strong><liferay-ui:message key="alternative-number"/></strong>
			<label><%= altNumber %></label>		
		</div>
	</div>
	<div class="row-fluid txtmargin">
		<div class="span5">
			<strong><liferay-ui:message key="bill-no"/></strong>
			<label><%= CommonUtil.getBillNumber(orderId) %></label>
		</div>
		<div class="span5">
			<strong><liferay-ui:message key="shipping-address"/></strong>
			<label><%= sb.toString() %></label>
		</div>
	</div>
	<div class="order-details"> 
		<table style="width:100%;height: auto;">
			<thead>
				<tr>
					<th><liferay-ui:message key="product-details"/></th>
					<th><liferay-ui:message key="quantity"/></th>
					<th><liferay-ui:message key="price"/></th>
					<th><liferay-ui:message key="tax"/><%= " ("+shoppingItem.getTax()+" %)" %></th>
					<th><liferay-ui:message key="total"/></th>
				</tr>
			</thead>
			<tbody>
				<%
				double price = shoppingOrderItem.getTotalPrice();
				String productDetails = shoppingOrderItem.getProductCode()+"<br/>"+shoppingOrderItem.getName();
				tax = CommonUtil.calculateVat(price, shoppingItem.getTax());
				double subTotal = shoppingOrderItem.getTotalPrice() - tax;
				%> 
				<tr>
					<td><%= productDetails %></td>
					<td><%= shoppingOrderItem.getQuantity() %></td>
					<td><%= decimalFormat.format(subTotal) %></td>
					<td><%= decimalFormat.format(tax) %></td>
					<td><%= decimalFormat.format(totalPrice)  %></td>
				</tr>
				<tr>
					<td colspan="4" class="heading text-center"><liferay-ui:message key="amount-payable"/></td>
					<td class="heading text-center"><%= CommonUtil.getPriceInNumberFormat(totalPrice, HConstants.RUPEE_SYMBOL) %></td>
				</tr>
			</tbody>	
		</table>
	</div>
</div>

<aui:script>
$(document).ready(function(){
	console.log("ready");
	$("#bcTarget").barcode("6933068935011", "ean13" );
});

function printArticle() {
	print();
}
</aui:script>