package com.htmsd.util;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Phone;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
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
		priceInString = HConstants.RUPPEE_SYMBOL + StringPool.SPACE + df.format(price);
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
	
	public static String[] getUserItems(long userId) {
		
		ShoppingCart shoppingCart = null;
		try {
			shoppingCart = ShoppingCartLocalServiceUtil.getShoppingCartByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		String[] userItemsArray = {};
		if (Validator.isNotNull(shoppingCart)) {
			if (!shoppingCart.getItemIds().isEmpty()) {
				userItemsArray = shoppingCart.getItemIds().split(StringPool.COMMA);
			}
		}
		
		return userItemsArray;
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
		List<Phone> phoneList = Collections.emptyList();
		List<Address> addresses = Collections.emptyList();
		try {
			user = UserLocalServiceUtil.fetchUser(userId);
			if (Validator.isNotNull(user)) 
				addresses = user.getAddresses();
				phoneList = user.getPhones();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (addresses.size() > 0 && phoneList.size() > 0) {
			phone = phoneList.get(0);
			address = addresses.get(0);
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
		
		if (isPendingOrderTab) {
			shoppingOrders = getShoppingOrders(HConstants.PENDING, groupId);
		} else {
			shoppingOrders = getShoppingOrders(HConstants.DELIVERED, groupId);
		}
		
		return shoppingOrders;
		
	}
}