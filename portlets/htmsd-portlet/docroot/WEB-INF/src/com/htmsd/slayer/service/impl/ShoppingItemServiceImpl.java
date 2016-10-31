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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.base.ShoppingItemServiceBaseImpl;
import com.htmsd.util.CommonUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
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
	public JSONArray getShoppingItems(long categoryId, long currencyId, long groupId, int status, int start, int end, String sortBy) {
		
		_log.info("Getting Shopping Item List Start:"+start+" End:"+end+" SortBy:"+sortBy);
		
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
		List<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();
		if (categoryId > 0) {
			shoppingItems = shoppingItemLocalService.getItemByCategoryId(sortBy, categoryId, start, end);
		} else {
			shoppingItems = shoppingItemLocalService.getItemByOrder(sortBy, start, end);
		}
		getItemJSONArray(groupId, jsonArray, shoppingItems, currencyId);
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
	public JSONArray getShoppingItemsBtTagName(String tagName, String sortBy, long groupId, long currencyId, int start, int end) {
		_log.info("Getting Shopping Item List Start:"+start+" End:"+end+" SortBy:"+sortBy);
		
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
		List<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();
		shoppingItems = shoppingItemLocalService.getItemByTagName(tagName, sortBy, start, end);
		getItemJSONArray(groupId, jsonArray, shoppingItems, currencyId);
		System.out.println(jsonArray);
		return jsonArray;
	}

	private void getItemJSONArray(long groupId, JSONArray jsonArray,
			List<ShoppingItem> shoppingItems, long currencyId) {
		
		double currencyRate = CommonUtil.getCurrentRate(currencyId);
		
		for (ShoppingItem shoppingItem : shoppingItems) {
			long imageId = shoppingItem.getSmallImage();
			double total = (currencyRate == 0) ? shoppingItem.getSellingPrice() :  shoppingItem.getSellingPrice() / currencyRate;
			double MRP = (currencyRate == 0) ? shoppingItem.getMRP():  shoppingItem.getMRP() / currencyRate;
			
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
			jsonObject.put(HConstants.ITEM_ID, shoppingItem.getItemId());
			jsonObject.put(HConstants.NAME, shoppingItem.getName());
			jsonObject.put(HConstants.DESCRIPTION, StringUtil.shorten(shoppingItem.getDescription(), 20));
			jsonObject.put(HConstants.TOTAL_PRICE, total);
			jsonObject.put("MRP", MRP);
			jsonObject.put(HConstants.IMAGE, CommonUtil.getThumbnailpath(imageId, groupId, false));
			
			if (DateUtil.getDaysBetween(shoppingItem.getModifiedDate(), Calendar.getInstance().getTime()) >= 2) {
				jsonObject.put(HConstants.IS_NEW_ITEM, false);
			} else {
				jsonObject.put(HConstants.IS_NEW_ITEM, true);
			}
			
			jsonArray.put(jsonObject);
		}
	}
	
	@AccessControlled(guestAccessEnabled=true)
	public JSONObject getAutocompleteItems() {
		_log.info("In getAutocompleteItems .."); 
		List<ShoppingItem> shoppingItems = null;
		try {
			shoppingItems = ShoppingItemLocalServiceUtil.getShoppingItems(-1, -1);
		} catch (SystemException e) {
			_log.error("No items found :-"+e);
		}
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
		if (Validator.isNotNull(shoppingItems)) {
			for (ShoppingItem shoppingItem: shoppingItems) {
				if ((Validator.isNotNull(shoppingItem)) && !shoppingItem.getProductCode().isEmpty()) {
					jsonArray.put(shoppingItem.getProductCode());
					jsonArray.put(shoppingItem.getName());
				}
			}
		}
		
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		jsonObject.put("autoCompleteData", jsonArray); 
		return jsonObject;
	}
	
	@AccessControlled(guestAccessEnabled=true)
	public JSONObject sendOTP(String phoneNumber) {
		String URL = HConstants.OTP_SEND_ENDPOINT;
		URL = URL.replace("${user_phone_no}", phoneNumber);
		
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		System.out.println(URL);
		jsonObject = CommonUtil.invoker(URL);
		return jsonObject;
	}
	
	@AccessControlled(guestAccessEnabled=true)
	public JSONObject verifyOTP(String sessionId, String otp) {
		String URL = HConstants.OTP_VERIFY_ENDPOINT;
		URL = URL.replace("${session_id}", sessionId);
		URL = URL.replace("${otp}", otp);
		
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		jsonObject = CommonUtil.invoker(URL);
		return jsonObject;
	}
}