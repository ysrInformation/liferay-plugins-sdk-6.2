<%@ include file="/html/orderpanel/init.jsp"%>

<%
	long orderId = ParamUtil.getLong(request, "orderId");
	String tabName = ParamUtil.getString(request, "tab1");
	ShoppingOrder shoppingOrder = null;
	try {
		shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
	} catch (Exception e) {
		System.out.println("No Item found .."+e.getMessage());
	}
	boolean isOrderCancelled = false;
	List<AssetCategory> orderStatusList = CommonUtil.getAssetCategoryList(themeDisplay.getScopeGroupId(), HConstants.ASSET_VOCABULARY_ORDER_STATUS);
	long orderStatusId = ShoppingOrderLocalServiceUtil.getAssetCategoryIdByName(HConstants.CANCEL_ORDER_STATUS);
%>

<portlet:actionURL var="updateOrderURL" name="updateOrderStatus"/>

<liferay-ui:header title="please-update-order-status" showBackURL="false"/> 

<div class="update-status">
	<aui:form method="post" action="<%= updateOrderURL %>" name="fm" id="fm" inlineLabels="true" target="_parent">  

		<aui:input name="orderId" type="hidden" value="<%= shoppingOrder.getOrderId() %>"/>
		<aui:input name="tabName" type="hidden" value="<%= tabName %>"/>
		
		<aui:select id="orderStatus" name="orderStatus" label="order-status" showEmptyOption="true" required="true" onChange="checkCancelOrder(this.value);"> 
			<c:if test="<%= Validator.isNotNull(orderStatusList) && !orderStatusList.isEmpty() %>">
			<% 
			for (AssetCategory assetCategory : orderStatusList) {
				%><aui:option label="<%= assetCategory.getName() %>" value="<%= assetCategory.getCategoryId() %>"/><% 
			}
			%>
			</c:if>
		</aui:select>
		
		<div id="cancelReason" style="display:none;">
			<aui:input name="cancelReason" label="please-specify-the-reason" type="textarea" cols="30" rows="5" >
				<aui:validator name="required" errorMessage="please-enter-the-reason-why-you-want-to-cancel-this-order"/> 
			</aui:input>
		</div>
	
		<aui:button-row>
			<aui:button type="button" cssClass="btn-primary" value="update" onClick="updateStatus();"/> 
		</aui:button-row>
	</aui:form>
</div>

<script type="text/javascript"> 
function checkCancelOrder(selectedOption) {
	
	var divId = document.getElementById('cancelReason');
	if (selectedOption == '<%= orderStatusId %>') {
		divId.style.display = "block";
	} else {
		divId.style.display = "none";
	}
}

function updateStatus(){
	 
	 var orderstatus =  document.getElementById("<portlet:namespace/>orderStatus").value;
	 var divId = document.getElementById("cancelReason");
	 
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