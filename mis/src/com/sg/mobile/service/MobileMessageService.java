package com.sg.mobile.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sg.mobile.dao.MobileMessageDao;
import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileMessage;
import com.sg.mobile.entity.MobileRpcParamenter;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.entity.MobileUser;
import com.sg.service.BaseEntityManager;

@Service
public class MobileMessageService implements BaseEntityManager<MobileMessage> {
	@Resource
	private MobileMessageDao mobileMessageDao;
	
	@Autowired
	private MobileUserService mobileUserService;
	
	@Autowired
	private MobileCompanyService mobileCompanyService;
	
	@Autowired
	private MobileSyncDataService mobileSyncDataService;

	@Override
	public void create(MobileMessage entity) {
		// TODO Auto-generated method stub
		mobileMessageDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileMessageDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileMessage> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileMessageDao.find(params, pageParams);
	}

	@Override
	public MobileMessage get(String id) {
		// TODO Auto-generated method stub
		return mobileMessageDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileMessage> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileMessageDao.findForUnPage(params);
	}

	@Override
	public void update(MobileMessage entity) {
		// TODO Auto-generated method stub
		mobileMessageDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileMessageDao.getTotalCount(params);
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<MobileMessage> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("终端消息列表");
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
			cell.setCellValue("内容");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("处理状态");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 4);
			cell.setCellValue("创建日期");
			cell.setCellStyle(style);

			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				MobileMessage model = (MobileMessage) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getOwnerUri());
				row.createCell((short) 1).setCellValue(model.getContent());
				row.createCell((short) 2).setCellValue(model.getProcessStatus());
				row.createCell((short) 3).setCellValue(model.getStatus());
				cell = row.createCell((short) 4);
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
	
	public String createMobileMessageJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String ownerUri = "";
		if(jsonObject.containsKey("ownerUri")){
			ownerUri = jsonObject.opt("ownerUri").toString();
		}
		
		String targetUris = "";
		if(jsonObject.containsKey("targetUris")){
			targetUris = jsonObject.opt("targetUris").toString();
		}
		
		String content = "";
		if(jsonObject.containsKey("content")){
			content = jsonObject.opt("content").toString();
		}
		
		if(ownerUri != null && !ownerUri.equals("") && targetUris != null && !targetUris.equals("") && content != null && !content.equals("")){
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
			
			if(mobileUser != null){
				try {
					MobileMessage mobileMessage = new MobileMessage();
					
					mobileMessage.setId(UUID.randomUUID().toString());
					mobileMessage.setOwnerUri(ownerUri);
					mobileMessage.setTargetUris(targetUris);
					mobileMessage.setContent(content);
					mobileMessage.setProcessStatus("UnProcess");
					mobileMessage.setStatus("Y");
					mobileMessage.setCreator(ownerUri);
					
					create(mobileMessage);
					
					jsonObjectResult.put("result", "1");
					jsonObjectResult.put("data", "200");
				} catch (Exception e) {
					jsonObjectResult.put("result", "0");
					jsonObjectResult.put("message", "创建消息失败");
				}
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "用户不存在");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	public String editMobileMessageJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String ownerUri = "";
		if(jsonObject.containsKey("ownerUri")){
			ownerUri = jsonObject.opt("ownerUri").toString();
		}
		
		String id = "";
		if(jsonObject.containsKey("id")){
			id = jsonObject.opt("id").toString();
		}
		
		String targetUris = "";
		if(jsonObject.containsKey("targetUris")){
			targetUris = jsonObject.opt("targetUris").toString();
		}
		
		String content = "";
		if(jsonObject.containsKey("content")){
			content = jsonObject.opt("content").toString();
		}
		
		if(ownerUri != null && !ownerUri.equals("") && targetUris != null && !targetUris.equals("") && content != null && !content.equals("")){
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
			
			if(mobileUser != null){
				try {
					MobileMessage mobileMessage = get(id);
					
					if(mobileMessage != null){
						
						
						mobileMessage.setTargetUris(targetUris);
						mobileMessage.setContent(content);
						mobileMessage.setUpdator(ownerUri);
						
						update(mobileMessage);
						
						jsonObjectResult.put("result", "1");
						jsonObjectResult.put("data", "200");
					}else {
						jsonObjectResult.put("result", "0");
						jsonObjectResult.put("message", "消息不存在");
					}
				} catch (Exception e) {
					jsonObjectResult.put("result", "0");
					jsonObjectResult.put("message", "编辑消息失败");
				}
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "用户不存在");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	public String deleteMobileMessageJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String ownerUri = "";
		if(jsonObject.containsKey("ownerUri")){
			ownerUri = jsonObject.opt("ownerUri").toString();
		}
		
		String id = "";
		if(jsonObject.containsKey("id")){
			id = jsonObject.opt("id").toString();
		}
		
		if(ownerUri != null && !ownerUri.equals("") && id != null && !id.equals("")){
			MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
			
			if(mobileUser != null){
				try {
					MobileMessage mobileMessage = get(id);
					
					if(mobileMessage != null){
						delete(id);
						
						jsonObjectResult.put("result", "1");
						jsonObjectResult.put("data", "200");
					}else {
						jsonObjectResult.put("result", "0");
						jsonObjectResult.put("message", "消息不存在");
					}
				} catch (Exception e) {
					jsonObjectResult.put("result", "0");
					jsonObjectResult.put("message", "编辑消息失败");
				}
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "用户不存在");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	public String sendMobileMessageJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String id = "";
		if(jsonObject.containsKey("id")){
			id = jsonObject.opt("id").toString();
		}
		
		if(id != null && !id.equals("")){
			MobileMessage mobileMessage = get(id);
			
			if(mobileMessage != null){
				String ownerUri = mobileMessage.getOwnerUri();
				
				MobileUser mobileUser = mobileUserService.getMobileUserByOwnerUri(ownerUri);
				
				if(mobileUser != null){
					try {
						String targetUris = mobileMessage.getTargetUris();
						String[] array = targetUris.split(",");
						
						String content = mobileMessage.getContent();
						
						MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByComapnyCode(mobileUser.getCompanyCode());
						
						for (String targetUri : array) {
							List<MobileRpcParamenter> mobileRpcParamenters = new ArrayList<MobileRpcParamenter>();
							MobileRpcParamenter mobileRpcParamenter1 = new MobileRpcParamenter();
							
							mobileRpcParamenter1.setParaName("TargetUri");
							mobileRpcParamenter1.setParaType("String");
							mobileRpcParamenter1.setParaValue(targetUri);
							mobileRpcParamenter1.setParaSort(1);
							mobileRpcParamenters.add(mobileRpcParamenter1);
							
							MobileRpcParamenter mobileRpcParamenter2 = new MobileRpcParamenter();
							mobileRpcParamenter2.setParaName("Content");
							mobileRpcParamenter2.setParaType("String");
							mobileRpcParamenter2.setParaValue(content);
							mobileRpcParamenter2.setParaSort(1);
							mobileRpcParamenters.add(mobileRpcParamenter2);
							
							mobileSyncDataService.SyncData("SendMobileMessage", mobileRpcParamenters, mobileCompany);
						}
						
						jsonObjectResult.put("result", "1");
						jsonObjectResult.put("data", "200");
					} catch (Exception e) {
						jsonObjectResult.put("result", "0");
						jsonObjectResult.put("message", "发送消息异常");
					}
				}else {
					jsonObjectResult.put("result", "0");
					jsonObjectResult.put("message", "用户不存在");
				}
			}else {
				jsonObjectResult.put("result", "0");
				jsonObjectResult.put("message", "消息不存在");
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
}
