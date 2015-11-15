<%@ include file="/html/orderpanel/init.jsp"%>

<%@page import="com.htmsd.slayer.model.Category"%>
<%@page import="com.htmsd.slayer.service.ShoppingItemLocalServiceUtil"%>

<head>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script src='<%= request.getContextPath() + "/js/jquery-barcode.min.js"%>' ></script>
	<script src='<%= request.getContextPath() + "/js/jquery-barcode.js"%>' ></script>
</head>
	
<%
	int orderStatus = 0;
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
	
	String shippingAddress = sb.toString();
	String emailAddress = shoppingOrder.getShippingEmailAddress();
	String fullName = shoppingOrder.getShippingFirstName().concat(StringPool.SPACE).concat(shoppingOrder.getShippingLastName());
	String altNumber = (Validator.isNotNull(shoppingOrder.getShippingAltMoble()) ? shoppingOrder.getShippingAltMoble():"N/A");
	String mobileNumber = (Validator.isNotNull(shoppingOrder.getShippingMoble()) ? shoppingOrder.getShippingMoble():"N/A");
	
	DecimalFormat decimalFormat = new DecimalFormat("0.00");
	ShoppingOrderItem shoppingOrderItem = ShoppingOrderItemLocalServiceUtil.fetchShoppingOrderItem(orderItemId);
	ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingOrderItem.getShoppingItemId());
	Category parentCategory = CommonUtil.getShoppingItemParentCategory(shoppingOrderItem.getShoppingItemId());
	Category category = ShoppingItemLocalServiceUtil.getShoppingItemCategory(shoppingOrderItem.getShoppingItemId());
	
	boolean isLiveCategory = Validator.isNotNull(parentCategory) && parentCategory.getName().equals("Live");
	int colspan = (isLiveCategory) ? 3 : 5;
	double totalPrice = shoppingOrderItem.getTotalPrice();
	double tax = CommonUtil.calculateVat(totalPrice, shoppingItem.getTax());
	double subTotal = shoppingOrderItem.getTotalPrice() - tax;
	String productDetails = shoppingOrderItem.getProductCode()+"<br/>"+shoppingOrderItem.getName();
	String productType = (Validator.isNotNull(category.getName())) ? category.getName() : "N/A"; 
	String tin = CommonUtil.getSellerCompanyDetails(shoppingItem.getUserId(), HConstants.TIN);
	String companyName = CommonUtil.getSellerCompanyDetails(shoppingItem.getUserId(), HConstants.COMPANY_NAME);
	String barCode = "6933068935011";
%>

<div id="print-area">
	<aui:input id="barCode" name="barCode" type="hidden" value="<%= barCode %>"/> 
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
			<div id="bcTarget"></div>
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
			<label><%= CommonUtil.getBillNumber(orderId) %></label>
		</div>
		<div class="span4">
			<strong><liferay-ui:message key="order-date"/></strong>
			<label><%= HConstants.DATE_FORMAT.format(shoppingOrderItem.getCreateDate()) %></label> 
		</div>
		<div class="span4">
			<strong><liferay-ui:message key="shipping-address"/></strong>
			<label><%= shippingAddress %></label>
		</div>
	</div>
	<div class="order-details"> 
		<table style="width:100%;height: auto;">
			<thead>
				<tr>
					<th><liferay-ui:message key="product-type"/></th>
					<th><liferay-ui:message key="product-details"/></th>
					<th><liferay-ui:message key="quantity"/></th>
					<c:if test="<%= !isLiveCategory %>">  
						<th><liferay-ui:message key="price"/></th>
						<th><liferay-ui:message key="tax"/><%= " ("+shoppingItem.getTax()+" %)" %></th>
					</c:if>
					<th><liferay-ui:message key="total"/></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td><%= productType %></td>
					<td><%= productDetails %></td>
					<td><%= shoppingOrderItem.getQuantity() %></td>
					<c:if test="<%= !isLiveCategory %>">
						<td><%= decimalFormat.format(subTotal) %></td>
						<td><%= decimalFormat.format(tax) %></td>
					</c:if>
					<td><%= decimalFormat.format(totalPrice)  %></td>
				</tr>
				<tr>
					<td colspan="<%= colspan %>" class="heading text-center"><liferay-ui:message key="amount-payable"/></td>
					<td class="heading text-center"><%= CommonUtil.getPriceInNumberFormat(totalPrice, HConstants.RUPEE_SYMBOL) %></td>
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
	$("#bcTarget").barcode(barCodeNumber, "ean13" );
});

function printArticle() {
	print();
}
</aui:script>