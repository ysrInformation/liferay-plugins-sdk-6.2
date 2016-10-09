<%@page import="com.htmsd.slayer.model.UserInfo"%>
<%@include file="/html/checkout/init.jsp" %>

<%
	String[] addressArray = new String[6];
	addressArray = CommonUtil.getUserAddress(themeDisplay.getUserId());

	boolean isUserHasAddress = CommonUtil.isUserHasAddress(themeDisplay.getUserId());
	long countryId = Long.parseLong(addressArray[3]);
	long regionId = Long.parseLong(addressArray[4]);
	String street = addressArray[0];
	String city = addressArray[1];
	String zip = addressArray[2];
	String mobileNumber = addressArray[5];
	
	UserInfo _userInfo = CommonUtil.findUserInfoByUserId(themeDisplay.getUserId());
	String altNumber = StringPool.BLANK;
	
	if (Validator.isNotNull(_userInfo)) {
		mobileNumber = (mobileNumber.isEmpty()) ? _userInfo.getMobileNumber() : mobileNumber;	
		altNumber = _userInfo.getAltNumber();
	}
	
	PortletURL processcheckoutURL = renderResponse.createRenderURL();
	processcheckoutURL.setParameter("jspPage", "/html/checkout/view.jsp");
	processcheckoutURL.setParameter("order_step", "step5");
	
	PortletURL updateUserInfoURL = renderResponse.createRenderURL();
	updateUserInfoURL.setParameter("jspPage", "/html/checkout/address.jsp");
	updateUserInfoURL.setParameter("backURL", themeDisplay.getURLCurrent());
%>

<div class="checkout-address-page">
	<div class="row-fluid">
		<div class="span6">
			<ul class="address first_item">
				<li>
					<h3 class="page-heading">
						<liferay-ui:message key="checkout-label-shipping-address"/> <span> (My address)</span>
					</h3>
				</li>
				<c:choose>
					<c:when test='<%= isUserHasAddress %>'> 
						<li><span class="address_name"> <%= user.getFullName() %> </span></li>
						<li><span class="address_street"><%= street %></span></li>
						<li><span class="address_city"><%= city %></span></li>
						<li><span class=""><%= CommonUtil.getState(regionId)  %></span></li>
						<li><span class="address_phone"><%= CommonUtil.getCountry(countryId) + ", " + zip  %> </span></li>
						<li><span class="address_phone_mobile"><%= mobileNumber %></span></li>
						<li><span class="address_alt_mobile"><%= altNumber %></span></li>
					</c:when>
					<c:otherwise>
						<li><h3><liferay-ui:message key="please-update-your-address"/></h3></li>
					</c:otherwise>
				</c:choose>
			</ul>
		</div>
		<div class="span6">
			<ul class="address second_item">
				<li>
					<h3 class="page-heading">
						<liferay-ui:message key="checkout-label-billing-address"/> <span> (My address)</span>
					</h3>
				</li>
				<c:choose>
					<c:when test='<%= isUserHasAddress %>'> 
						<li><span class="address_name"> <%= user.getFullName() %> </span></li>
						<li><span class="address_street"><%= street %></span></li>
						<li><span class="address_city"><%= city %></span></li>
						<li><span class=""><%= CommonUtil.getState(regionId)  %></span></li>
						<li><span class="address_phone"><%= CommonUtil.getCountry(countryId) + ", " + zip  %> </span></li>
						<li><span class="address_phone_mobile"><%= mobileNumber %></span></li>
						<li><span class="address_alt_mobile"><%= altNumber %></span></li>
					</c:when>
					<c:otherwise>
						<li><liferay-ui:message key="please-update-your-address"/></li>
					</c:otherwise>
				</c:choose>
			</ul>
		</div>
		<aui:button cssClass="btn-primary" type="button" value='<%= LanguageUtil.get(portletConfig, locale, "update") %>' href="<%= updateUserInfoURL.toString() %>"/> 
	</div>	
	<aui:button-row>
		<aui:button cssClass="pull-left btn-primary" type="button" value='<%= LanguageUtil.get(portletConfig, locale, "continue-shopping") %>' href="<%= continueShoppingURL %>"/>
		<aui:button cssClass="pull-right btn-primary" id="proceeedCheckoutId" type="button" value='<%= LanguageUtil.get(portletConfig, locale, "step1") %>'  data-url="<%= processcheckoutURL.toString() %>"/> 
	</aui:button-row>
</div>

<script type="text/javascript"> 
$("#proceeedCheckoutId").click(function(){
	var $this = $(this);
	var url = $this.attr("data-url");
	var bool = '<%= isUserHasAddress %>'; 
	
	if (!bool) {
		alert("Please update your address to proceed checkout.");
	} else {
		location.href = url.toString();
	}
});
</script>