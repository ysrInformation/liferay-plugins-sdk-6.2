<%@page import="com.liferay.portal.service.CountryServiceUtil"%>
<%@page import="com.liferay.portal.model.ListTypeConstants"%>
<%@page import="com.liferay.portal.model.Contact"%>
<%@include file="/html/common/init.jsp" %>
<portlet:actionURL var="saveAddressURL" name="saveAddress"/>

<aui:form action="<%=saveAddressURL %>" method="POST" target="_parent">
	<aui:fieldset>
		<aui:col width="<%= 50 %>">
		
			<aui:input  name="street1" />
		
			<aui:input  name="street2" />
		
			<aui:input  name="street3" />
			
			<aui:input  name="zip" />
			
		</aui:col>

		<aui:col width="<%= 50 %>">
			
			<aui:input  name="city" />
			
			<aui:select label="country" name="countryId" showEmptyOption="true" >
				<c:forEach var="country" items="<%=CountryServiceUtil.getCountries() %>">
					<aui:option value="${country.countryId}"  label="${country.name}" />
				</c:forEach>
			</aui:select>
			
			<aui:select label="region"  name="regionId" />
		
			<aui:button type="submit" value="Save"/>
		
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
						  var option = A.Node.create("<option value"+obj[x].regionId+">"+obj[x].name+"</option>").appendTo(regions);
					  }
				  }
				);
		
	});
});
</script>