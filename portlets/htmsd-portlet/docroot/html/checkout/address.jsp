<%@page import="com.htmsd.slayer.model.UserInfo"%>
<%@include file="/html/checkout/init.jsp" %>

<portlet:actionURL var="updateUserURL" name="updateUserInfo"/>

<%
	String[] addressArray = new String[6];
	addressArray = CommonUtil.getUserAddress(themeDisplay.getUserId());
	UserInfo _userInfo = CommonUtil.findUserInfoByUserId(themeDisplay.getUserId());
	
	long addressId = CommonUtil.getAddressId(themeDisplay.getUserId());
	long countryId = Long.parseLong(addressArray[3]);
	long regionId = Long.parseLong(addressArray[4]);
	String street = addressArray[0];
	String city = addressArray[1];
	String zip = addressArray[2];
	String mobileNumber = addressArray[5];
	String altNumber = StringPool.BLANK;
	
	if (Validator.isNotNull(_userInfo)) {
		mobileNumber = (mobileNumber.isEmpty()) ? _userInfo.getMobileNumber() : mobileNumber;	
		altNumber = _userInfo.getAltNumber();
	}
	String backURL = ParamUtil.getString(request, "backURL");
%>

<div class="new-checkout">
	<div class="checkout-details-container">
		<h1 id="checkout-addresslabel" class="page-heading"><liferay-ui:message key="checkout-label-your-address"/></h1>
		<div class="checkout-address-form">
			<aui:form id="checkout-step3" name="updateUserFm" method="post" action="${updateUserURL}" inlineLabels="<%= true %>">  
				
				<aui:input name="addressId" type="hidden" value="<%= addressId %>" /> 
				
				<aui:input type="text" name="firstName" value="<%= user.getFirstName()  %>" inlineField="true">
					<aui:validator name="required"/>
				</aui:input>
				
				<aui:input type="text" name="lastName" value="<%= user.getLastName() %>" inlineField="true">
					<aui:validator name="required"/>
				</aui:input>
				
				<aui:input type="email" name="email" value="<%= user.getEmailAddress() %>" inlineField="true">
					<aui:validator name="required"/>
				</aui:input>
				
				<aui:input type="text" name="mobileNumber" label="mobile-number" inlineField="true" value="<%= mobileNumber  %>" required="true" maxLength="10">
					<aui:validator name="digits"/>
			        <aui:validator name="rangeLength" errorMessage="please-enter-valid-mobile-number">[10,10]</aui:validator>
				</aui:input>
				
				<aui:input type="text" name="altNumber" label="alt-number" inlineField="true" maxLength="10" value="<%= altNumber %>"> 
					<aui:validator name="digits"/>
			        <aui:validator name="rangeLength" errorMessage="please-enter-valid-mobile-number">[10,10]</aui:validator>
				</aui:input>
				
				<div class="checkout-shipping-address">
					<p class="page-heading shippingAdress"><liferay-ui:message key="checkout-label-shipping-address"/></p>
					<aui:input type="text" name="street" inlineField="true" label="street" value="<%= street %>">
						<aui:validator name="required"/>
					</aui:input>
					
					<aui:input type="text" name="city" inlineField="true" label="city" value="<%= city %>">
						<aui:validator name="required"/>
					</aui:input>
					
					<aui:input type="text" name="zip" inlineField="true" label="post-code" required="true" value="<%= zip %>" maxLength="6">
						<aui:validator name="digits" />
				        <aui:validator name="rangeLength" errorMessage="please-enter-valid-pincode-number">[6,6]</aui:validator>
					</aui:input>
					
					<aui:select name="country" inlineField="true" label="country" id="country" required="true"/>
					
					<aui:select name="state" label="state" inlineField="true" id="state" required="true"/> 
				</div>
		
				<aui:button-row>
					<aui:button type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "update") %>' />
					<aui:button cssClass="btn-primary" type="button" value='<%= LanguageUtil.get(portletConfig, locale, "cancel") %>' href="<%= backURL %>" /> 
				</aui:button-row>
			</aui:form>
		</div>
	</div>
</div>

<aui:script use="liferay-dynamic-select">
new Liferay.DynamicSelect(
	[
		{
			select: '<portlet:namespace />country',
			selectData: Liferay.Address.getCountries,
			selectDesc: 'nameCurrentValue',
			selectSort: '<%= true %>',
			selectId: 'countryId',
			selectVal: '<%= countryId %>'
		},
		{
			select: '<portlet:namespace />state',
			selectData: Liferay.Address.getRegions,
			selectDesc: 'name',
			selectId: 'regionId',
			selectVal: '<%= regionId %>'
		}
	]
);
</aui:script>