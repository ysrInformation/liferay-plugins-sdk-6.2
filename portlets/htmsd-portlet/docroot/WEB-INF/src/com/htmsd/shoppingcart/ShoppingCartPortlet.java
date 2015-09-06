package com.htmsd.shoppingcart;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.htmsd.slayer.model.Invoice;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.ShoppingOrderItem;
import com.htmsd.slayer.service.InvoiceLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class ShoppingCartPortlet
 */
public class ShoppingCartPortlet extends MVCPortlet {
	
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
		Invoice invoice = InvoiceLocalServiceUtil.insertInvoice(themeDisplay.getUserId(), shoppingOrder.getOrderId());
		addShoppingOrderItem(themeDisplay, shoppingOrder);
		
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		String successMessage = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "you-order-has-been-registered-you-may-get-email-shortly");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		actionResponse.setRenderParameter(HConstants.JSP_PAGE, HConstants.PAGE_SHOPPING_CART_DETAILS); 
		actionResponse.setRenderParameter("tab1", "my-orders"); 
	}

	/**
	 * Method for adding items in ShoppingOrderItems
	 * @param themeDisplay
	 * @param shoppingOrder
	 */
	private void addShoppingOrderItem(ThemeDisplay themeDisplay, ShoppingOrder shoppingOrder) {
		
		double totalPrice = 0;
		List<ShoppingItem_Cart> shoppingItem_Carts = CommonUtil.getUserCartItems(themeDisplay.getUserId());
		
		if (Validator.isNotNull(shoppingItem_Carts) && shoppingItem_Carts.size() > 0) {
			for (ShoppingItem_Cart shoppingItem_Cart:shoppingItem_Carts) {
				
				ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingItem_Cart.getItemId()); 
				
				if (Validator.isNotNull(shoppingItem)) {
					totalPrice = shoppingItem.getSellerPrice()+shoppingItem.getTotalPrice();
					ShoppingOrderItem shoppingOrderItem = ShoppingOrderItemLocalServiceUtil
						.insertShoppingOrderItem((int)shoppingItem.getQuantity(), totalPrice, themeDisplay.getUserId(), 
						themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(), shoppingOrder.getOrderId(), 
						shoppingItem.getName(), shoppingItem.getDescription(), shoppingItem.getProductCode());
					
					_log.info("shopping order Item ==>"+shoppingOrderItem.getItemId());
					
					//deleting items from shoppingItem_Cart table. 
					try {
						ShoppingItem_CartLocalServiceUtil.deleteShoppingItem_Cart(shoppingItem_Cart.getId());
					} catch (PortalException e) {
						e.printStackTrace();
					} catch (SystemException e) {
						e.printStackTrace();
					}
				}
			}
		}
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
	private void removeItem(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long itemId = ParamUtil.getLong(resourceRequest, HConstants.ITEM_ID);
		
		_log.info("ItemId is ==>" +itemId); 
		
		List<ShoppingItem_Cart> shoppingItem_Carts = CommonUtil.getUserCartItems(themeDisplay.getUserId());
		
		if (Validator.isNotNull(shoppingItem_Carts)) {
			for (ShoppingItem_Cart shoppingItem_Cart:shoppingItem_Carts) {
				if (itemId == shoppingItem_Cart.getItemId()) {
					try {
						ShoppingItem_CartLocalServiceUtil.deleteShoppingItem_Cart(shoppingItem_Cart.getId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(ShoppingCartPortlet.class.getName());
}
