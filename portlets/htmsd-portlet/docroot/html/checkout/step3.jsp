<%@page import="java.util.Map"%>
<%@include file="/html/checkout/init.jsp" %>

<portlet:actionURL var="setDeliveryAddressURL" name="setDeliveryAddress"/>

<%
	int count = 0;
	boolean isUserHasAddress = CommonUtil.isUserHasAddress(themeDisplay.getUserId());

	List<UserInfo> userInfoList = UserInfoLocalServiceUtil.getUserAddressFromUserInfo(themeDisplay.getUserId(), 
			themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId());
	
	Map<String, Long> addressMap = CommonUtil.getDeliveryAddresses(themeDisplay.getUserId(), 
			themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId());
	
	PortletURL updateUserInfoURL = renderResponse.createRenderURL();
	updateUserInfoURL.setParameter("jspPage", "/html/checkout/address.jsp");
	updateUserInfoURL.setParameter("backURL", themeDisplay.getURLCurrent());
	
	UserInfo currentUserInfo = UserInfoLocalServiceUtil.getUserInfoByUserIdAndIsDelivery(themeDisplay.getUserId(), true);
%>

<div class="checkout-address-page">
	<aui:form name="addressDisplayForm" method="post" action="${setDeliveryAddressURL}" inlineLabels="true">  
		 <div class="add-new-address row-fluid">
		 	<div class="span4"> 
		 		<aui:button type="button" cssClass="btn-primary"  value='<%= LanguageUtil.get(portletConfig, locale, "add-new-address") %>' href="<%= updateUserInfoURL.toString() %>"/>
		 	</div>
		 	<div class="span8">
		 		<aui:select name="userInfoId" required="true" label="please-choose-your-delivery-address">
		 			<aui:option value=""> -- Please select -- </aui:option> 
		 			<% for(Map.Entry<String, Long> addMap : addressMap.entrySet()) { %>
		 				<aui:option value="<%= addMap.getValue() %>" selected='<%= (currentUserInfo != null) ? currentUserInfo.getUserInfoId() == addMap.getValue() : false  %>'><%= addMap.getKey() %></aui:option>  
		 			<% } %>
		 		</aui:select>
		 	</div>
		 </div>
		<div class="row-fluid">
			<c:choose>
				<c:when test='<%= Validator.isNotNull(userInfoList) && userInfoList.size() > 0 %>'> 
					<% 
					for (UserInfo userInfo : userInfoList) { 
						updateUserInfoURL.setParameter("userInfoId", String.valueOf(userInfo.getUserInfoId()));
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
			<aui:button cssClass="pull-right btn-primary" type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "step1") %>' />  
		</aui:button-row>
	</aui:form>
</div>