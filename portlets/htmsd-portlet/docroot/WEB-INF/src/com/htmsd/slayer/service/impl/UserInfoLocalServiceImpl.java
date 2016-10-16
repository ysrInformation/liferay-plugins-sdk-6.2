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

import com.htmsd.slayer.model.UserInfo;
import com.htmsd.slayer.model.impl.UserInfoImpl;
import com.htmsd.slayer.service.base.UserInfoLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

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
	
	public UserInfo addUserInfo(long userInfoId, long userId, long groupId, long companyId, long shippingAddressId, long billingAddressId, 
			String mobileNumber, String altNumber, String firstName, String lastName, String email, boolean isDeliveryAddress)  {
		
		UserInfo userInfo = new UserInfoImpl();
		boolean newentry = false;
		if (userInfoId == 0) {
			newentry = true;
			try {
				userInfoId = counterLocalService.increment();
			} catch (SystemException e) {
				e.printStackTrace();
			}
			userInfo = userInfoLocalService.createUserInfo(userInfoId); 
		} else {
			try {
				userInfo = userInfoLocalService.fetchUserInfo(userInfoId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		
		userInfo.setUserId(userId);
		userInfo.setGroupId(groupId);
		userInfo.setCompanyId(companyId); 
		userInfo.setShippingAddressId(shippingAddressId);
		userInfo.setBillingAddressId(billingAddressId); 
		userInfo.setMobileNumber(mobileNumber); 
		userInfo.setAltNumber(altNumber); 
		userInfo.setFirstName(firstName);
		userInfo.setLastName(lastName);
		userInfo.setEmail(email);
		userInfo.setIsDeliveryAddress(isDeliveryAddress); 
		
		if (newentry) {
			try {
				userInfo = userInfoLocalService.addUserInfo(userInfo);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		} else {
			try {
				userInfo = userInfoLocalService.updateUserInfo(userInfo);
			} catch (SystemException e) {
				e.printStackTrace();
			}
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
	
	public List<UserInfo> checkCreateUserInfo(long userId, long companyId, long groupId) {
		User user = null;
		List<Address> addresses = null;
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		try {
			user = UserLocalServiceUtil.fetchUser(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(user)) {
			try {
				addresses = user.getAddresses();
			} catch (SystemException e) {
				e.printStackTrace();
			}
			if (Validator.isNotNull(addresses) && addresses.size() > 0) {
				for (Address address: addresses) {
					UserInfo _userInfo = addUserInfo(0l, userId, groupId, companyId, address.getAddressId(), 0l, 
							StringPool.BLANK, StringPool.BLANK, user.getFirstName(), user.getLastName(), StringPool.BLANK, false);
					if (Validator.isNotNull(_userInfo)) {
						userInfoList.add(_userInfo);
					}
				}
			}
		}
		
		return userInfoList;
	}
	
	public List<UserInfo> getUserAddressFromUserInfo(long userId, long groupId, long companyId) {
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		try {
			userInfoList = getUserInfoByUserId(userId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNull(userInfoList)) {
			userInfoList = checkCreateUserInfo(userId, companyId, groupId);
		}
		
		return userInfoList;
	}
	
	public UserInfo getUserInfoByUserIdAndIsDelivery(long userId, boolean isDeliveryAddress) {
		UserInfo userInfo = null;
		try {
			return userInfoPersistence.fetchBySUserId_IsDelieryAddress(userId, isDeliveryAddress);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return userInfo;
	}
}