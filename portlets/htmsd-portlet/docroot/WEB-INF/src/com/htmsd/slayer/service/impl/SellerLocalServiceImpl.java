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

import java.util.Calendar;
import java.util.List;

import com.htmsd.slayer.model.Seller;
import com.htmsd.slayer.service.SellerLocalServiceUtil;
import com.htmsd.slayer.service.base.SellerLocalServiceBaseImpl;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.AddressLocalServiceUtil;
import com.liferay.portal.service.ListTypeServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The implementation of the seller local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.SellerLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.SellerLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.SellerLocalServiceUtil
 */
public class SellerLocalServiceImpl extends SellerLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.SellerLocalServiceUtil} to access the seller local service.
	 */
	
	public Seller update(long sellerId, long userId, long groupId, long companyId, long contactId,
			long bankAccountNumber, long countryId, long regionId,String userName, String ifscCode, String name, String TIN, String CST,
			String street1, String street2, String street3, String city, String zip, String bankName, ServiceContext serviceContext) {
		Seller seller = null;
		
		if (sellerId > 0) {
			try {
				seller = SellerLocalServiceUtil.fetchSeller(sellerId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		} else {
			try {
				seller = SellerLocalServiceUtil.createSeller(CounterLocalServiceUtil.increment());
				seller.setCreateDate(Calendar.getInstance().getTime());
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		
		seller.setUserId(userId);
		seller.setGroupId(groupId);
		seller.setCompanyId(companyId);
		seller.setModifiedDate(Calendar.getInstance().getTime());
		seller.setName(name);
		seller.setUserName(userName);
		seller.setBankName(bankName);
		seller.setBankAccountNumber(bankAccountNumber);
		seller.setIfscCode(ifscCode);
		seller.setTIN(TIN);
		seller.setCST(CST);
		
		int typeId = 0;
		
		if (sellerId > 0) {
			try {
				sellerLocalService.updateSeller(seller);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			
			User user = null;
			try {
				user = UserLocalServiceUtil.fetchUser(userId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			
			try {
				for (Address address : user.getAddresses()) {
					if (address.isPrimary()) {
						address.setStreet1(street1);
						address.setStreet1(street2);
						address.setStreet1(street3);
						address.setCity(city);
						address.setZip(zip);
						address.setRegionId(regionId);
						address.setCountryId(countryId);
						try {
							AddressLocalServiceUtil.updateAddress(address);
						} catch (SystemException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			} catch (SystemException e) {
				e.printStackTrace();
			}
		} else {
			try {
				sellerLocalService.addSeller(seller);
			} catch (SystemException e1) {
				e1.printStackTrace();
			}
			
			try {
				typeId = ListTypeServiceUtil.getListTypes(Contact.class.getName() + ListTypeConstants.ADDRESS)
						.get(TIN.isEmpty() && name.isEmpty() ? 2 : 0).getListTypeId();
			} catch (SystemException e) {
				e.printStackTrace();
			}
			
			try {
				AddressLocalServiceUtil.addAddress(userId, Contact.class.getName(), contactId, street1, street2, street3,
						city, zip, regionId, countryId, typeId, false, true, serviceContext);
			} catch (PortalException e) {
				e.printStackTrace();
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		return seller;
	}
	
	public boolean isSeller(long userId) {
		boolean isSeller = false;
		try {
			List<Seller> seller = sellerPersistence.findByUserId(userId);
			if (seller.size() > 0) isSeller = true;
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return isSeller;
	}
	
	public Seller getSellerByUserId(long userId) {
		Seller seller = null;
		try {
			List<Seller> sellers = sellerPersistence.findByUserId(userId);
			if (sellers.size() > 0) {
				seller =  sellers.get(0);
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return seller;
	}
	
	public Seller updateSeller(long sellerId, long userId, long groupId, long companyId,
			long bankAccountNumber, String userName, String ifscCode, String name, String TIN, String CST, String bankName) {
		
		Seller seller = null;
		try {
			seller = sellerLocalService.fetchSeller(sellerId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		seller.setUserId(userId);
		seller.setGroupId(groupId);
		seller.setCompanyId(companyId);
		seller.setModifiedDate(Calendar.getInstance().getTime());
		seller.setName(name);
		seller.setUserName(userName);
		seller.setBankName(bankName);
		seller.setBankAccountNumber(bankAccountNumber);
		seller.setIfscCode(ifscCode);
		seller.setTIN(TIN);
		seller.setCST(CST);
		try {
			sellerLocalService.updateSeller(seller);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return seller;
	}
}