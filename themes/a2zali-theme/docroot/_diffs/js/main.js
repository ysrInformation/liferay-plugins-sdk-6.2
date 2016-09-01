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

//adding inline class to web form(Quick contact) home page
$(function() {
	$(".quick-contact .control-group").removeClass("lfr-textarea-container");
	$(".quick-contact .control-group").addClass( "form-inline" );
});


//Search 
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

//Scroll to Top
$(function(){
	$(document).on( 'scroll', function(){
		if ($(window).scrollTop() > 100) {
			$('#to_top').addClass('disabled');
		} else {
			$('#to_top').removeClass('disabled');
		}
	});
	$('#to_top').on('click', scrollToTop);
	
	// auto complete code
	AUI().use('aui-base', function(A) {
		Liferay.Service('/htmsd-portlet.shoppingitem/get-autocomplete-items',
	  		function(data) {
	  			var autocompleteData = data.autoCompleteData;
	  			console.log(autocompleteData);
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
			userId: $user_id
		}, function(obj) {
			$('#cart-item-count').html("&nbsp;"+obj+"&nbsp;");
		});
	});

	function searchItems() {
		var searchParam = $('#search').val();
		if(searchParam) {
			window.location.href = '/search?search-param=' + searchParam;
		} 
	}
});