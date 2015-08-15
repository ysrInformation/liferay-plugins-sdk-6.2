<%@ include file="/html/shoppingcart/init.jsp"%>

<portlet:actionURL var="checkoutURL" name="checkout"/>

<h2><liferay-ui:message key="please-in-the-details"/></h2>

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

<aui:form action="<%= checkoutURL %>" name="fm"  method="post" inlineLabels="true">

	<aui:input type="text" name="firstName" value="<%= user.getFirstName()  %>">
		<aui:validator name="required"/>
	</aui:input>
	
	<aui:input type="text" name="lastName" value="<%= user.getLastName() %>">
		<aui:validator name="required"/>
	</aui:input>
	
	<aui:input type="text" name="email" value="<%= user.getEmailAddress() %>">
		<aui:validator name="required"/>
	</aui:input>
	
	<aui:input type="text" name="mobileNumber" label="mobile-number" value="<%= mobileNumber  %>">
		<aui:validator name="required"/>
	</aui:input>
	
	<aui:input type="text" name="altNumber" label="alt-number">
		<aui:validator name="required"/>
	</aui:input>
	
	<aui:input type="text" name="street" label="street" value="<%= street %>">
		<aui:validator name="required"/>
	</aui:input>
	
	<aui:input type="text" name="city" label="city" value="<%= city %>">
		<aui:validator name="required"/>
	</aui:input>
	
	<aui:input type="text" name="zip" label="post-code" value="<%= zip %>">
		<aui:validator name="required"/>
	</aui:input>
	
	<aui:select name="country" label="country" id="country" />
	
	<aui:select name="state" label="state" id="state" />

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
