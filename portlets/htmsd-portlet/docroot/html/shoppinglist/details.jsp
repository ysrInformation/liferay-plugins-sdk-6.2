<%@page import="com.htmsd.util.CommonUtil"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="java.text.DecimalFormat"%>
<%@include file="/html/shoppinglist/init.jsp" %>
<link type="text/css" rel="stylesheet" href="css/lightGallery.css" />                    
<script src="js/lightGallery.js"></script>
<%
	long itemId = ParamUtil.getLong(request, "itemId");
	ShoppingItem shoppingItem = ShoppingItemLocalServiceUtil.fetchShoppingItem(itemId);
	DecimalFormat decimalFormat = new DecimalFormat("#.00");
%>
<aui:fieldset>
	<aui:col width="70">
		<div >
			<ul id="lightGallery">
				<% 
					for (String str : shoppingItem.getImageIds().split(",")) {
						long imageId = Long.valueOf(str); 
						String imageURL = CommonUtil.getThumbnailpath(imageId, themeDisplay.getScopeGroupId());
						%>
							<li data-src="<%=imageURL%>">
        						<img src="<%=imageURL%>" width="100px" height="100px"/>
      						</li>
						<%
					}
				%>
			</ul>
		</div>
		<div>
			<liferay-ui:tabs names="description">
				<liferay-ui:section>
					<p>
						<%=shoppingItem.getDescription() %>
					</p>
				</liferay-ui:section>
			</liferay-ui:tabs>
		</div>
	</aui:col>
	<aui:col width="30">
		<aui:form>
			<div class="add-to-cart-item">
				<div class="add-to-cart-item-label">
					<h2><%=shoppingItem.getName() %></h2>
				</div>
				<div class="add-to-cart-item-code">
					<liferay-ui:message key="product-code"/>: <%=shoppingItem.getProductCode() %>
				</div>
				<div class="add-to-cart-item-price">
					<%= "Rs."+decimalFormat.format(shoppingItem.getSellerPrice()+shoppingItem.getTotalPrice()) %>
				</div>
				<div class="add-to-cart-item-quantity" >
					<aui:input name="quantity" value="1" cssClass="quantity" required="true" showRequiredLabel="false">
						<aui:validator name="digit"/>
					</aui:input>
				</div>
				<div class="add-to-cart-btn">
					<aui:button name="add-to-cart" type="submit" cssClass="add-to-cart" value='<%= LanguageUtil.get(pageContext, "add-to-cart") %>'/>
				</div>
			</div>
		</aui:form>
	</aui:col>	
</aui:fieldset>
<script>
	$(document).ready(function() {
	    $("#lightGallery").lightGallery({
	    	mode : 'fade'
	    }); 
	});
</script>