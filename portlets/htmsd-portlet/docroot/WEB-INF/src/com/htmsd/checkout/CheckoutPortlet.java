package com.htmsd.checkout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpSession;

import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.model.WholeSale;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.slayer.service.WholeSaleLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.htmsd.util.ShoppingBean;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class CheckoutPortlet
 */
public class CheckoutPortlet extends MVCPortlet {
	
	public void step1(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
	
		System.out.println("Checkout :- Step 1");
		actionResponse.setRenderParameter("order_step", "step2"); 
	}
	
	public void step2(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
	
		System.out.println("Checkout :- Step 2");
		actionResponse.setRenderParameter("order_step", "step3");
	}
	
	public void step3(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		System.out.println("Checkout :- Step 3");
		actionResponse.setRenderParameter("order_step", "step4");
	}
	
	public void step4(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		System.out.println("Checkout :- Step 4");
		actionResponse.setRenderParameter("order_step", "step5");
	}
	
	public void step5(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		System.out.println("Checkout :- Step 5");
	}
	
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws IOException, PortletException {
		String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);
		if (Validator.isNotNull(cmd)) {
			if (cmd.equalsIgnoreCase("remove-item")){
				removeItem(resourceRequest, resourceResponse);
			} else if (cmd.equalsIgnoreCase("total-price")) {
				updateItemPrice(resourceRequest);
			}
		}
	}
 
	private void updateItemPrice(ResourceRequest resourceRequest) {
		
		int quantity = ParamUtil.getInteger(resourceRequest, "quantity");
		double itemPrice = ParamUtil.getDouble(resourceRequest, "itemPrice");
		long id = ParamUtil.getLong(resourceRequest, "id");
		long itemId = ParamUtil.getLong(resourceRequest, "itemId");
		
		HttpSession session = PortalUtil.getHttpServletRequest(resourceRequest).getSession();
		List<ShoppingBean> shoppingCartList = CommonUtil.getGuestUserList(session);
		List<ShoppingBean> newShoppingCartList = new ArrayList<ShoppingBean>();
		itemPrice = getWholeSalePrice(quantity, itemId, itemPrice);
				
		if (id > 0) {
			double totalPrice = quantity * itemPrice;
			ShoppingItem_CartLocalServiceUtil.updateShoppingItem_Cart(id, quantity, totalPrice);
		} else {
			if (Validator.isNotNull(shoppingCartList) && shoppingCartList.size() > 0) {
				for (ShoppingBean shoppingBean:shoppingCartList) {
					if (itemId == shoppingBean.getItemId()) {
						double total = quantity * itemPrice;
						shoppingBean.setQuantity(quantity); 
						shoppingBean.setTotalPrice(total);
						newShoppingCartList.add(shoppingBean);
					} else {
						newShoppingCartList.add(shoppingBean);
					}
				}
			}
			session.setAttribute("SHOPPING_ITEMS", newShoppingCartList);
		}
	}

	/**
	 * Method for removing item from cart
	 * @param resourceRequest
	 * @param resourceResponse
	 */
	private void removeItem(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		
		_log.info("Inside removeItem ..."); 
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		HttpSession session = PortalUtil.getHttpServletRequest(resourceRequest).getSession();
		long itemId = ParamUtil.getLong(resourceRequest, HConstants.ITEM_ID);
		
		List<ShoppingBean> newCartList = new ArrayList<ShoppingBean>();
		List<ShoppingItem_Cart> shoppingItem_Carts = CommonUtil.getUserCartItems(themeDisplay.getUserId());
		List<ShoppingBean> shoppingbeanList = CommonUtil.getGuestUserList(session);
		
		if (themeDisplay.isSignedIn()) {
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
		} else {
			//delete from guest
			if (Validator.isNotNull(shoppingbeanList) && shoppingbeanList.size() > 0) {
				for (ShoppingBean shoppingBean:shoppingbeanList) {
					newCartList.add(shoppingBean);
					if (itemId == shoppingBean.getItemId()) {
						newCartList.remove(shoppingBean);
					}
				}
			}
			session.setAttribute("SHOPPING_ITEMS", newCartList); 
		}
	}
	
	private double getWholeSalePrice(int quantity, long itemId, double itemPrice) {
		
		List<WholeSale> wholesaleList = WholeSaleLocalServiceUtil.getWholeSaleByQty(itemId, quantity);
		
		if (Validator.isNotNull(wholesaleList)) {
			for (WholeSale wholeSale: wholesaleList) {
				if (quantity == wholeSale.getQuantity() || quantity >= wholeSale.getQuantity()) {
					itemPrice = wholeSale.getPrice(); 
				}
			}
		}
		
		return itemPrice;
	}
	
	private static final Log _log = LogFactoryUtil.getLog(CheckoutPortlet.class.getName());

}
