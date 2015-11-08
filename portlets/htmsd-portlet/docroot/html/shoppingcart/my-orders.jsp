<%@page import="java.text.SimpleDateFormat"%>
<%@ include file="/html/shoppingcart/init.jsp"%>

<h4><liferay-ui:message key="my-orders"/></h4>

<%
	String noOfItems  = ParamUtil.getString(request, "noOfItems", "8");
	int totalCount = ShoppingOrderItemLocalServiceUtil.getOrderItemsCount(themeDisplay.getUserId());;
	String curVal = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	long currencyid = (Validator.isNull(curVal)) ?  0 : Long.valueOf(curVal);
	List<ShoppingOrderItem> shoppingOrderItems = ShoppingOrderItemLocalServiceUtil.getShoppingOrderItems(themeDisplay.getUserId());
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
%>

<c:choose>
	<c:when test='<%= Validator.isNotNull(shoppingOrderItems) && !shoppingOrderItems.isEmpty()  %>'>
		<div class="main-div">
			<% for (ShoppingOrderItem soi:shoppingOrderItems) { 
				String total = CommonUtil.getPriceInNumberFormat(soi.getTotalPrice(), HConstants.RUPEE_SYMBOL);
				ShoppingItem shoppingItem = CommonUtil.getShoppingItem(soi.getShoppingItemId());
				long imageId = shoppingItem.getSmallImage();
				String imageSRC = CommonUtil.getThumbnailpath(imageId, themeDisplay.getScopeGroupId(), false);
				String status = CommonUtil.getOrderStatus(soi.getOrderStatus());
				%>
				<portlet:renderURL var="itemDetailsURL" windowState="<%= LiferayWindowState.POP_UP.toString()  %>"> 
            		<portlet:param name="<%= HConstants.JSP_PAGE %>" value="/html/shoppinglist/details.jsp"/> 
            		<portlet:param name="<%= HConstants.ITEM_ID %>" value="<%= String.valueOf(soi.getShoppingItemId()) %>"/>
            		<portlet:param name="<%= Constants.CMD %>" value="itemsDetails"/> 
            	</portlet:renderURL>
            	<% String itemsDetails = "javascript:showPopupDetails('"+itemDetailsURL+"','1200','Product Details');"; %>
				<div class="item-details">
					<div class="row-fluid first-item">
						<div class="span3 item-Image">
							<a href="<%= itemsDetails %>"><img src="<%= imageSRC %>" style="width: 100px; height: 100px"/></a> 
						</div>
						<div class="span4">
							<div><a href="<%= itemsDetails %>"><%= soi.getName() %></a></div> 
							<div><span class="qty">Qty : <%= soi.getQuantity() %></span></div> 
						</div>
						<div class="span3">
							<div><%= total %></div>
							<div class="<%= status %>">Status : <%= status %></div>
						</div>
						<div class="span2" title="Cancel this item">
							<portlet:actionURL var="cancelOrderURL" name="CancelOrder">
								<portlet:param name="orderId" value="<%= String.valueOf(soi.getOrderId()) %>"/>
								<portlet:param name="orderItemId" value="<%= String.valueOf(soi.getItemId()) %>"/>
							</portlet:actionURL>
								<%
									boolean isDisabled = (soi.getOrderStatus() == HConstants.PENDING) ? false : true;
								%>
							<a href="<%= cancelOrderURL %>"><aui:button cssClass="cancel-btn" type="button" 
								value='<%= LanguageUtil.get(locale, "cancel-order") %>' disabled="<%= isDisabled %>"/></a>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<span>Date : <%= sdf.format(soi.getCreateDate()) %></span>
						</div>
						<div class="pull-right">
							<span class="order-total">Order Total : <%= total %></span>
						</div>
					</div>
				</div>
			<% } %>
		</div>
	</c:when>
	<c:otherwise>
		<h4 class="text-center">No Items to Display</h4>
	</c:otherwise>
</c:choose>

<%--
<div class="main-div" id="shopping-list">
	<!-- item display -->
</div>

<aui:input name="len" type="hidden"/>
<div id="no-item-display" class="text-center"> 
	<h2>No Items to Display</h2>
</div>
<div id="loader-icon" >
	<img src="<%=request.getContextPath()%>/images/loader.gif" style="width: 100px; height: 100px"/> 
</div>
<div onclick="javascript:loadProducts()" id="load-button">
	<div id="loader-div">
		<div>
			<liferay-ui:message key="view-more"/>
		</div>
		<div>
			<i class="fa fa-chevron-down"></i>
		</div>
	</div>
</div>
 --%>
 
<aui:script> 
var dataLen = 0;
var portletId = '<%= themeDisplay.getPortletDisplay().getId() %>';

$(function() {
	if(<%=totalCount%> != 0) {
		$('#no-item-display').hide();
		$('#loader-icon').hide();
	}

	getShoppingItems(0, <%=noOfItems%>);
	
	window.onload = $("#<portlet:namespace/>len").val(<%=noOfItems%>);
	$('#current_count').html(dataLen);
});

function loadProducts() {
	var len = $("#<portlet:namespace/>len").val();
	var count = parseInt(len);
	getShoppingItems(count, parseInt(count) + parseInt(<%=noOfItems%>));
}

function getShoppingItems(s, e) {
	$.ajax({
		url : '<%= themeDisplay.getPortalURL()+"/api/jsonws/htmsd-portlet.shoppingcart/get-user-order-items" %>',
		type : "GET",
		data : {
			p_auth: Liferay.authToken,
			userId : '<%= themeDisplay.getUserId() %>',
			groupId : <%=themeDisplay.getScopeGroupId()%>,
			currencyId : '<%= currencyid %>',
			start : s,
			end: e,
		},
		beforeSend : function() {
			$('#loader-icon').show();
		},
		complete : function() {
			$('#loader-icon').hide();
		},
		success : function(data) {
			if (data.length > 0) {	
				$("#<portlet:namespace/>len").val(e);
				render(data);
				dataLen = parseInt(dataLen) + parseInt(data.length);
				$('#current_count').html(dataLen);
				if (dataLen == parseInt(<%= totalCount %>)) {
					$('#load-button').hide();
				}
			} else {
				$('#load-button').hide();
				$("#<portlet:namespace/>len").val(s);
			}
		},
		error : function() {
			alert('SomeThing went wrong!! please try again');
		}
	});
}

function render(data) {
	AUI().use('liferay-portlet-url', function(A) {
		$.each(data, function(i, item) {
			
			var ajaxURL = Liferay.PortletURL.createRenderURL();
			ajaxURL.setWindowState('popup');
			ajaxURL.setPortletId(portletId);
			ajaxURL.setParameter('jspPage', "/html/shoppinglist/details.jsp");
			ajaxURL.setParameter('itemId', item.itemId);
			ajaxURL.setParameter('cmd', "itemsDetails");
			
			div = '<div class="item-details"><div class="row-fluid first-item">'
					+ '<div class="span3 item-Image"><a href="'+ajaxURL+'"><img src=' + item.image +'/></a></div>'
					+ '<div class="span4"><div>'+ item.name +'</div>'
					+ '<div><span class="qty">Qty : '+ item.quantity +'</span></div></div>' 
					+ '<div class="span3"><div>' + item.totalPrice + '</div>'
					+ '<div class="'+ item.status +'"> Status :'+ item.status +'</div></div>'
					+ '<div class="span2" title="Cancel this item"><a href="'+ actionURL(item, portletId) +'">'
					+ '<button class="cancel-btn" disabled="'+ disableButton(item.orderStatus) +'">Cancel Order</button></a></div></div>' 
					+ '<div class="row-fluid"><div class="span5"><span>Date : '+ item.orderDate +'</span></div>'
					+ '<div class="pull-right"><span class="order-total">Order Total : '+ item.totalPrice +'</span></div>'  
					+ '</div></div>';
			$("#shopping_list").append(div);		
		});
	});
}

function actionURL(data, portletId) {
	
	var ajaxURL = Liferay.PortletURL.createRenderURL();
	ajaxURL.setPortletId(portletId);
	ajaxURL.setParameter('action', "javax.portlet.action");
	ajaxURL.setParameter('orderId', data.orderId);
	ajaxURL.setParameter('orderItemId', data.orderItemId);
	return ajaxURL;
}

function disableButton(status) {
	
	var flag = false;
	if (status == '<%= HConstants.PENDING %>') {
		flag = true;
	}
	return flag;
}

function showPopupDetails(url, width, title){
	
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

</aui:script>