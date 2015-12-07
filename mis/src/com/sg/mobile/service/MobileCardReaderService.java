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
import org.springframework.stereotype.Service;
import com.sg.mobile.dao.MobileCardReaderDao;
import com.sg.mobile.entity.MobileCardReader;
import com.sg.service.BaseEntityManager;
import com.sg.util.MemCached;

@Service
public class MobileCardReaderService implements BaseEntityManager<MobileCardReader> {
	@Resource
	private MobileCardReaderDao mobileCardReaderDao;

	@Override
	public void create(MobileCardReader entity) {
		// TODO Auto-generated method stub
		mobileCardReaderDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileCardReaderDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileCardReader> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileCardReaderDao.find(params, pageParams);
	}

	@Override
	public MobileCardReader get(String id) {
		// TODO Auto-generated method stub
		return mobileCardReaderDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileCardReader> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileCardReaderDao.findForUnPage(params);
	}

	@Override
	public void update(MobileCardReader entity) {
		// TODO Auto-generated method stub
		mobileCardReaderDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileCardReaderDao.getTotalCount(params);
	}
	
	@SuppressWarnings("rawtypes")
	public List<MobileCardReader> getMobileCardReaderCache() {
		List<MobileCardReader> list = new ArrayList<MobileCardReader>();
		
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("MobileCardReader");
			if(obj != null && !obj.equals("")) {
				String json = MemCached.getInstance().get("MobileCardReader").toString();
				
				JSONArray array = JSONArray.fromObject(json);
			    for(Iterator iter = array.iterator(); iter.hasNext();){
			        JSONObject jsonObject = (JSONObject)iter.next();
			        list.add((MobileCardReader)JSONObject.toBean(jsonObject, MobileCardReader.class));
			    }
			}else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("status", "Y");
				list = this.findForUnPage(params);
				
				JSONArray jsonObject = JSONArray.fromObject(list);
				
				MemCached.getInstance().set("MobileCardReader", jsonObject);
			}
		}
		
		return list;
	}
	
	public void clearMobileCardReaderCache() {
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("MobileCardReader");
			if(obj != null && !obj.equals("")) {
				MemCached.getInstance().set("MobileCardReader", "");
			}
		}
	}
	
	public MobileCardReader getMobileCardReaderByEquipmentNoInCache(String equipmentNo){
		List<MobileCardReader> mobileCardReaders = getMobileCardReaderCache();
		
		for (MobileCardReader mobileCardReader : mobileCardReaders) {
			if(mobileCardReader.getEquipmentNo().equals(equipmentNo)){
				return mobileCardReader;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<MobileCardReader> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("终端读卡器列表");
			/*第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short*/
			HSSFRow row = sheet.createRow((int) 0);
			/*第四步，创建单元格，并设置值表头 设置表头居中*/
			HSSFCellStyle style = wb.createCellStyle();
			/*左对齐*/
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT); 

			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("设备号");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 1);
			cell.setCellValue("描述");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("类型");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("区域编码");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 4);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 5);
			cell.setCellValue("创建日期");
			cell.setCellStyle(style);

			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				MobileCardReader model = (MobileCardReader) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getEquipmentNo());
				row.createCell((short) 1).setCellValue(model.getDescription());
				row.createCell((short) 2).setCellValue(model.getCategoryCode());
				row.createCell((short) 3).setCellValue(model.getAreaCode());
				row.createCell((short) 4).setCellValue(model.getStatus());
				cell = row.createCell((short) 5);
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
}
