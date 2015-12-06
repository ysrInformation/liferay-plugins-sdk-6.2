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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.htmsd.slayer.NoSuchShoppingCartException;
import com.htmsd.slayer.model.ShoppingCart;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingItem_Cart;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.ShoppingOrderItem;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.base.ShoppingCartServiceBaseImpl;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ac.AccessControlled;

/**
 * The implementation of the shopping cart remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.ShoppingCartService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have security checks based on the propagated JAAS credentials because this service can be accessed remotely.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.ShoppingCartServiceBaseImpl
 * @see com.htmsd.slayer.service.ShoppingCartServiceUtil
 */
public class ShoppingCartServiceImpl extends ShoppingCartServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.ShoppingCartServiceUtil} to access the shopping cart remote service.
	 */
	
	private static final Log _log = LogFactoryUtil.getLog(ShoppingCartServiceImpl.class);
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	@AccessControlled(guestAccessEnabled=true)
	public int getShoppingCartItemCount(long userId) {
		int count = 0;
		ShoppingCart shoppingCart = null;
		try {
			shoppingCart = shoppingCartPersistence.findByUserId(userId);
		} catch (NoSuchShoppingCartException e) {
			_log.error("NoSuchShoppingCartException");
		} catch (SystemException e) {
			_log.error(e);
		}
		
		if(Validator.isNull(shoppingCart)) return 0;
		
		List<ShoppingItem_Cart> shoppingItem_Carts = new ArrayList<ShoppingItem_Cart>();
		try {
			shoppingItem_Carts = shoppingItem_CartPersistence.findByCartId(shoppingCart.getCartId());
		} catch (SystemException e) {
			_log.error(e);
		}
		
		count = shoppingItem_Carts.size();
		
		return count;
	}
	
	@AccessControlled(guestAccessEnabled=true)
	public JSONArray getUserOrderItems(long userId, long groupId, long currencyId, int start, int end) {
		_log.info("Getting Shopping Item List Start:"+start+" End:"+end);
		
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
		List<ShoppingOrder> shoppingOrderItems = new ArrayList<ShoppingOrder>();
		shoppingOrderItems = shoppingOrderLocalService.getShoppingOrderByUserId(start, end, userId);
		getItemJSONArray(jsonArray, shoppingOrderItems, currencyId, groupId);
		_log.info(jsonArray);
		return jsonArray;
	}

	private void getItemJSONArray(JSONArray jsonArray,
			List<ShoppingOrder> orderItems, long currencyId, long groupId) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		for (ShoppingOrder orderItem : orderItems) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
			ShoppingItem shoppingItem = null;
			try {
				shoppingItem = ShoppingItemLocalServiceUtil.fetchShoppingItem(orderItem.getShoppingItemId());
			} catch (SystemException e) {
				e.printStackTrace();
			}
			if (Validator.isNull(shoppingItem)) continue;
			
			double currencyRate = CommonUtil.getCurrentRate(currencyId);
			double total = (currencyRate == 0) ? orderItem.getTotalPrice() :  orderItem.getTotalPrice() / currencyRate; 
			long imageId = shoppingItem.getSmallImage();
			
			jsonObject.put(HConstants.ITEM_ID, shoppingItem.getItemId());
			jsonObject.put(HConstants.NAME, shoppingItem.getName());
			jsonObject.put(HConstants.TOTAL_PRICE, total);
			jsonObject.put("orderId", orderItem.getOrderId());
			jsonObject.put("quantity", orderItem.getQuantity());
			jsonObject.put("orderDate", sdf.format(orderItem.getCreateDate()));
			jsonObject.put("orderStatus", orderItem.getOrderStatus());
			jsonObject.put("status", CommonUtil.getOrderStatus(orderItem.getOrderStatus()));
			jsonObject.put(HConstants.IMAGE, CommonUtil.getThumbnailpath(imageId, groupId, false));
			jsonArray.put(jsonObject);
		}
	}
	
}