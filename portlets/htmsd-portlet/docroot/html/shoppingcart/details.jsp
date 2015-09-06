<%@ include file="/html/shoppingcart/init.jsp"%>

<%
	int i=0;
	double totalPrice = 0;
	
	PortletURL detailsURL = renderResponse.createRenderURL();
	detailsURL.setWindowState(LiferayWindowState.POP_UP);
	detailsURL.setParameter("jspPage", "/html/shoppinglist/details.jsp");
	
	PortletURL checkoutURL = renderResponse.createRenderURL();
	checkoutURL.setParameter("jspPage", "/html/shoppingcart/checkout.jsp");
	
	List<ShoppingItem_Cart> shoppingItem_Carts = CommonUtil.getUserCartItems(themeDisplay.getUserId());
%>

<portlet:resourceURL var="removeItemURL"> 
	<portlet:param name="<%= Constants.CMD %>" value="remove-item"/> 
</portlet:resourceURL>

<liferay-portlet:renderURL  var="loginURL" portletName="<%= PortletKeys.LOGIN %>">
	<portlet:param name="struts_action" value="/login/login" />
	<portlet:param name="redirect" value="<%= checkoutURL.toString() %>" />
	<portlet:param name="portletResource" value="<%= portletDisplay.getId() %>" />
	<portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>" />
</liferay-portlet:renderURL>

<% 
String checkOutURL = (themeDisplay.isSignedIn())? checkoutURL.toString():loginURL;
if (Validator.isNotNull(shoppingItem_Carts) && shoppingItem_Carts.size() > 0) {
	for (ShoppingItem_Cart shoppingItem_Cart : shoppingItem_Carts) {
		long itemId = shoppingItem_Cart.getItemId();
		ShoppingItem shoppingItem = CommonUtil.getShoppingItem(itemId);
		double price = shoppingItem.getTotalPrice();
		totalPrice += price;
		String collapsed = (i==0) ? StringPool.BLANK:"collapsed";
		String removeCartItem = "javascript:removeItems('"+shoppingItem.getItemId()+"','"+removeItemURL+"');";
		String[] imageIdsList = Validator.isNotNull(shoppingItem) ? shoppingItem.getImageIds().split(StringPool.COMMA):new String[]{}; 
		%> 
		<div class="itemDetails"> 
			<liferay-ui:panel-container accordion="false" extended="true"> 
				<liferay-ui:panel title="<%= shoppingItem.getName() %>" defaultState='<%= collapsed  %>'>
					<aui:column columnWidth="40">
						<%
						boolean imageExist = false;
						
						if (Validator.isNotNull(imageIdsList) && imageIdsList.length > 0) {
							String image_upload_preview = HConstants.IMAGE_UPLOAD_PREVIEW+1;
							String imageURL = CommonUtil.getThumbnailpath(Long.parseLong(imageIdsList[0]), themeDisplay.getScopeGroupId(), false);
							imageExist = (!imageURL.isEmpty())?true:false;
							detailsURL.setParameter(HConstants.ITEM_ID, String.valueOf(itemId));
							detailsURL.setParameter(Constants.CMD, "itemsDetails");
							String showInPopup = "javascript:showPopup('"+detailsURL.toString()+"','800','1200','Product Details');";
							%>
							<div class="images-disp">
								<aui:a href="<%= showInPopup %>"><img id="<%= image_upload_preview %>" src="<%= imageURL %>" /></aui:a>
							</div>
							<%
						}
						%>
						<c:if test='<%= !imageExist %>'>
							<strong><liferay-ui:message key="no-image"/></strong>
						</c:if>
					</aui:column>
					<aui:column columnWidth="60">  
						<div><h3><%= (Validator.isNotNull(shoppingItem))? shoppingItem.getName():"NA" %></h3></div>
						<div><p><%= (Validator.isNotNull(shoppingItem))?shoppingItem.getDescription():"NA" %></p></div>
						<div><h4><%= (Validator.isNotNull(shoppingItem))? CommonUtil.getPriceFormat(price):0L %></h4></div>
						<%-- <div><aui:input type="text" name="quantity" label="quantity" /></div> --%>
						<div><h4><aui:a href="<%= removeCartItem %>"><liferay-ui:message key="remove"/></aui:a></h4></div>
					</aui:column>
				</liferay-ui:panel>
			</liferay-ui:panel-container>
		</div><%
		i++;
	} 
}
%>

<aui:fieldset>
	<div class="product-total">
		<div><strong><liferay-ui:message key="total"/></strong></div>
		<div><strong><%= CommonUtil.getPriceFormat(totalPrice) %></strong></div>
	</div>
</aui:fieldset>

<aui:button-row>
	<aui:button type="button" value='<%= LanguageUtil.get(portletConfig, locale, "checkout") %>' href="<%= checkOutURL %>"/>
</aui:button-row>

<aui:script>
function removeItems(itemId, url) {
	
	var A = AUI();
	A.use('aui-io-request', function(A) {
		A.io.request(url, {
			method : 'POST',
			dataType : 'html',
			data : {
				<portlet:namespace/>itemId:itemId,
			},
			sync : false,
			on : {
	         	success : function() {
	         		location.reload();
				},
	       		failure : function() {
	        		alert("Something went wrong !!Please try again..");
				}
			}
	 	});
	});
}

function showPopup(url, height, width, title){
	
	AUI().use('aui-base','liferay-util-window','aui-io-plugin-deprecated',function(A){
		  
    	var popup = Liferay.Util.Window.getWindow(
                {
                    dialog: {
                        centered: true,
                        constrain2view: true,
                        modal: true,
                        resizable: false,
                        width: width
                    }
                }).plug(A.Plugin.DialogIframe,
                     {
                     autoLoad: true,
                     iframeCssClass: 'dialog-iframe',
                     uri:url
                }).render();
    		popup.show();
    		popup.titleNode.html(title);
	});
}
</aui:script>
