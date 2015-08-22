<%@ include file="/html/shoppingcart/init.jsp"%>

<portlet:actionURL var="checkoutURL" name="checkout"/>

<%
	String[] addressArray = new String[6];
	addressArray = CommonUtil.getUserAddress(themeDisplay.getUserId());
	
	long countryId = Long.parseLong(addressArray[3]);
	long regionId = Long.parseLong(addressArray[4]);
	String street = addressArray[0];
	String city = addressArray[1];
	String zip = addressArray[2];
	String mobileNumber = addressArray[5];
	
	PortletURL backURL = renderResponse.createRenderURL();
	backURL.setParameter(HConstants.JSP_PAGE, "/html/shoppingcart/view.jsp");
%>

<liferay-ui:header title="please-in-the-details" backURL="<%= backURL.toString() %>"/>

<aui:form action="<%= checkoutURL %>" name="fm"  method="post" inlineLabels="true">

	<aui:column columnWidth="50" first="true"> 
		
		<aui:column columnWidth="30">
			<label><liferay-ui:message key="payment-method"/></label>
		</aui:column>
		
		<aui:column columnWidth="70">
			<aui:input name="paymentMode" type="radio" label="<%= HConstants.CASH_ON_DELIVERY %>" value="cod">
				<aui:validator name="required"/>
			</aui:input>
		</aui:column>
		
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
		
		<aui:input type="text" name="altNumber" label="alt-number" required="true" maxLength="10">
			<aui:validator name="digits"/>
	        <aui:validator name="rangeLength" errorMessage="please-enter-valid-mobile-number">[10,10]</aui:validator>
		</aui:input>
	</aui:column>
	
	<aui:column columnWidth="50">
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
		
		<aui:select name="country" label="country" id="country" />
		
		<aui:select name="state" label="state" id="state" />
	</aui:column>

	<aui:button-row>
		<aui:button type="submit" value='<%= LanguageUtil.get(portletConfig, locale, "buy-now")%>'/>
	</aui:button-row>
	
</aui:form>

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

