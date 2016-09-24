package com.htmsd.handlers;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;

public class ShoppingItemWorkflowHandler extends BaseWorkflowHandler {
	public static final String CLASS_NAME = ShoppingItem.class.getName();
	public static final String TYPE = "ShoppingItem";
	
	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public String getType(Locale arg0) {
		return TYPE;
	}

	@Override
	public ShoppingItem updateStatus(int status, Map<String, Serializable> workflowContext)
			throws PortalException, SystemException {
		
		long userId = GetterUtil.getLong(workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));
		long resourcePrimKey = GetterUtil.getLong(workflowContext.get(WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));
		ShoppingItem shoppingItem = ShoppingItemLocalServiceUtil.fetchShoppingItem(resourcePrimKey);
		shoppingItem.setStatus(status);
		shoppingItem.setStatusByUserId(userId);
		shoppingItem.setStatusDate(new Date());
		shoppingItem = ShoppingItemLocalServiceUtil.updateShoppingItem(shoppingItem);
		if (status == WorkflowConstants.STATUS_APPROVED) {
			AssetEntryLocalServiceUtil.updateVisible(CLASS_NAME, resourcePrimKey, true);
		} else {
			AssetEntryLocalServiceUtil.updateVisible(CLASS_NAME, resourcePrimKey, false);
		}
		return shoppingItem;
	}
}