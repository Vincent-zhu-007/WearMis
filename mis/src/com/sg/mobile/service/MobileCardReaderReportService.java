package com.sg.mobile.service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.sg.mobile.dao.MobileCardReaderReportDao;
import com.sg.mobile.entity.MobileCardReaderReport;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.entity.MobileUser;
import com.sg.service.BaseEntityManager;

@Service
public class MobileCardReaderReportService implements BaseEntityManager<MobileCardReaderReport> {
	@Resource
	private MobileCardReaderReportDao mobileCardReaderReportDao;
	
	@Autowired
	private MobileUserService mobileUserService;

	@Override
	public void create(MobileCardReaderReport entity) {
		// TODO Auto-generated method stub
		mobileCardReaderReportDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileCardReaderReportDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileCardReaderReport> findForPage(Map params,
			int... pageParams) {
		// TODO Auto-generated method stub
		return mobileCardReaderReportDao.find(params, pageParams);
	}

	@Override
	public MobileCardReaderReport get(String id) {
		// TODO Auto-generated method stub
		return mobileCardReaderReportDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileCardReaderReport> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileCardReaderReportDao.findForUnPage(params);
	}

	@Override
	public void update(MobileCardReaderReport entity) {
		// TODO Auto-generated method stub
		mobileCardReaderReportDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileCardReaderReportDao.getTotalCount(params);
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<MobileCardReaderReport> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("终端读卡器上报列表");
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
			cell.setCellValue("设备号");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("创建日期");
			cell.setCellStyle(style);

			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				MobileCardReaderReport model = (MobileCardReaderReport) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getOwnerUri());
				row.createCell((short) 1).setCellValue(model.getEquipmentNo());
				row.createCell((short) 2).setCellValue(model.getStatus());
				cell = row.createCell((short) 3);
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
	
	@SuppressWarnings("deprecation")
	public String createMobileCardReaderReportJson(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String ownerUri = "";
		if(jsonObject.containsKey("ownerUri")){
			ownerUri = jsonObject.opt("ownerUri").toString();
		}
		
		String equipmentNo = "";
		if(jsonObject.containsKey("equipmentNo")){
			equipmentNo = jsonObject.opt("equipmentNo").toString();
		}
		
		if(!ownerUri.equals("") && !equipmentNo.equals("")){
			Date now = new Date();
			String month = String.valueOf(now.getMonth());
			String day = String.valueOf(now.getDay());
			String hour = String.valueOf(now.getHours());
			
			MobileCardReaderReport mobileCardReaderReport = new MobileCardReaderReport();
			String id = UUID.randomUUID().toString();
			mobileCardReaderReport.setId(id);
			mobileCardReaderReport.setOwnerUri(ownerUri);
			mobileCardReaderReport.setEquipmentNo(equipmentNo);
			mobileCardReaderReport.setMonth(month);
			mobileCardReaderReport.setDay(day);
			mobileCardReaderReport.setHour(hour);
			mobileCardReaderReport.setStatus("Y");
			mobileCardReaderReport.setCreateTime(now);
			mobileCardReaderReport.setCreator(ownerUri);
			
			create(mobileCardReaderReport);
			
			jsonObjectResult.put("result", "1");
			jsonObjectResult.put("data", "200");			
		}else {
			jsonObjectResult.put("result", "0");
			jsonObjectResult.put("message", "缺少必要的参数");
		}
		
		json = jsonObjectResult.toString();
		
		return json;
	}
	
	@SuppressWarnings("deprecation")
	public String createMobileCardReaderReportJsonByMin(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String userName = "";
		if(jsonObject.containsKey("user")){
			userName = jsonObject.opt("user").toString();
		}
		
		String equipmentNo = "";
		if(jsonObject.containsKey("equipmentNo")){
			equipmentNo = jsonObject.opt("equipmentNo").toString();
		}
		
		if(userName != null && !userName.equals("") && equipmentNo != null && !equipmentNo.equals("")){
			MobileUser mobileUser = mobileUserService.getMobileUserByUserName(userName);
			
			if(mobileUser != null){
				String ownerUri = mobileUser.getOwnerUri();
				
				Date now = new Date();
				String month = String.valueOf(now.getMonth());
				String day = String.valueOf(now.getDay());
				String hour = String.valueOf(now.getHours());
				
				MobileCardReaderReport mobileCardReaderReport = new MobileCardReaderReport();
				String id = UUID.randomUUID().toString();
				mobileCardReaderReport.setId(id);
				mobileCardReaderReport.setOwnerUri(ownerUri);
				mobileCardReaderReport.setEquipmentNo(equipmentNo);
				mobileCardReaderReport.setMonth(month);
				mobileCardReaderReport.setDay(day);
				mobileCardReaderReport.setHour(hour);
				mobileCardReaderReport.setStatus("Y");
				mobileCardReaderReport.setCreateTime(now);
				mobileCardReaderReport.setCreator(ownerUri);
				
				create(mobileCardReaderReport);
				
				jsonObjectResult.put("result", "1");
				jsonObjectResult.put("data", "200");
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
}
