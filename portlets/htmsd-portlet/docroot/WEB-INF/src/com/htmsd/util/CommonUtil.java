package com.htmsd.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.htmsd.slayer.NoSuchShoppingItem_CartException;
import com.htmsd.slayer.model.Category;
import com.htmsd.slayer.model.Currency;
import com.htmsd.slayer.model.Invoice;
import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.ShoppingOrderItem;
import com.htmsd.slayer.service.CategoryLocalServiceUtil;
import com.htmsd.slayer.service.CurrencyLocalServiceUtil;
import com.htmsd.slayer.service.InvoiceLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItem_CartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Country;
import com.liferay.portal.model.Phone;
import com.liferay.portal.model.Region;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CountryServiceUtil;
import com.liferay.portal.service.RegionServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetVocabulary;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileShortcut;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;

public class CommonUtil {
	
	private final static Log _log = LogFactoryUtil.getLog(CommonUtil.class);
	
	/**
	 * 
	 * @param fileEntryId
	 * @param themeDisplay
	 * @return
	 */
	public static String getThumbnailpath(long fileEntryId,
			long groupId, boolean isThumbnail) {

		_log.info(" get Thumbnail ");

		String thumbnail = StringPool.BLANK;
		FileEntry fileEntry = null;
		DLFileShortcut dlFileShortcut = null;

		try {
			fileEntry = DLAppLocalServiceUtil.getFileEntry(fileEntryId);
			thumbnail = "/documents/"
					+ groupId
					+ StringPool.SLASH
					+ fileEntry .getFolderId()
					+ StringPool.SLASH
					+ HttpUtil.encodeURL(HtmlUtil.unescape(String.valueOf(fileEntry .getTitle())))
					+ StringPool.SLASH
					+ fileEntry.getUuid()
					+ "?version=" + fileEntry .getVersion()
					+ "&t=" + fileEntry.getFileVersion().getModifiedDate().getTime();
			if (isThumbnail) {
				thumbnail+= "&imageThumbnail=1";
			}

		} catch (Exception e) {
			_log.error("Exception occured when getting Thumbnail" + e);
		}
		return thumbnail;
	}
	
	public static List<ShoppingItem> getAllPendingItems(int status) {
		
		List<ShoppingItem> shoppingItems = Collections.emptyList();
		
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingItem.class, PortletClassLoaderUtil.getClassLoader());
		dynamicQuery.add(RestrictionsFactoryUtil.eq("status", status));
		try {
			shoppingItems = ShoppingItemLocalServiceUtil.dynamicQuery(dynamicQuery);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return shoppingItems;
	}
	
	public static String getPriceFormat(double price) {
		
		String priceInString = StringPool.BLANK;
		DecimalFormat df = new DecimalFormat("0.00");
		priceInString = HConstants.RUPPEE_SYMBOL+StringPool.PERIOD+ StringPool.SPACE + df.format(price);
		return priceInString;
	}
	 
	public static boolean hasShoppingCart(long userId) {
		
		ShoppingCart shoppingCart = null;
		try {
			shoppingCart = ShoppingCartLocalServiceUtil.getShoppingCartByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return (Validator.isNotNull(shoppingCart));
	}

	public static ShoppingCart getShoppingCartByUserId(long userId) {
		
		ShoppingCart shoppingCart = null;
		try {
			shoppingCart = ShoppingCartLocalServiceUtil.getShoppingCartByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return shoppingCart;
	}
	
	public static List<ShoppingItem_Cart> getUserCartItems(long userId) {
		
		ShoppingCart shoppingCart = null;
		try {
			shoppingCart = ShoppingCartLocalServiceUtil.getShoppingCartByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		List<ShoppingItem_Cart> shoppingItem_Carts = Collections.emptyList();
		if (Validator.isNotNull(shoppingCart)) {
			try {
				shoppingItem_Carts = ShoppingItem_CartLocalServiceUtil.findByCartId(shoppingCart.getCartId());
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		
		return shoppingItem_Carts;
	}
	
	public static ShoppingItem getShoppingItem(long itemId) {
		
		ShoppingItem shoppingItem = null;
		if (itemId > 0) {
			try {
				shoppingItem = ShoppingItemLocalServiceUtil.fetchShoppingItem(itemId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		return shoppingItem;
	}
	
	public static String[] getUserAddress(long userId) {
		
		String[] userAddress = new String[6];

		User user = null;
		Phone phone = null;
		Address address = null;
		List<Phone> phoneList = new ArrayList<Phone>();
		List<Address> addresses = new ArrayList<Address>(); 
		try {
			user = UserLocalServiceUtil.fetchUser(userId);
			if (Validator.isNotNull(user)) 
				addresses = user.getAddresses();
				phoneList = user.getPhones();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (addresses.size() > 0) {
			address = addresses.get(0);
		}
		
		if (phoneList.size() > 0) {
			phone = phoneList.get(0);			
		}
		
		userAddress[0] = (Validator.isNotNull(address))?address.getStreet1():StringPool.BLANK;
		userAddress[1] = (Validator.isNotNull(address))?address.getCity():StringPool.BLANK;
		userAddress[2] = (Validator.isNotNull(address))?address.getZip():StringPool.BLANK;
		userAddress[3] = (Validator.isNotNull(address))?String.valueOf(address.getCountryId()):"0";
		userAddress[4] = (Validator.isNotNull(address))?String.valueOf(address.getRegionId()):"0";
		userAddress[5] = (Validator.isNotNull(phone))? phone.getNumber():StringPool.BLANK;

		return userAddress;
	}
	
	@SuppressWarnings("unchecked")
	public static List<ShoppingOrder> getShoppingOrders(int orderStatus, long groupId) {
		
		List<ShoppingOrder> pendingOrderList = Collections.emptyList();
		DynamicQuery orderQuery = DynamicQueryFactoryUtil.forClass(ShoppingOrder.class);
		orderQuery.add(RestrictionsFactoryUtil.eq("orderStatus", orderStatus));
		orderQuery.add(RestrictionsFactoryUtil.eq("groupId", groupId));
		try {
			pendingOrderList = ShoppingOrderLocalServiceUtil.dynamicQuery(orderQuery);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return pendingOrderList;
	}
	
	public static List<ShoppingOrder> getShoppingOrderByTabNames(long groupId, String tabName) {
		
		List<ShoppingOrder> shoppingOrders = Collections.emptyList();
		boolean isPendingOrderTab = tabName.equalsIgnoreCase("Pending");
		boolean isShippingOrderTab = tabName.equalsIgnoreCase("Shipping");
		boolean isOrderCancelledTab = tabName.equalsIgnoreCase("Order Cancelled");
		
		if (isPendingOrderTab) {
			shoppingOrders = getShoppingOrders(HConstants.PENDING, groupId);
		} else if (isShippingOrderTab) {
			shoppingOrders = getShoppingOrders(HConstants.SHIPPING, groupId);
		} else if(isOrderCancelledTab) { 
			shoppingOrders = getShoppingOrders(HConstants.CANCEL_ORDER, groupId);
		} else {
			shoppingOrders = getShoppingOrders(HConstants.DELIVERED, groupId);
		}
		
		return shoppingOrders;
	}
	
	public static String getCountry(long countryId) {
		
		String countryName = StringPool.BLANK;
		if (countryId >0l) {
			Country country = null;
			try {
				country = CountryServiceUtil.fetchCountry(countryId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			countryName = (Validator.isNotNull(country))?
				TextFormatter.format(country.getName(), TextFormatter.J):StringPool.BLANK;
		}
		
		return countryName;
	}
	
	public static String getState(long regionId) {
		
		String state = StringPool.BLANK;
		if (regionId >0l) {
			Region region = null;
			try {
				region = RegionServiceUtil.getRegion(regionId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			state = (Validator.isNotNull(region))?region.getName():StringPool.BLANK;
		}
		
		return state;
	}
	
	public static List<ShoppingOrder> getMyOrders(long userId) {
		return (ShoppingOrderLocalServiceUtil.getShoppingOrderByUserId(userId));
	}
	
	public static int getItemsCount(long userId, ThemeDisplay themeDisplay, HttpSession session) {
		
		int itemsCount = 0;
		if (themeDisplay.isSignedIn()) {
			try {
				itemsCount = ShoppingItem_CartLocalServiceUtil.getItemsCountByCartId(userId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			itemsCount = (!getGuestUserList(session).isEmpty()) ? getGuestUserList(session).size() : 0;
		}
		return itemsCount;
	}
	
	public static List<ShoppingOrderItem> getShoppingOrderItems(long orderId) {
		
		List<ShoppingOrderItem> shoppingOrderItems = Collections.emptyList();
		try {
			shoppingOrderItems = ShoppingOrderItemLocalServiceUtil.getShoppingOrderItemsByOrderId(orderId);
		} catch (Exception e) {
			_log.error("No order exist  ::"+e);
		}
		return shoppingOrderItems;
	}
	
	public static String getBillNumber(long orderId) {
		
		String billNumber = StringPool.BLANK;
		Invoice invoice = null;
		try {
			invoice = InvoiceLocalServiceUtil.getInvoiceByOrderId(orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		billNumber = (Validator.isNotNull(invoice))?String.valueOf(invoice.getInvoiceId()):"NA";
		return billNumber;
	}
	
	public static String getOrderStatus(int orderStatus) {
		
		String orderStatusString = "N/A";
		AssetCategory category = getAssetCategoryById(orderStatus);
		if (Validator.isNull(category)) return orderStatusString;
		
		if (orderStatus == HConstants.PENDING) {
			orderStatusString = HConstants.PROCESSING_STATUS;
		} else  {
			if (category.getName().equals(HConstants.CANCEL_ORDER_STATUS)) {
				orderStatusString = HConstants.ORDER_CANCELLED_STATUS;
			} else {
				orderStatusString = category.getName();
			}
		}
		return orderStatusString;
	}
	
	public static int[] getItemsQuantity(long itemId, long cartId) {
		
		int allUserQuantity = 0;
		int remainingQnty = 0;
		int userQnty = 0;
		long totalStock = 0l;
		int[] quantityArray = null;
		ShoppingItem_Cart shoppingItem_Cart = null;
		ShoppingItem shoppingItem = CommonUtil.getShoppingItem(itemId);
		List<ShoppingItem_Cart> shoppingItem_Carts = new ArrayList<ShoppingItem_Cart>();
		
		if (Validator.isNotNull(shoppingItem)) {
			totalStock = shoppingItem.getQuantity();
			try {
				shoppingItem_Carts = ShoppingItem_CartLocalServiceUtil.getShoppingItemByItemId(itemId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			try {
				shoppingItem_Cart = ShoppingItem_CartLocalServiceUtil.findByCartAndItemId(cartId, itemId);
			} catch (NoSuchShoppingItem_CartException e) {
				_log.error("No such Item exist with the cartId"+e); 
			} catch (SystemException e) {
				e.printStackTrace();
			}
			
			if (Validator.isNotNull(shoppingItem_Carts) && shoppingItem_Carts.size() > 0) {
				for (ShoppingItem_Cart shoppingItem_cart : shoppingItem_Carts) {
					allUserQuantity += shoppingItem_cart.getQuantity();
				}
			}
			userQnty = (Validator.isNotNull(shoppingItem_Cart) && shoppingItem_Cart.getQuantity() > 0) ? 
					shoppingItem_Cart.getQuantity()  : 0;
		}
		
		if ((allUserQuantity) < totalStock) {
			remainingQnty = ((int) totalStock - allUserQuantity) + userQnty;
		} else if(totalStock == -1) {
			remainingQnty = HConstants.MAX_QUANTITY + userQnty;
		} else {
			remainingQnty = userQnty;
		}
		
		quantityArray = new int[remainingQnty];
		if (remainingQnty > 0) {
			for (int i=1;i<=(remainingQnty);i++) {
				quantityArray[i-1] = i;
			}
		}
		
		return quantityArray;
	}
	
	public static String getPriceInNumberFormat(double totalPrice, String priceSymbol) {
		
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMinimumFractionDigits(2);
		String formattedPrice = priceSymbol+StringPool.SPACE+numberFormat.format(totalPrice);
		return formattedPrice;
	}
	
	public  static Category getShoppingItemParentCategory(long itemId) {
		Category category = ShoppingItemLocalServiceUtil.getShoppingItemCategory(itemId);
		long parentCategoryId = category.getParentCategoryId();
		Category parenCategory = null;
		try {
			parenCategory = CategoryLocalServiceUtil.fetchCategory(parentCategoryId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return parenCategory;
	}
	
	public static String getCurrencySymbol(long currencyId) {
		Currency currencyRates = getCurrency(currencyId);
		if (Validator.isNull(currencyRates)) return HConstants.RUPPEE_SYMBOL;
        java.util.Currency currency = java.util.Currency.getInstance(currencyRates.getCurrencyCode());
        
        return currency.getSymbol();
	}

	private static Currency getCurrency(long currencyId) {
		
		Currency currencyRates = null;
		try {
			currencyRates = CurrencyLocalServiceUtil.fetchCurrency(currencyId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return currencyRates;
	}
	
	public static double getCurrentRate(long currencyId) {
		Currency currencyRates = getCurrency(currencyId);
		if (Validator.isNull(currencyRates)) return 0;
		return currencyRates.getConversion();
	}
	
	public static String getPriceFormat(double price, long currencyId) {
		String priceInString = StringPool.BLANK;
		NumberFormat df = NumberFormat.getInstance();
		String symbol = getCurrencySymbol(currencyId);
		try {
			java.util.Currency currency = java.util.Currency.getInstance(symbol);
			priceInString = currency.getSymbol() +" "+ df.format(price);
		} catch (IllegalArgumentException e) {
			priceInString = symbol + " " + df.format(price);
		}
		return priceInString;
	}
	
	public static List<ShoppingBean> getSignedInUserItems(long userId){
		
		_log.info("Inside getSignedInUserItems ..."); 
		List<ShoppingBean> shoppingBeans = new ArrayList<ShoppingBean>();
		
		for (ShoppingItem_Cart shIt_cart: getUserCartItems(userId)) {
			ShoppingBean shoppingBean = new ShoppingBean();
			ShoppingItem shoppingItem = getShoppingItem(shIt_cart.getItemId());
			String[] imageIds = (Validator.isNotNull(shoppingItem))?shoppingItem.getImageIds().split(","):new String[]{}; 
			shoppingBean.setCartId(shIt_cart.getCartId());
			shoppingBean.setQuantity(shIt_cart.getQuantity());
			shoppingBean.setTotalPrice(shIt_cart.getTotalPrice()); 
			shoppingBean.setItemId(shIt_cart.getItemId()); 
			shoppingBean.setUnitPrice(shoppingItem.getTotalPrice()); 
			shoppingBean.setProductName(shoppingItem.getName());
			shoppingBean.setImageId(Long.valueOf(imageIds[0]));
			shoppingBean.setCartItemId(shIt_cart.getId());
			shoppingBean.setDescription((Validator.isNotNull(shoppingItem))?shoppingItem.getDescription():StringPool.BLANK); 
			shoppingBean.setProductCode(shoppingItem.getProductCode()); 
			shoppingBeans.add(shoppingBean);
		}
		
		return shoppingBeans;
	}
	
	@SuppressWarnings("unchecked")
	public static void getGuestUserItems(long itemId, HttpSession session) {
		
		_log.info("Inside guestUserItem ...."); 
		
		List<ShoppingBean> newShoppingCartList = new ArrayList<ShoppingBean>();
		List<ShoppingBean> shoppingCartList = getGuestUserList(session);
		
		ShoppingBean shoppingBean = new ShoppingBean();
		ShoppingItem shoppingItem = getShoppingItem(itemId);
		String[] imageIds = (Validator.isNotNull(shoppingItem))?shoppingItem.getImageIds().split(","):new String[]{};
		double totalPrice = shoppingItem.getTotalPrice() * HConstants.INITIAL_QUANTITY;
		shoppingBean.setItemId(itemId);
		shoppingBean.setProductCode(shoppingItem.getProductCode());
		shoppingBean.setProductName(shoppingItem.getName());
		shoppingBean.setQuantity(HConstants.INITIAL_QUANTITY);
		shoppingBean.setUnitPrice(shoppingItem.getTotalPrice()); 
		shoppingBean.setTotalPrice(totalPrice);
		shoppingBean.setDescription((Validator.isNotNull(shoppingItem))?shoppingItem.getDescription():StringPool.BLANK); 
		shoppingBean.setImageId(Long.parseLong(imageIds[0]));
		
		if (Validator.isNotNull(shoppingCartList) && shoppingCartList.size() > 0) {
			for (ShoppingBean shoppingcartItem: shoppingCartList) {
				newShoppingCartList.add(shoppingcartItem);
			} 
		}
		newShoppingCartList.add(shoppingBean);
		
		session.removeAttribute("SHOPPING_ITEMS"); 
		session.setAttribute("SHOPPING_ITEMS", newShoppingCartList);
	}
	
	@SuppressWarnings("unchecked")
	public static List<ShoppingBean> getGuestUserList(HttpSession httpsession) {
		
		_log.info("Inside getGuestUserList ..."); 
		List<ShoppingBean> shoppingBeanList = new ArrayList<ShoppingBean>();
		if (Validator.isNotNull(httpsession.getAttribute("SHOPPING_ITEMS"))) {
			shoppingBeanList = (List<ShoppingBean>) httpsession.getAttribute("SHOPPING_ITEMS");
		}
		return shoppingBeanList;
	}
	
	public static List<AssetCategory> getAssetCategoryList(long groupId, String vocabularyName) {
		
		AssetVocabulary assetVocabulary = null;
		List<AssetCategory> assetCategories = new ArrayList<AssetCategory>();
		try {
			assetVocabulary = AssetVocabularyLocalServiceUtil.getGroupVocabulary(groupId, vocabularyName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (Validator.isNull(assetVocabulary)) return assetCategories;
		
		assetCategories = getAssetCategory(assetVocabulary.getVocabularyId());
		
		return assetCategories;
	}
	
	public static List<AssetCategory> getAssetCategory(long vocabularyId) {
		
		_log.info("Inside Asset Category....");
		List<AssetCategory> assetCategories = new ArrayList<AssetCategory>();
		try {
			assetCategories = AssetCategoryLocalServiceUtil.getVocabularyCategories(vocabularyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return assetCategories;
	}
	
	public static AssetCategory getAssetCategoryById(long categoryId) {
		AssetCategory category = null;
		try {
			category = AssetCategoryLocalServiceUtil.fetchAssetCategory(categoryId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return category;
	}
}