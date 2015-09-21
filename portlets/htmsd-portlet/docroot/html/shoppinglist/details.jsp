<%@include file="/html/shoppinglist/init.jsp" %>
<%
	long itemId = ParamUtil.getLong(request, HConstants.ITEM_ID);
	
	ShoppingItem shoppingItem = ShoppingItemLocalServiceUtil.fetchShoppingItem(itemId);
%> 
<c:choose>
	<c:when test="<%=Validator.isNotNull(shoppingItem) %>">
		<%
			double price = shoppingItem.getTotalPrice();
			
			PortletURL addItemsToCartActionURL = renderResponse.createActionURL();
			addItemsToCartActionURL.setParameter(ActionRequest.ACTION_NAME, "addItemToCart");
			addItemsToCartActionURL.setParameter(HConstants.ITEM_ID, String.valueOf(itemId));
			String cmd = ParamUtil.getString(request, Constants.CMD);
			String videoURL = shoppingItem.getVedioURL();
		%>
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
										<a href="<%=imageURL%>">
		        							<img src="<%=thumbnailURL%>" class="thumbnail details-img" />
		        						</a>
		      						</li>
								<%
							}
						%>
						<c:if test="<%= Validator.isNotNull(videoURL) && !videoURL.isEmpty() %>">
							<li data-src="<%=shoppingItem.getVedioURL()%>" class="span2">
								<%
									String videoId = videoURL.substring(videoURL.indexOf("v=")+2, videoURL.length());
									String youtubeThumbnailURL = "http://img.youtube.com/vi/"+videoId+"/default.jpg";
								%>
								<img src="<%=youtubeThumbnailURL%>" class="thumbnail details-img" />
							</li>
						</c:if>
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
						<c:if test='<%= !cmd.equalsIgnoreCase("itemsDetails") %>'>
							<div class="add-to-cart-btn">
								<aui:button name="add-to-cart" type="submit" cssClass="add-to-cart" value='<%= LanguageUtil.get(pageContext, "add-to-cart") %>'/>
							</div>
						</c:if>
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