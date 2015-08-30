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

import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.service.base.ShoppingItemLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * The implementation of the shopping item local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.ShoppingItemLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.ShoppingItemLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.ShoppingItemLocalServiceUtil
 */
public class ShoppingItemLocalServiceImpl
	extends ShoppingItemLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.ShoppingItemLocalServiceUtil} to access the shopping item local service.
	 */
	
	public ShoppingItem addItem(long groupId, long companyId, long userId,
			String userName, long updateUserId, String updateUserName,
			String productCode, String name, String description,
			Double sellerPrice, Double totalPrice, long quantity, int status,
			String imageIds, String vedioURL, String remark) {

		ShoppingItem shoppingItem = null;
		try {
			shoppingItem = createShoppingItem(counterLocalService.increment(ShoppingItem.class.getName()));
			
			shoppingItem.setGroupId(groupId);
			shoppingItem.setCompanyId(companyId);
			shoppingItem.setUserId(userId);
			shoppingItem.setUserName(userName);
			shoppingItem.setUpdateUserId(updateUserId);
			shoppingItem.setUpdateUserName(updateUserName);
			shoppingItem.setProductCode(productCode);
			shoppingItem.setName(name);
			shoppingItem.setDescription(description);
			shoppingItem.setSellerPrice(sellerPrice);
			shoppingItem.setTotalPrice(totalPrice);
			shoppingItem.setQuantity(quantity);
			shoppingItem.setStatus(status);
			shoppingItem.setImageIds(imageIds);
			shoppingItem.setVedioURL(vedioURL);
			shoppingItem.setCreateDate(new Date());
			shoppingItem.setModifiedDate(null);
			shoppingItem.setRemark(remark);
			
			addShoppingItem(shoppingItem);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return shoppingItem;
	}
	
	public ShoppingItem updateItem(long itemId, long groupId, long companyId,
			long userId, String userName, long updateUserId, String updateUserName, String productCode, String name, String description,
			Double sellerPrice, Double totalPrice, long quantity, int status, String imageIds, String vedioURL, String remark) {

		ShoppingItem shoppingItem = null;
		try {
			shoppingItem = fetchShoppingItem(itemId);
			
			shoppingItem.setGroupId(groupId);
			shoppingItem.setCompanyId(companyId);
			shoppingItem.setUserId(userId);
			shoppingItem.setUserName(userName);
			shoppingItem.setUpdateUserId(updateUserId);
			shoppingItem.setUpdateUserName(updateUserName);
			shoppingItem.setProductCode(productCode);
			shoppingItem.setName(name);
			shoppingItem.setDescription(description);
			shoppingItem.setSellerPrice(sellerPrice);
			shoppingItem.setTotalPrice(totalPrice);
			shoppingItem.setQuantity(quantity);
			shoppingItem.setStatus(status);
			shoppingItem.setImageIds(imageIds);
			shoppingItem.setVedioURL(vedioURL);
			shoppingItem.setModifiedDate(new Date());
			shoppingItem.setRemark(remark);
			
			updateShoppingItem(shoppingItem);
		} catch (SystemException e) {
			_log.error(e);
		}
		return shoppingItem;
	}
	
	public ShoppingItem updateStatus(long itemId, int status, long updateUserId, String updateUserName) {
		
		ShoppingItem shoppingItem = null;
		try {
			shoppingItem = fetchShoppingItem(itemId);
			shoppingItem = setUpdateAudit(updateUserId, shoppingItem, updateUserName);
			shoppingItem.setStatus(status);
			updateShoppingItem(shoppingItem);
		} catch (SystemException e) {
			_log.error(e);
		}
		return shoppingItem;
	}
	
	public ShoppingItem updateTitle(long itemId, String name, long updateUserId, String updateUserName) {
			
		ShoppingItem shoppingItem = null;
		try {
			shoppingItem = fetchShoppingItem(itemId);
			shoppingItem = setUpdateAudit(updateUserId, shoppingItem, updateUserName);
			shoppingItem.setName(name);
			updateShoppingItem(shoppingItem);
		} catch (SystemException e) {
			_log.error(e);
		}
		return shoppingItem;
		}
	
	public ShoppingItem updateDescription(long itemId, String description, long updateUserId, String updateUserName) {
		
		ShoppingItem shoppingItem = null;
		try {
			shoppingItem = fetchShoppingItem(itemId);
			shoppingItem = setUpdateAudit(updateUserId, shoppingItem, updateUserName);
			shoppingItem.setDescription(description);
			updateShoppingItem(shoppingItem);
		} catch (SystemException e) {
			_log.error(e);
		}
		return shoppingItem;
	}
	
	public ShoppingItem updateTotalPrice(long itemId, Double totalPrice, long updateUserId, String updateUserName) {
		
		ShoppingItem shoppingItem = null;
		try {
			shoppingItem = fetchShoppingItem(itemId);
			shoppingItem = setUpdateAudit(updateUserId, shoppingItem, updateUserName);
			shoppingItem.setTotalPrice(totalPrice);
			updateShoppingItem(shoppingItem);
		} catch (SystemException e) {
			_log.error(e);
		}
		return shoppingItem;
	}
	
	public ShoppingItem updateTag(long itemId, long tagId, long updateUserId, String updateUserName) {
			
		ShoppingItem shoppingItem = null;
		if(tagId != 0 && itemId != 0) {
			try {
				shoppingItem = fetchShoppingItem(itemId);
				shoppingItem = setUpdateAudit(updateUserId, shoppingItem, updateUserName);
				updateShoppingItem(shoppingItem);
				
				tagLocalService.removeMapping(itemId);
				addTagShoppingItem(tagId, itemId);
				
			} catch (SystemException e) {
				_log.error(e);
			}
		}
		return shoppingItem;
		}
	
	public ShoppingItem updateCategory(long itemId, long categoryId, long updateUserId, String updateUserName) {
		
		ShoppingItem shoppingItem = null;
		if(categoryId !=0 && itemId !=0 ) {
			try {
				shoppingItem = fetchShoppingItem(itemId);
				shoppingItem = setUpdateAudit(updateUserId, shoppingItem, updateUserName);
				updateShoppingItem(shoppingItem);
				
				categoryLocalService.removeMapping(itemId);
				addCategoryShoppingItem(categoryId, itemId);
				
			} catch (SystemException e) {
				_log.error(e);
			}
		}
		return shoppingItem;
	}
	
	public ShoppingItem updateImages(long itemId, String imageIds, long updateUserId, String updateUserName) {
			
		ShoppingItem shoppingItem = null;
		try {
			shoppingItem = fetchShoppingItem(itemId);
			shoppingItem = setUpdateAudit(updateUserId, shoppingItem, updateUserName);
			shoppingItem.setImageIds(imageIds);
			updateShoppingItem(shoppingItem);
		} catch (SystemException e) {
			_log.error(e);
		}
		return shoppingItem;
	}
	
	public ShoppingItem updateProductCode(long itemId, String productCode, long updateUserId, String updateUserName) {
		
		ShoppingItem shoppingItem = null;
		try {
			shoppingItem = fetchShoppingItem(itemId);
			shoppingItem = setUpdateAudit(updateUserId, shoppingItem, updateUserName);
			shoppingItem.setProductCode(productCode);
			updateShoppingItem(shoppingItem);
		} catch (SystemException e) {
			_log.error(e);
		}
		return shoppingItem;
	}
	
	public ShoppingItem updatevedioURL(long itemId, String vedioURL, long updateUserId, String updateUserName) {
			
		ShoppingItem shoppingItem = null;
		try {
			shoppingItem = fetchShoppingItem(itemId);
			shoppingItem = setUpdateAudit(updateUserId, shoppingItem, updateUserName);
			shoppingItem.setVedioURL(vedioURL);
			updateShoppingItem(shoppingItem);
		} catch (SystemException e) {
			_log.error(e);
		}
		return shoppingItem;
	}
	
	public ShoppingItem updateRemark(long itemId, String remark, long updateUserId, String updateUserName) {
		
		ShoppingItem shoppingItem = null;
		try {
			shoppingItem = fetchShoppingItem(itemId);
			shoppingItem = setUpdateAudit(updateUserId, shoppingItem, updateUserName);
			shoppingItem.setRemark(remark);
			updateShoppingItem(shoppingItem);
		} catch (SystemException e) {
			_log.error(e);
		}
		return shoppingItem;
	}
		
	public ShoppingItem setUpdateAudit(long updateUserId, ShoppingItem shoppingItem, String updateUserName) {

		shoppingItem.setUpdateUserId(updateUserId);
		shoppingItem.setUpdateUserName(updateUserName);
		shoppingItem.setModifiedDate(new Date());
			
		return shoppingItem;
	}
	
	public void deleteItem(long itemId) {
		
		try {
			deleteShoppingItem(itemId);
			categoryFinder.deleteCatItemByItemId(itemId);
			tagFinder.deleteTagItemByItemId(itemId);
		} catch (SystemException e) {
			_log.error(e);
		} catch (PortalException e) {
			_log.error(e);
		}
	}
	
	public void deleteUserItems(long userId) {
		
		List<ShoppingItem> shoppingItems = getByUserId(userId);
		
		for(ShoppingItem shoppingItem : shoppingItems) {
			deleteItem(shoppingItem.getItemId());
		}
	}
	
	public List<ShoppingItem> getByUserId(long userId) {
		
		List<ShoppingItem> shoppingItems = null;
		
		try {
			shoppingItems = shoppingItemPersistence.findByUserId(userId);
		} catch (SystemException e) {
			_log.error(e);
		}
		
		return shoppingItems;
	}
	
	public List<ShoppingItem> getByUserId(long userId, int start, int end) {
		
		List<ShoppingItem> shoppingItems = null;
		
		try {
			shoppingItems = shoppingItemPersistence.findByUserId(userId, start, end);
		} catch (SystemException e) {
			_log.error(e);
		}
		
		return shoppingItems;
	}
	
	public int getByUserIdCount(long userId) {
		
		int count = 0;
		try {
			count =  shoppingItemPersistence.countByUserId(userId);
		} catch (SystemException e) {
			_log.error(e);
		}
		return count;
	}
	
	public List<ShoppingItem> getByStatus(int status) {
		
		List<ShoppingItem> shoppingItems = null;
		
		try {
			shoppingItems = shoppingItemPersistence.findByStatus(status);
		} catch (SystemException e) {
			_log.error(e);
		}
		return shoppingItems;
	}
	
	public List<ShoppingItem> getByStatus(int status,int start, int end) {
			
		List<ShoppingItem> shoppingItems = null;
		
		try {
			shoppingItems = shoppingItemPersistence.findByStatus(status, start, end);
		} catch (SystemException e) {
			_log.error(e);
		}
		return shoppingItems;
	}

	public int getByStatusCount(int status) {
			
		int count = 0;
		try {
			count =  shoppingItemPersistence.countByStatus(status);
		} catch (SystemException e) {
			_log.error(e);
		}
		return count;
	}
	
	public List<ShoppingItem> getItemByCategoryId(String sort, long categoryId, int start, int end) {
		return shoppingItemFinder.getItemByCategoryId(sort, categoryId, start, end);
	}
	
	public List<ShoppingItem> getItemByTagId(long tagId) {
		return shoppingItemFinder.getItemByTagId(tagId);
	}
	
	public int getItemByCategoryCount(long categoryId) {
		return shoppingItemFinder.getItemByCategoryIdCount(categoryId);
	}
	
	private Log _log = LogFactoryUtil.getLog(ShoppingItemLocalServiceImpl.class);
}