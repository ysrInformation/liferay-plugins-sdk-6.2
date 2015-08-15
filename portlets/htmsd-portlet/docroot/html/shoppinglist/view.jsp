<%@include file="/html/shoppinglist/init.jsp" %>

<aui:input name="len" type="hidden"/>

<ul id="shopping_list" class="row">
	<!-- item display -->
</ul>

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

<aui:script>
	var portletId = '<%= themeDisplay.getPortletDisplay().getId() %>';
	$(function() {
		window.onload = $("#<portlet:namespace/>len").val(8);
		getShoppingItems(0, 8);
	});
	
	function loadProducts() {
		var len = $("#<portlet:namespace/>len").val();
		var count = parseInt(len);
		getShoppingItems(count, parseInt(count) + parseInt(4));
	}
	
	function getShoppingItems(s, e) {
		
	$.ajax({
			url : '<%=themeDisplay.getPortalURL()+"/api/jsonws/htmsd-portlet.shoppingitem/get-shopping-items"%>',
			type : "GET",
			data : {
				groupId : <%=themeDisplay.getScopeGroupId()%>,
				status : <%=HConstants.APPROVE%>,
				start : s,
				end : e
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
				} else {
					$('#load-button').hide();
					$("#<portlet:namespace/>len").val(s);
				}
			},
			error : function() {
			}
		});
	}

	function render(data) {
		AUI().use('liferay-portlet-url', function(A) {
			$.each(data, function(i, item) {
				var cssClass = "span3", li;
				if (i % 4 === 0) {
					cssClass += ' first margin_left_zero';
				}
				
				var ajaxURL = Liferay.PortletURL.createRenderURL();
				ajaxURL.setPortletId(portletId);
				ajaxURL.setParameter('jspPage', "/html/shoppinglist/details.jsp");
				ajaxURL.setParameter('itemId', item.itemId);
				
				li = '<li class="' + cssClass + '"><div class="product"><div class="product-image">'
						+ '<a href="'+ajaxURL+'">	<img src=' + item.image + ' /></a>'
						+ '</div><div class="product-details">'
						+ '<h4 class="text-color-red">' + item.name + '</h4>'
						+ '<p>' + item.description + '</p>'
						+ '<h6>' + item.totalPrice + '</h6>'
						+ '</div></div></li>';
				$("#shopping_list").append(li);		
			});
		});
	}
</aui:script>