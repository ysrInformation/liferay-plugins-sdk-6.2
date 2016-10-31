<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.htmsd.slayer.service.ItemTypeLocalServiceUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.htmsd.slayer.model.ItemType"%>
<%@include file="/html/configuration/init.jsp"%>

<portlet:actionURL var="saveItemTypeURL" name="saveItemType"/>

<%
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/configuration/itemType.jsp");
	iteratorURL.setParameter("tab1", "Item Types");
	
	int count=1;
	List<ItemType>  itemTypes =  ItemTypeLocalServiceUtil.getItemTypes(-1, -1);
%>

<aui:fieldset>
	<aui:form action="${saveItemTypeURL}">
		<aui:input name="name" required="true"/>
		<%-- <aui:input name="documentRequired" type="checkbox" helpMessage="document-required-msg"/> --%>
		<aui:button type="submit" />
	</aui:form>
</aui:fieldset>

<table id="itemTypeTable" class="table table-striped table-bordered dt-responsive nowrap" width="100%" cellspacing="0">
	<thead>
		<tr>
			<th><liferay-ui:message key="no."/></th>
			<th><liferay-ui:message key="name"/></th>
			<%-- <th><liferay-ui:message key="document-reqired"/></th> --%>
			<th><liferay-ui:message key="actions"/></th>
		</tr>
	</thead>
	<tbody>
		<c:if test='<%= Validator.isNotNull(itemTypes) && itemTypes.size() > 0 %>'>
			<% for (ItemType itemType :itemTypes) { %>
				<tr>
					<td><%= count++ %></td>
					<td><%= itemType.getName() %></td>
					<%-- <td><%= itemType.getDocumentRequired() %></td> --%>
					<td>
						<portlet:actionURL var="deleteItemTypeURL" name="deleteItemType">
							<portlet:param name="itemTypeId" value="<%= String.valueOf(itemType.getItemTypeId())%>"/>
						</portlet:actionURL>
						<aui:a href="${deleteItemTypeURL}"  label="delete"/>
					</td>
				</tr>
			<% } %>
		</c:if>
	</tbody>
</table>
<%-- <liferay-ui:search-container delta="10"   emptyResultsMessage="no-items-to-display" iteratorURL="<%=iteratorURL %>"  rowChecker="<%= new RowChecker(renderResponse)%>">

	<liferay-ui:search-container-results>
		 <%
		
		 	List<ItemType>  itemTypes =  ItemTypeLocalServiceUtil.getItemTypes(-1, -1);
			
			pageContext.setAttribute("results", itemTypes);
			pageContext.setAttribute("total", itemTypes.size());
		%>
	</liferay-ui:search-container-results>			 
	
	<liferay-ui:search-container-row className="com.htmsd.slayer.model.ItemType" modelVar="itemType" indexVar="indexVar" keyProperty="itemTypeId">

		<liferay-ui:search-container-column-text name="no.">
			<%=String.valueOf(searchContainer.getStart()+1+indexVar)%>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text property="name" />
		
		<liferay-ui:search-container-column-text name="document-reqired"  >
			<%= itemType.getDocumentRequired() %>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-text name="actions">
			<portlet:actionURL var="deleteItemTypeURL" name="deleteItemType">
				<portlet:param name="itemTypeId" value="<%= String.valueOf(itemType.getItemTypeId())%>"/>
			</portlet:actionURL>
			<aui:a href="${deleteItemTypeURL}"  label="delete"/>
		</liferay-ui:search-container-column-text>
		
	</liferay-ui:search-container-row>
	
	<liferay-ui:search-iterator  searchContainer="<%=searchContainer %>"/>
</liferay-ui:search-container> --%>

<script type="text/javascript"> 
$(function(){
	$("#itemTypeTable").DataTable();
});
</script>