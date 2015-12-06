<%@page import="java.text.SimpleDateFormat"%>
<%@ include file="/html/shoppingcart/init.jsp"%>

<h4><liferay-ui:message key="my-orders"/></h4>

<%
	String noOfItems  = portletPreferences.getValue("noOfItems", "8");
	int totalCount = ShoppingOrderLocalServiceUtil.getItemsCountByUserId(themeDisplay.getUserId());
	List<ShoppingOrder> shoppingOrderItems = ShoppingOrderLocalServiceUtil.getShoppingOrderByUserId(themeDisplay.getUserId());
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
	String currentCurrencyid = (String) portletSession.getAttribute("currentCurrencyId", PortletSession.APPLICATION_SCOPE);
	long currencyId2 = (Validator.isNull(currentCurrencyid)) ?  0 : Long.valueOf(currentCurrencyid);
	String currencySymbol = CommonUtil.getCurrencySymbol(Long.valueOf(currencyId2));
%>

<%-- <c:choose>
	<c:when test='<%= Validator.isNotNull(shoppingOrderItems) && !shoppingOrderItems.isEmpty()  %>'>
		<div class="main-div">
			<% 
			for (ShoppingOrder soi:shoppingOrderItems) { 
				double currentRate = CommonUtil.getCurrentRate(Long.valueOf(currencyId2));
				double totalPrice = (currentRate == 0) ? soi.getTotalPrice() : soi.getTotalPrice() / currentRate;
				String total = CommonUtil.getPriceFormat(totalPrice, currencyId2);
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
							<div><a href="<%= itemsDetails %>"><%= shoppingItem.getName() %></a></div> 
							<div><span class="qty"><liferay-ui:message key="shopping-qty"/> : <%= soi.getQuantity() %></span></div> 
						</div>
						<div class="span3">
							<div><%= total %></div>
							<div class="<%= status %>"><liferay-ui:message key="status"/> : <%= status %></div>
						</div>
						<div class="span2" title="Cancel this item">
							<portlet:renderURL var="cancelOrderURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>"> 
								<portlet:param name="<%= HConstants.JSP_PAGE %>" value="/html/shoppingcart/capture-reason.jsp" />
								<portlet:param name="orderId" value="<%= String.valueOf(soi.getOrderId()) %>"/>
							</portlet:renderURL>
							<%
								boolean isDisabled = (soi.getOrderStatus() == HConstants.PENDING) ? false : true;
								String cancelOrder = "javascript:showPopupDetails('"+cancelOrderURL+"','1000','Cancel Order');"; 
							%>
							<a href="<%= cancelOrder %>"><aui:button cssClass="cancel-btn" type="button" 
								value='<%= LanguageUtil.get(locale, "cancel-order") %>' disabled="<%= isDisabled %>"/></a>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<span><liferay-ui:message key="date"/> : <%= sdf.format(soi.getCreateDate()) %></span>
						</div>
						<div class="pull-right">
							<span class="order-total"><liferay-ui:message key="order-total"/> : <%= CommonUtil.getPriceInNumberFormat(totalPrice, currencySymbol) %></span>
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
 --%>
<div class="main-div" id="shopping_list">
	<!-- item display -->
</div>
<aui:input name="len" type="hidden"/>

<div id="no-item-display" class="text-center"> 
	<h4>No Items to Display</h4>
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

<aui:script use="aui-base,liferay-portlet-url"> 
var dataLen = 0;
var portletId = '<%= themeDisplay.getPortletDisplay().getId() %>';

$(function() {
	if(<%=totalCount%> != 0) {
		$('#no-item-display').hide();
		$('#loader-icon').hide();
	}

	getOrderItems(0, <%=noOfItems%>);
	window.onload = $("#<portlet:namespace/>len").val(<%=noOfItems%>);
});

loadProducts = function() {
	var len = $("#<portlet:namespace/>len").val();
	var count = parseInt(len);
	getOrderItems(count, parseInt(count) + parseInt(<%=noOfItems%>));
}

function getOrderItems(s, e) {
	$.ajax({
		url : '<%= themeDisplay.getPortalURL()+"/api/jsonws/htmsd-portlet.shoppingcart/get-user-order-items" %>',
		type : "GET",
		data : {
			p_auth: Liferay.authToken,
			userId : '<%= themeDisplay.getUserId() %>',
			groupId : <%=themeDisplay.getScopeGroupId()%>,
			currencyId : '<%= currencyId2 %>',
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
				console.log(data);
				$("#<portlet:namespace/>len").val(e);
				render(data);
				dataLen = parseInt(dataLen) + parseInt(data.length);
				
				if (dataLen == parseInt(<%= totalCount %>)) {
					$('#load-button').hide();
				}
			} else {
				$('#load-button').hide();
				$("#<portlet:namespace/>len").val(s);
			}
		},
		error : function() {
			alert('Something went wrong!! please try again');
		}
	});
}

function render(data) {
	AUI().use('liferay-portlet-url', function(A) {
		$.each(data, function(i, item) {
			
			var myOrdersDiv = "",itemId,orderId,orderDate,status,itemName,quantity,imageSrc,total;
			itemId = item.itemId;
			orderId = item.orderId;
			orderDate = item.orderDate;
			itemName = item.name;
			status = item.status;
			quantity = item.quantity;
			imageSrc = item.image;
			total = item.totalPrice;
			
			var itemURL = createRenderURL("/html/shoppinglist/details.jsp", itemId, orderId, true);
			var cancelURL = createRenderURL("/html/shoppingcart/capture-reason.jsp", itemId, orderId, false);
			var itemsDetails = "javascript:showPopupDetails('"+itemURL+"','1200','Product Details')";
			var cancelOrder = "javascript:showPopupDetails('"+cancelURL+"','1000','Cancel Order')";
			
			console.log('itemURL ::'+itemURL); 
			 
			myOrdersDiv += '<div class="item-details">';
			myOrdersDiv += '<div class="row-fluid first-item">';
			myOrdersDiv += '<div class="span3 item-Image"><a href="'+itemsDetails+'"><img src="'+imageSrc+'" style="width: 100px; height: 100px"/></a></div>';
			myOrdersDiv += '<div class="span4"><div title="'+itemName+'"><a href="'+itemsDetails+'">'+itemName+'</a></div><div><span class="qty">QTY : '+quantity+'</span></div></div>';
			myOrdersDiv += '<div class="span3"><div>'+formatPrice(total)+'</div><div class="'+status+'">Status : '+status+'</div></div>'; 
			myOrdersDiv += '<div class="span2" title="Cancel this item"><a href="'+cancelOrder+'">'; 
			myOrdersDiv += '<button type="button" disabled="'+disableButton(status)+'" class="btn cancel-btn"> Cancel Order </button>';
			myOrdersDiv += '</a></div>';
			myOrdersDiv += '</div>';  
			myOrdersDiv += '<div class="row-fluid"><div class="span5"><span>Date : '+orderDate+'</span></div><div class="pull-right"><span class="order-total">Order Total : '+ formatPrice(total) +'</span></div></div>';
			myOrdersDiv += '</div>';
			
			$("#shopping_list").append(myOrdersDiv);	
		});
	});
}

function formatPrice(number) {
	accounting.settings.currency.symbol = '<%=CommonUtil.getCurrencySymbol(currencyId2)%> ';
	var text = '';
	text = accounting.formatMoney(Math.abs(number));
	return text;
}

function createRenderURL(jspPath, itemId, orderId, showItemDetail) {
	var renderURL = ""; 
	AUI().use('liferay-portlet-url', function(A) {
		var ajaxURL = Liferay.PortletURL.createRenderURL();
		ajaxURL.setWindowState('pop_up');
		ajaxURL.setPortletId(portletId);
		ajaxURL.setParameter('jspPage', jspPath);
		if (showItemDetail) {
			ajaxURL.setParameter('itemId', itemId);
			ajaxURL.setParameter('cmd', "itemsDetails");
		} else {
			ajaxURL.setParameter('orderId', orderId);
		}
		renderURL = ajaxURL.toString();
	});
	return renderURL;
}

function disableButton(status) {
	
	var flag = true;
	if (status == '<%= HConstants.PENDING %>') {
		flag = false;
	}
	return flag;
}

showPopupDetails = function(url, width, title) {
	
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