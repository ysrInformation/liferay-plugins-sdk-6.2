package com.htmsd.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.model.DLFileShortcut;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;

public class CommonUtil {
	
	private final static Log _log = LogFactoryUtil.getLog(CommonUtil.class);
	
	/**
	 * 
	 * @param fileEntryId
	 * @param themeDisplay
	 * @return
	 */
	public static String getThumbnailpath(long fileEntryId,
			long groupId) {

		_log.info(" get Thumbnail ");

		String thumbnail = StringPool.BLANK;
		FileEntry fileEntry = null;
		DLFileShortcut dlFileShortcut = null;

		try {
			fileEntry = DLAppLocalServiceUtil.getFileEntry(fileEntryId);
			/*thumbnail = DLUtil.getThumbnailSrc(fileEntry,
					fileEntry.getFileVersion(), dlFileShortcut, themeDisplay);*/
			thumbnail = "/documents/"
					+ groupId
					+ StringPool.SLASH
					+ fileEntry .getFolderId()
					+ StringPool.SLASH
					+ HttpUtil.encodeURL(HtmlUtil.unescape(String
					.valueOf(fileEntry .getTitle()))) + "?version="
					+ fileEntry .getVersion();

		} catch (Exception e) {
			_log.error("Exception occured when getting Thumbnail" + e);
		}
		return thumbnail;
	}
}
