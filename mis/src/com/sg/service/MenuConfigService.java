package com.sg.service;

import java.io.FileOutputStream;
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

import com.sg.dao.MenuConfigDao;
import com.sg.entity.MenuConfig;
import com.sg.util.MemCached;

@Service
public class MenuConfigService implements BaseEntityManager<MenuConfig> {
	@Resource
	private MenuConfigDao menuConfigDao;

	@Override
	public void create(MenuConfig entity) {
		// TODO Auto-generated method stub
		menuConfigDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		menuConfigDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MenuConfig> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return menuConfigDao.find(params, pageParams);
	}

	@Override
	public MenuConfig get(String id) {
		// TODO Auto-generated method stub
		return menuConfigDao.get(id);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<MenuConfig> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return menuConfigDao.findForUnPage(params);
	}

	@Override
	public void update(MenuConfig entity) {
		// TODO Auto-generated method stub
		menuConfigDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return menuConfigDao.getTotalCount(params);
	}
	
	@SuppressWarnings("rawtypes")
	public List<MenuConfig> GetMenuConfigCache() {
		List<MenuConfig> list = new ArrayList<MenuConfig>();
		
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("MenuConfig");
			if(obj != null && !obj.equals("")) {
				String json = MemCached.getInstance().get("MenuConfig").toString();
				
				JSONArray array = JSONArray.fromObject(json);
			    for(Iterator iter = array.iterator(); iter.hasNext();){
			        JSONObject jsonObject = (JSONObject)iter.next();
			        list.add((MenuConfig)JSONObject.toBean(jsonObject, MenuConfig.class));
			    }
			}else {
				Map<String, Object> params = new HashMap<String, Object>();
		    	params.put("status", "Y");
				list = this.findForUnPage(params);
				
				JSONArray jsonObject = JSONArray.fromObject(list);
				
				MemCached.getInstance().set("MenuConfig", jsonObject);
			}
		}
		
		return list;
	}
	
	public void ClearMenuConfigCache() {
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("MenuConfig");
			if(obj != null && !obj.equals("")) {
				MemCached.getInstance().set("MenuConfig", "");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public String ExpExcel(List<MenuConfig> list, String path) {
		String filePath = "";
		
		if(list.size() > 0) {
			/*第一步，创建一个webbook，对应一个Excel文件*/
			HSSFWorkbook wb = new HSSFWorkbook();
			/*第二步，在webbook中添加一个sheet,对应Excel文件中的sheet*/
			HSSFSheet sheet = wb.createSheet("用户列表");
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
			cell.setCellValue("地址");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 3);
			cell.setCellValue("是否包含子菜单");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 4);
			cell.setCellValue("顺序");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 5);
			cell.setCellValue("类型");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 6);
			cell.setCellValue("父菜单编码");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 7);
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			
			/*第五步，写入实体数据 实际应用中这些数据从数据库得到，*/
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				MenuConfig model = (MenuConfig) list.get(i);
				/*第四步，创建单元格，并设置值*/
				row.createCell((short) 0).setCellValue(model.getCode());
				row.createCell((short) 1).setCellValue(model.getDescription());
				row.createCell((short) 2).setCellValue(model.getUrl());
				row.createCell((short) 3).setCellValue(model.getHasChildren());
				row.createCell((short) 4).setCellValue(model.getDisplaySort());
				row.createCell((short) 5).setCellValue(model.getLevelCode());
				row.createCell((short) 6).setCellValue(model.getParentCode());
				row.createCell((short) 7).setCellValue(model.getStatus());
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
