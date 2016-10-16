function formatPrice(number) {
	accounting.settings.currency.symbol = '&#8377;';
	var text = '';
	text = accounting.formatMoney(Math.abs(number));
	return text;
}

function showPageInPopup(url, height, width, title){
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

function getShoppingItems(categoryId, currencyId, groupId, status, start, end, sort, url, plid, namespace) {
	$.ajax({
		url : Liferay.ThemeDisplay.getPortalURL() + '/api/jsonws/htmsd-portlet.shoppingitem/get-shopping-items',
		type : "GET",
		dataType : "json",
		data : {
			p_auth: Liferay.authToken,
			categoryId : categoryId,
			currencyId : currencyId, 
			groupId : groupId,
			status : status,
			start : start,
			end: end,
			sortBy: sort
		},
		beforeSend : function() {
			$('#loader-icon-'+namespace).show();
		},
		complete : function() {
			$('#loader-icon-'+namespace).hide();
		},
		success : function(data) {
			if (data.length > 0) {	
				render(data, url, namespace, plid);
			} else {
				$('#no-item-display').show();
			}
		},
		error : function() {
		}
	});
}

function render(data, url, namespace, plid) {
	AUI().use('liferay-portlet-url', function(A) {
		$.each(data, function(i, item) {
			var detailsURL = Liferay.PortletURL.createRenderURL();
			detailsURL.setPortletId("3_WAR_htmsdportlet");
			detailsURL.setParameter("p_l_id", plid);
			detailsURL.setParameter('jspPage', "/html/shoppinglist/details.jsp");
			detailsURL.setParameter('itemId', item.itemId);
			
			var addToCartURL = url + "&"+namespace+"itemId="+item.itemId;
			
			var isNew = (item.isNewItem) ? '<span class="new"><i>New</i></span>' : '';
			li = '<div class="swiper-slide">'
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
							/* + '<p class="description">' + item.description + '</p>' */
							+ '<h6 id="price">' + formatPrice(item.totalPrice); + '</h6>'
						+'</div>'
					+'</div>'
				+'</div>';
			$("#featured_list_"+namespace).append(li);		
		});
		var swiper = new Swiper('.swiper-container', {
	        slidesPerView: 6,
	        paginationClickable: true,
	        spaceBetween: 35,
	        nextButton: '.swiper-button-next',
	        prevButton: '.swiper-button-prev',
	        autoplay: 3500,
	        autoplayDisableOnInteraction: false,
	    });
	});
}