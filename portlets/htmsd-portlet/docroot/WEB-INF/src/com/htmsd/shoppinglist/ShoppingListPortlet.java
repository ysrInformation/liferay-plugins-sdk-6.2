package com.htmsd.shoppinglist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.servlet.http.Cookie;

import com.htmsd.slayer.NoSuchShoppingItem_CartException;
import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ShoppingListPortlet
 */
public class ShoppingListPortlet extends MVCPortlet {

	private final static Log _log = LogFactoryUtil.getLog(ShoppingListPortlet.class);
	
	/**
	 * Save Item To Shopping Cart 
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	public void addItemToCart(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		_log.info("Adding Item to Cart");
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long itemId = ParamUtil.getLong(actionRequest, HConstants.ITEM_ID);
		
		if (themeDisplay.isSignedIn()) {
			addSignedInUserCart(actionRequest, themeDisplay, itemId);
		} else {
			addGuestUserCart(actionRequest,actionResponse, itemId);
		}
		
		actionResponse.setRenderParameter(HConstants.JSP_PAGE, HConstants.PAGE_SHOPPING_LIST_DETAILS);
		actionResponse.setRenderParameter(HConstants.ITEM_ID, String.valueOf(itemId));
	}

	private void addGuestUserCart(ActionRequest actionRequest, ActionResponse actionResponse, long itemId) {

		_log.info("In addGuestUserCart method, itemId :::"+itemId); 
		/*HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(actionRequest);
		
		Cookie[] cookies = servletRequest.getCookies(); 
		
		String itemIdString = null;
		for (Cookie cookie : cookies) {
			System.out.println("Name ::" +cookie.getName()+"| Value ::"+cookie.getValue()); 
		    if ("SHOPPING_ITEM_ID".equals(cookie.getName())){
		    	itemIdString = cookie.getValue();
		    }
		}
		if (Validator.isNotNull(itemIdString) && itemIdString.equalsIgnoreCase(String.valueOf(itemId))) {  
			SessionErrors.add(actionRequest, "item-exist");
		} else {
			Cookie cookie = createCookie("SHOPPING_ITEM_ID", String.valueOf(itemId));
			actionResponse.addProperty(cookie); 
		}
		*/
		PortletSession portletSession = actionRequest.getPortletSession();
		List<Long> itemIds = new ArrayList<Long>(); 
		itemIds.add(itemId);
		if (Validator.isNotNull(portletSession.getAttribute("SHOPPING_ITEM_ID", PortletSession.APPLICATION_SCOPE))) {
			SessionErrors.add(actionRequest, "item-exist");
		} else {
			portletSession.setAttribute("SHOPPING_ITEM_ID", itemIds, PortletSession.APPLICATION_SCOPE);	
			System.out.println(portletSession.getAttribute("SHOPPING_ITEM_ID", PortletSession.APPLICATION_SCOPE));
		}
	}

	private void addSignedInUserCart(ActionRequest actionRequest,
			ThemeDisplay themeDisplay, long itemId) {
		
		ShoppingCart shoppingCart = ShoppingCartLocalServiceUtil.insertShoppingCart(
				themeDisplay.getUserId(), themeDisplay.getCompanyId(), 
				themeDisplay.getScopeGroupId(), themeDisplay.getUser().getFullName());
		
		ShoppingItem_Cart shoppingItem_Cart = null;
		try {
			shoppingItem_Cart = ShoppingItem_CartLocalServiceUtil.findByCartAndItemId(shoppingCart.getCartId(), itemId);
		} catch (NoSuchShoppingItem_CartException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(shoppingItem_Cart)) {
			SessionErrors.add(actionRequest, "item-exist");
		} else {
			ShoppingItem shoppingItem = CommonUtil.getShoppingItem(itemId);
			double totalPrice = (Validator.isNotNull(shoppingItem) ? shoppingItem.getTotalPrice():0);
			ShoppingItem_CartLocalServiceUtil.insertItemsToCart(HConstants.INITIAL_QUANTITY, shoppingCart.getCartId(), itemId, totalPrice);
		}
	}
	
	private Cookie createCookie(String cookieName, String cookieValue) {
	    Cookie cookie = new Cookie(cookieName, cookieValue);
	    cookie.setPath(StringPool.SLASH);
	    cookie.setMaxAge(0);
	    cookie.setHttpOnly(true);
	    cookie.setSecure(true);
	    cookie.setVersion(0);
	    return cookie;
	}
}
