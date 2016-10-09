<%@page import="com.liferay.portal.NoSuchLayoutException"%>
<%@page import="com.liferay.portal.service.LayoutLocalServiceUtil"%>
<%@page import="com.liferay.portal.model.Layout"%>
<%@page import="javax.portlet.PortletSession"%>
<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@include file="/html/shoppinglist/init.jsp" %>

<%
	String noOfItems  = portletPreferences.getValue("noOfItems", "8");
	String categoryToDisplay  = portletPreferences.getValue("categoryToDisplay", "-1");
	Layout detailLayout = null;
	try {
		detailLayout = LayoutLocalServiceUtil.getFriendlyURLLayout(themeDisplay.getScopeGroupId(), false, "/detail");
	} catch (NoSuchLayoutException e) {
		
	}
	long detailPlid = layout.getPlid();
	if (Validator.isNotNull(detailLayout)) {
		detailPlid = detailLayout.getPlid();
	}
%>
<style>
	#no-item-display {
		text-align: center;
		display: none;
	}
</style>

<liferay-portlet:actionURL var="addToCartURL" name="addItemToCart" >
	<liferay-portlet:param name="isRedirectDisabled" value="true"/>
</liferay-portlet:actionURL>

<div class="row-fluid featured-container">
	<div class="span2 leftColHeight">
		<div>
			<c:choose>
				<c:when test="<%=Long.valueOf(categoryToDisplay) > 0 %>">
					<h2><%=CommonUtil.getCategoryName(Long.valueOf(categoryToDisplay)) %></h2>
				</c:when>
				<c:otherwise>
					<h2>All Items</h2>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div class="span10 rightColHeight">
		<div class="swiper-container">
			<div id="featured_list_<portlet:namespace/>" class="swiper-wrapper">
				<!-- item display -->
			</div>
			<div class="swiper-button-next"></div>
			<div class="swiper-button-prev"></div>
		</div>
	</div>
</div>

<div id="no-item-display" >
	<h2>No Items to Display</h2>
</div>
<div id="loader-icon-<portlet:namespace/>" >
	<img src="<%=request.getContextPath()%>/images/loader.gif" style="width: 100px; height: 100px"/>
</div>
<aui:script>
	$(function() {
		var portletId = '<%= themeDisplay.getPortletDisplay().getId() %>';
		var namespace = '<portlet:namespace/>';
		getShoppingItems(<%=categoryToDisplay%>, <%=currencyId%>, <%=themeDisplay.getScopeGroupId()%>, <%=HConstants.APPROVE%>, 0, <%=noOfItems%>, 'createDate DESC', '${addToCartURL}', <%=detailPlid%>, namespace);
	
		var colHeight = $('.featured-container .rightColHeight').height();
		$('.featured-container .leftColHeight').css('height', colHeight);
	});
</aui:script>