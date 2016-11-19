<%@page import="javax.portlet.PortletSession"%>
<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@include file="/html/shoppinglist/init.jsp" %>

<%
	HttpServletRequest httpRequest = PortalUtil.getOriginalServletRequest(request);
	String searchParam = httpRequest.getParameter("search-param");
	if(Validator.isNull(searchParam)) 
		searchParam = StringPool.BLANK ;
	else 
		searchParam = searchParam.trim();
	String noOfItems  = portletPreferences.getValue("noOfItems", "8");
	String categoryToDisplay  = portletPreferences.getValue("categoryToDisplay", "-1");
	String sortBy = ParamUtil.getString(request, "sortBy", "createDate DESC");
	int totalCount = ShoppingItemLocalServiceUtil.getItemByCategoryCount(Long.valueOf(categoryToDisplay));
	
	if(Validator.isNotNull(searchParam) && !searchParam.isEmpty()) {
		totalCount = ShoppingItemLocalServiceUtil.getItemByTagNameCount(searchParam);
	} else {
		if(categoryToDisplay.equalsIgnoreCase("-1")) {
			totalCount = ShoppingItemLocalServiceUtil.getByStatusCount(HConstants.APPROVE);
		} else {
			totalCount = ShoppingItemLocalServiceUtil.getItemByCategoryCount(Long.valueOf(categoryToDisplay));
		}
	}
	String[] autoCompleteList = ShoppingItemLocalServiceUtil.getAutoCompleteItems();
	String autoCompleteListToString = CommonUtil.toJavaScriptArray(autoCompleteList);
%>
<style>
#no-item-display {
	text-align: center;
	display: none;
}
</style>

<portlet:actionURL var="addToCartURL" name="addItemToCart">
	<portlet:param name="isRedirectDisabled" value="true"/>
</portlet:actionURL>

<aui:input name="len" type="hidden"/>
<div class="shopListSort row-fluid">
	<div class="span6">
		<aui:select name="sort-by" inlineLabel="true" onChange="javascript:refresh(this);" >
			<aui:option value="createDate DESC" label="new"/>
			<aui:option value="name ASC" label="Name(A - Z)" />
			<aui:option value="name DESC" label="Name(Z - A)" />
			<aui:option value="totalPrice DESC" label="Price High To Low" />
			<aui:option value="totalPrice ASC" label="Price Low To High"/>
		</aui:select>
	</div>
	<div class="span6">
		<div class="pull-right">
			<h5>
				<liferay-ui:message key="showing"/>
				<span id="current_count"></span> 
				<liferay-ui:message key="of"/>
				<span id="total_count">
					<%=totalCount %>
				</span>
			</h5>
		</div>
	</div>
</div>	
<ul id="shopping_list" class="row-fluid">
	<!-- item display -->
</ul>
<div id="no-item-display" >
	<h2>No Items to Display</h2>
</div>
<div id="loader-icon" >
	<img src="<%=request.getContextPath()%>/images/loader.gif" style="width: 100px; height: 100px"/>
</div>

<div onclick="javascript:loadProducts()" id="load-button" style="display: none;">
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
	var dataLen = 0;
	var portletId = '<%= themeDisplay.getPortletDisplay().getId() %>';
	var searchParam = '<%=searchParam%>';
	
	$(window).load(function(){
		if(<%=totalCount%> != 0) {
			$('#loader-icon').hide();
		} else {
			$('#no-item-display').show();
		}
	
		if(searchParam != 'null' && searchParam != "") {
			getSearchShoppingItems(0, <%=noOfItems%>);
		} else {
			getShoppingItems(0, <%=noOfItems%>);
		}
		
		$('#<portlet:namespace/>sort-by option[value="<%=sortBy%>"').attr("selected", "selected")
		window.onload = $("#<portlet:namespace/>len").val(<%=noOfItems%>);
		$('#current_count').html(dataLen);
	});
	$(window).ready(function(){
		$('#load-button').show();
	});
	<c:choose>
		<c:when test="<%=Validator.isNotNull(searchParam) && !searchParam.isEmpty()%>">
			function loadProducts() {
				var len = $("#<portlet:namespace/>len").val();
				var count = parseInt(len);
				getSearchShoppingItems(count, parseInt(count) + parseInt(<%=noOfItems%>));
			}
		</c:when>
		<c:otherwise>
			function loadProducts() {
				var len = $("#<portlet:namespace/>len").val();
				var count = parseInt(len);
				getShoppingItems(count, parseInt(count) + parseInt(<%=noOfItems%>));
			}
		</c:otherwise>
	</c:choose>
	
	
	function getShoppingItems(s, e) {
		$.ajax({
			url : '<%=themeDisplay.getPortalURL()+"/api/jsonws/htmsd-portlet.shoppingitem/get-shopping-items"%>',
			type : "GET",
			data : {
				p_auth: Liferay.authToken,
				categoryId : <%=categoryToDisplay%>,
				currencyId : <%=currencyId%>, 
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
					dataLen = parseInt(dataLen) + parseInt(data.length);
					$('#current_count').html(dataLen);
					if(dataLen == parseInt(<%=totalCount%>)) {
						$('#load-button').hide();
					}
				} else {
					$('#load-button').hide();
					$("#<portlet:namespace/>len").val(s);
				}
			},
			error : function() {
			}
		});
	}
	
	function getSearchShoppingItems(s, e) {
		console.info("Searching . . .");
		if (searchParam) {
			$.ajax({
				url : '<%=themeDisplay.getPortalURL()+"/api/jsonws/htmsd-portlet.shoppingitem/get-shopping-items-bt-tag-name"%>',
				type : "GET",
				data : {
					p_auth: Liferay.authToken,
					tagName : searchParam,
					sortBy: '<%=sortBy%>',
					groupId : <%=themeDisplay.getScopeGroupId()%>,
					currencyId : <%=currencyId%>,
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
						if(dataLen == parseInt(<%=totalCount%>)) {
							$('#load-button').hide();
						}
					} else {
						$('#load-button').hide();
						$("#<portlet:namespace/>len").val(s);
					}
				},
				error : function() {
				}
			});
		}
	}

	function render(data) {
		AUI().use('liferay-portlet-url', function(A) {
			$.each(data, function(i, item) {
				var cssClass = "span3", li;
				if (i % 4 === 0) {
					cssClass += ' first margin_left_zero';
				}
				
				var detailsURL = Liferay.PortletURL.createRenderURL();
				detailsURL.setPortletId(portletId);
				detailsURL.setParameter('jspPage', "/html/shoppinglist/details.jsp");
				detailsURL.setParameter('itemId', item.itemId);
				
				var addToCartURL = '${addToCartURL}' + "&<portlet:namespace/>itemId="+item.itemId;
				
				var isNew = (item.isNewItem) ? '<span class="new"><i>New</i></span>' : '';
				var MRP = formatPrice(item.MRP).toString();
				if (item.MRP === 0 ) {
					MRP = '';				
				}
				
				li = '<li class="' + cssClass + '">'
						+'<div class="product">'
							+'<div class="product-image">'
								+'<a href="'+detailsURL+'">'
									+'<img src=' + item.image + ' />'
									+ isNew
								+'</a>'
								+ '<div class="hover_fly">'
									+'<a href="'+addToCartURL+'">'
										+'<div>'
											+'<i class="fa fa-shopping-cart"></i>'
											+'<span>Add to cart</span>'
										+'</div>'
									+'</a>'
								+'</div>'
							+ '</div>'
							+'<div class="product-details">'
								+'<a href="'+detailsURL+'">'
									+ '<h4 class="item-name">' + item.name + '</h4>'
								+'</a>'	
							 	+ '<p class="description">' + item.shortDescription + '</p>' 
								+ '<h6 id="price">' + formatPrice(item.totalPrice)
									+ '<span class="old-price product-price">'+ MRP +'</span>'									
								+ '</h6>'
							+'</div>'
						+'</div>'
					+'</li>';	
				$("#shopping_list").append(li);		
			});
		});
		var griditems = $("#shopping_list > li");
	    for (var i = 0; i < griditems.length; i+=4) {
	    	griditems.slice(i, i+4).wrapAll("<ul class='row-fluid'></ul>");
	    }
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
	
	$(function() {
		$('#search_query_top').val('<%=searchParam%>');
	})
</aui:script>