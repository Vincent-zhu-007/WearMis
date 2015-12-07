package com.sg.mobile.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sg.mobile.dao.MobilePowerDao;
import com.sg.mobile.entity.MobilePower;
import com.sg.mobile.entity.MobileServerConfig;
import com.sg.mobile.entity.MobileUser;
import com.sg.service.BaseEntityManager;

@Service
public class MobilePowerService implements BaseEntityManager<MobilePower> {
	@Resource
	private MobilePowerDao mobilePowerDao;
	
	@Autowired
	private MobileUserService mobileUserService;
	
	@Override
	public void create(MobilePower entity) {
		// TODO Auto-generated method stub
		mobilePowerDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobilePowerDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobilePower> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobilePowerDao.find(params, pageParams);
	}

	@Override
	public MobilePower get(String id) {
		// TODO Auto-generated method stub
		return mobilePowerDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobilePower> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobilePowerDao.findForUnPage(params);
	}

	@Override
	public void update(MobilePower entity) {
		// TODO Auto-generated method stub
		mobilePowerDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobilePowerDao.getTotalCount(params);
	}
	
	public String createMobilePowerJsonByMin(MobileServerConfig mobileServerConfig, Document doc, String urlPara){
		String json = "";
		
		JSONObject jsonObjectResult = new JSONObject();
		
		String paraJson = urlPara;
		JSONObject jsonObject = JSONObject.fromObject(paraJson);
		
		String userName = "";
		if(jsonObject.containsKey("user")){
			userName = jsonObject.opt("user").toString();
		}
		
		String percent = "";
		if(jsonObject.containsKey("percent")){
			percent = jsonObject.opt("percent").toString();
		}
		
		String isWarning = "";
		if(jsonObject.containsKey("isWarning")){
			isWarning = jsonObject.opt("isWarning").toString();
		}
		
		if(userName != null && !userName.equals("") && percent != null && !percent.equals("")){
			MobileUser mobileUser = mobileUserService.getMobileUserByUserName(userName);
			
			if(mobileUser != null){
				String ownerUri = mobileUser.getOwnerUri();
				
				MobilePower mobilePower = new MobilePower();
				String id = UUID.randomUUID().toString();
				mobilePower.setId(id);
				mobilePower.setOwnerUri(ownerUri);
				mobilePower.setPercent(percent);
				mobilePower.setCreator(ownerUri);
				mobilePower.setStatus("Y");
				
				create(mobilePower);
				
				if(isWarning != null && !isWarning.equals("") && isWarning.equals("Y")){
					
				}
				
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
