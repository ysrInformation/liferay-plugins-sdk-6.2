package com.htmsd.shoppingcart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.ShoppingOrderItem;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ShoppingCartPortlet
 */
public class ShoppingCartPortlet extends MVCPortlet {
	
	
	/**
	 * Method to cart of user and save their entries
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	public void addToCart(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long itemId = ParamUtil.getLong(actionRequest, HConstants.ITEM_ID);
		ShoppingCart shoppingCartItem = CommonUtil.getShoppingCartByUserId(themeDisplay.getUserId());
		long cartId = (Validator.isNotNull(shoppingCartItem))?shoppingCartItem.getCartId():0L;
		
		List<Long> itemIds =  itemIds(itemId, shoppingCartItem);
		
		ShoppingCart shoppingCart = null;
		if (!CommonUtil.hasShoppingCart(themeDisplay.getUserId())) {
			shoppingCart = ShoppingCartLocalServiceUtil.insertShoppingCart(themeDisplay.getUserId(), themeDisplay.getCompanyId(),
					themeDisplay.getScopeGroupId(), themeDisplay.getUser().getFullName(), StringUtil.merge(itemIds, StringPool.COMMA)); 
		} else {
			shoppingCart = ShoppingCartLocalServiceUtil.updateShoppingCartItems(cartId, StringUtil.merge(itemIds, StringPool.COMMA));
		}
		
		SessionMessages.add(actionRequest, "you-have-added-item-an-in-your-cart");
	}
	
	/**
	 * Method is for adding details of order made by the user
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	public void checkout(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		_log.info("In checkout method ..."); 
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		String shippingFirstName = ParamUtil.getString(actionRequest, "firstName");
		String shippingLastName = ParamUtil.getString(actionRequest, "lastName");
		String shippingEmailAddress = ParamUtil.getString(actionRequest, "email");
		String shippingMoble = ParamUtil.getString(actionRequest, "mobileNumber");
		String shippingAltMoble = ParamUtil.getString(actionRequest, "altNumber");
		String shippingStreet = ParamUtil.getString(actionRequest, "street");
		String shippingCity = ParamUtil.getString(actionRequest, "city");
		String shippingZip = ParamUtil.getString(actionRequest, "zip");
		String shippingCountry = ParamUtil.getString(actionRequest, "country");
		String shippingState = ParamUtil.getString(actionRequest, "state");
		
		ShoppingOrder shoppingOrder = ShoppingOrderLocalServiceUtil.insertShoppingOrder(HConstants.PENDING, 
				themeDisplay.getUserId(),themeDisplay.getCompanyId(),themeDisplay.getScopeGroupId(),
				shippingFirstName, shippingLastName, shippingStreet, shippingCity, shippingZip, 
				shippingEmailAddress, shippingState, shippingCountry, shippingMoble, shippingAltMoble);
		
		_log.info("orderId is ==>"+shoppingOrder.getOrderId()); 
		addShoppingOrderItem(themeDisplay, shoppingOrder);
	}

	/**
	 * Method for adding items in ShoppingOrderItems
	 * @param themeDisplay
	 * @param shoppingOrder
	 */
	private void addShoppingOrderItem(ThemeDisplay themeDisplay,
			ShoppingOrder shoppingOrder) {
		
		ShoppingCart shoppingCart = CommonUtil.getShoppingCartByUserId(themeDisplay.getUserId());
		String[] itemIds = null;
		if (shoppingCart.getItemIds().contains(StringPool.COMMA)) {
			itemIds = shoppingCart.getItemIds().split(StringPool.COMMA);
		} else {
			itemIds = new String[]{shoppingCart.getItemIds()};
		}
		
		if (itemIds != null) {
			for (int i = 0; i<itemIds.length; i++) {
				ShoppingItem shoppingItem = CommonUtil.getShoppingItem(Long.parseLong(itemIds[i])); 
				if (shoppingItem != null) {
					ShoppingOrderItem shoppingOrderItem = ShoppingOrderItemLocalServiceUtil
						.insertShoppingOrderItem(shoppingItem.getTotalPrice(), themeDisplay.getUserId(), 
						themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(), shoppingOrder.getOrderId(), 
						shoppingItem.getName(), shoppingItem.getDescription(), StringPool.BLANK);
				}
			}
		}
	}
	
	/**
	 * Method is to save items ids in to shoppingcart and returns array of Ids
	 * @param itemId
	 * @param shoppingCartItem
	 * @return
	 */
	private List<Long> itemIds(long itemId, ShoppingCart shoppingCartItem) {
		
		List<Long> itemIds = new ArrayList<Long>(); 
		
		String[] itemIdArray;
		if (Validator.isNotNull(shoppingCartItem) && !shoppingCartItem.getItemIds().isEmpty()) {
			itemIdArray = shoppingCartItem.getItemIds().split(StringPool.COMMA);
			for (int i=0;i<itemIdArray.length;i++) {
				itemIds.add(Long.parseLong(itemIdArray[i]));
			}
			itemIds.add(itemId);
		} else {
			itemIds.add(itemId);
		}
		return itemIds;
	}
	
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {
		
		String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);
		if (Validator.isNotNull(cmd)) {
			if (cmd.equalsIgnoreCase("remove-item")){
				removeItem(resourceRequest, resourceResponse);
			}
		}
	}

	/**
	 * Method for removing item from cart
	 * @param resourceRequest
	 * @param resourceResponse
	 */
	private void removeItem(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long cartId = 0l;
		String itemId = ParamUtil.getString(resourceRequest, "itemId");
		_log.info("ItemId" +itemId); 
		
		ShoppingCart shoppingCart = CommonUtil.getShoppingCartByUserId(themeDisplay.getUserId());
		cartId = shoppingCart.getCartId();
		String itemIds = shoppingCart.getItemIds();
		
		List<Long> itemIdList = new ArrayList<Long>();
		
		for (String cartItemId:itemIds.split(StringPool.COMMA)) {
			_log.info("item Idsss ---"+cartItemId);
			
			if (!cartItemId.contains(itemId)) {
				_log.info("Item Ids ==>"+cartItemId); 
				itemIdList.add(Long.parseLong(cartItemId));
			}
		}
		ShoppingCartLocalServiceUtil.updateShoppingCartItems(cartId, StringUtil.merge(itemIdList, StringPool.COMMA));
	}

	private static final Log _log = LogFactoryUtil.getLog(ShoppingCartPortlet.class.getName());

}
