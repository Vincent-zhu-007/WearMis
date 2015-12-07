package com.sg.service;

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
import com.sg.dao.CityDao;
import com.sg.entity.City;
import com.sg.util.MemCached;

@Service
public class CityService implements BaseEntityManager<City> {
	@Resource
	private CityDao cityDao;

	@Override
	public void create(City entity) {
		// TODO Auto-generated method stub
		cityDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		cityDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<City> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return cityDao.find(params, pageParams);
	}

	@Override
	public City get(String id) {
		// TODO Auto-generated method stub
		return cityDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<City> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return cityDao.findForUnPage(params);
	}

	@Override
	public void update(City entity) {
		// TODO Auto-generated method stub
		cityDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return cityDao.getTotalCount(params);
	}
	
	@SuppressWarnings("rawtypes")
	public List<City> getCityCache() {
		List<City> list = new ArrayList<City>();
		
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("City");
			if(obj != null && !obj.equals("")) {
				String json = MemCached.getInstance().get("City").toString();
				
				JSONArray array = JSONArray.fromObject(json);
			    for(Iterator iter = array.iterator(); iter.hasNext();){
			        JSONObject jsonObject = (JSONObject)iter.next();
			        list.add((City)JSONObject.toBean(jsonObject, City.class));
			    }
			}else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("status", "Y");
				list = this.findForUnPage(params);
				
				JSONArray jsonObject = JSONArray.fromObject(list);
				
				MemCached.getInstance().set("City", jsonObject);
			}
		}
		
		return list;
	}
	
	public void clearCityCache() {
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("City");
			if(obj != null && !obj.equals("")) {
				MemCached.getInstance().set("City", "");
			}
		}
	}
	
	public List<Map<String, Object>> getCityCacheListMapByProvinceCodeName(String provinceCodeName) {
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		List<City> list = getCityCache();
		for (City entity : list) {
			if(entity.getProvinceCodeName().equals(provinceCodeName)){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", entity.getCodeName());
				map.put("text", entity.getDescription());
				maps.add(map);
			}
		}
		return maps;
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<City> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("市级列表");
			/*第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short*/
			HSSFRow row = sheet.createRow((int) 0);
			/*第四步，创建单元格，并设置值表头 设置表头居中*/
			HSSFCellStyle style = wb.createCellStyle();
			/*左对齐*/
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT); 

			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("编码名称");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 1);
			cell.setCellValue("描述");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 2);
			cell.setCellValue("省级编码");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("顺序");
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
				City model = (City) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getCodeName());
				row.createCell((short) 1).setCellValue(model.getDescription());
				row.createCell((short) 2).setCellValue(model.getProvinceCodeName());
				row.createCell((short) 3).setCellValue(model.getDisplaySort());
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
