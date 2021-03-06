package com.htmsd.shoppinglist;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.servlet.http.HttpSession;

import com.htmsd.slayer.NoSuchShoppingItem_CartException;
import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.htmsd.util.ShoppingBean;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
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
		boolean isRedirectDisabled = ParamUtil.getBoolean(actionRequest, "isRedirectDisabled");
		
		if (themeDisplay.isSignedIn()) {
			addSignedInUserCart(actionRequest, themeDisplay, itemId);
		} else {
			addGuestUserCart(actionRequest,actionResponse, itemId);
		}
		
		if (isRedirectDisabled) {
			actionResponse.setRenderParameter(HConstants.JSP_PAGE, HConstants.PAGE_SHOPPING_LIST_DETAILS);
			actionResponse.setRenderParameter(HConstants.ITEM_ID, String.valueOf(itemId));
		}
	}
	
	/**
	 * Method for adding guest user Items
	 * @param actionRequest
	 * @param actionResponse
	 * @param itemId
	 */
	@SuppressWarnings("unchecked")
	private void addGuestUserCart(ActionRequest actionRequest, ActionResponse actionResponse, long itemId) {

		_log.info("In addGuestUserCart method, itemId :::"+itemId); 

		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest).getSession();
	
		boolean isItemExist = false;
		List<ShoppingBean> shoppingCartList = CommonUtil.getGuestUserList(session);
		if (Validator.isNotNull(shoppingCartList) && shoppingCartList.size() > 0) {
			for (ShoppingBean shoppingBean:shoppingCartList) {
				if (itemId == shoppingBean.getItemId()) {
					isItemExist = true;
				}
			}
		}
		
		if (isItemExist) {
			SessionErrors.add(actionRequest, "item-exist");
		} else {
			CommonUtil.getGuestUserItems(itemId, session);
		}
	}
	
	/**
	 * Method for adding loggedin user items in cart
	 * @param actionRequest
	 * @param themeDisplay
	 * @param itemId
	 */
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
			double totalPrice = (Validator.isNotNull(shoppingItem) ? shoppingItem.getSellingPrice() : 0);
			ShoppingItem_CartLocalServiceUtil.insertItemsToCart(HConstants.INITIAL_QUANTITY, shoppingCart.getCartId(), itemId, totalPrice);
		}
	}
}
