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

import java.util.List;

import com.htmsd.slayer.model.UserInfo;
import com.htmsd.slayer.service.base.UserInfoLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;

/**
 * The implementation of the user info local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.UserInfoLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.UserInfoLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.UserInfoLocalServiceUtil
 */
public class UserInfoLocalServiceImpl extends UserInfoLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.UserInfoLocalServiceUtil} to access the user info local service.
	 */
	
	public UserInfo addUserInfo(long userId, long groupId, long companyId, long shippingAddressId, long billingAddressId, 
			String mobileNumber, String altNumber) throws SystemException {
		
		long userInfoId = 0l;
		boolean newentry = false;
		UserInfo userInfo = findUserInfoByUserId(userId);
		if (Validator.isNotNull(userInfo)) {
			userInfoId = userInfo.getUserInfoId();
			userInfo = userInfoLocalService.fetchUserInfo(userInfoId); 
		} else {
			newentry = true;
			userInfoId = counterLocalService.increment();
			userInfo = userInfoLocalService.createUserInfo(userInfoId); 
		}
		
		userInfo.setUserId(userId);
		userInfo.setGroupId(groupId);
		userInfo.setCompanyId(companyId); 
		userInfo.setShippingAddressId(shippingAddressId);
		userInfo.setBillingAddressId(billingAddressId); 
		userInfo.setMobileNumber(mobileNumber); 
		userInfo.setAltNumber(altNumber); 
		
		if (newentry) {
			userInfo = userInfoLocalService.addUserInfo(userInfo);
		} else {
			userInfo = userInfoLocalService.updateUserInfo(userInfo);
		}
		
		return userInfo;
	}
	
	public List<UserInfo> getUserInfoByUserId(long userId) throws SystemException {
		return userInfoPersistence.findByUserId(userId); 
	}
	
	public UserInfo findUserInfoByUserId(long userId) {
		UserInfo userInfo = null;
		try {
		  userInfo = userInfoPersistence.fetchBySUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return userInfo;
	}
}