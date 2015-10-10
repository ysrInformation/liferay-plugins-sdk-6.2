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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.model.impl.ShoppingOrderImpl;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.slayer.service.base.ShoppingOrderLocalServiceBaseImpl;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Junction;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * The implementation of the shopping order local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.htmsd.slayer.service.ShoppingOrderLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author YSR
 * @see com.htmsd.slayer.service.base.ShoppingOrderLocalServiceBaseImpl
 * @see com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil
 */
public class ShoppingOrderLocalServiceImpl
	extends ShoppingOrderLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil} to access the shopping order local service.
	 */
	public ShoppingOrder insertShoppingOrder(int orderStatus,long userId, long companyId, long groupId,
			String shippingFirstName, String shippingLastName, String shippingStreet, String shippingCity,
			String shippingZip, String shippingEmailAddress, String shippingState, String shippingCountry,
			String shippingMoble, String shippingAltMoble){
		
		ShoppingOrder shoppingOrder = new ShoppingOrderImpl();
		long orderId = 0L;
		
		try {
			orderId = counterLocalService.increment();
		} catch(Exception e){
			e.printStackTrace();
		}
		shoppingOrder = shoppingOrderLocalService.createShoppingOrder(orderId);
		
		shoppingOrder.setUserId(userId);
		shoppingOrder.setCompanyId(companyId);
		shoppingOrder.setGroupId(groupId);
		shoppingOrder.setCreateDate(new Date());
		shoppingOrder.setOrderStatus(orderStatus);
		shoppingOrder.setShippingFirstName(shippingFirstName);
		shoppingOrder.setShippingLastName(shippingLastName);
		shoppingOrder.setShippingStreet(shippingStreet);
		shoppingOrder.setShippingCity(shippingCity);
		shoppingOrder.setShippingZip(shippingZip);
		shoppingOrder.setShippingEmailAddress(shippingEmailAddress);
		shoppingOrder.setShippingState(shippingState);
		shoppingOrder.setShippingCountry(shippingCountry);
		shoppingOrder.setShippingMoble(shippingMoble);
		shoppingOrder.setShippingAltMoble(shippingAltMoble);
		
		try {
			shoppingOrder = shoppingOrderLocalService.addShoppingOrder(shoppingOrder);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return shoppingOrder;
	}
	
	/**
	 * Method for updating order status
	 * @param orderStatus
	 * @param orderId
	 */
	public void updateShoppingOrder(int orderStatus, long orderId) {
		
		ShoppingOrder shoppingOrder = null;
		try {
			shoppingOrder = shoppingOrderLocalService.fetchShoppingOrder(orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (Validator.isNull(shoppingOrder)) return;
		
		shoppingOrder.setOrderStatus(orderStatus);
		shoppingOrder.setModifiedDate(new Date());
		
		try {
			shoppingOrderLocalService.updateShoppingOrder(shoppingOrder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<ShoppingOrder> getShoppingOrderByUserId(long userId){
		List<ShoppingOrder> shoppingOrders = null;
		try {
			shoppingOrders = shoppingOrderPersistence.findByUserId(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shoppingOrders;
	}
	
	public List<ShoppingOrder> getShoppingOrderByTabNames(int start, int end, String tabName) {
		
		List<ShoppingOrder> shoppingOrders = Collections.emptyList();
		boolean isPendingOrderTab = tabName.equalsIgnoreCase("Pending");
		boolean isShippingOrderTab = tabName.equalsIgnoreCase("Shipping");
		boolean isOrderCancelledTab = tabName.equalsIgnoreCase("Order Cancelled");
		
		if (isPendingOrderTab) {
			shoppingOrders = getShoppingOrders(HConstants.PENDING, start, end);
		} else if (isShippingOrderTab) {
			shoppingOrders = getShoppingOrders(HConstants.SHIPPING, start, end);
		} else if(isOrderCancelledTab) { 
			shoppingOrders = getShoppingOrders(HConstants.CANCEL_ORDER, start, end);
		} else {
			shoppingOrders = getShoppingOrders(HConstants.DELIVERED, start, end);
		}
		
		return shoppingOrders;
	}
	
	public List<ShoppingOrder> getShoppingOrders(int orderStatus, int start, int end) {
		
		List<ShoppingOrder> pendingOrderList = new ArrayList<ShoppingOrder>();

		try {
			pendingOrderList = shoppingOrderPersistence.findByOrderStatus(orderStatus, start, end);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return pendingOrderList;
	}
	
	public int getCountByOrderStatus(int orderStatus) {
		
		int count = 0;
		try {
			shoppingOrderPersistence.countByOrderStatus(orderStatus);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public  List<ShoppingOrder> searchItems(int orderStatus, String keyword, int start, int end) {
		
		System.out.println("Searched Method  :"+orderStatus); 
		String likeKeyword = StringUtil.quote(keyword, StringPool.PERCENT);
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingOrder.class);
		
		Junction junctionOR = RestrictionsFactoryUtil.disjunction();
		
		Property property = PropertyFactoryUtil.forName("shippingFirstName");
		junctionOR.add(property.like(likeKeyword));
		
		property = PropertyFactoryUtil.forName("shippingLastName");
		junctionOR.add(property.like(likeKeyword));
		
		dynamicQuery.add(junctionOR);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("orderStatus", orderStatus));
		
		List<ShoppingOrder> searchList = new ArrayList<ShoppingOrder>();
		try {
			searchList =  ShoppingOrderLocalServiceUtil.dynamicQuery(dynamicQuery, start, end);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return searchList;
	}
	
	public int searchCount(int orderStatus, String keyword) {
		
		String likeKeyword = StringUtil.quote(keyword, StringPool.PERCENT);
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingOrder.class);
		
		Junction junctionOR = RestrictionsFactoryUtil.disjunction();
		
		Property property = PropertyFactoryUtil.forName("shippingFirstName");
		junctionOR.add(property.like(likeKeyword));
		
		property = PropertyFactoryUtil.forName("shippingLastName");
		junctionOR.add(property.like(likeKeyword));
		
		dynamicQuery.add(junctionOR);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("orderStatus", orderStatus));
		
		int searchCount = 0;
		try {
			searchCount = (int) ShoppingOrderLocalServiceUtil.dynamicQueryCount(dynamicQuery);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		return searchCount;
	}
	
	public int getOrderStatusByTabName(String tabName) {
		
		int orderStatus = 0;
		if (Validator.isNotNull(tabName)) {
			if (tabName.equalsIgnoreCase("Pending")) {
				orderStatus = HConstants.PENDING;
			} else if (tabName.equalsIgnoreCase("Shipping")) {
				orderStatus = HConstants.SHIPPING;
			} else if (tabName.equalsIgnoreCase("Order Cancelled")) {
				orderStatus = HConstants.CANCEL_ORDER;
			} else {
				orderStatus = HConstants.DELIVERED;
			}
		}
		return orderStatus;
	}
}