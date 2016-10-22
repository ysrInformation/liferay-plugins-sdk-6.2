<%@page import="com.liferay.portal.kernel.util.TextFormatter"%>
<%@page import="com.liferay.portal.model.Country"%>
<portlet:actionURL name="saveSellerDetails" var="saveSellerURL"/>

<aui:form id="businessForm" name="businessForm" action="${saveSellerURL}" >
	<aui:fieldset>
		<aui:col width="<%= 50 %>">
			<aui:input name="bankName" required="true" />
			<aui:input name="bankAccountNumber" required="true" />
			<aui:input name="companyName" required="true" />
			<aui:input  name="street1" required="true" />
			<aui:input  name="street2" />
			<aui:input  name="street3" />
			<aui:input  name="zip" />
		</aui:col>
		<aui:col width="<%= 50 %>">
			<aui:input name="ifsc-code" required="true" />
			<aui:input id="tin"  type="text" name="tin" />
			<aui:input id="cst"  type="text" name="cst" />
			<aui:input  name="city" required="true"/>
			<aui:select label="country" name="countryId" showEmptyOption="true" onChange="getRegions();" required="true">
				<%
					for (Country country: CountryServiceUtil.getCountries()) {
						%>
							<aui:option value="<%=country.getCountryId() %>"  label="<%=TextFormatter.format(country.getName(), TextFormatter.J) %>" />									
						<%
					}
				%>
			</aui:select>
			<aui:select label="region"  name="regionId" required="true"/>
		</aui:col>
	</aui:fieldset>
	<aui:input name="terms"  type="checkbox" required="true" label="" inlineField="true" />
	<aui:a href="/terms-and-conditions">Terms of use</aui:a>
	And
	<aui:a href="/posting-policy">Posting Policies</aui:a>
	<aui:button-row>
		<aui:button type="submit" value="Submit"/>
	</aui:button-row>	
</aui:form>

<script>
	function getRegions() {
		AUI().use('aui-base', function(A) {
			Liferay.Service('/region/get-regions', {
				countryId : A.one("#<portlet:namespace/>countryId").attr('value')
			}, function(obj) {
				var regions = A.one("#<portlet:namespace/>regionId");
				regions.get('children').remove();
				for (x in obj) {
					var option = A.Node.create("<option value="+obj[x].regionId+">" + obj[x].name + "</option>").appendTo(regions);
				}
			});
		});
	}
</script>