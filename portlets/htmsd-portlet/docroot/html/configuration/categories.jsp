<%@include file="/html/configuration/init.jsp"%>

<portlet:actionURL name="addCategory"  var="addCategoryURL">
	<portlet:param name="backURL" value="<%=themeDisplay.getURLCurrent() %>"/>
</portlet:actionURL>

<%
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/configuration/view.jsp");
	iteratorURL.setParameter("tab1", "Category");
%>

<portlet:actionURL var="deleteSetURL" name="deleteCategorySet" />

<aui:fieldset>
	<aui:form action="<%=addCategoryURL %>" method="POST">
		<aui:input name="<%=HConstants.NAME %>"  required="true"/>
		<aui:input name="<%=HConstants.DESCRIPTION %>" required="true" type="textarea" />
		<aui:button  type="submit" value="add-category" />
	</aui:form>
</aui:fieldset>

<aui:form action="<%=deleteSetURL %>"  method="POST">
	<aui:button type="submit" value="delete"  />
	
	<liferay-ui:search-container delta="10"   emptyResultsMessage="No Items to display" iteratorURL="<%=iteratorURL%>"  rowChecker="<%= new RowChecker(renderResponse)%>">
	
		<liferay-ui:search-container-results>
			 <%
			 
			 	List<Category>  categories = CategoryLocalServiceUtil.getCategories(searchContainer.getStart(),  searchContainer.getEnd());
				total = CategoryLocalServiceUtil.getCategoriesCount();
				
				pageContext.setAttribute("results", categories);
				pageContext.setAttribute("total", total);
			%>
		</liferay-ui:search-container-results>			 
		
		<liferay-ui:search-container-row className="com.htmsd.slayer.model.Category" modelVar="category" indexVar="indexVar" keyProperty="categoryId">
	
			<liferay-ui:search-container-column-text name="no.">
				<%=String.valueOf(searchContainer.getStart()+1+indexVar)%>
			</liferay-ui:search-container-column-text>
			
			<liferay-ui:search-container-column-text name="category-name" property="name" />
			
			<liferay-ui:search-container-column-text name="category-description" property="description" />
				
			<liferay-ui:search-container-column-text name="date">
				<fmt:formatDate value="<%=category.getCreateDate() %>" pattern="dd/MM/yyyy" />
			</liferay-ui:search-container-column-text>
		
		</liferay-ui:search-container-row>
		
		<liferay-ui:search-iterator  searchContainer="<%=searchContainer %>"/>
	</liferay-ui:search-container>
</aui:form>