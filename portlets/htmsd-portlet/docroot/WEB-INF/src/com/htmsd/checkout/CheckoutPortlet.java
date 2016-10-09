package com.htmsd.checkout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpSession;

import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.model.UserInfo;
import com.htmsd.slayer.model.WholeSale;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.slayer.service.UserInfoLocalServiceUtil;
import com.htmsd.slayer.service.WholeSaleLocalServiceUtil;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.htmsd.util.ShoppingBean;
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
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.ListType;
import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.AddressLocalServiceUtil;
import com.liferay.portal.service.ListTypeServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class CheckoutPortlet
 */
public class CheckoutPortlet extends MVCPortlet {

	public void confirmOrder(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		_log.info("In confirmOrder.."); 
		
		String paymentMode = ParamUtil.getString(actionRequest, "paymentMethod");
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		String successMessage = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "you-order-has-been-registered-you-may-get-email-shortly");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		actionResponse.setRenderParameter("order_step", "step5");
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

	public void updateUserInfo(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException, PortalException, SystemException {
		
		_log.info("In updateUserInfo method.."); 
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		ServiceContext serviceContext = getServiceContext(actionRequest);
		
		long userId = themeDisplay.getUserId();
		long addressId = ParamUtil.getLong(actionRequest, "addressId");
		long shippingCountry = ParamUtil.getLong(actionRequest, "country");
		long shippingState = ParamUtil.getLong(actionRequest, "state");
		
		String firstName = ParamUtil.getString(actionRequest, "firstName");
		String lastName = ParamUtil.getString(actionRequest, "lastName");
		String email = ParamUtil.getString(actionRequest, "email");
		String shippingMoble = ParamUtil.getString(actionRequest, "mobileNumber");
		String shippingAltMoble = ParamUtil.getString(actionRequest, "altNumber");
		String shippingStreet = ParamUtil.getString(actionRequest, "street");
		String shippingCity = ParamUtil.getString(actionRequest, "city");
		String shippingZip = ParamUtil.getString(actionRequest, "zip");
		
		User user = UserLocalServiceUtil.getUser(userId); 
		user.setFirstName(firstName);
		user.setLastName(lastName); 
		user.setEmailAddress(email);
		UserLocalServiceUtil.updateUser(user);
		
		Address address = null;
		if (addressId > 0) {
			address = AddressLocalServiceUtil.fetchAddress(addressId);
			address.setCountryId(shippingCountry); 
			address.setRegionId(shippingState);
			address.setStreet1(shippingStreet);
			address.setZip(shippingZip);
			address.setCity(shippingCity); 
			AddressLocalServiceUtil.updateAddress(address);
		} else {
			int typeId = getListTypeId(Contact.class.getName() + ListTypeConstants.ADDRESS, "business");
			try {
				address = AddressLocalServiceUtil.addAddress(themeDisplay.getUserId(), Contact.class.getName(), 
						themeDisplay.getContact().getContactId(), shippingStreet, StringPool.BLANK, StringPool.BLANK,
						shippingCity, shippingZip, shippingState, shippingCountry, typeId, false, true, serviceContext);
			} catch (PortalException e) {
				_log.error(e);
			} catch (SystemException e) {
				_log.error(e);
			}
		}
		
		UserInfo userInfo = UserInfoLocalServiceUtil.addUserInfo(userId, themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(), address.getAddressId(), 0l, 
				shippingMoble, shippingAltMoble);
		
		_log.info("UserInfoId is :"+userInfo.getUserInfoId()); 
		
		String successMessage = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "your-address-saved-successfully");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		actionResponse.setRenderParameter("jspPage", "/html/checkout/view.jsp");
		actionResponse.setRenderParameter("order_step", "step3");
	}

	private ServiceContext getServiceContext(ActionRequest actionRequest) {
		ServiceContext serviceContext = null;
		try {
			serviceContext = ServiceContextFactory.getInstance(actionRequest);
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
		return serviceContext;
	}
	
	public int getListTypeId(String type, String name) throws SystemException {
		int typeId = 0;
		List<ListType> listTypes = ListTypeServiceUtil.getListTypes(type);
		if (Validator.isNotNull(listTypes)) {
			for (ListType listType : listTypes) {
				if (listType.getName().equals(name)) {
					typeId = listType.getListTypeId();
					break;
				}
			}
		}
		return typeId;
	}
	
	private static final Log _log = LogFactoryUtil.getLog(CheckoutPortlet.class.getName());

}
