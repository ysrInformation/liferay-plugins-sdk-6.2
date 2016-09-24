package com.htmsd.dashboard;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.htmsd.addresscapture.AddressCapture;
import com.htmsd.slayer.model.Category;
import com.htmsd.slayer.model.ShoppingItem;
import com.htmsd.slayer.model.ShoppingOrder;
import com.htmsd.slayer.service.CategoryLocalServiceUtil;
import com.htmsd.slayer.service.ItemHistoryLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingCartLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingItemLocalServiceUtil;
import com.htmsd.slayer.service.ShoppingOrderLocalServiceUtil;
import com.htmsd.slayer.service.WholeSaleLocalServiceUtil;
import com.htmsd.util.HConstants;
import com.htmsd.util.NotificationUtil;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.NoSuchWorkflowDefinitionLinkException;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.model.WorkflowDefinitionLink;
import com.liferay.portal.service.AddressLocalServiceUtil;
import com.liferay.portal.service.ListTypeServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class DashboardPortlet
 */
public class DashboardPortlet extends MVCPortlet {

	private  long smallImageId = 0;
	
	
	@Override
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {
		 
	String resourceId = resourceRequest.getResourceID();
	JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

	if(resourceId.equals("getCategoryId")) {
		long parentCategoryId = ParamUtil.getLong(resourceRequest, "parentCategoryId");
		List<Category> categories = CategoryLocalServiceUtil.getByParent(parentCategoryId);
		for(Category category : categories) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
			jsonObject.put("categoryName", category.getName());
			jsonObject.put("categoryId", category.getCategoryId());
			jsonArray.put(jsonObject);
		}
	} else if(resourceId.equals("changeOrderStatus")) {
		long orderId = ParamUtil.getLong(resourceRequest, "orderId");
		changeOrderStatus(orderId);
	} else {
		/*long categoryId = ParamUtil.getLong(resourceRequest, HConstants.CATEGORY_ID);
		Category category = null;
		try {
			category = CategoryLocalServiceUtil.fetchCategory(categoryId);
		} catch (SystemException e) {
			_log.error(e);
		}*/
		
		//if(Validator.isNotNull(category) && !category.getName().equalsIgnoreCase("Live")){
			int typeId = 0;
			try {
				typeId = ListTypeServiceUtil.getListTypes(Contact.class.getName() + ListTypeConstants.ADDRESS).get(0).getListTypeId();
			} catch (SystemException e) {
				_log.error(e);
			}
			ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
			DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Address.class);
			dynamicQuery.add(RestrictionsFactoryUtil.eq("userId", themeDisplay.getUserId()));
			dynamicQuery.add(RestrictionsFactoryUtil.eq("typeId", typeId));
			try {
				long count = AddressLocalServiceUtil.dynamicQueryCount(dynamicQuery);
				if(count > 0){
					jsonArray.put("true");
				}else{
					jsonArray.put("false");
				}
			} catch (SystemException e) {
				_log.error(e);
			}
		}
		
	//}
		PrintWriter printWriter = resourceResponse.getWriter();
		printWriter.write(jsonArray.toString());
		printWriter.flush();
	}
	

	/**
	 * Method to changeOrderStatus
	 * @param actionRequest
	 * @param actionResponse
	 *  @throws IOException
	 * @throws PortletException
	 */
	private void changeOrderStatus(long orderId) {
		if (orderId > 0) {
			
			int orderStatus = ShoppingOrderLocalServiceUtil.getOrderStatusByTabName(HConstants.SHIPPING_STATUS);
			String articleId = ShoppingCartLocalServiceUtil.getArticleId(orderStatus);
			
			ShoppingOrderLocalServiceUtil.updateShoppingOrder(orderStatus, orderId, StringPool.BLANK); 
			
			ShoppingOrder shoppingOrder = null;
			try {
				shoppingOrder = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
			} catch (SystemException e) {
				e.printStackTrace();
			}
			
			if (Validator.isNotNull(shoppingOrder) && !articleId.isEmpty()) {
				String[] tokens = ShoppingCartLocalServiceUtil.getOrderTokens();
				String[] values = ShoppingCartLocalServiceUtil.getValueTokens(shoppingOrder);
				NotificationUtil.sendNotification(shoppingOrder.getGroupId(), 
						shoppingOrder.getUserName(), shoppingOrder.getShippingEmailAddress(), articleId, tokens, values);
			}
		}
	}


	/**
	 * Method to saveBusiness for saving user business info
	 * @param actionRequest
	 * @param actionResponse
	 *  @throws IOException
	 * @throws PortletException
	 */
	public void saveBusiness(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		
		AddressCapture addressCapture = new AddressCapture();
		addressCapture.saveAddress(actionRequest, actionResponse);
	}

	@Override
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		String keywords = ParamUtil.getString(actionRequest, "keywords");
		if (keywords.isEmpty()) {
			super.processAction(actionRequest, actionResponse);
		} else {
			PortalUtil.copyRequestParameters(actionRequest, actionResponse);
		}
	}
	
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
		PortletConfig portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		
		long itemId = ParamUtil.getLong(uploadRequest, HConstants.ITEM_ID);
		long userId  = ParamUtil.getLong(uploadRequest, "userId");
		String userName = ParamUtil.getString(uploadRequest, "userName");
		String userEmail = ParamUtil.getString(uploadRequest, "emailId");
		long currentUserId  = themeDisplay.getUserId();
		String currentUserName = themeDisplay.getUser().getScreenName();
		String currentUserEmail = themeDisplay.getUser().getEmailAddress();
		String name = ParamUtil.getString(uploadRequest, HConstants.NAME);
		String productCode = ParamUtil.getString(uploadRequest, HConstants.PRODUCT_CODE);
		String description = ParamUtil.getString(uploadRequest, HConstants.DESCRIPTION);
		Double sellerPrice = ParamUtil.getDouble(uploadRequest, HConstants.PRICE);
		Double totalPrice = ParamUtil.getDouble(uploadRequest, HConstants.TOTAL_PRICE);
		Double tax = ParamUtil.getDouble(uploadRequest, HConstants.TAX);
		boolean unlimitedQuantity = ParamUtil.getBoolean(uploadRequest, HConstants.UNILIMITED_QUANTITY);
		long quantity = unlimitedQuantity ? -1 : ParamUtil.getLong(uploadRequest, HConstants.QUANTITY);
		long categoryId = ParamUtil.getLong(uploadRequest, HConstants.CATEGORY_ID);
		String vedioURL = ParamUtil.getString(uploadRequest, HConstants.VEDIO_URL);
		String staffRemark = ParamUtil.getString(uploadRequest, HConstants.STAFF_REMARKS, "Item Added");
		List<Long> imageIds = new ArrayList<Long>();
		ShoppingItem shoppingItem = null;
		int status = ParamUtil.getInteger(uploadRequest, HConstants.status);
		String articleId = StringPool.BLANK;
		
		String remark = StringPool.BLANK;
		String addupdateMessage = (itemId == 0) ? "added" : "updated"; 
		
		ServiceContext serviceContext = null;
		try {
			serviceContext = ServiceContextFactory.getInstance(ShoppingItem.class.getName(), actionRequest);
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (itemId == 0) {
		
			//Adding items 
			imageIds = saveFiles(uploadRequest, HConstants.IMAGE, HConstants.ITEM_FOLDER_NAME);
			shoppingItem = ShoppingItemLocalServiceUtil.addItem(themeDisplay.getScopeGroupId(),
					themeDisplay.getCompanyId(), currentUserId,currentUserName, currentUserEmail, currentUserId, StringPool.BLANK, StringPool.BLANK, productCode,
					name, description, sellerPrice, sellerPrice, tax, quantity, WorkflowConstants.STATUS_PENDING,
					StringUtil.merge(imageIds, StringPool.COMMA), vedioURL,  getSmallImageId(), StringPool.BLANK);
			itemId = shoppingItem.getItemId();
			ItemHistoryLocalServiceUtil.addItemHistory(itemId, currentUserId, currentUserName, currentUserEmail, HConstants.ITEM_ADDED, StringPool.BLANK);
			articleId = HConstants.ITEM_ADDED_TEMPLATE;
		
			WorkflowDefinitionLink workflowDefinitionLink = null;
			try {
				workflowDefinitionLink = WorkflowDefinitionLinkLocalServiceUtil.getDefaultWorkflowDefinitionLink(themeDisplay.getCompanyId(), ShoppingItem.class.getName(), 0, 0);
			} catch (Exception e) {
				if(e instanceof NoSuchWorkflowDefinitionLinkException){
					SessionMessages.add(actionRequest.getPortletSession(),"workflow-not-enabled");
				}
				e.printStackTrace();
			}
			
			if (Validator.isNotNull(shoppingItem) && Validator.isNotNull(workflowDefinitionLink)) {
				
				try {
					AssetEntryLocalServiceUtil.updateEntry(currentUserId, themeDisplay.getScopeGroupId(),
							ShoppingItem.class.getName(), shoppingItem.getItemId(),
							serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames());
				} catch (PortalException e) {
					e.printStackTrace();
				} catch (SystemException e) {
					e.printStackTrace();
				}
				
				try {
					WorkflowHandlerRegistryUtil.startWorkflowInstance(shoppingItem.getCompanyId(),
							shoppingItem.getUserId(), ShoppingItem.class.getName(), shoppingItem.getItemId(),
							shoppingItem, serviceContext);
				} catch (PortalException e) {
					e.printStackTrace();
				} catch (SystemException e) {
					e.printStackTrace();
				}
			}
			actionResponse.setRenderParameter("tab1", ParamUtil.getString(uploadRequest, "tab1"));
		} else {
			String redirect = ParamUtil.getString(actionRequest, "redirect");
			try {
				status = ShoppingItemLocalServiceUtil.fetchShoppingItem(itemId).getStatus();
			} catch (SystemException e) {
				e.printStackTrace();
			}
			//Updating items
			imageIds = updateImages(uploadRequest, HConstants.IMAGE, HConstants.IMAGE_ID);
			
			shoppingItem = ShoppingItemLocalServiceUtil.updateItem(itemId,
					themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(), userId, userName, userEmail, currentUserId, currentUserName, currentUserEmail,productCode, name,
					description, sellerPrice, totalPrice, tax, quantity, status,
					StringUtil.merge(imageIds, StringPool.COMMA), vedioURL, 0, remark);
			ItemHistoryLocalServiceUtil.addItemHistory(itemId, currentUserId, currentUserName, currentUserEmail, status, staffRemark);
			articleId = HConstants.ITEM_ADDED_UPDATED_TEMPLATE;
			
			actionResponse.sendRedirect(redirect);
		}
	
		// Tag
			updateTags(uploadRequest, itemId, themeDisplay);
		
		//Category 
		ShoppingItemLocalServiceUtil.updateCategory(itemId, categoryId, userId, userName);
				
		//WholeSale
		updateWholeSale(uploadRequest,itemId);
		
		String[] oldStr = {"[$USER_EMAIL$]", "[$USER_NAME$]", "[$ITEM_NAME$]", "[$PRODUCT_CODE$]", "[$DESCRIPTION$]", "[$TAX$]", "[$SELLER_PRICE$]", "[$QUANTITY$]"};
		String[] newStr = {currentUserEmail, currentUserName, name, productCode, description, Double.toString(tax), Double.toString(sellerPrice), quantity == -1 ? "Unlimited Quantity" : String.valueOf(quantity)};
		
		NotificationUtil.sendNotification(themeDisplay.getScopeGroupId(), 
				themeDisplay.getUser().getFullName(), themeDisplay.getUser().getEmailAddress(), articleId, oldStr, newStr);
		
		String notificationContent = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "item-add-update-notification-message");
		notificationContent = notificationContent.replace("[$USER_NAME$]", currentUserName);
		notificationContent = notificationContent.replace("[$Product_details$]", name);
		notificationContent = notificationContent.replace("[$ADDED_UPDATED$]", addupdateMessage);
		NotificationUtil.sendUserNotification(currentUserId, notificationContent, actionRequest);
		
	} 
	
	
	/**
	 * Method updateWholeSale to update updateWholeSale of items 
	 * @param uploadRequest
	 * @param itemId
	 */
	private void updateWholeSale(UploadPortletRequest uploadRequest, long itemId) {
		
		WholeSaleLocalServiceUtil.deleteWholeSaleByItem(itemId);
		if(ParamUtil.getBoolean(uploadRequest, HConstants.WHOLESALE_DISCOUNT)) {
			
			for(int i = 1 ; i <= HConstants.WHOLESALE_LIMIT; i++) {
				long quantity = ParamUtil.getLong(uploadRequest, HConstants.WHOLESALE_QUANTITY+i);
				double price = ParamUtil.getDouble(uploadRequest, HConstants.WHOLESALE_PRICE+i);
				if(quantity > 0 && price > 0.0) {
					WholeSaleLocalServiceUtil.putWholeSale(itemId, quantity, price);
				}
			}
		}
	}

	/**
	 * Method updateTags to update assesttags of items 
	 * @param uploadRequest
	 * @param itemId
	 */
	private void updateTags(UploadPortletRequest uploadRequest, long itemId,ThemeDisplay themeDisplay) {
		
		ServiceContext serviceContext = null;
		try {
			 serviceContext = ServiceContextFactory.getInstance(uploadRequest);
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
		
		try {
			AssetEntryLocalServiceUtil.updateEntry(themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), ShoppingItem.class.getName(), itemId, null, serviceContext.getAssetTagNames());
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
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
								sourceFileName, contentType, file.getName()+CounterLocalServiceUtil.increment(),
								sourceFileName, StringPool.BLANK, true, resizeImage(file, 600, 600),
								serviceContext);
						if(i == 1) {
						DLAppLocalServiceUtil.updateFileEntry(
								themeDisplay.getUserId(), ParamUtil.getLong(uploadRequest, HConstants.SMALL_IMAGE),
								sourceFileName, contentType, sourceFileName+HConstants.SMALL_IMAGE+CounterLocalServiceUtil.increment(),
								sourceFileName+HConstants.SMALL_IMAGE, StringPool.BLANK, true, resizeImage(file, 100, 100),
								serviceContext);
					}
					} catch (PortalException e) {
						_log.error(e);
					} catch (SystemException e) {
						_log.error(e);
					}
				}
			}else if(fileEntryId > 0 && deleteImage) {
				
				try {
					DLAppLocalServiceUtil.deleteFileEntry(fileEntryId);
					if(i == 1) {
						DLAppLocalServiceUtil.deleteFileEntry(ParamUtil.getLong(uploadRequest, HConstants.SMALL_IMAGE));
					}
				} catch (PortalException e) {
					_log.error(e);
					e.printStackTrace();
				} catch (SystemException e) {
					_log.error(e);
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
		long smallImgIds = 0;
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
							fileEntry = DLAppLocalServiceUtil.addFileEntry(themeDisplay.getUserId(), repositoryId, folderId, sourceFileName, contentType, file.getName()+CounterLocalServiceUtil.increment(), StringPool.BLANK, StringPool.BLANK,  resizeImage(file, 600, 600), serviceContext);
							//Adding small Image
							if(i == 1) {
								FileEntry fileEntry2 =  DLAppLocalServiceUtil.addFileEntry(themeDisplay.getUserId(), repositoryId, folderId, sourceFileName+HConstants.SMALL_IMAGE, contentType, file.getName()+HConstants.SMALL_IMAGE+CounterLocalServiceUtil.increment(), StringPool.BLANK, StringPool.BLANK,  resizeImage(file, 300, 300), serviceContext);
								setSmallImageId(fileEntry2.getFileEntryId());	
							}
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
	 * Method to resizeImage return the file with compressed size and given size
	 * @param file
	 * @param width
	 * @param height
	 * @return
	 */
	private File resizeImage(File file, int width, int height) {
		
		BufferedImage bufferedOriginalImage = null;
		try {
			bufferedOriginalImage = ImageIO.read(file);
		} catch (IOException e) {
			_log.error(e);
		} 
		if(Validator.isNotNull(bufferedOriginalImage)) {
			
			if(width == 0 || height == 0) {
				width = bufferedOriginalImage.getWidth();
				height = bufferedOriginalImage.getHeight();
			}
			int type = bufferedOriginalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : bufferedOriginalImage.getType();
			BufferedImage bufferedCompressedImage = new BufferedImage(width, height, type);
			Graphics2D graphics2d = bufferedCompressedImage.createGraphics();
			graphics2d.drawImage(bufferedOriginalImage, 0, 0, width, height, null);
			graphics2d.dispose();
			
			try {
				ImageIO.write(bufferedCompressedImage, "jpg", file);
			} catch (IOException e) {
				_log.error(e);
			}
		}
		return file;
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
	
	public long getSmallImageId() {
		return smallImageId;
	}

	public void setSmallImageId(long smallImageId) {
		this.smallImageId = smallImageId;
	}

	private Log _log = LogFactoryUtil.getLog(DashboardPortlet.class);
}
