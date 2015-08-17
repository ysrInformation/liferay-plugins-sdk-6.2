<%@include file="/html/shoppinglist/init.jsp" %>

<%
	String noOfItems  = portletPreferences.getValue("noOfItems", "8");
	String categoryToDisplay  = portletPreferences.getValue("categoryToDisplay", "-1");
	String sortBy = ParamUtil.getString(request, "sortBy", "totalPrice DESC");
%>

<aui:input name="len" type="hidden"/>

<aui:select name="sort-by" inlineLabel="true" onChange="javascript:refresh(this);" >
	<aui:option value="createDate DESC" label="new"/>
	<aui:option value="name ASC" label="Name(A - Z)" />
	<aui:option value="name DESC" label="Name(Z - A)" />
	<aui:option value="totalPrice DESC" label="Price High To Low" />
	<aui:option value="totalPrice ASC" label="Price Low To High"/>
</aui:select>

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
		$('#<portlet:namespace/>sort-by option[value="<%=sortBy%>"').attr("selected", "selected")
		window.onload = $("#<portlet:namespace/>len").val(<%=noOfItems%>);
		getShoppingItems(0, <%=noOfItems%>);
	});
	
	function loadProducts() {
		var len = $("#<portlet:namespace/>len").val();
		var count = parseInt(len);
		getShoppingItems(count, parseInt(count) + parseInt(len));
	}
	
	function getShoppingItems(s, e, sort) {
		
	$.ajax({
			url : '<%=themeDisplay.getPortalURL()+"/api/jsonws/htmsd-portlet.shoppingitem/get-shopping-items"%>',
			type : "GET",
			data : {
				categoryId : <%=categoryToDisplay%>,
				groupId : <%=themeDisplay.getScopeGroupId()%>,
				status : <%=HConstants.APPROVE%>,
				start : s,
				end: e,
				sortBy: '<%=sortBy%>'
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
	
	function refresh(obj) {
		AUI().use('liferay-portlet-url', function(A) {
			var ajaxURL = Liferay.PortletURL.createRenderURL();
			ajaxURL.setPortletId(portletId);
			ajaxURL.setParameter('jspPage', "/html/shoppinglist/view.jsp");
			ajaxURL.setParameter('sortBy', obj.value);
			window.location.href = '<%=themeDisplay.getPortalURL()%>' + ajaxURL;
		});
	}
</aui:script>