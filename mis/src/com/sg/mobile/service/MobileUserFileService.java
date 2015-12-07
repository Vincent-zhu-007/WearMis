package com.sg.mobile.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sg.mobile.dao.MobileUserFileDao;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.entity.MobileUserFile;
import com.sg.mobile.entity.MobileUserFileShare;
import com.sg.mobile.util.XmlUtil;
import com.sg.service.BaseEntityManager;
import com.sg.util.DateUtil;
import com.sg.util.FileUtil;

@Service
public class MobileUserFileService implements BaseEntityManager<MobileUserFile> {
	@Resource
	private MobileUserFileDao mobileUserFileDao;
	
	@Autowired
	private MobileUserFileShareService mobileUserFileShareService;
	
	@Autowired
	private MobileUserService mobileUserService;

	@Override
	public void create(MobileUserFile entity) {
		// TODO Auto-generated method stub
		mobileUserFileDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileUserFileDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileUserFile> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileUserFileDao.find(params, pageParams);
	}

	@Override
	public MobileUserFile get(String id) {
		// TODO Auto-generated method stub
		return mobileUserFileDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileUserFile> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileUserFileDao.findForUnPage(params);
	}

	@Override
	public void update(MobileUserFile entity) {
		// TODO Auto-generated method stub
		mobileUserFileDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileUserFileDao.getTotalCount(params);
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<MobileUserFile> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("终端用户文件");
			/*第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short*/
			HSSFRow row = sheet.createRow((int) 0);
			/*第四步，创建单元格，并设置值表头 设置表头居中*/
			HSSFCellStyle style = wb.createCellStyle();
			/*左对齐*/
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT); 

			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("用户地址");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 1);
			cell.setCellValue("描述");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("文件名");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("文件大小");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 4);
			cell.setCellValue("文件类型");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 5);
			cell.setCellValue("文件地址");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 6);
			cell.setCellValue("位置");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 7);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 8);
			cell.setCellValue("创建日期");
			cell.setCellStyle(style);

			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				MobileUserFile model = (MobileUserFile) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getOwnerUri());
				row.createCell((short) 1).setCellValue(model.getDescription());
				row.createCell((short) 2).setCellValue(model.getFileName());
				row.createCell((short) 3).setCellValue(model.getFileSize() == null ? "" : model.getFileSize());
				row.createCell((short) 4).setCellValue(model.getFileType());
				row.createCell((short) 5).setCellValue(model.getFileUrl());
				row.createCell((short) 6).setCellValue(model.getLocation());
				row.createCell((short) 7).setCellValue(model.getStatus());
				cell = row.createCell((short) 8);
				cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(model.getCreateTime()));
			}
			
			/*第六步，将文件存到指定位置*/
			try
			{
				String fileName = UUID.randomUUID() + ".xls";
				String tempFilePath = path + "\\" + fileName;
				
				FileOutputStream fout = new FileOutputStream(tempFilePath);
				wb.write(fout);
				fout.close();
				
				filePath = tempFilePath;
			}
			catch (Exception e)
			{
				System.out.print(e.getMessage());
			}
		}
		
		return filePath;
	}
	
	@SuppressWarnings("rawtypes")
	public String getMobileUserFileFileTypeBySuffix(String suffix){
		String result = "";
		
		Document doc = XmlUtil.getXmlDoc("MobileFileType.xml");
		Element rootElement = doc.getRootElement();
		Iterator rootIter = rootElement.elementIterator();
		
		while (rootIter.hasNext()) {
			Element mobileUserFileFileTypeElement = (Element) rootIter.next();
			
			String id = mobileUserFileFileTypeElement.attribute("id").getValue();
			String type = mobileUserFileFileTypeElement.attribute("type").getValue();
			
			String[] array = type.split(",");
			for (String str : array) {
				if(str.equals(suffix)){
					result = id;
					return result;
				}
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getMobileUserFileFileTypeDescriptionMap(){
		Map<String, Object> map = new HashMap<String, Object>();
		
		Document doc = XmlUtil.getXmlDoc("MobileFileType.xml");
		Element rootElement = doc.getRootElement();
		Iterator rootIter = rootElement.elementIterator();
		
		while (rootIter.hasNext()) {
			Element mobileUserFileFileTypeElement = (Element) rootIter.next();
			
			String id = mobileUserFileFileTypeElement.attribute("id").getValue();
			String description = mobileUserFileFileTypeElement.attribute("description").getValue();
			map.put(id, description);
		}
		
		return map;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getMobileUserFileFileTypeTypeMap(){
		Map<String, Object> map = new HashMap<String, Object>();
		
		Document doc = XmlUtil.getXmlDoc("MobileFileType.xml");
		Element rootElement = doc.getRootElement();
		Iterator rootIter = rootElement.elementIterator();
		
		while (rootIter.hasNext()) {
			Element mobileUserFileFileTypeElement = (Element) rootIter.next();
			
			String id = mobileUserFileFileTypeElement.attribute("id").getValue();
			String type = mobileUserFileFileTypeElement.attribute("type").getValue();
			map.put(id, type);
		}
		
		return map;
	}
	
	public String getMobileUserFileJsonByFileType(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		JSONObject resultJosnObject = new JSONObject();
		
		try {
			String paraJson = urlPara;
			
			JSONObject jsonObject = JSONObject.fromObject(paraJson);
			
			String ownerUri = "";
			if(jsonObject.containsKey("ownerUri")){
				ownerUri = jsonObject.opt("ownerUri").toString();
			}
			
			String fileType = "";
			if(jsonObject.containsKey("fileType")){
				fileType = jsonObject.opt("fileType").toString();
			}
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("companyCode", mobileServerConfig.getCompanyCode());
			params.put("status", "Y");
			params.put("ownerUri", ownerUri);
			
			if(!fileType.equals("0")){
				params.put("fileType", fileType);
			}
			
			List<MobileUserFile> mobileUserFiles = findForUnPage(params);
			
			JSONArray jsonArray = new JSONArray();
			if(mobileUserFiles != null && mobileUserFiles.size() > 0){
				
				for (MobileUserFile mobileUserFile : mobileUserFiles) {
					JSONObject mobileUserFileJsonObject = new JSONObject();
					mobileUserFileJsonObject.put("fileId", mobileUserFile.getId());
					mobileUserFileJsonObject.put("fileName", mobileUserFile.getFileName());
					
					String createTime = DateUtil.getDate(mobileUserFile.getCreateTime(), "yyyyMMddHHmmss");
					mobileUserFileJsonObject.put("createtime", createTime);
					
					String displayCreateTime = DateUtil.getDate(mobileUserFile.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
					mobileUserFileJsonObject.put("displayCreateTime", displayCreateTime);
					
					mobileUserFileJsonObject.put("createUser", mobileUserFile.getCreator());
					mobileUserFileJsonObject.put("fileType", mobileUserFile.getFileType());
					mobileUserFileJsonObject.put("fileUrl", mobileUserFile.getFileUrl());
					mobileUserFileJsonObject.put("location", mobileUserFile.getLocation() == null ? "" : mobileUserFile.getLocation());
					mobileUserFileJsonObject.put("description", mobileUserFile.getDescription());
					
					mobileUserFileJsonObject.put("fileSize", mobileUserFile.getFileSize() == null ? "" : mobileUserFile.getFileSize());
					
					jsonArray.add(mobileUserFileJsonObject);
				}
			}
			
			resultJosnObject.put("r", "1");
			resultJosnObject.put("files", jsonArray);
			
		} catch (Exception e) {
			// TODO: handle exception
			resultJosnObject.put("r", "0");
			resultJosnObject.put("m", e.getMessage());
		}
		
		json = resultJosnObject.toString();
		
		return json;
	}
	
	public String createMobileUserFileJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject resultJosnObject = new JSONObject();
		
		try {
			String paraJson = urlPara;
			JSONObject jsonObject = JSONObject.fromObject(paraJson);
			
			String ownerUri = "";
			if(jsonObject.containsKey("ownerUri")){
				ownerUri = jsonObject.opt("ownerUri").toString();
			}
			
			if(ownerUri != null && !ownerUri.equals("")){
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("companyCode", mobileServerConfig.getCompanyCode());
				params.put("ownerUri", ownerUri);
				
				List<MobileUser> mobileUsers = mobileUserService.findForUnPage(params);
				
				if(mobileUsers != null && mobileUsers.size() > 0){
					MobileUserFile mobileUserFile = new MobileUserFile();
					String id = UUID.randomUUID().toString();
					
					mobileUserFile.setId(id);
					mobileUserFile.setOwnerUri(ownerUri);
					mobileUserFile.setStatus("Y");
					mobileUserFile.setCreator(ownerUri);
					
					if(jsonObject.containsKey("fileName")){
						String fileName = jsonObject.opt("fileName").toString();
						mobileUserFile.setFileName(fileName);
					}
					
					if(jsonObject.containsKey("fileSize")){
						String fileSize = jsonObject.opt("fileSize").toString();
						mobileUserFile.setFileSize(fileSize);
					}
					
					if(jsonObject.containsKey("fileType")){
						String fileType = jsonObject.opt("fileType").toString();
						mobileUserFile.setFileType(fileType);
					}
					
					if(jsonObject.containsKey("fileUrl")){
						String fileUrl = jsonObject.opt("fileUrl").toString();
						mobileUserFile.setFileUrl(fileUrl);
					}
					
					if(jsonObject.containsKey("location")){
						String location = jsonObject.opt("location").toString();
						mobileUserFile.setLocation(location);
					}
					
					if(jsonObject.containsKey("address")){
						String address = jsonObject.opt("address").toString();
						mobileUserFile.setAddress(address);
					}
					
					if(jsonObject.containsKey("description")){
						String description = jsonObject.opt("description").toString();
						mobileUserFile.setDescription(description);
					}
					
					if(jsonObject.containsKey("targetMemberUri")){
						String targetMemberUris = jsonObject.opt("targetMemberUri").toString();
						if(targetMemberUris != null && !targetMemberUris.equals("")){
							String[] array = targetMemberUris.split(",");
							for (String targetMemberUri : array) {
								MobileUserFileShare model = new MobileUserFileShare();
								model.setId(UUID.randomUUID().toString());
								model.setMobileUserFileId(id);
								model.setTargetMemberUri(targetMemberUri);
								
								mobileUserFileShareService.create(model);
							}
						}
					}
					
					create(mobileUserFile);
					
					resultJosnObject.put("result", "1");
					resultJosnObject.put("data", "200");
				}else {
					resultJosnObject.put("result", "0");
					resultJosnObject.put("message", "该用户不存在");
				}
			}else {
				resultJosnObject.put("result", "0");
				resultJosnObject.put("message", "缺少必要参数");
			}
		} catch (Exception e) {
			// TODO: handle exception
			resultJosnObject.put("result", "0");
			resultJosnObject.put("message", e.getMessage());
		}
		
		json = resultJosnObject.toString();
		
		return json;
	}
	
	public String createMobileUserFileAndUploadFileJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		String companyCode = mobileServerConfig.getCompanyCode();
		
		JSONObject resultJosnObject = new JSONObject();
		
		try {
			String paraJson = urlPara;
			
			JSONObject jsonObject = JSONObject.fromObject(paraJson);
			
			String ownerUri = "";
			if(jsonObject.containsKey("ownerUri")){
				ownerUri = jsonObject.opt("ownerUri").toString();
			}
			
			if(ownerUri != null && !ownerUri.equals("")){
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ownerUri", ownerUri);
				params.put("companyCode", companyCode);
				
				List<MobileUser> mobileUsers = mobileUserService.findForUnPage(params);
				
				if(mobileUsers != null && mobileUsers.size() > 0){
					
					MobileUserFile mobileUserFile = new MobileUserFile();
					String id = UUID.randomUUID().toString();
					
					mobileUserFile.setId(id);
					mobileUserFile.setOwnerUri(ownerUri);
					mobileUserFile.setStatus("Y");
					mobileUserFile.setCreator(ownerUri);
					
					String suffix = "";
					if(jsonObject.containsKey("suffix")){
						suffix = jsonObject.opt("suffix").toString();
					}
					
					if(suffix != null && !suffix.equals("")){
						String fileType = getMobileUserFileFileTypeBySuffix(suffix);
						mobileUserFile.setFileType(fileType);
						
						String fileName = "";
						if(jsonObject.containsKey("fileName")){
							fileName = jsonObject.opt("fileName").toString();
							mobileUserFile.setFileName(fileName);
						}
						
						if(jsonObject.containsKey("fileSize")){
							String fileSize = jsonObject.opt("fileSize").toString();
							mobileUserFile.setFileSize(fileSize);
						}
						
						String fileUrl = "";
						String binary = "";
						if(jsonObject.containsKey("binary")){
							binary = jsonObject.opt("binary").toString();
							
							String dirName = "";
							if(fileType.equals("1")){
								dirName = "image"; 
							}else {
								dirName = "file";
							}
							
							String savePath = mobileServerConfig.getUserAgent() + "attached/";
							
							InputStream is = FileUtil.strImageToInputStream(binary);
							String jsonSavePath = FileUtil.getSaveUrlByStream(fileName, dirName, savePath, is);
							JSONObject jsonObjectSavePath = JSONObject.fromObject(jsonSavePath);
							
							String uploadResult = "";
							if(jsonObjectSavePath.containsKey("result")){
								uploadResult = jsonObjectSavePath.opt("result").toString();
								
								if(uploadResult.equals("1")){
									if(jsonObjectSavePath.containsKey("data")){
										fileUrl = jsonObjectSavePath.opt("data").toString();
										mobileUserFile.setFileUrl(fileUrl);
										
										if(jsonObject.containsKey("location")){
											String location = jsonObject.opt("location").toString();
											mobileUserFile.setLocation(location);
										}
										
										if(jsonObject.containsKey("address")){
											String address = jsonObject.opt("address").toString();
											mobileUserFile.setAddress(address);
										}
										
										if(jsonObject.containsKey("description")){
											String description = jsonObject.opt("description").toString();
											mobileUserFile.setDescription(description);
										}
										
										if(jsonObject.containsKey("targetMemberUri")){
											String targetMemberUris = jsonObject.opt("targetMemberUri").toString();
											if(targetMemberUris != null && !targetMemberUris.equals("")){
												String[] array = targetMemberUris.split(",");
												for (String targetMemberUri : array) {
													MobileUserFileShare model = new MobileUserFileShare();
													model.setId(UUID.randomUUID().toString());
													model.setMobileUserFileId(id);
													model.setTargetMemberUri(targetMemberUri);
													
													mobileUserFileShareService.create(model);
												}
											}
										}
										
										create(mobileUserFile);
										
										resultJosnObject.put("result", "1");
										resultJosnObject.put("data", "200");
									}
								}else {
									String message = "";
									if(jsonObjectSavePath.containsKey("message")){
										message = jsonObjectSavePath.opt("message").toString();
									}
									resultJosnObject.put("result", "0");
									resultJosnObject.put("message", message);
									
									json = resultJosnObject.toString();
									return json;
								}
							}
						}
					}else {
						resultJosnObject.put("result", "0");
						resultJosnObject.put("message", "文件后缀错误");
					}
				}else {
					resultJosnObject.put("result", "0");
					resultJosnObject.put("message", "用户不存在");
				}
			}else {
				resultJosnObject.put("result", "0");
				resultJosnObject.put("message", "缺少必要参数");
			}
		} catch (Exception e) {
			// TODO: handle exception
			resultJosnObject.put("result", "0");
			resultJosnObject.put("message", e.getMessage());
		}
		
		json = resultJosnObject.toString();
		
		return json;
	}
}
