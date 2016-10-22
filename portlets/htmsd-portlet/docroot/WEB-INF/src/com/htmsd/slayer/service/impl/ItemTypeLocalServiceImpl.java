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

import com.htmsd.slayer.model.ItemType;
import com.htmsd.slayer.service.base.ItemTypeLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the item type local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.ItemTypeLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.ItemTypeLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.ItemTypeLocalServiceUtil
 */
public class ItemTypeLocalServiceImpl extends ItemTypeLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.ItemTypeLocalServiceUtil} to access the item type local service.
	 */
	
	public ItemType update(long itemTypeId, String name, boolean documentRequired) {
		ItemType itemType = null;
		
		if (itemTypeId > 0) {
			try {
				itemType = itemTypeLocalService.fetchItemType(itemTypeId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		} else {
			try {
				itemType = itemTypeLocalService.createItemType(counterLocalService.increment());
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		
		itemType.setName(name);
		itemType.setDocumentRequired(documentRequired);
		
		if (itemTypeId > 0) {
			try {
				itemType = itemTypeLocalService.updateItemType(itemType);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		} else {
			try {
				itemType = itemTypeLocalService.addItemType(itemType);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		return itemType;
	}
}