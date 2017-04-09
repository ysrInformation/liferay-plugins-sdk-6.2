<%@ include file="/html/orderpanel/init.jsp"%>

<portlet:resourceURL var="changeOrderStatusURL" id="changeOrderStatus"/> 

<%
	long sellerId = themeDisplay.getUserId();
	int  slno = 1, start = 0, end = ShoppingOrderLocalServiceUtil.getCountBySellerId(sellerId);
	List<ShoppingOrder> shoppingOrders = new ArrayList<ShoppingOrder>();
	shoppingOrders = ShoppingOrderLocalServiceUtil.getShoppingOrderListBySellerId(start, end, sellerId);

	String val2 = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	long currencyId1 = (Validator.isNull(val2)) ?  0 : Long.valueOf(val2);

	long detailPlid = CommonUtil.getDetailPageLayoutId(themeDisplay.getScopeGroupId(), layout.getPlid());
	double currentRate = CommonUtil.getCurrentRate(Long.valueOf(currencyId1));
	String backURL = themeDisplay.getURLCurrent();
%>

<div class="orderlist">
	<table id="sellerItemOrders" class="table table-striped table-bordered dt-responsive nowrap" width="100%" cellspacing="0"> 
		<thead>
			<tr>
				<th><liferay-ui:message key="no"/></th>
				<th><liferay-ui:message key="order-id"/></th>
				<th><liferay-ui:message key="user-name"/></th>
				<th><liferay-ui:message key="seller-name"/></th>
				<th><liferay-ui:message key="item-title"/></th>
				<th><liferay-ui:message key="order-date"/></th>
				<th><liferay-ui:message key="status"/></th>
				<th><liferay-ui:message key="quantity"/></th>
				<th><liferay-ui:message key="total"/></th>
				<th><liferay-ui:message key="action"/></th>
			</tr>
		</thead>
		<tbody>
			<c:if test='<%= Validator.isNotNull(shoppingOrders) && shoppingOrders.size() > 0 %>'>
				<% 
				for (ShoppingOrder shoppingOrder : shoppingOrders) { 
		            double totalPrice = (currentRate == 0) ? shoppingOrder.getTotalPrice() : shoppingOrder.getTotalPrice() / currentRate;
		           	String userName = shoppingOrder.getUserName();
		           	String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		           	String orderId = HConstants.A2ZALI + currentYear.substring(2, 4) + shoppingOrder.getOrderId();
		           	String status = CommonUtil.getOrderStatus(shoppingOrder.getOrderStatus());
		           	String orderedDate = HConstants.DATE_FORMAT.format(shoppingOrder.getCreateDate()); 
		            String sellerName = shoppingOrder.getSellerName();
		            String itemName = StringPool.BLANK;
		            try {
		            	ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId());
		            	itemName = Validator.isNotNull(shoppingItem) ? shoppingItem.getName() : "N/A"; 
		            } catch(Exception e) {
		            	System.out.println(e);
		            }
		            String showItemDetails = "javascript:showDetailsPage('"+backURL+"','"+shoppingOrder.getShoppingItemId()+"','"+detailPlid+"');";
		            String updateOrderStatus = "javascript:updateOrderStatus('"+changeOrderStatusURL+"','"+String.valueOf(shoppingOrder.getOrderId())+"');";
					%>
					<tr>
						<td><%= slno++ %></td>
						<td><%= orderId %></td>
						<td><%= userName %></td>
						<td><%= sellerName %></td>
						<td>
            				<a onclick="<%= showItemDetails %>" href="javascript:void(0);"><%= itemName %></a> 
						</td>
						<td><%= orderedDate %></td>
						<td class="<%= status %>">
							<c:choose>
			            		<c:when test='<%= status.equalsIgnoreCase(HConstants.ORDER_REVIEW_STATUS) %>'>
			            			<a href="<%= updateOrderStatus %>" class="<%= status %>"><%= status %></a>  
			            		</c:when>
			            		<c:otherwise><%= status %></c:otherwise>
			            	</c:choose>
						</td>
						<td><%= shoppingOrder.getQuantity() %></td>
						<td><%= CommonUtil.getPriceFormat(totalPrice, currencyId1) %></td>
						<td>
							<portlet:renderURL var="viewReceiptURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
								<portlet:param name="<%= HConstants.JSP_PAGE %>" value="/html/dashboard/sellerReciept.jsp"/>
								<portlet:param name="orderId" value="<%= String.valueOf(shoppingOrder.getOrderId()) %>"/> 
							</portlet:renderURL>
							<% String viewReciept = "javascript:showPageInPopup('"+viewReceiptURL.toString()+"','500','1200','View Receipt');"; %>  
			            	<a href="<%= viewReciept %>"><liferay-ui:message key="view-reciept"/></a> 
						</td>						
					</tr>
					<% 
				} %>
			</c:if>	
		</tbody>
	</table>
</div>

<script type="text/javascript">
$(function(){
	$("#sellerItemOrders").DataTable();
});

function updateOrderStatus(url, orderid) {
	var msg = "<liferay-ui:message key='update-order-status-confirm-msg'/>";
	if(confirm(msg)) {
		$.ajax({
			url : url,
			type : "GET",
			data : {
				<portlet:namespace/>orderId:orderid
			},
			success : function(data) {
				location.reload();
			},
			error : function() {
				alert("Something went wrong !!Please try again..");
			}
		});
	}
}
</script>