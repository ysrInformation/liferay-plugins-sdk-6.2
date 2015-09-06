package com.htmsd.dashboard;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.htmsd.slayer.model.ItemHistory;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.Tag;
import com.htmsd.slayer.service.ItemHistoryLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.TagLocalServiceUtil;
import com.htmsd.util.HConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class DashboardPortlet
 */
public class DashboardPortlet extends MVCPortlet {
 
	
	/**
	 * Method to addItem into the item list
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	public void addItem(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		_log.info("addItem");
		
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(actionRequest);
		ThemeDisplay themeDisplay=(ThemeDisplay)uploadRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		long itemId = ParamUtil.getLong(uploadRequest, HConstants.ITEM_ID);
		long userId  = ParamUtil.getLong(uploadRequest, "userId");
		String userName = ParamUtil.getString(uploadRequest, "userName");
		long currentUserId  = themeDisplay.getUserId();
		String currentUserName = themeDisplay.getUser().getScreenName();
		String name = ParamUtil.getString(uploadRequest, HConstants.NAME);
		String productCode = ParamUtil.getString(uploadRequest, HConstants.PRODUCT_CODE);
		String description = ParamUtil.getString(uploadRequest, HConstants.DESCRIPTION);
		Double sellerPrice = ParamUtil.getDouble(uploadRequest, HConstants.PRICE);
		Double totalPrice = ParamUtil.getDouble(uploadRequest, HConstants.TOTAL_PRICE);
		boolean unlimitedQuantity = ParamUtil.getBoolean(uploadRequest, HConstants.UNILIMITED_QUANTITY);
		long quantity = unlimitedQuantity ? -1 : ParamUtil.getLong(uploadRequest, HConstants.QUANTITY);
		long categoryId = ParamUtil.getLong(uploadRequest, HConstants.CATEGORY_ID);
		long tagId = ParamUtil.getLong(uploadRequest, HConstants.TAG_ID);
		String tagName = ParamUtil.getString(uploadRequest, HConstants.TAG);
		String vedioURL = ParamUtil.getString(uploadRequest, HConstants.VEDIO_URL);
		List<Long> imageIds = new ArrayList<Long>();
		ShoppingItem shoppingItem = null;
		Tag tag = null;
		int status = ParamUtil.getInteger(uploadRequest, HConstants.status);
		String remark = ( status == HConstants.REJECT ) ? ParamUtil.getString(uploadRequest, HConstants.REMARK)  : StringPool.BLANK;
		
		if (itemId == 0) {
			//Adding items 
			imageIds = saveFiles(uploadRequest, HConstants.IMAGE, HConstants.ITEM_FOLDER_NAME);
			shoppingItem = ShoppingItemLocalServiceUtil.addItem(themeDisplay.getScopeGroupId(),
					themeDisplay.getCompanyId(), currentUserId,currentUserName, currentUserId, StringPool.BLANK, productCode,
					name, description, sellerPrice, totalPrice, quantity, HConstants.NEW,
					StringUtil.merge(imageIds, StringPool.COMMA), vedioURL, StringPool.BLANK);
			itemId = shoppingItem.getItemId();
			ItemHistoryLocalServiceUtil.addItemHistory(itemId, currentUserId, currentUserName, HConstants.ITEM_ADDED, StringPool.BLANK);
		} else {
			//Updating items 
				imageIds = updateImages(uploadRequest, HConstants.IMAGE, HConstants.IMAGE_ID);
				shoppingItem = ShoppingItemLocalServiceUtil.updateItem(itemId,
						themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(), userId, userName,currentUserId, currentUserName, productCode, name,
						description, sellerPrice, totalPrice, quantity, status,
						StringUtil.merge(imageIds, StringPool.COMMA), vedioURL, remark);
				ItemHistoryLocalServiceUtil.addItemHistory(itemId, currentUserId, currentUserName, HConstants.ITEM_UPDATED, StringPool.BLANK);
		}
		//Adding New Tag
		if(tagId == 0 && !tagName.isEmpty()) {
			List<Tag> tagNames = TagLocalServiceUtil.getTagByName(tagName);
			if(tagNames.size() > 0){
				tag = tagNames.get(0);
			}else {
				tag = TagLocalServiceUtil.addTag(themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(), themeDisplay.getUserId(), tagName);
			}
			tagId = tag.getTagId();
		}
		
		//Update Tag_Mapping and Category Mapping
		ShoppingItemLocalServiceUtil.updateCategory(itemId, categoryId, userId, userName);
		ShoppingItemLocalServiceUtil.updateTag(itemId, tagId, userId, userName);
		actionResponse.setRenderParameter("tab1", ParamUtil.getString(uploadRequest, "tab1"));
	} 
	
	/**
	 * Method updateItemSet to update set of items status
	 * @param actionRequest
	 * @param actionResponse
	 */
	public void updateItemSet(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String [] rowIds=ParamUtil.getParameterValues(actionRequest, "rowIds");
		int status = ParamUtil.getInteger(actionRequest, HConstants.status);
		for(String rowId : rowIds) {
			ShoppingItemLocalServiceUtil.updateStatus(Long.valueOf(rowId), status, themeDisplay.getUserId(),themeDisplay.getUser().getScreenName());
		}
		actionResponse.setRenderParameter("tab1", ParamUtil.getString(actionRequest, "tab1"));
	}
	
	/**
	 * Method deleteItemSet delete set of items
	 * @param actionRequest
	 * @param action Response
	*/
	public void deleteItemSet(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		String [] rowIds=ParamUtil.getParameterValues(actionRequest, "rowIds");
		for(String rowId : rowIds) {
			ShoppingItemLocalServiceUtil.deleteItem(Long.valueOf(rowId));
		}
		actionResponse.setRenderParameter("tab1", ParamUtil.getString(actionRequest, "tab1"));
	}
	
	/**
	 * Method to updateImages save all updated images  into DLFile entry and return array of fileIds
	 * @param uploadRequest
	 * @param inputName
	 * @param imageIdName
	 * @return
	 */
	private List<Long> updateImages(UploadPortletRequest uploadRequest, String inputName, String imageIdName) {
		
		List<Long> fileIds = new ArrayList<Long>();
		ThemeDisplay themeDisplay=(ThemeDisplay)uploadRequest.getAttribute(WebKeys.THEME_DISPLAY);
		ServiceContext serviceContext = null;
		try {
			serviceContext = ServiceContextFactory.getInstance(uploadRequest);
			serviceContext.setAddGuestPermissions(true);
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
		
		for (int i = 1; i <= HConstants.IMAGES_UPLOAD_LIMIT; i++) {
			
			long fileEntryId = ParamUtil.getLong(uploadRequest, HConstants.IMAGE_ID+i);
			boolean deleteImage = ParamUtil.getBoolean(uploadRequest, HConstants.DELETE_IMAGE+i);
			String  fileName = inputName+i;
			
			long sizeInBytes = uploadRequest.getSize(fileName);
			String contentType = StringPool.BLANK;
			
			if (fileEntryId > 0 && !deleteImage) {
				
				fileIds.add(fileEntryId);
				
				if(sizeInBytes > 0) {
			
					File file = uploadRequest.getFile(fileName);
					
					String sourceFileName = uploadRequest.getFileName(fileName);
		        	contentType = MimeTypesUtil.getContentType(sourceFileName);	

					try {
						DLAppLocalServiceUtil.updateFileEntry(
								themeDisplay.getUserId(), fileEntryId,
								sourceFileName, contentType, sourceFileName,
								sourceFileName, StringPool.BLANK, true, file,
								serviceContext);
					} catch (PortalException e) {
						_log.error(e);
					} catch (SystemException e) {
						_log.error(e);
					}
				}
			}
		}
		return fileIds;
	}

	
	/**
	 * Method to saveFiles save all images in upload into DLFile entry and return array of fileIds
	 * @param uploadRequest
	 * @param inputName
	 * @param folderName
	 * @return 
	 */
	private List<Long> saveFiles(UploadPortletRequest uploadRequest, String inputName, String folderName) {
		
		List<Long> fileIds = new ArrayList<Long>();
		ThemeDisplay themeDisplay=(ThemeDisplay)uploadRequest.getAttribute(WebKeys.THEME_DISPLAY);
		ServiceContext serviceContext = null;
		try {
			serviceContext = ServiceContextFactory.getInstance(uploadRequest);
			serviceContext.setAddGuestPermissions(true);;
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
		
		long folderId = getFolder(themeDisplay, folderName).getFolderId();
    	long repositoryId = themeDisplay.getScopeGroupId();
    	FileEntry fileEntry = null;
    	String contentType = StringPool.BLANK;
    	

		for (int i = 0; i <= HConstants.IMAGES_UPLOAD_LIMIT; i++) {
				
				String  fileName = inputName+i;
				
				long sizeInBytes = uploadRequest.getSize(fileName);
				
				if (sizeInBytes > 0) {
					File file = uploadRequest.getFile(fileName);
					String sourceFileName = uploadRequest.getFileName(fileName);
					
					//adding document file into document library
			    	if(file.exists() && file.length() > 0) {
			        	 contentType = MimeTypesUtil.getContentType(sourceFileName);	
			        	 try {
							fileEntry = DLAppLocalServiceUtil.addFileEntry(themeDisplay.getUserId(), repositoryId, folderId, sourceFileName, contentType, file.getName(), StringPool.BLANK, StringPool.BLANK, file, serviceContext);
						} catch (PortalException e) {
							e.printStackTrace();
						} catch (SystemException e) {
							e.printStackTrace();
						}
			        	 fileIds.add(fileEntry.getFileEntryId());
			    	}
					
				}
			}
			return fileIds;
	}
	
	/**
	 * Method to getFolder return the folde with respected name or create folder and return id
	 * @param themeDisplay
	 * @param folderName
	 * @return
	 */
	private Folder getFolder(ThemeDisplay themeDisplay, String folderName){
    	Folder folder = null;
    	ServiceContext serviceContext = new ServiceContext();
		serviceContext.setScopeGroupId(themeDisplay.getScopeGroupId());
		serviceContext.setAddGuestPermissions(true);
    	try {
    		folder = DLAppLocalServiceUtil.getFolder(themeDisplay.getScopeGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, folderName);
		} catch (Exception e) {
			try {
				folder = DLAppLocalServiceUtil.addFolder(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), 0L, folderName, "description", serviceContext);
			} catch (Exception e1) {
				_log.error(e1.getMessage());
			}
		}
    	return folder;
    }

	/**
	 * Method to updateStock update stock of particular product
	 * @param actionRequest
	 * @param actionResponse
	 * @return
	 */
	public void updateStock(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		long itemId = ParamUtil.getLong(actionRequest, HConstants.ITEM_ID);
		boolean unlimitedQuantity = ParamUtil.getBoolean(actionRequest, HConstants.UNILIMITED_QUANTITY);
		long quantity = unlimitedQuantity ? -1 : ParamUtil.getLong(actionRequest, HConstants.QUANTITY);
		ThemeDisplay themeDisplay=(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

		ShoppingItemLocalServiceUtil.updateStock(itemId, quantity, themeDisplay.getUserId(), themeDisplay.getUser().getScreenName());
		
		actionResponse.sendRedirect(ParamUtil.getString(actionRequest, "redirectURL"));
	}
	 
	@Override
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {
		
		List<Tag> tags = null;;
		try {
			tags = TagLocalServiceUtil.getTags(0, TagLocalServiceUtil.getTagsCount());
		} catch (SystemException e) {
			_log.error(e);
		}
		JSONArray tagArray = JSONFactoryUtil.createJSONArray();
		for(Tag tag : tags) {
			JSONObject tagObject = JSONFactoryUtil.createJSONObject();
			tagObject.put("tagId", tag.getTagId());
			tagObject.put("tagName", tag.getName());
			tagArray.put(tagObject);
		}
		PrintWriter out=resourceResponse.getWriter();
		out.println(tagArray.toString());

	}
	
	private Log _log = LogFactoryUtil.getLog(DashboardPortlet.class);
}
