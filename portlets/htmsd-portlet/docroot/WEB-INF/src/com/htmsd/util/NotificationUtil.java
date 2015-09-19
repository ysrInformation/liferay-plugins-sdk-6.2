package com.htmsd.util;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.InternetAddress;

import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

public class NotificationUtil {
	
	private static Log _log = LogFactoryUtil.getLog(NotificationUtil.class);
	
	/**
	 * Send Email Notification
	 * @param groupId
	 * @param userName
	 * @param email
	 * @param articleId
	 */
	public static void sendNotification(long groupId, String userName, String email, String articleId) {
		_log.info("Sedning Email to:"+userName+"<"+email+">");
		JournalArticle journalArticle = null;
		try {
			journalArticle = JournalArticleLocalServiceUtil.getArticle(groupId, articleId);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (Validator.isNotNull(journalArticle)) {
			Document document = null;
			try {
				document = SAXReaderUtil.read(journalArticle.getContent());
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			String subject = getValue(document, HConstants.EMAIL_SUBJECT);
			String body = getValue(document, HConstants.EMAIL_MESSAGE);
			sendMail(userName, email, body, subject);
		}
	}
	
	/**
	 * 
	 * @param userName
	 * @param email
	 * @param body
	 * @param subject
	 */
	private static void sendMail(String userName, String email, String body, String subject) {
		MailMessage mailMessage = new MailMessage();
		mailMessage.setFrom(getInternetAddress("H.T.M.S.D PET'S", PropsUtil.get(PropsKeys.ADMIN_EMAIL_FROM_ADDRESS)));
		mailMessage.setTo(getInternetAddress(userName, email));
		mailMessage.setSubject(subject);
		mailMessage.setBody(body);
		mailMessage.setHTMLFormat(true);
		MailServiceUtil.sendEmail(mailMessage);
	}

	/**
	 * 
	 * @param personal
	 * @param email
	 * @return
	 */
	private static InternetAddress getInternetAddress(String personal, String email) {
		InternetAddress internetAddress = new InternetAddress();
		internetAddress.setAddress(email);
		try {
			internetAddress.setPersonal(personal);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return internetAddress;
	}
	
	/**
	 * 
	 * @param document
	 * @param name
	 * @return
	 */
	private static String getValue(Document document,String name) {
		Node node = document.selectSingleNode("/root/dynamic-element[@name='"  + name + "']/dynamic-content");
		return node.getText();
	}
}