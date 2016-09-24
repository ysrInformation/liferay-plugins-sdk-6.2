package com.htmsd.assetrendere;

import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.BaseAssetRendererFactory;

public class ShoppingItemAssetRendereFactory extends BaseAssetRendererFactory {

	public static final String TYPE = "ShoppingItem";
	public static final String CLASS_NAME = ShoppingItem.class.getName();

	@Override
	public AssetRenderer getAssetRenderer(long classPK, int type) throws PortalException, SystemException {
		ShoppingItem shoppingItem = ShoppingItemLocalServiceUtil.fetchShoppingItem(classPK);
		return new ShoppingItemAssetRendere(shoppingItem);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public String getType() {
		return TYPE;
	}
}