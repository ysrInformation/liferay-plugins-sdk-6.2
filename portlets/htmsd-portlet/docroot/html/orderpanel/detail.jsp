<%@ include file="/html/orderpanel/init.jsp"%>

<head>
	<script src="<%= request.getContextPath() %>/js/jquery.printElement.js"></script>
	<script src="http://code.jquery.com/jquery-migrate-1.0.0.js"></script>
</head>

<%
	int slno = 1;
	double totalPrice = 0;
	long orderId = ParamUtil.getLong(request, "orderId");
	
	ShoppingOrder shoppingOrder = null;
	try {
		if (Validator.isNotNull(orderId)) {
			shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);	
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
	
	List<ShoppingOrderItem> shoppingOrderItems = Collections.emptyList();
	try {
		shoppingOrderItems = ShoppingOrderItemLocalServiceUtil.getShoppingOrderItemsByOrderId(shoppingOrder.getOrderId());
	} catch (Exception e) {
		System.out.println("No order exist  ::"+e);
	}
	
	DecimalFormat decimalFormat = new DecimalFormat("#.00");
%>

<portlet:renderURL var="backURL">
	<portlet:param name="<%= HConstants.JSP_PAGE %>" value="/html/orderpanel/view.jsp"/> 
</portlet:renderURL>

<liferay-ui:header title="order-details" backURL="<%= backURL %>"/>

<portlet:actionURL var="updateOrderURL" name="updateOrderStatus"/>

<div class="print-btn">
	<aui:button name="print" value="print" id="print-btn"/> 
</div>

<div id="print-area">
	<div class="user-details">
		<table style="width:100%">
			<tr>
				<td class="heading"><liferay-ui:message key="name"/></td>
				<td><label><%= fullName %></label></td>
			</tr>
			<tr>
				<td class="heading"><liferay-ui:message key="mobile-number"/></td>
				<td><label><%= shoppingOrder.getShippingMoble() %></label></td>
			</tr>
			<tr>
				<td class="heading"><liferay-ui:message key="alternative-number"/></td>
				<td><label><%= shoppingOrder.getShippingAltMoble() %></label></td>
			</tr>
			<tr>
				<td class="heading"><liferay-ui:message key="shipping-address"/></td>
				<td><label><%= sb.toString() %></label></td>
			</tr>
		</table>
	</div>
	<div class="product-details"> 
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
					String productDetails = shoppingOrderItem.getName()+"<br/>"+shoppingOrderItem.getDescription();
					%> 
					<tr>
						<td><%= slno++  %></td>
						<td><%= productDetails %></td>
						<td><%= HConstants.APPROVE %></td>
						<td><%= decimalFormat.format(price) %></td>
					</tr>
					<% 
				} 
				%>
				<tr>
					<td><%= StringPool.BLANK %></td>
					<td colspan="2" class="heading"><liferay-ui:message key="total"/></td>
					<td><%= CommonUtil.getPriceFormat(totalPrice) %></td>
				</tr>
			</tbody>	
		</table>
	</div>
</div>

<aui:fieldset>
	<aui:form method="post" action="<%= updateOrderURL %>" name="fm" inlineLabels="true"> 
	
		<aui:input name="orderId" type="hidden" value="<%= shoppingOrder.getOrderId() %>"/>
		
		<aui:select name="orderStatus" label="order-status" showEmptyOption="true" required="true"> 
			<aui:option label="<%= HConstants.DELIVERED_STATUS %>" value="<%= HConstants.DELIVERED %>"/>
			<aui:option label="<%= HConstants.NOT_DELIVERED_STATUS %>" value="<%= HConstants.NOT_DELIVERED %>"/>
		</aui:select>
	
		<aui:button-row>
			<aui:button type="submit" value="update"/> 
		</aui:button-row>
	</aui:form>
</aui:fieldset>


<style>
.print-btn {
	float: right;
}

.user-details {
	padding: 5px;
	text-decoration: none;
	font-family: sans-serif;
}

.product-details table td, product-details table th {
	border: 2px solid grey;
	padding: 10px;
}

.heading {
	font-weight: bold;
}
</style>

<script type="text/javascript"> 
$(function()
{
	$("#<portlet:namespace/>print").click(function(){
		$("#print-area").printElement(
		{
			leaveOpen:true,
			printMode:'popup'
		});
	});
 });
</script>