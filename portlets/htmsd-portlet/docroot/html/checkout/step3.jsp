<%@include file="/html/checkout/init.jsp" %>

<portlet:actionURL var="step3URL" name="step3"/>

<%
	String[] addressArray = new String[6];
	addressArray = CommonUtil.getUserAddress(themeDisplay.getUserId());

	long countryId = Long.parseLong(addressArray[3]);
	long regionId = Long.parseLong(addressArray[4]);
	String street = addressArray[0];
	String city = addressArray[1];
	String zip = addressArray[2];
	String mobileNumber = addressArray[5];
%>

<div class="checkout-details-container">
	<h1 id="checkout-addresslabel" class="page-heading"><liferay-ui:message key="checkout-label-your-address"/></h1>
	<div class="checkout-address-form">
		<aui:form id="checkout-step3" name="step3" method="post" action="${step3URL}" inlineLabels="<%= true %>">  
			
			<aui:input type="text" name="firstName" value="<%= user.getFirstName()  %>">
				<aui:validator name="required"/>
			</aui:input>
			
			<aui:input type="text" name="lastName" value="<%= user.getLastName() %>">
				<aui:validator name="required"/>
			</aui:input>
			
			<aui:input type="email" name="email" value="<%= user.getEmailAddress() %>">
				<aui:validator name="required"/>
			</aui:input>
			
			<aui:input type="text" name="mobileNumber" label="mobile-number" value="<%= mobileNumber  %>" required="true" maxLength="10">
				<aui:validator name="digits"/>
		        <aui:validator name="rangeLength" errorMessage="please-enter-valid-mobile-number">[10,10]</aui:validator>
			</aui:input>
			
			<aui:input type="text" name="altNumber" label="alt-number" maxLength="10">
				<aui:validator name="digits"/>
		        <aui:validator name="rangeLength" errorMessage="please-enter-valid-mobile-number">[10,10]</aui:validator>
			</aui:input>
			
			<div class="checkout-shipping-address">
				<p class="page-heading shippingAdress"><liferay-ui:message key="checkout-label-shipping-address"/></p>
				<aui:input type="text" name="street" label="street" value="<%= street %>">
					<aui:validator name="required"/>
				</aui:input>
				
				<aui:input type="text" name="city" label="city" value="<%= city %>">
					<aui:validator name="required"/>
				</aui:input>
				
				<aui:input type="text" name="zip" label="post-code" required="true" value="<%= zip %>" maxLength="6">
					<aui:validator name="digits" />
			        <aui:validator name="rangeLength" errorMessage="please-enter-valid-pincode-number">[6,6]</aui:validator>
				</aui:input>
				
				<aui:select name="country" label="country" id="country" required="true"/>
				
				<aui:select name="state" label="state" id="state" required="true"/> 
			</div>
	
			<aui:button-row>
				<aui:button cssClass="pull-left" type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "continue-shopping") %>' href="<%= continueShoppingURL %>"/>
				<aui:button cssClass="pull-right" type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "step1") %>' />
			</aui:button-row>
		</aui:form>
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