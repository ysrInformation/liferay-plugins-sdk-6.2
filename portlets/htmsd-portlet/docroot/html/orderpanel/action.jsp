<%@ include file="/html/orderpanel/init.jsp"%>

<%
	ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
	ShoppingOrder shoppingOrderItem = (ShoppingOrder) row.getObject();
	
	long orderId = shoppingOrderItem.getOrderId();
	String tabName = ParamUtil.getString(request, "tab1", "Pending"); 
	
	PortletURL  updateOrderStatus = renderResponse.createRenderURL();
	updateOrderStatus.setWindowState(LiferayWindowState.POP_UP);
	updateOrderStatus.setParameter(HConstants.JSP_PAGE, "/html/orderpanel/order-status-form.jsp");
	updateOrderStatus.setParameter("orderId", String.valueOf(orderId));
	
	PortletURL  viewRecieptURL = renderResponse.createRenderURL();
	viewRecieptURL.setWindowState(LiferayWindowState.POP_UP);
	viewRecieptURL.setParameter(HConstants.JSP_PAGE, "/html/orderpanel/reciept.jsp");
	viewRecieptURL.setParameter("orderId", String.valueOf(orderId));
	
	PortletURL sendInvoiceURL = renderResponse.createActionURL();
	sendInvoiceURL.setParameter(ActionRequest.ACTION_NAME, "generateInvoice");
	sendInvoiceURL.setParameter("orderId", String.valueOf(orderId));
	
	if (Validator.isNotNull(tabName)) {
		updateOrderStatus.setParameter("tab1", tabName); 
		viewRecieptURL.setParameter("tab1", tabName);
		sendInvoiceURL.setParameter("tabName", tabName);
	}
	
	ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingOrderItem.getShoppingItemId());
	String updateStatus = "javascript:showPopup('"+updateOrderStatus.toString()+"','"+"Update Order Status','1200"+"');";
	String viewReciept = "javascript:showPopup('"+viewRecieptURL.toString()+"','"+"View Receipt','1200"+"');";
%>
<c:choose>
	<c:when test='<%= Validator.isNotNull(shoppingItem) %>'>
		<liferay-ui:icon-menu>
			<liferay-ui:icon image="view" message="view-reciept" url="<%= viewReciept %>" />
			<liferay-ui:icon image="edit" message="update-status"  url="<%= updateStatus  %>" />
			<% sendInvoiceURL.setParameter("isSeller", "true"); %>
			<liferay-ui:icon image="post" message="send-invoice-to-seller" url="<%= sendInvoiceURL.toString() %>" />
			<% sendInvoiceURL.setParameter("isSeller", "false"); %> 
			<liferay-ui:icon image="post" message="send-invoice-to-customer" url="<%= sendInvoiceURL.toString() %>" />
		</liferay-ui:icon-menu>
	</c:when> 
	<c:otherwise>
		<span class="Order Cancelled"><liferay-ui:message key="no-action"/></span>
	</c:otherwise>
</c:choose>
<script type="text/javascript">
function showPopup(url, title, width) {
	
	AUI().use('aui-base','liferay-util-window','aui-io-plugin-deprecated',function(A){
    	var popup = Liferay.Util.Window.getWindow(
                {
                    dialog: {
                        centered: true,
                        constrain2view: true,
                        modal: true,
                        resizable: false,
                        width: width
                    }
                }).plug(A.Plugin.DialogIframe,
 				{
                     autoLoad: true,
                     iframeCssClass: 'dialog-iframe',
                     uri:url
                }).render();
    		popup.show();
    		popup.titleNode.html(title);
	});	
}
</script>