package com.htmsd.slayer.service.persistence;

import java.util.List;

import com.htmsd.slayer.model.Category;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.impl.CategoryImpl;
import com.htmsd.slayer.model.impl.ShoppingItemImpl;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

public class ShoppingItemFinderImpl extends BasePersistenceImpl<ShoppingItem>
		implements ShoppingItemFinder {

	private final static Log _log = LogFactoryUtil.getLog(ShoppingItemFinderImpl.class);
	
	public static String FIND_ITEM_BY_TAGID = ShoppingItemFinderImpl.class
			.getName() + ".findItemByTagId";

	public static String FIND_ITEM_BY_CATEGORYID = ShoppingItemFinderImpl.class
			.getName() + ".findItemByCategoryId";
	
	public static String FIND_ITEM_BY_ORDER = ShoppingItemFinderImpl.class
			.getName() + ".getItemByOrder";

	public static String FIND_ITEM_BY_TAG_NAME = ShoppingItemFinderImpl.class
			.getName() + ".findItemByTagName";
	
	public static String SHOPPING_ITEM_CATEGORY = ShoppingItemFinderImpl.class
			.getName() + ".getShoppingItemCategory";
	
	
	@SuppressWarnings("unchecked")
	public List<ShoppingItem> getItemByTagId(long tagId) {

		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(FIND_ITEM_BY_TAGID);

		_log.info("The Query is >>>>>>>>>" + sql);

		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("ShoppingItem", ShoppingItemImpl.class);

		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(tagId);
		return (List<ShoppingItem>) query.list();
	}

	@SuppressWarnings("unchecked")
	public List<ShoppingItem> getItemByCategoryId(String sort, long categoryId, int start, int end) {

		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(FIND_ITEM_BY_CATEGORYID);
		sql = sql.replace("[$sort]", "ORDER BY "+sort);
		
		_log.info("The Query is >>>>>>>>>" + sql);
		
		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("ShoppingItem", ShoppingItemImpl.class);

		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(categoryId);
		return (List<ShoppingItem>) QueryUtil.list(query, getDialect(), start, end);
	}
	
	@SuppressWarnings("unchecked")
	public List<ShoppingItem> getItemByOrder(String sort, int start, int end) {

		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(FIND_ITEM_BY_ORDER);
		sql = sql.replace("[$sort]", "ORDER BY "+sort);
		
		_log.info("The Query is >>>>>>>>>" + sql);
		
		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("ShoppingItem", ShoppingItemImpl.class);

		return (List<ShoppingItem>) QueryUtil.list(query, getDialect(), start, end);
	}
	
	public int getItemByCategoryIdCount(long categoryId) {

		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(FIND_ITEM_BY_CATEGORYID);
		sql = sql.replace("[$sort]", "");
		
		_log.info("The Query is >>>>>>>>>" + sql);

		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("ShoppingItem", ShoppingItemImpl.class);

		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(categoryId);
		List<ShoppingItem> shoppingItems = (List<ShoppingItem>) query.list();
		return shoppingItems.size();
	}
	
	public int getItemCount() {

		Session session = openSession();

		String sql = CustomSQLUtil.get(FIND_ITEM_BY_ORDER);
		sql = sql.replace("[$sort]", "");
		_log.info("The Query is >>>>>>>>>" + sql);

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("ShoppingItem", ShoppingItemImpl.class);

		List<ShoppingItem> shoppingItems = (List<ShoppingItem>) query.list();
		return shoppingItems.size();
	}
	
	public List<ShoppingItem> getItemsByTagName(String tagName, String sort, int start, int end) {
		Session session = openSession();
		
		
		System.out.println(tagName+ "  "+ start + " " + end);
		
		String sql = CustomSQLUtil.get(FIND_ITEM_BY_TAG_NAME);
		sql = sql.replace("[$sort]", "ORDER BY "+sort);

		_log.info("The Query is >>>>>>>>>" + sql);
		
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("ShoppingItem", ShoppingItemImpl.class);
		
		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(StringPool.PERCENT + tagName+ StringPool.PERCENT);
		queryPos.add(StringPool.PERCENT + tagName+ StringPool.PERCENT);
		
		return (List<ShoppingItem>) QueryUtil.list(query, getDialect(), start, end);
	}
	
	public int getItemsByTagNameCount(String tagName) {
		Session session = openSession();
		
		String sql = CustomSQLUtil.get(FIND_ITEM_BY_TAG_NAME);
		sql = sql.replace("[$sort]", "");
		
		_log.info("The Query is >>>>>>>>>" + sql);
		
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("ShoppingItem", ShoppingItemImpl.class);
		
		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(StringPool.PERCENT + tagName+ StringPool.PERCENT );
		queryPos.add(StringPool.PERCENT + tagName+ StringPool.PERCENT);
		
		return query.list().size();
	}
	
	public Category getShoppingItemCategory(long itemId) {
		Session session = openSession();
		String sql = CustomSQLUtil.get(SHOPPING_ITEM_CATEGORY);
		
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("Category", CategoryImpl.class);
		
		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(itemId);
		
		Category category = (query.list().size() > 0) ? (Category) query.list().get(0) : new CategoryImpl();
		
		return category;
	}
}