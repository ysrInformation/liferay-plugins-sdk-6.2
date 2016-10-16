<%@include file="/html/checkout/init.jsp" %>

<portlet:actionURL var="updateUserURL" name="updateUserInfo"/>

<%
	UserInfo _userInfo = null;
	
	long regionId = 0, countryId = 0, listTypeId = 0;
	long userInfoId = ParamUtil.getLong(request, "userInfoId"); 
	String backURL = ParamUtil.getString(request, "backURL");
	
	String street = StringPool.BLANK, city = StringPool.BLANK, zip = StringPool.BLANK;
	if (Validator.isNotNull(userInfoId)) {
		_userInfo = UserInfoLocalServiceUtil.fetchUserInfo(userInfoId);
		Address address = CommonUtil.getAddressByAddressId(_userInfo.getShippingAddressId());
		regionId = (Validator.isNotNull(address)) ? address.getRegionId() : 0;
		countryId = (Validator.isNotNull(address)) ? address.getCountryId() : 0;
		street = (Validator.isNotNull(address.getStreet1())) ? address.getStreet1() : StringPool.BLANK;
		city = (Validator.isNotNull(address.getCity())) ? address.getCity() : StringPool.BLANK;
		zip = (Validator.isNotNull(address.getZip())) ? address.getZip() : StringPool.BLANK;
		listTypeId = (Validator.isNotNull(address)) ? address.getTypeId() : 0l; 
	} else {
		_userInfo = new UserInfoImpl();
		userInfoId = 0;
	}
	
	String type = Contact.class.getName() + ListTypeConstants.ADDRESS;
	List<ListType> listTypes = ListTypeServiceUtil.getListTypes(type);
%>

<div class="new-checkout">
	<div class="checkout-details-container">
		<h1 id="checkout-addresslabel" class="page-heading"><liferay-ui:message key="checkout-label-your-address"/></h1>
		<div class="checkout-address-form">
			<aui:form id="checkout-step3" name="updateUserFm" method="post" action="${updateUserURL}" inlineLabels="<%= true %>">  
				
				<aui:input name="addressId" type="hidden" value="<%= _userInfo.getShippingAddressId() %>" /> 
				<aui:input name="userInfoId" type="hidden" value="<%= userInfoId %>" />
				
				<aui:input type="text" name="firstName" value="<%= _userInfo.getFirstName()  %>" inlineField="true">
					<aui:validator name="required"/>
				</aui:input>
				
				<aui:input type="text" name="lastName" value="<%= _userInfo.getLastName() %>" inlineField="true">
					<aui:validator name="required"/>
				</aui:input>
				
				<aui:input type="email" name="email" value="<%= _userInfo.getEmail() %>" inlineField="true">
					<aui:validator name="required"/>
				</aui:input>
				
				<aui:input type="text" name="mobileNumber" label="mobile-number" inlineField="true" value="<%= _userInfo.getMobileNumber()  %>" required="true" maxLength="10">
					<aui:validator name="digits"/>
			        <aui:validator name="rangeLength" errorMessage="please-enter-valid-mobile-number">[10,10]</aui:validator>
				</aui:input>
				
				<aui:input type="text" name="altNumber" label="alt-number" inlineField="true" maxLength="10" value="<%= _userInfo.getAltNumber() %>"> 
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
					
					<aui:select name="typeId" label="checkout-address-type" inlineField="true">
						<% for (ListType listType : listTypes) { %> 
							<aui:option value="<%= listType.getListTypeId() %>" label="<%= TextFormatter.format(listType.getName(), TextFormatter.J) %>"
								selected='<%= (listTypeId == listType.getListTypeId()) %>' />
						<% } %>
					</aui:select>
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