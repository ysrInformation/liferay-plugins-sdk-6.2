package com.htmsd.slayer.service.persistence;

import java.util.List;

import com.htmsd.slayer.model.Category;
import com.htmsd.slayer.model.impl.CategoryImpl;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

public class CategoryFinderImpl extends BasePersistenceImpl<Category> implements
		CategoryFinder {

	public static String FIND_CATEGORY_BY_ITEMID = CategoryFinderImpl.class
			.getName() + ".findCategoryByItemId";
	
	public static String DELETE_CATITEM_BY_ITEMID = CategoryFinderImpl.class
			.getName() + ".deleteCatItemyByItemId";
	
	public static String DELETE_CATITEM_BY_CATEGORYID = CategoryFinderImpl.class
			.getName() + ".deleteCatItemyBycategoryId";

	@SuppressWarnings("unchecked")
	public List<Category> getCategoryByItemId(long itemId) {

		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(FIND_CATEGORY_BY_ITEMID);

		System.out.println("The Query is >>>>>>>>>" + sql);

		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("Category", CategoryImpl.class);
	
		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(itemId);

		return (List<Category>) query.list();
	}
	
	public void deleteCatItemByItemId(long itemId) {
		
		
		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(DELETE_CATITEM_BY_ITEMID);

		System.out.println("The Query is >>>>>>>>>" + sql);

		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(itemId);
		
		query.executeUpdate();
	}
	
	public void deleteCatItemByCategoryId(long categoryId) {
		
		
		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(DELETE_CATITEM_BY_CATEGORYID);

		System.out.println("The Query is >>>>>>>>>" + sql);

		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(categoryId);
		
		query.executeUpdate();
	}
}
