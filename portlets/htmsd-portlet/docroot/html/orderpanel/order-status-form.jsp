<%@ include file="/html/orderpanel/init.jsp"%>

<%
	long orderItemId = ParamUtil.getLong(request, "orderItemId");
	String tabName = ParamUtil.getString(request, "tab1");
	ShoppingOrderItem shoppingOrderItem = null;
	try {
		shoppingOrderItem = ShoppingOrderItemLocalServiceUtil.fetchShoppingOrderItem(orderItemId);
	} catch (Exception e) {
		System.out.println("No Item found .."+e.getMessage());
	}
	List<AssetCategory> orderStatusList = CommonUtil.getAssetCategoryList(themeDisplay.getScopeGroupId(), HConstants.ASSET_VOCABULARY_ORDER_STATUS);
%>

<portlet:actionURL var="updateOrderURL" name="updateOrderStatus"/>

<liferay-ui:header title="please-update-order-status" showBackURL="false"/> 

<div class="update-status">
	<aui:form method="post" action="<%= updateOrderURL %>" name="fm" id="fm" inlineLabels="true" target="_parent">  

		<aui:input name="orderId" type="hidden" value="<%= shoppingOrderItem.getOrderId() %>"/>
		<aui:input name="orderItemId" type="hidden" value="<%= shoppingOrderItem.getItemId() %>"/>
		<aui:input name="tabName" type="hidden" value="<%= tabName %>"/>
		
		<aui:select id="orderStatus" name="orderStatus" label="order-status" showEmptyOption="true" required="true"> 
			<c:if test="<%= Validator.isNotNull(orderStatusList) && !orderStatusList.isEmpty() %>">
			<% 
			for (AssetCategory assetCategory : orderStatusList) { 
				%><aui:option label="<%= assetCategory.getName() %>" value="<%= assetCategory.getCategoryId() %>"/><% 
			}
			%>
			</c:if>
		</aui:select>
	
		<aui:button-row>
			<aui:button type="button" cssClass="btn-primary" value="update" onClick="updateStatus();"/> 
		</aui:button-row>
	</aui:form>
</div>

<script type="text/javascript"> 
function updateStatus(){
	 
	 var orderstatus =  document.getElementById("<portlet:namespace/>orderStatus").value;
	
	 if (orderstatus.length == 0 || orderstatus == 'undefined' || orderstatus == 'null') {
		 alert('Please select the order status');
		 return false;
	 }
	 
	 var message = '<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-update-the-status?")%>';
	 if(confirm(message)){
		 document.<portlet:namespace/>fm.submit();
	 }
}
</script>