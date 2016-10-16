<%@page import="javax.portlet.ActionRequest"%>
<%@include file="/html/checkout/init.jsp" %>

<%
	String[] addressArray = new String[6];
	addressArray = CommonUtil.getUserAddress(themeDisplay.getUserId());

	int count = 0;
	boolean isUserHasAddress = CommonUtil.isUserHasAddress(themeDisplay.getUserId());

	List<UserInfo> userInfoList = UserInfoLocalServiceUtil.getUserAddressFromUserInfo(themeDisplay.getUserId(), 
			themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId());
	
	PortletURL setDeliveryAddressURL = renderResponse.createActionURL();
	setDeliveryAddressURL.setParameter(ActionRequest.ACTION_NAME, "setDeliveryAddress");
	
	PortletURL updateUserInfoURL = renderResponse.createRenderURL();
	updateUserInfoURL.setParameter("jspPage", "/html/checkout/address.jsp");
	updateUserInfoURL.setParameter("backURL", themeDisplay.getURLCurrent());
%>

<div class="checkout-address-page">
	 <div class="add-new-address">
	 	<aui:button type="button" cssClass="btn-primary"  value='<%= LanguageUtil.get(portletConfig, locale, "add-new-address") %>' href="<%= updateUserInfoURL.toString() %>"/> 
	 </div>
	<div class="row-fluid">
		<c:choose>
			<c:when test='<%= Validator.isNotNull(userInfoList) && userInfoList.size() > 0 %>'> 
				<% 
				for (UserInfo userInfo : userInfoList) { 
					updateUserInfoURL.setParameter("userInfoId", String.valueOf(userInfo.getUserInfoId()));
					setDeliveryAddressURL.setParameter("userInfoId", String.valueOf(userInfo.getUserInfoId()));
					Address address = CommonUtil.getAddressByAddressId(userInfo.getShippingAddressId());
					String zip = (Validator.isNotNull(address.getZip())) ? address.getZip() : StringPool.BLANK;
					String firstName = Validator.isNotNull(userInfo.getFirstName()) ? userInfo.getFirstName() : StringPool.BLANK;
					String lastName = Validator.isNotNull(userInfo.getLastName()) ? userInfo.getLastName() : StringPool.BLANK;
					%>
					<div class="span4 address-div">
						<ul class="address first_item">
							<li>
								<h3 class="page-heading">
									<liferay-ui:message key="checkout-label-shipping-address"/> <span> (My address <%= count+1 %>)</span>
								</h3>
							</li>
							<c:choose>
								<c:when test='<%= isUserHasAddress %>'> 
									<li><span class="address_name"> <%= firstName + StringPool.SPACE + lastName %> </span></li>
									<li><span class="address_street"><%= Validator.isNotNull(address.getStreet1()) ? address.getStreet1() : StringPool.BLANK %></span></li>
									<li><span class="address_city"><%= Validator.isNotNull(address.getCity()) ? address.getCity() : StringPool.BLANK %></span></li>
									<li><span class=""><%= Validator.isNotNull(address.getRegionId()) ? CommonUtil.getState(address.getRegionId()) : StringPool.BLANK %></span></li>
									<li><span class="address_phone"><%= Validator.isNotNull(address.getCountryId()) ? CommonUtil.getCountry(address.getCountryId()) : StringPool.BLANK  + ", " + zip  %> </span></li>
									<li><span class="address_phone_mobile"><%= Validator.isNotNull(userInfo.getMobileNumber()) ?  userInfo.getMobileNumber(): StringPool.BLANK %></span></li>
									<li><span class="address_alt_mobile"><%= Validator.isNotNull(userInfo.getAltNumber()) ? userInfo.getAltNumber() : StringPool.BLANK %></span></li>
								</c:when>
								<c:otherwise>
									<li><h3><liferay-ui:message key="please-update-your-address"/></h3></li>
								</c:otherwise>
							</c:choose>
						</ul>
						<aui:button-row cssClass="address-btns"> 
							<aui:button cssClass="btn-primary" type="button" value='<%= LanguageUtil.get(portletConfig, locale, "edit") %>' href="<%= updateUserInfoURL.toString() %>"/>
							<aui:button cssClass="btn-primary" type="button" value='<%= LanguageUtil.get(portletConfig, locale, "set-this-as-delivery-address") %>' href="<%= setDeliveryAddressURL.toString() %>"/>
						</aui:button-row>
					</div>
					<% 
					count++;
					if ((count) % 3 == 0) { 
						%></div><div class="row-fluid"><% 
					}
				} 
				%>
			</c:when>
			<c:otherwise>
				<div class="span6"><h3><liferay-ui:message key="please-update-your-address"/></h3></div>
			</c:otherwise>
		</c:choose>
	</div>	
	<aui:button-row>
		<aui:button cssClass="pull-left btn-primary" type="button" value='<%= LanguageUtil.get(portletConfig, locale, "continue-shopping") %>' href="<%= continueShoppingURL %>"/>
		<%-- <aui:button cssClass="pull-right btn-primary" id="proceeedCheckoutId" type="button" value='<%= LanguageUtil.get(portletConfig, locale, "step1") %>'  data-url="<%= processcheckoutURL.toString() %>"/> --%> 
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