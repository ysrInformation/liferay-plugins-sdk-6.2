<%@page import="com.htmsd.util.CommonUtil"%>
<%@page import="java.text.DecimalFormat"%>
<%@include file="/html/shoppinglist/init.jsp" %>

<%
	List<ShoppingItem> shoppingItems =  
		ShoppingItemLocalServiceUtil.getByStatus(HConstants.APPROVE, -1, -1);
	DecimalFormat decimalFormat = new DecimalFormat("#.00");
%>

<ul id="shopping_list" class="row">
	<%
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
    							<img src="<%=CommonUtil.getThumbnailpath(imageId, themeDisplay) %>" />
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
	%>
</ul>

<script>

	//Function that renders the list items from our records
	function ulWriter(rowIndex, record, columns, cellWriter) {
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
			perPageOptions : [10, 20, 30, 40, 50, 100 ]
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
	});
</script>