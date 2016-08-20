/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.htmsd.slayer.service.impl;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import com.htmsd.orderpanel.GenerateInvoice;
import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.service.base.ShoppingCartLocalServiceBaseImpl;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.asset.model.AssetCategory;

/**
 * The implementation of the shopping cart local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.ShoppingCartLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.ShoppingCartLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.ShoppingCartLocalServiceUtil
 */
public class ShoppingCartLocalServiceImpl
	extends ShoppingCartLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.ShoppingCartLocalServiceUtil} to access the shopping cart local service.
	 */
	public ShoppingCart insertShoppingCart(long userId, long companyId, long groupId, String Username) {
		
		long cartId = 0L;
		ShoppingCart shoppingCart = null;
		try {
			shoppingCart = getShoppingCartByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}

		if(Validator.isNotNull(shoppingCart)) return shoppingCart;
		
		try {
			cartId = counterLocalService.increment();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		shoppingCart = shoppingCartLocalService.createShoppingCart(cartId);
		shoppingCart.setCompanyId(companyId);
		shoppingCart.setCreateDate(new Date());
		shoppingCart.setUserId(userId);
		shoppingCart.setGroupId(groupId);
		shoppingCart.setUserName(Username); 
		
		try {
			shoppingCart = shoppingCartLocalService.addShoppingCart(shoppingCart);
		} catch (SystemException e) {
			e.printStackTrace();
		}

		return shoppingCart;
	}
	
	public ShoppingCart getShoppingCartByUserId(long userId) throws SystemException {
		return shoppingCartPersistence.fetchByUserId(userId);
	}
	
	public  String[] getOrderTokens(){
		return new String[] {
				"[$USER_NAME$]","[$ORDER_ID$]","[$PRODUCT_DETAILS$]","[$ITEM_PRICE$]","[$QTY$]",
				"[$SUB_TOTAL$]","[$TOTAL$]","[$MOBILE_NO$]","[$ADDRESS$]"
		};
	}
	
	public String[] getValueTokens(ShoppingOrder shoppingOrder) {
		
		DecimalFormat df = new DecimalFormat("0.00");
		String[] valueTokens = new String[9];
		
		String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
       	String orderId = HConstants.HTMSD + currentYear.substring(2, 4) + shoppingOrder.getOrderId();
		ShoppingItem shoppingItem = CommonUtil.getShoppingItem(shoppingOrder.getShoppingItemId());
		
		valueTokens[0] = shoppingOrder.getUserName();
		valueTokens[1] = orderId;
		valueTokens[2] = (Validator.isNotNull(shoppingItem)? shoppingItem.getProductCode()+ StringPool.DASH +shoppingItem.getName():StringPool.DASH);
		valueTokens[3] = df.format(Validator.isNotNull(shoppingItem)? shoppingItem.getTotalPrice() : 0);
		valueTokens[4] = String.valueOf(shoppingOrder.getQuantity());
		valueTokens[5] = df.format(shoppingOrder.getTotalPrice());
		valueTokens[6] = CommonUtil.getPriceInNumberFormat(shoppingOrder.getTotalPrice(), HConstants.RUPEE_SYMBOL);
		valueTokens[7] = shoppingOrder.getShippingMoble();
		valueTokens[8] = GenerateInvoice.getAddress(shoppingOrder); 
		
		return valueTokens;
	}
	
	public String getArticleId(int orderStatus) {
		
		String articleId = StringPool.BLANK;
		AssetCategory category = CommonUtil.getAssetCategoryById(orderStatus);
		if (Validator.isNull(category)) return StringPool.BLANK;
		
		if (category.getName().equals(HConstants.CANCEL_ORDER_STATUS)) {
			articleId = "ORDER_CANCELLED";
		} else if (category.getName().equals(HConstants.DELIVERED_STATUS)) {
			articleId = "ORDER_DELIVERED";
		} else if (category.getName().equals(HConstants.SHIPPING_STATUS)) {
			articleId = "ORDER_SHIPPED";
		} else if (category.getName().equals(HConstants.ORDER_REVIEW_STATUS)) {
			articleId = StringPool.BLANK;
		}
		return articleId;
	}
}