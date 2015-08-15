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

import com.htmsd.slayer.model.Category;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.service.base.CategoryLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * The implementation of the category local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.CategoryLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.CategoryLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.CategoryLocalServiceUtil
 */
public class CategoryLocalServiceImpl extends CategoryLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.CategoryLocalServiceUtil} to access the category local service.
	 */
	

	public Category addCategory(long groupId, long companyId, long userId, String name, String description) {
		
		Category category = null;
		try {
			category = categoryLocalService.createCategory(counterLocalService.increment(Category.class.getName()));
			
			category.setGroupId(groupId);
			category.setCompanyId(companyId);
			category.setUserId(userId);
			category.setCreateDate(new Date());
			category.setModifiedDate(null);
			category.setName(name);
			category.setDescription(description);
			category.setParentCategoryId(0l);
			
			categoryLocalService.addCategory(category);
		} catch (SystemException e) {
			_log.error(e);
		}
		return category;
	}
	
	public Category updateTag(long tagId, long groupId, long companyId, long userId, String name) {
			
		Category category = null;
		try {
			category = categoryLocalService.createCategory(counterLocalService.increment(Category.class.getName()));
			
			category.setGroupId(groupId);
			category.setCompanyId(companyId);
			category.setUserId(userId);
			category.setModifiedDate(new Date());
			category.setName(name);
			category.setDescription(name);
			category.setParentCategoryId(0l);
			
			categoryLocalService.updateCategory(category);
		} catch (SystemException e) {
			_log.error(e);
		}
		return category;
		}
	
	public List<Category> getCategoryByItemId(long itemId) {
		
		return categoryFinder.getCategoryByItemId(itemId);
	}
	
	public void removeCategory(long categoryId) {
		
		
		try {
			categoryFinder.deleteCatItemByCategoryId(categoryId);
			categoryLocalService.deleteCategory(categoryId);
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
	}
	
	public void removeMapping(long itemId) {
		
		categoryFinder.deleteCatItemByItemId(itemId);
	}
	
	private Log _log = LogFactoryUtil.getLog(CategoryLocalServiceImpl.class);

}