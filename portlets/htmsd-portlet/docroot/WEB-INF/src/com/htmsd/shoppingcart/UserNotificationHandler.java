package com.htmsd.shoppingcart;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.UserNotificationEvent;
import com.liferay.portal.service.ServiceContext;

public class UserNotificationHandler extends BaseUserNotificationHandler {
	
	public final static String PORTLET_ID = "4_WAR_htmsdportlet";
	
	public final String TITLE = "A2ZALI Notification";
	
	public UserNotificationHandler() {
		setPortletId(PORTLET_ID);
	}
	
	protected String getBody(UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext) throws Exception {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(userNotificationEvent.getPayload());
		
		String notificationContent = jsonObject.getString("notificationContent");
		String title = "<strong>"+TITLE+"</strong>";
		String body = StringUtil.replace(getBodyTemplate(), new String[] {
				"[$TITLE$]", "[$BODY_TEXT$]"}, new String[] { title,
			notificationContent });
		return body;
	}
	
	protected String getBodyTemplate() throws Exception {
		StringBundler sb = new StringBundler(5);
		sb.append("<div class=\"title\">[$TITLE$]</div><div ");
		sb.append("class=\"body\">[$BODY_TEXT$]</div>");
		return sb.toString();
	}
}
