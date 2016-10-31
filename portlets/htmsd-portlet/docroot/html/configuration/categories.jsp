<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@include file="/html/configuration/init.jsp"%>

<portlet:actionURL name="addCategory"  var="addCategoryURL">
	<portlet:param name="backURL" value="<%=themeDisplay.getURLCurrent() %>"/>
</portlet:actionURL>

<%
	List<Category> parentCategories = CategoryLocalServiceUtil.getByParent(0);
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("jspPage", "/html/configuration/view.jsp");
	iteratorURL.setParameter("tab1", "Category");
	
	int count=1, start = -1, end = -1;
 	List<Category>  categories = tabs1.equals("Head Category") ? CategoryLocalServiceUtil.getByParent(0, start,  end) 
				: CategoryLocalServiceUtil.getByChild(0, start,  end);
%>

<portlet:actionURL var="deleteSetURL" name="deleteCategorySet" />

<aui:fieldset>
	<aui:form action="<%=addCategoryURL %>" name="fm1" >
		<aui:input type="hidden" name="tab1" value="<%= tabs1 %>"/>
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

<aui:form action="<%= deleteSetURL %>"  name="fm2">
	
	<aui:input type="hidden" name="tab1" value="<%= tabs1 %>"/>
	<aui:button-row>
		<aui:button type="submit" value="delete" />
	</aui:button-row>
	
	<table id="categoryTable" class="table table-striped table-bordered dt-responsive nowrap" width="100%" cellspacing="0">
		<thead>
			<tr>
				<th><input type="checkbox" class="categoriesrowId" name="<portlet:namespace/>rowIds" value=""/></th> 
				<th><liferay-ui:message key="no."/></th>
				<th><liferay-ui:message key="category-name"/></th>
				<c:if test='<%=tabs1.equals("Category")%>'>
					<th><liferay-ui:message key="parent-category"/></th>
				</c:if>
				<th><liferay-ui:message key="category-description"/></th>
				<th><liferay-ui:message key="date"/></th>
			</tr>
		</thead>
		<tbody>
			<c:if test='<%= Validator.isNotNull(categories) && categories.size() > 0 %>'>
				<% for (Category category : categories) { %>
					<tr>
						<td><input type="checkbox" class="categoriesrowIds" name="<portlet:namespace/>rowIds" value="<%= category.getCategoryId() %>"/></td>
						<td><%= count++ %></td>
						<td><%= category.getName() %></td>
						<c:if test='<%=tabs1.equals("Category")%>'>
							<%
							String parentName = "NA";
							try {
								parentName = CategoryLocalServiceUtil.getCategory(category.getParentCategoryId()).getName();	
							} catch (Exception e) {}
							%>
							<td><%= parentName %></td>
						</c:if>
						<td><%= category.getDescription() %></td>
						<td><fmt:formatDate value="<%=category.getCreateDate() %>" pattern="dd/MM/yyyy" /></td>
					</tr>
				<% } %>
			</c:if>
		</tbody>
	</table>
	
<%-- 	
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
					<%
						String parentName = "NA";
						try {
							parentName = CategoryLocalServiceUtil.getCategory(category.getParentCategoryId()).getName();	
						} catch (Exception e) {}
					%>
					<%= parentName %>
				</liferay-ui:search-container-column-text>
			</c:if>
			
			<liferay-ui:search-container-column-text name="category-description" property="description" />
				
			<liferay-ui:search-container-column-text name="date">
				<fmt:formatDate value="<%=category.getCreateDate() %>" pattern="dd/MM/yyyy" />
			</liferay-ui:search-container-column-text>
		
		</liferay-ui:search-container-row>
		
		<liferay-ui:search-iterator  searchContainer="<%=searchContainer %>"/>
	</liferay-ui:search-container> 
--%>
</aui:form>

<script>
$(function(){
	$("#categoryTable").DataTable();
	$(".categoriesrowId").click(function(){
		var $elem = $(this);
		if ($elem.is(":checked")) {
			$(".categoriesrowIds").prop("checked", true);
		} else {
			$(".categoriesrowIds").prop("checked", false);
		}
	});
});
</script>