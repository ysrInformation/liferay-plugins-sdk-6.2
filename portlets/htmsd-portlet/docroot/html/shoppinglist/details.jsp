<%@page import="com.htmsd.slayer.service.WholeSaleLocalServiceUtil"%>
<%@page import="com.htmsd.slayer.service.WholeSaleLocalService"%>
<%@page import="com.htmsd.slayer.model.WholeSale"%>
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
			String currencySymbol = CommonUtil.getCurrencySymbol(Long.valueOf(currencyId));
			double currentRate = CommonUtil.getCurrentRate(Long.valueOf(currencyId));
			List<WholeSale> wholeSales = WholeSaleLocalServiceUtil.getWholeSaleItem(shoppingItem.getItemId());
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
		        							<img width="200" height="100" src="<%=thumbnailURL%>" class="thumbnail details-img" />
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
								<div class="video">
									<img width="200" height="100" src="<%=youtubeThumbnailURL%>" class="thumbnail details-img" />
									<a href="#"></a>
								</div>
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
						<c:choose>
							<c:when test='<%= Validator.isNotNull(cmd) && cmd.equalsIgnoreCase("itemsDetails") %>'>
								<div class="add-to-cart-item-price" id="itemPrice">
									<% price = (currentRate == 0) ? price :  price / currentRate; %>
									<%= CommonUtil.getPriceFormat(price, currencyId) %>
								</div>
							</c:when>
							<c:otherwise>
								<div class="add-to-cart-item-price" id="itemPrice"></div>
							</c:otherwise>
						</c:choose>
						<div class="add-to-cart-item-quantity" >
							<c:if test="<%= wholeSales.size() > 0 %>">
								<table class="qty-discounts">
									<thead>
										<tr>
											<th><liferay-ui:message key="qunatity"/></th>
											<th><liferay-ui:message key="price"/></th>
										</tr>
									</thead>
									<tbody>
										<%
											for(WholeSale wholeSale : wholeSales) {
												%>
													<tr>
														<td><%=wholeSale.getQuantity() + " Units"%></td>
														<%	double total = (currentRate == 0) ? wholeSale.getPrice() :  wholeSale.getPrice() / currentRate; 
 %>
														<td><%=CommonUtil.getPriceFormat(total, currencyId) %></td>
													</tr>
												<%
											}
										%>
									</tbody>
								</table>
							</c:if>
						</div>
						<c:if test='<%= !cmd.equalsIgnoreCase("itemsDetails") %>'>
							<div class="add-to-cart-btn">
								<%
									Category parenCategory = CommonUtil.getShoppingItemParentCategory(shoppingItem.getItemId());
									String btnName =  LanguageUtil.get(pageContext, "add-to-cart");
									if (parenCategory.getName().equalsIgnoreCase("live")) btnName = LanguageUtil.get(pageContext, "request-for-adoption");
								%>
								<aui:button name="add-to-cart" type="submit" cssClass="add-to-cart" value='<%= btnName %>'/>
							</div>
						</c:if>
					</div>
				</aui:form>
			</aui:col>	
		</aui:fieldset>
		<script>
			$(document).ready(function() {
				var price = <%=(currentRate == 0) ? price :  price / currentRate%>;
				$('#itemPrice').html(formatPrice(price));
			    $("#lightGallery").lightGallery({
			    	mode : 'fade',
			    	thumbnail : false
			    });
			    $('#breadcrumbs ul li').removeClass("only");
			    $('#breadcrumbs ul').append('<li class=""><%=shoppingItem.getName()%></li>');
			});
		</script>
	</c:when>
	<c:otherwise>
		<div style="text-align: center;">
			<h2>Item Not Found</h2>
		</div>
	</c:otherwise>
</c:choose>