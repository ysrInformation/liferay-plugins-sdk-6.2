<%@page import="com.htmsd.util.CommonUtil"%>
<%@page import="java.text.DecimalFormat"%>
<%@include file="/html/shoppinglist/init.jsp" %>

<%
	List<ShoppingItem> shoppingItems =  
		ShoppingItemLocalServiceUtil.getByStatus(HConstants.APPROVE, -1, -1);
	DecimalFormat decimalFormat = new DecimalFormat("#.00");
%>
<aui:input name="len" type="hidden"/>
<ul id="shopping_list" class="row">
<%-- 	<%
		for(int i = 0; i < shoppingItems.size(); i++ )  {
			ShoppingItem shoppingItem = shoppingItems.get(i);	
			%>
				<li class="span3" data-color="gray">
					<div class="product">
						<div class="product-image">
							<% 
    							String imageIds = shoppingItem.getImageIds();
    							long imageId = 0l;
    							if(Validator.isNotNull(imageIds)) {
    								imageId = (imageIds.split(",").length > 0) ? Long.valueOf(imageIds.split(",")[0]) : 0;
    							}
    						%>
    						<a href="#">	
    							<img src="<%=CommonUtil.getThumbnailpath(imageId, themeDisplay.getScopeGroupId()) %>" />
    						</a>
						</div>
						<div class="product-details">
							<h4 class="text-color-red"><%= shoppingItem.getName() %></h4>
    						<p><%= shoppingItem.getDescription() %></p>
    						<h6><%= "Rs. "+decimalFormat.format(shoppingItem.getSellerPrice() + shoppingItem.getTotalPrice())  %></h6>
						</div>
					</div>
				</li>
			<%
		}
	%> --%>
</ul>
<div id="loader-icon" >
	<img src="<%=request.getContextPath()%>/images/loader.gif" style="width: 100px; height: 100px"/>
</div>
<script>

	$(function() {
		window.onload = $("#<portlet:namespace/>len").val(8);
		getShoppingItems(0, 8);
		$(window).scroll(function() {
			var len = $("#<portlet:namespace/>len").val();
			var count = parseInt(len);
			 if ($(window).scrollTop() == $(document).height() - $(window).height()-1) {
				getShoppingItems(count, parseInt(count) + parseInt(4));
			}
		});
	});
	
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
					$("#<portlet:namespace/>len").val(s);
				}
			},
			error : function() {
			}
		});
	}

	function render(data) {
		$.each(data, function(i, item) {
			var cssClass = "span3", li;
			if (i % 4 === 0) {
				cssClass += ' first margin_left_zero';
			}
			li = '<li class="' + cssClass + '"><div class="product"><div class="product-image">'
					+ '<a href="#">	<img src=' + item.image + ' /></a>'
					+ '</div><div class="product-details">'
					+ '<h4 class="text-color-red">' + item.name + '</h4>'
					+ '<p>' + item.description + '</p>'
					+ '<h6>' + item.totalPrice + '</h6>'
					+ '</div></div></li>';
			$("#shopping_list").append(li);		
		});
	}
	
	//Function that renders the list items from our records
	/* function ulWriter(rowIndex, record, columns, cellWriter) {
		var cssClass = "span3", li;
		if (rowIndex % 4 === 0) {
			cssClass += ' first margin_left_zero';
		}
		li = '<li class="' + cssClass + '"><div class="product"><div class="product-image">'
				+ record.thumbnail
				+ '</div><div class="product-details">'
				+ record.caption + '</div></div></li>';
		return li;
	}

	// Function that creates our records from the DOM when the page is loaded
	function ulReader(index, li, record) {
		var $li = $(li), $caption = $li.find('.product-details');
		record.thumbnail = $li.find('.product-image').html();
		record.caption = $caption.html();
		record.label = $caption.find('h4').text();
		record.description = $caption.find('p').text();
		record.color = $li.data('color');
	}

	$('#shopping_list').dynatable({
		table : {
			bodyRowSelector : 'li'
		},
		dataset : {
			perPageDefault : 20,
			perPageOptions : [ 10, 20, 30, 40, 50, 100 ]
		},
		writers : {
			_rowWriter : ulWriter
		},
		readers : {
			_rowReader : ulReader
		},
		params : {
			records : 'items'
		}
	}); */
</script>