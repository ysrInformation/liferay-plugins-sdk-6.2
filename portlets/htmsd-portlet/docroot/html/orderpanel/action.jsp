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
	
	if (Validator.isNotNull(tabName)) {
		updateOrderStatus.setParameter("tab1", tabName); 
		viewRecieptURL.setParameter("tab1", tabName);
	}
	
	String updateStatus = "javascript:showPopup('"+updateOrderStatus.toString()+"','"+"Update Order Status','1200"+"');";
	String viewReciept = "javascript:showPopup('"+viewRecieptURL.toString()+"','"+"View Receipt','1200"+"');";
%>

<liferay-ui:icon-menu>
	<liferay-ui:icon image="view" message="view-reciept" url="<%= viewReciept %>" />
	<liferay-ui:icon image="edit" message="update-status"  url="<%= updateStatus  %>" />
	<%-- <liferay-ui:icon image="mail" message="send-mail" url="#" /> --%>
</liferay-ui:icon-menu>

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