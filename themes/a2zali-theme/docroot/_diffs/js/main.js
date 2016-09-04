AUI().ready(
	'liferay-hudcrumbs', 'liferay-navigation-interaction', 'liferay-sign-in-modal',
	function(A) {
		var navigation = A.one('#navigation');

		if (navigation) {
			navigation.plug(Liferay.NavigationInteraction);
		}

		var siteBreadcrumbs = A.one('#breadcrumbs');

		if (siteBreadcrumbs) {
			siteBreadcrumbs.plug(A.Hudcrumbs);
		}

		var signIn = A.one('li.sign-in a');

		if (signIn && signIn.getData('redirect') !== 'true') {
			signIn.plug(Liferay.SignInModal);
		}
	}
);

$(function(){
	
	$(".quick-contact .control-group").removeClass("lfr-textarea-container");
	$(".quick-contact .control-group").addClass( "form-inline" );
	

	var elTop = $('#search-div').offset().top;
	var mobile = !(Liferay.Util.isPhone() || Liferay.Util.isTablet());
		$(window).scroll(function() {
			if(mobile) {
	        $('#search-div').toggleClass('sticky', $(window).scrollTop() > elTop);
	        $('#fixed_div').toggleClass('fixed_div', $(window).scrollTop() > elTop);
		} else {
			$('#srch-btn').html('<i class="fa fa-search"></i>');
		}
	});
	
	$(document).on( 'scroll', function(){
		if ($(window).scrollTop() > 100) {
			$('#to_top a').removeClass('disabled');
		} else {
			$('#to_top a').addClass('disabled');
		}
	});
	$('#to_top').on('click', scrollToTop);
	
	// auto complete code
	AUI().use('aui-base', function(A) {
		Liferay.Service('/htmsd-portlet.shoppingitem/get-autocomplete-items',
	  		function(data) {
	  			var autocompleteData = data.autoCompleteData;
				$("#search").autocomplete({
					source : autocompleteData
				});
	  		}
	  	);
	});
	
	function scrollToTop() {
		verticalOffset = typeof(verticalOffset) != 'undefined' ? verticalOffset : 0;
		element = $('body');
		offset = element.offset();
		offsetTop = offset.top;
		$('html, body').animate({scrollTop: offsetTop}, 500, 'linear');
	}

	AUI().use('aui-base', function(A) {
		Liferay.Service('/htmsd-portlet.shoppingcart/get-shopping-cart-item-count', {
			userId: Liferay.ThemeDisplay.getUserId()
		}, function(obj) {
			console.info("Item COunt");
			try {
				if (parseInt(obj) > 0) {
					$('.ajax_cart_quantity').show();
					$('.ajax_cart_quantity').html("&nbsp;"+obj+"&nbsp;");
				}
			} catch(err) {
				console.error(err);
			}
		});
	});

	function searchItems() {
		var searchParam = $('#search').val();
		if(searchParam) {
			window.location.href = '/search?search-param=' + searchParam;
		} 
	}
	var menuLeft = $('.pushmenu-left');
	var nav_list = $('#nav_list');
	var close = $('#pushMenuclose');
	  
	nav_list.click(function() {
		$(this).toggleClass('active');
	$('.pushmenu-push').toggleClass('pushmenu-push-toright');
	menuLeft.toggleClass('pushmenu-open');
	  });
	  
	close.click(function() {
		$('.pushmenu-push').toggleClass('pushmenu-push-toright');
		menuLeft.toggleClass('pushmenu-open');
	});
});