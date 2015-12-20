<%@page import="com.liferay.portal.kernel.util.TextFormatter"%>
<%@page import="com.liferay.portal.model.Country"%>
<%@page import="com.liferay.portal.service.CountryServiceUtil"%>
<%@page import="com.liferay.portal.model.ListTypeConstants"%>
<%@page import="com.liferay.portal.model.Contact"%>
<%@include file="/html/common/init.jsp" %>
<portlet:actionURL var="saveAddressURL" name="saveAddress"/>

<aui:form action="<%=saveAddressURL %>" method="POST" target="_parent">
	<aui:fieldset>
		<aui:col width="<%= 50 %>">
		
			<aui:input  name="street1" required="true"/>
			
			<aui:input  name="street2" />
		
			<aui:input  name="street3" />
			
			<aui:input  name="zip" >
				<aui:validator name="required" />
				<aui:validator name="number"></aui:validator>
				<aui:validator name="minLength">6</aui:validator>
				<aui:validator name="maxLength">6</aui:validator>
			</aui:input>
			
		</aui:col>

		<aui:col width="<%= 50 %>">
			
			<aui:input  name="city" >
				<aui:validator name="required" />
				<aui:validator name="alpha" />
			</aui:input>
			
			<aui:select label="country" name="countryId" showEmptyOption="true" >
				<%
					for(Country country : CountryServiceUtil.getCountries()) {
						%>
							<aui:option value="<%=country.getCountryId() %>"  
								label="<%=TextFormatter.format(country.getName(themeDisplay.getLocale()), TextFormatter.J) %>" />
						<%
					}
				%>
			</aui:select>
			
			<aui:select label="region"  name="regionId" />
		
			<aui:button type="submit" value="save"/>
		
		</aui:col>
				
	</aui:fieldset>
	
</aui:form>


<script>
AUI().use('aui-base', function(A){
	
	A.one("#<portlet:namespace/>countryId").on('change', function(e){
		Liferay.Service(
				  '/region/get-regions',
				  {
				    countryId: e.currentTarget.val()
				  },
				  function(obj) {
					  
					  var regions = A.one("#<portlet:namespace/>regionId");
					  regions.get('children').remove();
					  for(x in obj) {
						  var option = A.Node.create("<option value="+obj[x].regionId+">"+obj[x].name+"</option>").appendTo(regions);
					  }
				  }
				);
		
	});
});
</script>