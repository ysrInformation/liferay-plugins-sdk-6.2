<%@include file="/html/configuration/init.jsp"%>

<portlet:actionURL name="addCategory"  var="addCategoryURL">
	<portlet:param name="backURL" value="<%=themeDisplay.getURLCurrent() %>"/>
</portlet:actionURL>

<%
	List<Category> parentCategories = CategoryLocalServiceUtil.getByParent(0);
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/configuration/view.jsp");
	iteratorURL.setParameter("tab1", "Category");
%>

<portlet:actionURL var="deleteSetURL" name="deleteCategorySet" />

<aui:fieldset>
	<aui:form action="<%=addCategoryURL %>" name="fm1" >
		<aui:input name="<%=HConstants.NAME %>"  required="true"/>
		<aui:input name="<%=HConstants.DESCRIPTION %>" required="true" type="textarea" />
		<c:if test='<%=tabs1.equals("Category")%>'>
			<aui:select name="<%=HConstants.CATEGORY_ID %>"   required="true" showEmptyOption="true">
				<c:forEach items="<%=parentCategories %>" var="category">   
					<aui:option label="${category.name}"  value="${category.categoryId}"/>
				</c:forEach>
			</aui:select>
		</c:if>
		<aui:button  type="submit" value="add-category" />
	</aui:form>
</aui:fieldset>

<aui:form action="<%=deleteSetURL %>"  name="fm2">
	<aui:button type="submit" value="delete"  />
	
	<liferay-ui:search-container delta="10"   emptyResultsMessage="No Items to display" iteratorURL="<%=iteratorURL%>"  rowChecker="<%= new RowChecker(renderResponse)%>">
	
		<liferay-ui:search-container-results>
			 <%
			 
			 	List<Category>  categories = tabs1.equals("Head Category") ? CategoryLocalServiceUtil.getByParent(0, searchContainer.getStart(),  searchContainer.getEnd()) 
			 															: CategoryLocalServiceUtil.getByChild(0, searchContainer.getStart(),  searchContainer.getEnd());
				
			 	System.out.println(CategoryLocalServiceUtil.getByChild(0));
			 	
			 	total = tabs1.equals("Head Category") ? CategoryLocalServiceUtil.getByChildCount(0)
												 : CategoryLocalServiceUtil.getByParentCount(0);
				
				pageContext.setAttribute("results", categories);
				pageContext.setAttribute("total", total);
			%>
		</liferay-ui:search-container-results>			 
		
		<liferay-ui:search-container-row className="com.htmsd.slayer.model.Category" modelVar="category" indexVar="indexVar" keyProperty="categoryId">
	
			<liferay-ui:search-container-column-text name="no.">
				<%=String.valueOf(searchContainer.getStart()+1+indexVar)%>
			</liferay-ui:search-container-column-text>
			
			<liferay-ui:search-container-column-text name="category-name" property="name" />
			
			<c:if test='<%=tabs1.equals("Category")%>'>
				<liferay-ui:search-container-column-text name="parent-category">
					<%=CategoryLocalServiceUtil.getCategory(category.getParentCategoryId()).getName()%>
				</liferay-ui:search-container-column-text>
			</c:if>
			
			<liferay-ui:search-container-column-text name="category-description" property="description" />
				
			<liferay-ui:search-container-column-text name="date">
				<fmt:formatDate value="<%=category.getCreateDate() %>" pattern="dd/MM/yyyy" />
			</liferay-ui:search-container-column-text>
		
		</liferay-ui:search-container-row>
		
		<liferay-ui:search-iterator  searchContainer="<%=searchContainer %>"/>
	</liferay-ui:search-container>
</aui:form>