<%@ include file="/html/orderpanel/init.jsp"%>

<%
	int no = 1;
	int orderStatus = 0;
	double totalPrice = 0;
	long orderId = ParamUtil.getLong(request, "orderId");
	String tabs = ParamUtil.getString(request, "tab1", "Pending");
	
	PortletURL recieptURL = renderResponse.createRenderURL();
	recieptURL.setWindowState(LiferayWindowState.POP_UP);
	recieptURL.setParameter(HConstants.JSP_PAGE, "/html/orderpanel/reciept.jsp");
	
	ShoppingOrder shoppingOrder = null;
	try {
		if (Validator.isNotNull(orderId)) {
			shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);	
			orderStatus = shoppingOrder.getOrderStatus();
			recieptURL.setParameter("orderId", String.valueOf(orderId));
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
	
	boolean showOrderForm = orderStatus == HConstants.PENDING || orderStatus == HConstants.SHIPPING;
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter(HConstants.JSP_PAGE, "/html/shoppingcart/detail.jsp");
%>

<portlet:renderURL var="backURL">
	<portlet:param name="<%= HConstants.JSP_PAGE %>" value="/html/orderpanel/view.jsp"/> 
	<portlet:param name="tab1" value="<%= tabs %>"/> 
</portlet:renderURL>

<liferay-ui:header title="order-details" backURL="<%= backURL %>"/>

<portlet:actionURL var="updateOrderURL" name="updateOrderStatus"/>

<div class="pull-right">
	<aui:button value='<%= LanguageUtil.get(locale, "view-reciept") %>' type="button" href="javascript:printReciept();"/>
</div>

<div id="ordered-items">
	<div class="row-fluid txtmargin">
		<div class="span5">
			<strong><liferay-ui:message key="name"/></strong>
			<label><%= fullName %></label>
		</div>
		<div class="span5">
			<strong><liferay-ui:message key="email"/></strong>
			<label><%= emailAddress %></label>
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
	<hr/>
	<div class="order-item-list">
		<liferay-ui:search-container delta="10" iteratorURL="<%= iteratorURL %>" emptyResultsMessage="No items to display">
	    	 <liferay-ui:search-container-results
	            results="<%= ListUtil.subList(shoppingOrderItems, searchContainer.getStart(), searchContainer.getEnd()) %>"
	            total="<%= shoppingOrderItems.size() %>"
	         />
	        
	        	<liferay-ui:search-container-row className="com.htmsd.slayer.model.ShoppingOrderItem" 
	        		modelVar="orderItem">
	           
		            <% 
		            String productCode = (Validator.isNotNull(orderItem.getProductCode())) ? orderItem.getProductCode():"N/A";
		          
		            String sellerName = StringPool.BLANK;
		            try {
		            	ShoppingItem shoppingItem = CommonUtil.getShoppingItem(orderItem.getShoppingItemId());
		            	User seller =  UserLocalServiceUtil.fetchUser(shoppingItem.getUserId());
		            	sellerName = (Validator.isNotNull(seller)) ? seller.getFullName() : "N/A";
		            } catch(Exception e) {
		            	System.out.println(e);
		            }
		            %>
		            
		            <liferay-ui:search-container-column-text name="no" value="<%= String.valueOf(no++) %>"/>
		
		            <liferay-ui:search-container-column-text name="product-code" value="<%= productCode %>"/>
		            
		            <liferay-ui:search-container-column-text name="seller-name" value="<%= sellerName %>"/>
		            
		            <liferay-ui:search-container-column-text name="name" value="<%= orderItem.getName() %>"/>
		            
		            <liferay-ui:search-container-column-text name="quantity" value="<%= String.valueOf(orderItem.getQuantity()) %>"/>
		            
		            <liferay-ui:search-container-column-text name="total">
		            	<%= CommonUtil.getPriceFormat(orderItem.getTotalPrice()) %>
		            </liferay-ui:search-container-column-text>
			
		        </liferay-ui:search-container-row>
		     <liferay-ui:search-iterator searchContainer="<%= searchContainer %>"/>
		</liferay-ui:search-container>
	</div>
</div>

<div class="update-status">
	<c:if test="<%= showOrderForm %>">
		<aui:form method="post" action="<%= updateOrderURL %>" name="fm" inlineLabels="true"> 
	
			<aui:input name="orderId" type="hidden" value="<%= shoppingOrder.getOrderId() %>"/>
			
			<aui:select name="orderStatus" label="order-status" showEmptyOption="true" required="true"> 
				<aui:option label="<%= HConstants.SHIPPING_STATUS %>" value="<%= HConstants.SHIPPING %>" selected="<%= shoppingOrder.getOrderStatus() == HConstants.SHIPPING  %>"/>
				<aui:option label="<%= HConstants.DELIVERED_STATUS %>" value="<%= HConstants.DELIVERED %>" selected="<%= shoppingOrder.getOrderStatus() == HConstants.DELIVERED  %>"/>
				<aui:option label="<%= HConstants.NOT_DELIVERED_STATUS %>" value="<%= HConstants.CANCEL_ORDER %>" selected="<%= shoppingOrder.getOrderStatus() == HConstants.NOT_DELIVERED  %>"/>
			</aui:select>
		
			<aui:button-row>
				<aui:button type="button" value="update" onClick="updateStatus();"/> 
			</aui:button-row>
		</aui:form>
	</c:if>
</div>

<script type="text/javascript"> 
/* $(function()
{
	$("#<portlet:namespace/>print").click(function(){
		$("#print-area").printElement(
		{
			leaveOpen:true,
			printMode:'popup',
		});
	});
 }); */
 
function printReciept() {
	window.open('<%= recieptURL.toString()%>', '', "directories=0,height=600,left=80,location=1,menubar=1,resizable=1,scrollbars=yes,status=0,toolbar=0,top=180,");
};
 
function updateStatus(){
	 
	 var orderstatus = $("#<portlet:namespace/>orderStatus").val();
	 
	 if (orderstatus.length == 0) {
		 alert('Please select the order status');
		 return false;
	 }
	 
	 var message = '<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-update-the-status?")%>'
	 if(confirm(message)){
		 $("#<portlet:namespace/>fm").submit();
	 }
 }
</script>