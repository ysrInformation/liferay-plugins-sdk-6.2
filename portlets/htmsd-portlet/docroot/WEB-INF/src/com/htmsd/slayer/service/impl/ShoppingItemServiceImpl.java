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
import java.util.ArrayList;
import java.util.List;

import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.service.base.ShoppingItemServiceBaseImpl;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ac.AccessControlled;

/**
 * The implementation of the shopping item remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.ShoppingItemService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have security checks based on the propagated JAAS credentials because this service can be accessed remotely.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.ShoppingItemServiceBaseImpl
 * @see com.htmsd.slayer.service.ShoppingItemServiceUtil
 */
public class ShoppingItemServiceImpl extends ShoppingItemServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.ShoppingItemServiceUtil} to access the shopping item remote service.
	 */
	
	/**
	 *@param groupId
	 *@param status
	 *@param start
	 *@param end
	 *@return  
	 */
	@AccessControlled(guestAccessEnabled=true)
	public JSONArray getShoppingItems(long categoryId, long groupId, int status, int start, int end) {
		
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
		List<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();
		try {
			shoppingItems = shoppingItemLocalService.getCategoryShoppingItems(categoryId, start, end);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		
		for (ShoppingItem shoppingItem : shoppingItems) {

			if(shoppingItem.getStatus() != HConstants.APPROVE) continue;
			
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
			jsonObject.put(HConstants.ITEM_ID, shoppingItem.getItemId());
			jsonObject.put(HConstants.NAME, shoppingItem.getName());
			jsonObject.put(HConstants.DESCRIPTION, shoppingItem.getDescription());
			jsonObject.put(HConstants.TOTAL_PRICE, decimalFormat.format(shoppingItem.getSellerPrice() + shoppingItem.getTotalPrice()));
			
			String imageIds = shoppingItem.getImageIds();
			long imageId = 0l;
			
			if(Validator.isNotNull(imageIds)) {
				imageId = (imageIds.split(",").length > 0) ? Long.valueOf(imageIds.split(",")[0]) : 0;
			}
			
			jsonObject.put(HConstants.IMAGE, CommonUtil.getThumbnailpath(imageId, groupId, true));
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
}