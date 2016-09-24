package com.htmsd.assetrendere;

import java.util.Locale;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.htmsd.slayer.model.ShoppingItem;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.BaseAssetRenderer;

public class ShoppingItemAssetRendere extends BaseAssetRenderer {

	public static final String CLASS_NAME = ShoppingItem.class.getName();
	private ShoppingItem shoppingItem;

	public ShoppingItemAssetRendere(ShoppingItem shoppingItem) {
		this.shoppingItem = shoppingItem;
	}
	
	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public long getClassPK() {
		return shoppingItem.getItemId();
	}

	@Override
	public long getGroupId() {
		return shoppingItem.getGroupId();
	}

	@Override
	public String getSummary(Locale arg0) {
		return shoppingItem.getName();
	}

	@Override
	public String getTitle(Locale arg0) {
		return shoppingItem.getName();
	}

	@Override
	public long getUserId() {
		return shoppingItem.getUserId();
	}

	@Override
	public String getUserName() {
		return shoppingItem.getUserName();
	}

	@Override
	public String getUuid() {
		return ((AssetRenderer) shoppingItem).getUuid();
	}

	@Override
	public String render(RenderRequest renderRequest, RenderResponse renderResponse, String template) throws Exception {
    	renderRequest.setAttribute("itemId",shoppingItem.getItemId());
        return "/html/dashboard/worflowDisplay.jsp";
	}
}