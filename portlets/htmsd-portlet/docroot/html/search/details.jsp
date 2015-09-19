<%@include file="/html/shoppinglist/init.jsp" %>
<%
	long itemId = ParamUtil.getLong(request, HConstants.ITEM_ID);
	ShoppingItem shoppingItem = ShoppingItemLocalServiceUtil.fetchShoppingItem(itemId);
%> 
<c:choose>
	<c:when test="<%=Validator.isNotNull(shoppingItem) %>">
		<%double price = shoppingItem.getTotalPrice(); %>
		<liferay-portlet:actionURL var="addItemsToCartActionURL" name="addItemToCart" >
			<liferay-portlet:param name="<%=HConstants.ITEM_ID %>" value="<%=String.valueOf(itemId) %>"/>
		</liferay-portlet:actionURL>
		
		<liferay-ui:error key="item-exist" message="item-already-exist"/>
		<aui:fieldset>
			<aui:col width="70">
				<div >
					<ul id="lightGallery" class="row" style="margin-left: 0px;">
						<% 
							for (String str : shoppingItem.getImageIds().split(",")) {
								long imageId = Long.valueOf(str.isEmpty() ? "0" : str); 
								String imageURL = CommonUtil.getThumbnailpath(imageId, themeDisplay.getScopeGroupId(), false);
								String thumbnailURL = CommonUtil.getThumbnailpath(imageId, themeDisplay.getScopeGroupId(), true);
								%>
									<li data-src="<%=imageURL%>" class="span2">
		        						<img src="<%=thumbnailURL%>" class="thumbnail details-img" />
		      						</li>
								<%
							}
						%>
					</ul>
				</div>
				<div>
					<liferay-ui:tabs names="description">
						<liferay-ui:section>
							<p class="description"> 
								<%=shoppingItem.getDescription() %>
							</p>
						</liferay-ui:section>
					</liferay-ui:tabs>
				</div>
			</aui:col>
			<aui:col width="30">
				<aui:form action="<%=addItemsToCartActionURL %>">
					<div class="add-to-cart-item">
						<div class="add-to-cart-item-label">
							<h2><%=shoppingItem.getName() %></h2>
						</div>
						<div class="add-to-cart-item-code">
							<liferay-ui:message key="product-code"/>: <%=shoppingItem.getProductCode() %>
						</div>
						<div class="add-to-cart-item-price" id="itemPrice">
						</div>
						<%-- <div class="add-to-cart-item-quantity" >
							<aui:input name="quantity" value="1" cssClass="quantity" required="true" showRequiredLabel="false">
								<aui:validator name="digit"/>
							</aui:input>
						</div> --%>
						<div class="add-to-cart-btn">
							<aui:button name="add-to-cart" type="submit" cssClass="add-to-cart" value='<%= LanguageUtil.get(pageContext, "add-to-cart") %>'/>
						</div>
					</div>
				</aui:form>
			</aui:col>	
		</aui:fieldset>
		<script>
			$(document).ready(function() {
				var price = <%=price%>;
				$('#itemPrice').html(formatPrice(price));
			    $("#lightGallery").lightGallery({
			    	mode : 'fade',
			    	thumbnail : false
			    }); 
			});
		</script>
	</c:when>
	<c:otherwise>
		<div style="text-align: center;">
			<h2>Item Not Found</h2>
		</div>
	</c:otherwise>
</c:choose>		