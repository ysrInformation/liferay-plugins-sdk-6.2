<%@ include file="/html/orderpanel/init.jsp"%>

<%
	int slno = 1;
	int orderStatus = 0;
	double totalPrice = 0;
	long orderId = ParamUtil.getLong(request, "orderId");
	
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
	String mobileNumber = (Validator.isNotNull(shoppingOrder.getShippingMoble()) ? shoppingOrder.getShippingAltMoble():"N/A");

	List<ShoppingOrderItem> shoppingOrderItems = CommonUtil.getShoppingOrderItems(orderId);
	DecimalFormat decimalFormat = new DecimalFormat("#.00");
%>

<div class="print-btn">
	<liferay-ui:icon-menu>
		<liferay-ui:icon image="print" url="javascript:printArticle();" message="print"  />
	</liferay-ui:icon-menu>	
</div>

<div id="print-area">
	<div class="text-center">
		<liferay-ui:journal-article articleId="BILL_LOGO" groupId="<%= themeDisplay.getScopeGroupId() %>"/>
	</div><hr/>
	<div class="row-fluid">
		<div class="span2">
			<strong><liferay-ui:message key="name"/></strong>
			<label><%= fullName %></label>
		</div>
		<div class="span2">
			<strong><liferay-ui:message key="email"/></strong>
			<label><%= emailAddress %></label>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span2">
			<strong><liferay-ui:message key="mobile-number"/></strong>
			<label><%= mobileNumber %></label>
		</div>
		<div class="span2">
			<strong><liferay-ui:message key="alternative-number"/></strong>
			<label><%= altNumber %></label>		
		</div>
	</div>
	<div class="row-fluid">
		<div class="span2">
			<strong><liferay-ui:message key="bill-no"/></strong>
			<label><%= CommonUtil.getBillNumber(orderId) %></label>
		</div>
		<div class="span2">
			<strong><liferay-ui:message key="shipping-address"/></strong>
			<label><%= sb.toString() %></label>
		</div>
	</div>
	<div class="order-details"> 
		<table style="width:100%;height: auto;">
			<thead>
				<tr>
					<th><liferay-ui:message key="no"/></th>
					<th><liferay-ui:message key="product-details"/></th>
					<th><liferay-ui:message key="quantity"/></th>
					<th><liferay-ui:message key="total"/></th>
				</tr>
			</thead>
			<tbody>
				<%
				for (ShoppingOrderItem shoppingOrderItem:shoppingOrderItems) {
					double price = shoppingOrderItem.getTotalPrice();
					totalPrice += price;
					String productDetails = shoppingOrderItem.getProductCode()+"<br/>"+shoppingOrderItem.getName();
					%> 
					<tr>
						<td><%= slno++  %></td>
						<td><%= productDetails %></td>
						<td><%= shoppingOrderItem.getQuantity() %></td>
						<td><%= decimalFormat.format(price) %></td>
					</tr>
					<% 
				} 
				%>
				<tr>
					<td><%= StringPool.BLANK %></td>
					<td colspan="2" class="heading text-center"><liferay-ui:message key="amount-payable"/></td>
					<td class="heading text-center"><%= CommonUtil.getPriceFormat(totalPrice) %></td>
				</tr>
			</tbody>	
		</table>
	</div>
</div>

<aui:script>
function printArticle() {
	print();
}
</aui:script>