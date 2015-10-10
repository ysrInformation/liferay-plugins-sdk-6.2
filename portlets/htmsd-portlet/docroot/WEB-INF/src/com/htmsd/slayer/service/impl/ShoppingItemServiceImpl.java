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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
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
	
	private static final Log _log = LogFactoryUtil.getLog(ShoppingItemServiceImpl.class);
	
	/**
	 *@param groupId
	 *@param status
	 *@param start
	 *@param end
	 *@return  
	 */
	@AccessControlled(guestAccessEnabled=true)
	public JSONArray getShoppingItems(long categoryId, long groupId, int status, int start, int end, String sortBy) {
		
		_log.info("Getting Shopping Item List Start:"+start+" End:"+end+" SortBy:"+sortBy);
		
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
		List<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();
		if (categoryId > 0) {
			shoppingItems = shoppingItemLocalService.getItemByCategoryId(sortBy, categoryId, start, end);
		} else {
			shoppingItems = shoppingItemLocalService.getItemByOrder(sortBy, start, end);
		}
		getItemJSONArray(groupId, jsonArray, shoppingItems);
		return jsonArray;
	}
	
	/**
	 * 
	 * @param tagName
	 * @param sortBy
	 * @param groupId
	 * @param start
	 * @param end
	 * @return
	 */
	@AccessControlled(guestAccessEnabled=true)
	public JSONArray getShoppingItemsBtTagName(String tagName, String sortBy, long groupId, int start, int end) {
		_log.info("Getting Shopping Item List Start:"+start+" End:"+end+" SortBy:"+sortBy);
		
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
		List<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();
		shoppingItems = shoppingItemLocalService.getItemByTagName(tagName, sortBy, start, end);
		getItemJSONArray(groupId, jsonArray, shoppingItems);
		return jsonArray;
	}

	private void getItemJSONArray(long groupId, JSONArray jsonArray,
			List<ShoppingItem> shoppingItems) {
		for (ShoppingItem shoppingItem : shoppingItems) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
			jsonObject.put(HConstants.ITEM_ID, shoppingItem.getItemId());
			jsonObject.put(HConstants.NAME, shoppingItem.getName());
			jsonObject.put(HConstants.DESCRIPTION, StringUtil.shorten(shoppingItem.getDescription(), 50, ". . ."));
			jsonObject.put(HConstants.TOTAL_PRICE, shoppingItem.getTotalPrice());
			long imageId = shoppingItem.getSmallImage();
			jsonObject.put(HConstants.IMAGE, CommonUtil.getThumbnailpath(imageId, groupId, false));
			jsonArray.put(jsonObject);
		}
	}
}