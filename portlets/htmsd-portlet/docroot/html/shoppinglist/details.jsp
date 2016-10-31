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
			String stock = (shoppingItem.getQuantity() == -1) ? "Unlimited" : String.valueOf(shoppingItem.getQuantity());
			String redirectURL = ParamUtil.getString(request, "backURL");
		%>
		<c:if test='<%= cmd.equalsIgnoreCase("itemsDetails") %>'>
			<ul class="nav nav-collapse pull-right">
				<li class="backhome">
					<a href="<%= redirectURL %>"> &#10149; Back </a>				
				</li>
			</ul>
		</c:if>
		<liferay-ui:error key="item-exist" message="item-already-exist"/>
		<aui:fieldset>
			<div class="shoppingItemDetails row-fluid">
				<div class="span5">
					<div class="swiper-container gallery-top">
						<div class="swiper-wrapper">
							<% 
								for (String str : shoppingItem.getImageIds().split(",")) {
									long imageId = Long.valueOf(str.isEmpty() ? "0" : str); 
									String imageURL = CommonUtil.getThumbnailpath(imageId, themeDisplay.getScopeGroupId(), false);
									String thumbnailURL = CommonUtil.getThumbnailpath(imageId, themeDisplay.getScopeGroupId(), true);
									%>
										<div class="swiper-slide" style="background-image: url(<%=thumbnailURL%>);">
										</div>
									<%
								}
							%>
							<c:if test="<%= Validator.isNotNull(videoURL) && !videoURL.isEmpty() %>">
								<div class="swiper-slide">
									<%
										String videoId = videoURL.substring(videoURL.indexOf("v=")+2, videoURL.length());
									%>
									<iframe src="<%="//www.youtube.com/embed/"+videoId%>" width="100%" height="100%"></iframe>
								</div>
							</c:if>
						</div>
						<div class="swiper-button-next swiper-button-white"></div>
						<div class="swiper-button-prev swiper-button-white"></div>
					</div>
					<div class="swiper-container gallery-thumbs">
						<div class="swiper-wrapper">
							<% 
								for (String str : shoppingItem.getImageIds().split(",")) {
									long imageId = Long.valueOf(str.isEmpty() ? "0" : str); 
									String imageURL = CommonUtil.getThumbnailpath(imageId, themeDisplay.getScopeGroupId(), false);
									String thumbnailURL = CommonUtil.getThumbnailpath(imageId, themeDisplay.getScopeGroupId(), true);
									%>
										<div class="swiper-slide" style="background-image: url(<%=thumbnailURL%>);">
										</div>
									<%
								}
							%>
							<c:if test="<%= Validator.isNotNull(videoURL) && !videoURL.isEmpty() %>">
								<%
									String videoId = videoURL.substring(videoURL.indexOf("v=")+2, videoURL.length());
									String youtubeThumbnailURL = "http://img.youtube.com/vi/"+videoId+"/default.jpg";
								%>
								<div class="swiper-slide" style="background-image: url(<%=youtubeThumbnailURL%>);">
									<i class="play-icon" aria-hidden="true" ></i>
								</div>
							</c:if>
						</div>
					</div>
				</div>
				<div class="span7">
					<aui:form action="<%=addItemsToCartActionURL %>">
						<div class="add-to-cart-item">
							<div class="add-to-cart-item-label">
								<h2><%=shoppingItem.getName() %></h2>
							</div>
							<div class="add-to-cart-item-code">
								<liferay-ui:message key="product-code"/>: <%=shoppingItem.getProductCode() %>
							</div>
							<div class="add-to-cart-item-code">
								<liferay-ui:message key="product-stock"/>: <%= stock %>
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
															<%	
																double total = (currentRate == 0) ? wholeSale.getPrice() :  wholeSale.getPrice() / currentRate; 
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
										if (parenCategory.getName().contains("live") || parenCategory.getName().contains("Live")) btnName = LanguageUtil.get(pageContext, "request-for-adoption");
									%>
									<aui:button name="add-to-cart" type="submit" cssClass="add-to-cart" value='<%= btnName %>'/>
								</div>
							</c:if>
						</div>
					</aui:form>
					<div>
						<liferay-ui:tabs names="description">
							<liferay-ui:section>
								<div class="descriptionTabContent">
									<p class="description"> 
										<%=shoppingItem.getDescription() %>
									</p>
								</div>
							</liferay-ui:section>
						</liferay-ui:tabs>
					</div>
				</div>	
			</div>
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
			    
			    var galleryTop = new Swiper('.swiper-container.gallery-top', {
			    	nextButton: '.swiper-button-next',
			        prevButton: '.swiper-button-prev',
			        spaceBetween: 10,
			    });
			    
			    var galleryThumbs = new Swiper('.swiper-container.gallery-thumbs', {
			    	spaceBetween: 10,
			        centeredSlides: true,
			        slidesPerView: 'auto',
			        touchRatio: 0.2,
			        slideToClickedSlide: true
			    });
			    
			    galleryTop.params.control = galleryThumbs;
			    galleryThumbs.params.control = galleryTop;
			});
		</script>
	</c:when>
	<c:otherwise>
		<div style="text-align: center;">
			<h2>Item Not Found</h2>
		</div>
	</c:otherwise>
</c:choose>