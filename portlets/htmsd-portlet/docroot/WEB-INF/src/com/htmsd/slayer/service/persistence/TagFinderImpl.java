package com.htmsd.slayer.service.persistence;

import java.util.List;

import com.htmsd.slayer.model.Tag;
import com.htmsd.slayer.model.impl.TagImpl;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

public class TagFinderImpl extends BasePersistenceImpl<Tag> implements
		TagFinder {

	public static String FIND_TAG_BY_ITEMID = TagFinderImpl.class.getName()
			+ ".findtagByItemId";
	
	public static String DELETE_TAGITEM_BY_ITEMID = TagFinderImpl.class.getName()
			+ ".deleteTagItemByItemId";
	
	public static String DELETE_TAGITEM_TAG_ID = TagFinderImpl.class.getName()
			+ ".deleteTagItemByTagId";

	@SuppressWarnings("unchecked")
	public List<Tag> getTagByItemId(long itemId) {

		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(FIND_TAG_BY_ITEMID);

		System.out.println("The Query is >>>>>>>>>" + sql);

		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity("Tag", TagImpl.class);
		
		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(itemId);

		return (List<Tag>) query.list();
	}
	
	public void deleteTagItemByItemId(long itemId) {
		
		
		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(DELETE_TAGITEM_BY_ITEMID);

		System.out.println("The Query is >>>>>>>>>" + sql);

		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(itemId);
		
		query.executeUpdate();
	}
	
	public void deleteTagItemByTagId(long tagId) {
		
		
		Session session = openSession();

		// 2. Get SQL statement from XML file with its name
		String sql = CustomSQLUtil.get(DELETE_TAGITEM_TAG_ID);

		System.out.println("The Query is >>>>>>>>>" + sql);

		// 3. Transform the normal query to HQL query
		SQLQuery query = session.createSQLQuery(sql);
		QueryPos queryPos = QueryPos.getInstance(query);
		queryPos.add(tagId);
		
		query.executeUpdate();
	}
}
