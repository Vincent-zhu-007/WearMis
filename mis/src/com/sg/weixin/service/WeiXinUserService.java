package com.sg.weixin.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.entity.MobileUser;
import com.sg.mobile.service.MobileUserService;
import com.sg.service.BaseEntityManager;
import com.sg.service.CodeService;
import com.sg.util.MD5;
import com.sg.util.MapUtil;
import com.sg.weixin.dao.WeiXinUserDao;
import com.sg.weixin.dao.WeiXinUserExtensionDao;
import com.sg.weixin.entity.WeiXinUser;

@Service
public class WeiXinUserService implements BaseEntityManager<WeiXinUser> {
	@Resource
	private WeiXinUserDao weiXinUserDao;
	
	@Resource
	private WeiXinUserExtensionDao weiXinUserExtensionDao;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private MobileUserService mobileUserService;
	
	@Transactional
	@Override
	public void create(WeiXinUser entity) {
		// TODO Auto-generated method stub
		weiXinUserExtensionDao.create(entity);
		weiXinUserDao.create(entity);
	}
	
	@Transactional
	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		weiXinUserExtensionDao.delete(id);
		weiXinUserDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<WeiXinUser> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return weiXinUserDao.find(params, pageParams);
	}

	@Override
	public WeiXinUser get(String id) {
		// TODO Auto-generated method stub
		return weiXinUserDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<WeiXinUser> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return weiXinUserDao.findForUnPage(params);
	}
	
	@Transactional
	@Override
	public void update(WeiXinUser entity) {
		// TODO Auto-generated method stub
		weiXinUserExtensionDao.update(entity);
		weiXinUserDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return weiXinUserDao.getTotalCount(params);
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<WeiXinUser> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			Map<String, Object> genderMap = codeService.getCodeCacheMapByCategory("GENDER");
			Map<String, Object> statusMap = codeService.getCodeCacheMapByCategory("STATUS");
			
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("微信用户列表");
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
			cell.setCellValue("用户名");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("显示名称");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("真实姓名");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 4);
			cell.setCellValue("终端号码");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 5);
			cell.setCellValue("性别");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 6);
			cell.setCellValue("邮箱地址");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 7);
			cell.setCellValue("生日");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 8);
			cell.setCellValue("头像路径 ");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 9);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 10);
			cell.setCellValue("创建日期");
			cell.setCellStyle(style);

			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				WeiXinUser model = (WeiXinUser) list.get(i);
				
				String gender = "";
				if(model.getGender() != null && !model.getGender().equals("")){
					gender = genderMap.get(model.getGender()).toString();
				}
				
				String status = statusMap.get(model.getStatus()).toString();
				
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getOwnerUri());
				row.createCell((short) 1).setCellValue(model.getUserName());
				row.createCell((short) 2).setCellValue(model.getDisplayName());
				row.createCell((short) 3).setCellValue(model.getTrueName());
				row.createCell((short) 4).setCellValue(model.getMobilePhone());
				row.createCell((short) 5).setCellValue(gender);
				row.createCell((short) 6).setCellValue(model.getMail());
				row.createCell((short) 7).setCellValue(model.getBirthday());
				row.createCell((short) 8).setCellValue(model.getHeadPortrait());
				row.createCell((short) 9).setCellValue(status);
				cell = row.createCell((short) 10);
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
	
	public Map<String, Object> getWeiXinUserMap(String targetOwnerUri){
		Map<String, Object> map = new HashMap<String, Object>();
		
		Map<String, Object> targetWeiXinUserParams = new HashMap<String, Object>();
		targetWeiXinUserParams.put("ownerUri", targetOwnerUri);

		List<WeiXinUser> targetWeiXinUsers = findForUnPage(targetWeiXinUserParams);
		
		if(targetWeiXinUsers != null && targetWeiXinUsers.size() > 0){
			WeiXinUser targetWeiXinUser = targetWeiXinUsers.get(0);
			
			String orgStructure = targetWeiXinUser.getOrgStructure();
			
			if(orgStructure != null && !orgStructure.equals("")){
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("orgStructure", orgStructure);
				params.put("status", "Y");
				List<WeiXinUser> list = findForUnPage(params);
				
				for (WeiXinUser weiXinUser : list) {
					map.put(weiXinUser.getOwnerUri(), weiXinUser.getDisplayName());
				}
				
				map = MapUtil.mapSortByKey(map);
			}
		}
		
		return map;
	}
	
	public List<WeiXinUser> getWeiXinUserByCompanyCode(String companyCode){
		List<WeiXinUser> list = new ArrayList<WeiXinUser>();
		
		if(companyCode != null && !companyCode.equals("")){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("companyCode", companyCode);
			params.put("status", "Y");
			list = findForUnPage(params);
		}
		
		return list;
	}
	
	public WeiXinUser getWeiXinUserByOwnerUriAndCompanyCode(String ownerUri, String companyCode){
		if(ownerUri != null && !ownerUri.equals("") && companyCode != null && !companyCode.equals("")){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ownerUri", ownerUri);
			params.put("companyCode", companyCode);
			params.put("status", "Y");
			
			List<WeiXinUser> list = findForUnPage(params);
			
			if(list != null && list.size() > 0){
				return list.get(0);
			}
		}
		
		return null;
	}
	
	public WeiXinUser getWeiXinUserByOwnerUri(String ownerUri){
		if(ownerUri != null && !ownerUri.equals("")){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ownerUri", ownerUri);
			params.put("status", "Y");
			
			List<WeiXinUser> list = findForUnPage(params);
			
			if(list != null && list.size() > 0){
				return list.get(0);
			}
		}
		
		return null;
	}
	
	public WeiXinUser getWeiXinUserByUserName(String userName){
		if(userName != null && !userName.equals("")){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userName", userName);
			params.put("status", "Y");
			
			List<WeiXinUser> list = findForUnPage(params);
			
			if(list != null && list.size() > 0){
				return list.get(0);
			}
		}
		
		return null;
	}
	
	public WeiXinUser getWeiXinUserByOpenId(String openId){
		if(openId != null && !openId.equals("")){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("openId", openId);
			params.put("status", "Y");
			
			List<WeiXinUser> list = findForUnPage(params);
			
			if(list != null && list.size() > 0){
				return list.get(0);
			}
		}
		
		return null;
	}
	
	public String getWeiXinUserByOpenIdFromFwhJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String openId = "";
		if(jsonObject.containsKey("openId")){
			openId = jsonObject.opt("openId").toString();
		}
		
		if(openId != null && !openId.equals("")){
			WeiXinUser weiXinUser = getWeiXinUserByOpenId(openId);
			
			if(weiXinUser != null){
				String weiXinOwnerUri = weiXinUser.getOwnerUri();
				
				JSONObject jsonObjectData = new JSONObject();
				
				jsonObjectData.put("ownerUri", weiXinOwnerUri);
				jsonObjectData.put("displayName", weiXinUser.getDisplayName());
				jsonObjectData.put("trueName", weiXinUser.getTrueName() == null ? "" : weiXinUser.getTrueName());
				jsonObjectData.put("mobilePhone", weiXinUser.getMobilePhone() == null ? "" : weiXinUser.getMobilePhone());
				jsonObjectData.put("gender", weiXinUser.getGender() == null ? "" : weiXinUser.getGender());
				jsonObjectData.put("mail", weiXinUser.getMail() == null ? "" : weiXinUser.getMail());
				jsonObjectData.put("birthday", weiXinUser.getBirthday() == null ? "" : weiXinUser.getBirthday());
				jsonObjectData.put("headPortrait", weiXinUser.getHeadPortrait() == null ? "" : weiXinUser.getHeadPortrait());
				jsonObjectData.put("province", weiXinUser.getProvince() == null ? "" : weiXinUser.getProvince());
				jsonObjectData.put("city", weiXinUser.getCity() == null ? "" : weiXinUser.getCity());
				jsonObjectData.put("sign", weiXinUser.getSign() == null ? "" : weiXinUser.getSign());
				jsonObjectData.put("companyCode", weiXinUser.getCompanyCode() == null ? "" : weiXinUser.getCompanyCode());
				jsonObjectData.put("orgStructure", weiXinUser.getOrgStructure() == null ? "" : weiXinUser.getOrgStructure());
				
				JSONArray jsonArrayMobileUser = new JSONArray();
				
				List<MobileUser> mobileUsers = mobileUserService.getMobileUserByOpenId(openId);
				if(mobileUsers != null && mobileUsers.size() > 0){
					for (MobileUser mobileUser : mobileUsers) {
						JSONObject jsonObjectMobileUser = new JSONObject();
						jsonObjectMobileUser.put("mobileOwnerUri", mobileUser.getOwnerUri());
						jsonObjectMobileUser.put("mobileUserName", mobileUser.getUserName());
						jsonObjectMobileUser.put("mobileDisplayName", mobileUser.getDisplayName());
						
						jsonArrayMobileUser.add(jsonObjectMobileUser);
					}
				}
				
				if(jsonArrayMobileUser != null && jsonArrayMobileUser.size() > 0){
					jsonObjectData.put("mobileUsers", jsonArrayMobileUser.toString());
				}else {
					jsonObjectData.put("mobileUsers", "");
				}
				
				jsonObjectResult.put("result", "1");
				jsonObjectResult.put("data", jsonObjectData.toString());
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "非法用户");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要的参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	public String createWeiXinUserFromFwhJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String publicId = "";
		if(jsonObject.containsKey("publicId")){
			publicId = jsonObject.opt("publicId").toString();
		}
		
		String openId = "";
		if(jsonObject.containsKey("openId")){
			openId = jsonObject.opt("openId").toString();
		}
		
		String mobilePhone = "";
		if(jsonObject.containsKey("mobilePhone")){
			mobilePhone = jsonObject.opt("mobilePhone").toString();
		}
		
		String password = "";
		if(jsonObject.containsKey("password")){
			password = jsonObject.opt("password").toString();
		}
		
		String displayName = "";
		if(jsonObject.containsKey("displayName")){
			displayName = jsonObject.opt("displayName").toString();
		}
		
		if(publicId != null && !publicId.equals("") && openId != null && !openId.equals("") && mobilePhone != null && !mobilePhone.equals("") && password != null && !password.equals("") && displayName != null && !displayName.equals("")){
			WeiXinUser weiXinUser = getWeiXinUserByOpenId(openId);
			
			if(weiXinUser != null){
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "用户已经存在");
			}else {
				String ownerUri = "wx:" + mobilePhone + "@" + publicId + ".com";
				String userName = mobilePhone + "@" + publicId;
				String md5Password = MD5.GetMD5Code(password);
				String companyCode = mobileServerConfig.getCompanyCode();
				
				WeiXinUser model = new WeiXinUser();
				
				String uuid = UUID.randomUUID().toString();
				
				model.setId(uuid);
				model.setExtensionId(uuid);
				model.setOwnerUri(ownerUri);
				model.setUserName(userName);
				model.setPassword(md5Password);
				model.setDisplayName(displayName);
				model.setCompanyCode(companyCode);
				model.setOrgStructure(companyCode);
				model.setOpenId(openId);
				model.setStatus("Y");
				model.setCreator(userName);
				
				model.setMobilePhone(mobilePhone);
				
				create(model);
				
				jsonObjectResult.put("result", "1");
				jsonObjectResult.put("data", "200");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要的参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
}
