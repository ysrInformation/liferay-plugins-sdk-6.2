package com.htmsd.slayer.service.persistence;

import java.util.List;

import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.impl.ShoppingItemImpl;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

public class ShoppingItemFinderImpl extends BasePersistenceImpl<ShoppingItem>
		implements ShoppingItemFinder {

	public static String FIND_ITEM_BY_TAGID = ShoppingItemFinderImpl.class
			.getName() + ".findItemByTagId";

	public static String FIND_ITEM_BY_CATEGORYID = ShoppingItemFinderImpl.class
			.getName() + ".findItemByCategoryId";

	@SuppressWarnings("unchecked")
	public List<ShoppingItem> getItemByTagId(long tagId) {

		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(FIND_ITEM_BY_TAGID);

		System.out.println("The Query is >>>>>>>>>" + sql);

		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("ShoppingItem", ShoppingItemImpl.class);
		
		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(tagId);
		return (List<ShoppingItem>) query.list();
	}

	@SuppressWarnings("unchecked")
	public List<ShoppingItem> getItemByCategoryId(long tagId) {

		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(FIND_ITEM_BY_CATEGORYID);

		System.out.println("The Query is >>>>>>>>>" + sql);

		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("ShoppingItem", ShoppingItemImpl.class);

		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(tagId);

		return (List<ShoppingItem>) query.list();
	}
}
