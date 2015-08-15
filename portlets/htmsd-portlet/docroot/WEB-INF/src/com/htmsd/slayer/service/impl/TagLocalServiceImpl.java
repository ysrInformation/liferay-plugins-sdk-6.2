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
import com.htmsd.slayer.model.Tag;
import com.htmsd.slayer.service.base.TagLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * The implementation of the tag local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.TagLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.TagLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.TagLocalServiceUtil
 */
public class TagLocalServiceImpl extends TagLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.TagLocalServiceUtil} to access the tag local service.
	 */
	
	public Tag addTag(long groupId, long companyId, long userId, String name) {
		
		Tag tag = null;
		try {
			tag = tagLocalService.createTag(counterLocalService.increment(Tag.class.getName()));
			
			tag.setGroupId(groupId);
			tag.setCompanyId(companyId);
			tag.setUserId(userId);
			tag.setCreateDate(new Date());
			tag.setModifiedDate(null);
			tag.setName(name);
			
			tagLocalService.addTag(tag);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return tag;
	}
	
	public Tag updateTag(long tagId, long groupId, long companyId, long userId, String name) {
			
			Tag tag = null;
			try {
				tag = tagLocalService.fetchTag(tagId);
				
				tag.setGroupId(groupId);
				tag.setCompanyId(companyId);
				tag.setUserId(userId);
				tag.setModifiedDate(new Date());
				tag.setName(name);
				
				tagLocalService.updateTag(tag);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return tag;
		}
	
	public List<Tag> getTagByName(String name) {
		
		List<Tag> tags = null;
		try {
			tags = tagPersistence.findByName(name);
		} catch (SystemException e) {
			_log.error(e);
		}
		return tags;
	}
	
	public List<Tag> getTagByItemId(long itemId) {
		
		return tagFinder.getTagByItemId(itemId);
	}
	
	public void removeTag(long tagId) {
		
		try {
			tagFinder.deleteTagItemByTagId(tagId);
			tagLocalService.deleteTag(tagId);
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
	}

	public void removeMapping(long itemId) {
		tagFinder.deleteTagItemByItemId(itemId);
	}

	private Log _log = LogFactoryUtil.getLog(TagLocalServiceImpl.class);
}