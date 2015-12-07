package com.sg.mobile.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sg.mobile.dao.MobileVersionConfigDao;
import com.sg.mobile.entity.MobileCompany;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.entity.MobileVersionConfig;
import com.sg.service.BaseEntityManager;
import com.sg.util.ListUtil;
import com.sg.util.MemCached;

@Service
public class MobileVersionConfigService implements BaseEntityManager<MobileVersionConfig> {
	@Resource
	private MobileVersionConfigDao mobileVersionConfigDao;
	
	@Autowired
	private MobileCompanyService mobileCompanyService;

	@Override
	public void create(MobileVersionConfig entity) {
		// TODO Auto-generated method stub
		mobileVersionConfigDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileVersionConfigDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileVersionConfig> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileVersionConfigDao.find(params, pageParams);
	}

	@Override
	public MobileVersionConfig get(String id) {
		// TODO Auto-generated method stub
		return mobileVersionConfigDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileVersionConfig> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileVersionConfigDao.findForUnPage(params);
	}

	@Override
	public void update(MobileVersionConfig entity) {
		// TODO Auto-generated method stub
		mobileVersionConfigDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileVersionConfigDao.getTotalCount(params);
	}
	
	@SuppressWarnings("rawtypes")
	public List<MobileVersionConfig> getMobileVersionConfigCache() {
		List<MobileVersionConfig> list = new ArrayList<MobileVersionConfig>();
		
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("MobileVersionConfig");
			if(obj != null && !obj.equals("")) {
				String json = MemCached.getInstance().get("MobileVersionConfig").toString();
				
				JSONArray array = JSONArray.fromObject(json);
			    for(Iterator iter = array.iterator(); iter.hasNext();){
			        JSONObject jsonObject = (JSONObject)iter.next();
			        list.add((MobileVersionConfig)JSONObject.toBean(jsonObject, MobileVersionConfig.class));
			    }
			}else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("status", "Y");
				list = this.findForUnPage(params);
				
				JSONArray jsonObject = JSONArray.fromObject(list);
				
				MemCached.getInstance().set("MobileVersionConfig", jsonObject);
			}
		}
		
		return list;
	}
	
	public void clearMobileVersionConfigCache() {
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("MobileVersionConfig");
			if(obj != null && !obj.equals("")) {
				MemCached.getInstance().set("MobileVersionConfig", "");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<MobileVersionConfig> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("Mobile版本列表");
			/*第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short*/
			HSSFRow row = sheet.createRow((int) 0);
			/*第四步，创建单元格，并设置值表头 设置表头居中*/
			HSSFCellStyle style = wb.createCellStyle();
			/*左对齐*/
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT); 

			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("名称");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 1);
			cell.setCellValue("文件地址");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("版本展示号");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("适用类型");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 4);
			cell.setCellValue("版本号");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 5);
			cell.setCellValue("是否强制");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 6);
			cell.setCellValue("备注");
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
				MobileVersionConfig model = (MobileVersionConfig) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getName());
				row.createCell((short) 1).setCellValue(model.getVerFileAddress());
				row.createCell((short) 2).setCellValue(model.getVerNo());
				row.createCell((short) 3).setCellValue(model.getVerCategory());
				row.createCell((short) 4).setCellValue(model.getVerSort());
				row.createCell((short) 5).setCellValue(model.getIsMandatoryUpdate());
				row.createCell((short) 6).setCellValue(model.getVerRemark());
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
	
	public String getMobileVersionConfigJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String verCategory = "";
		if(jsonObject.containsKey("verCategory")){
			verCategory = jsonObject.opt("verCategory").toString();
		}
		
		String verCode = "";
		if(jsonObject.containsKey("verCode")){
			verCode = jsonObject.opt("verCode").toString();
		}
		
		if(verCategory != null && !verCategory.equals("") && verCode != null && !verCode.equals("")){
			String companyCode = mobileServerConfig.getCompanyCode();
			
			MobileCompany mobileCompany = mobileCompanyService.getMobileCompanyByComapnyCode(companyCode);
			
			List<MobileVersionConfig> mobileVersionConfigs = getMobileVersionConfigCache();
			
			List<MobileVersionConfig> newList = new ArrayList<MobileVersionConfig>();
			
			if(mobileVersionConfigs != null && mobileVersionConfigs.size() > 0){
				for (MobileVersionConfig mobileVersionConfig : mobileVersionConfigs) {
					if(mobileVersionConfig.getVerCategory().equals(verCategory)){
						newList.add(mobileVersionConfig);
					}
				}
			}
			
			if(newList != null && newList.size() > 0){
				ListUtil<MobileVersionConfig> sortList = new ListUtil<MobileVersionConfig>();
				sortList.SortByInt(newList, "getVerSort", "desc");
				
				int verSort = Integer.parseInt(verCode);
				
				String isMandatoryUpdate = "";
				for (MobileVersionConfig mobileVersionConfig : newList) {
					if(Integer.parseInt(mobileVersionConfig.getVerSort()) > verSort){
						if(mobileVersionConfig.getIsMandatoryUpdate().equals("Y")){
							isMandatoryUpdate = "Y";
							break;
						}
					}
				}
				
				MobileVersionConfig mobileVersionConfig = newList.get(0);
				String verFileAddress = mobileVersionConfig.getVerFileAddress();
				
				String host = mobileCompany.getMobileHost();
				String port = mobileCompany.getMobilePort();
				
				verFileAddress = host + ":" + port + verFileAddress;
				mobileVersionConfig.setVerFileAddress(verFileAddress);
				
				if(isMandatoryUpdate.equals("Y")){
					mobileVersionConfig.setIsMandatoryUpdate("Y");
				}
				
				JSONObject jsonObjectMobileVersionConfig = JSONObject.fromObject(mobileVersionConfig);
				
				jsonObjectResult.put("result", "1");
				jsonObjectResult.put("data", jsonObjectMobileVersionConfig.toString());
			}
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要的信息");
		}
		
		json = jsonObjectResult.toString();
		
		
		return json;
	}
}
