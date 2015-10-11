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

import java.util.Date;
import java.util.List;

import com.htmsd.slayer.model.ItemHistory;
import com.htmsd.slayer.service.base.ItemHistoryLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the item history local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.ItemHistoryLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.ItemHistoryLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.ItemHistoryLocalServiceUtil
 */
public class ItemHistoryLocalServiceImpl extends ItemHistoryLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.ItemHistoryLocalServiceUtil} to access the item history local service.
	 */
	
	
	public void  addItemHistory(long itemId, long userId, String userName, String userEmail, int action, String remark) {
		
		ItemHistory itemHistory = null;
		try {
			itemHistory = createItemHistory(counterLocalService
					.increment(ItemHistory.class.getName()));
		} catch (SystemException e) {
			e.printStackTrace();
		}
		itemHistory.setItemId(itemId);
		itemHistory.setCreateDate(new Date());
		itemHistory.setUserId(userId);
		itemHistory.setUserName(userName);
		itemHistory.setUserEmail(userEmail);
		itemHistory.setAction(action);
		itemHistory.setRemark(remark);

		 try {
			addItemHistory(itemHistory);
		} catch (SystemException e) {
			e.printStackTrace();
		}

	}
	
	public List<ItemHistory> getItemHistoryByItemId(long itemId, int start, int end) {
		
		List<ItemHistory> itemHistories = null;
		try {
			 itemHistories = itemHistoryPersistence.findByItemId(itemId, start, end);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return itemHistories;
	}
	
	public int  getItemIdCount(long itemId) {
		
		int count = 0;
		try {
			count =  itemHistoryPersistence.countByItemId(itemId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public List<ItemHistory> getItemHistoryByUserId(long userId, int start, int end) {
			
			List<ItemHistory> itemHistories = null;
			try {
				 itemHistories = itemHistoryPersistence.findByUserId(userId, start, end);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return itemHistories;
	}
	
	public int  getUserIdCount(long userId) {
			
			int count = 0;
			try {
				count =  itemHistoryPersistence.countByUserId(userId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return count;
	}
	
	public List<ItemHistory> getItemHistoryByAction(int action, int start, int end) {
		
		List<ItemHistory> itemHistories = null;
		try {
			 itemHistories = itemHistoryPersistence.findByAction(action, start, end);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return itemHistories;
	}
	
	public int  getActionCount(int action) {
		
		int count = 0;
		try {
			count =  itemHistoryPersistence.countByAction(action);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return count;
	}
	 
}