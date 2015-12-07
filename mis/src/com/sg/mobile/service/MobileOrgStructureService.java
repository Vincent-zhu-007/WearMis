package com.sg.mobile.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sg.dao.CodeDao;
import com.sg.mobile.dao.MobileOrgStructureDao;
import com.sg.mobile.entity.MobileOrgStructure;
import com.sg.service.BaseEntityManager;
import com.sg.util.MemCached;

@Service
public class MobileOrgStructureService implements BaseEntityManager<MobileOrgStructure> {
	@Resource
	private MobileOrgStructureDao mobileOrgStructureDao;
	
	@Resource
	private CodeDao codeDao;

	@Override
	@Transactional
	public void create(MobileOrgStructure entity) {
		// TODO Auto-generated method stub
		mobileOrgStructureDao.create(entity);
		codeDao.create(entity);
	}

	@Override
	@Transactional
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileOrgStructureDao.delete(id);
		codeDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileOrgStructure> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileOrgStructureDao.find(params, pageParams);
	}

	@Override
	public MobileOrgStructure get(String id) {
		// TODO Auto-generated method stub
		return mobileOrgStructureDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileOrgStructure> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileOrgStructureDao.findForUnPage(params);
	}

	@Override
	@Transactional
	public void update(MobileOrgStructure entity) {
		// TODO Auto-generated method stub
		mobileOrgStructureDao.update(entity);
		codeDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileOrgStructureDao.getTotalCount(params);
	}
	
	@SuppressWarnings("rawtypes")
	public List<MobileOrgStructure> getMobileOrgStructureCache() {
		List<MobileOrgStructure> list = new ArrayList<MobileOrgStructure>();
		
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("MobileOrgStructure");
			if(obj != null && !obj.equals("")) {
				String json = MemCached.getInstance().get("MobileOrgStructure").toString();
				
				JSONArray array = JSONArray.fromObject(json);
			    for(Iterator iter = array.iterator(); iter.hasNext();){
			        JSONObject jsonObject = (JSONObject)iter.next();
			        list.add((MobileOrgStructure)JSONObject.toBean(jsonObject, MobileOrgStructure.class));
			    }
			}else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("status", "Y");
				list = this.findForUnPage(params);
				
				JSONArray jsonObject = JSONArray.fromObject(list);
				
				MemCached.getInstance().set("MobileOrgStructure", jsonObject);
			}
		}
		
		return list;
	}
	
	public void clearMobileOrgStructureCache() {
		if(MemCached.used()){
			Object obj = MemCached.getInstance().get("MobileOrgStructure");
			if(obj != null && !obj.equals("")) {
				MemCached.getInstance().set("MobileOrgStructure", "");
			}
		}
	}
	
	public JSONArray buildMobileOrgStructureTree(List<MobileOrgStructure> mobileOrgStructures, List<MobileOrgStructure> allMobileOrgStructures) {
		JSONArray jSONArray = new JSONArray();
		
		if (mobileOrgStructures != null && mobileOrgStructures.size() > 0) {
			for (int i = 0; i < mobileOrgStructures.size(); i++) {
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", mobileOrgStructures.get(i).getCodeName());
				jsonObject1.put("text", mobileOrgStructures.get(i).getDescription());
				
				if(mobileOrgStructures.get(i).getUpdateTime() == null){
					mobileOrgStructures.get(i).setUpdateTime(new Date());
				}
				
				if(mobileOrgStructures.get(i).getUpdator() == null){
					mobileOrgStructures.get(i).setUpdator("");
				}
				
				JSONObject jsonObject = JSONObject.fromObject(mobileOrgStructures.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				List<MobileOrgStructure> childMobileOrgStructures = new ArrayList<MobileOrgStructure>();
				for (MobileOrgStructure entity : allMobileOrgStructures) {
					if(entity.getParentCode().equals(mobileOrgStructures.get(i).getCodeName())) {
						childMobileOrgStructures.add(entity);
					}
				}
				
				if (childMobileOrgStructures != null && childMobileOrgStructures.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildMobileOrgStructureTree(childMobileOrgStructures, allMobileOrgStructures));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		return jSONArray;
	}
	
	public void getChildMobileOrgStructure(MobileOrgStructure mobileOrgStructure, List<MobileOrgStructure> mobileOrgStructures,List<MobileOrgStructure> allMobileOrgStructures) {
		
		if (mobileOrgStructure != null) {
			List<MobileOrgStructure> childMobileOrgStructures = new ArrayList<MobileOrgStructure>();
			
			for (MobileOrgStructure entity : allMobileOrgStructures) {
				if(entity.getParentCode() != null && !entity.getParentCode().equals("") && entity.getParentCode().equals(mobileOrgStructure.getCodeName())) {
					mobileOrgStructures.add(entity);
					childMobileOrgStructures.add(entity);
				}
			}
			
			if (childMobileOrgStructures != null && childMobileOrgStructures.size() > 0) {
				for (MobileOrgStructure childMobileOrgStructure : childMobileOrgStructures) {
					getChildMobileOrgStructure(childMobileOrgStructure, mobileOrgStructures, allMobileOrgStructures);
				}
			}
		}
	}
	
	public JSONArray buildCreateMobileUserOrgStructureTree(List<MobileOrgStructure> mobileOrgStructures, List<MobileOrgStructure> allMobileOrgStructures) {
		JSONArray jSONArray = new JSONArray();
		
		if (mobileOrgStructures != null && mobileOrgStructures.size() > 0) {
			for (int i = 0; i < mobileOrgStructures.size(); i++) {
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", mobileOrgStructures.get(i).getCodeName());
				jsonObject1.put("text", mobileOrgStructures.get(i).getDescription());
				
				if(mobileOrgStructures.get(i).getUpdateTime() == null){
					mobileOrgStructures.get(i).setUpdateTime(new Date());
				}
				
				if(mobileOrgStructures.get(i).getUpdator() == null){
					mobileOrgStructures.get(i).setUpdator("");
				}
				
				JSONObject jsonObject = JSONObject.fromObject(mobileOrgStructures.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				List<MobileOrgStructure> childMobileOrgStructures = new ArrayList<MobileOrgStructure>();
				for (MobileOrgStructure entity : allMobileOrgStructures) {
					if(entity.getParentCode().equals(mobileOrgStructures.get(i).getCodeName())) {
						childMobileOrgStructures.add(entity);
					}
				}
				
				if (childMobileOrgStructures != null && childMobileOrgStructures.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildCreateMobileUserOrgStructureTree(childMobileOrgStructures, allMobileOrgStructures));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		return jSONArray;
	}
	
	public JSONArray buildEditMobileUserOrgStructureTree(List<MobileOrgStructure> mobileOrgStructures, List<MobileOrgStructure> allMobileOrgStructures) {
		JSONArray jSONArray = new JSONArray();
		
		if (mobileOrgStructures != null && mobileOrgStructures.size() > 0) {
			for (int i = 0; i < mobileOrgStructures.size(); i++) {
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", mobileOrgStructures.get(i).getCodeName());
				jsonObject1.put("text", mobileOrgStructures.get(i).getDescription());
				
				if(mobileOrgStructures.get(i).getUpdateTime() == null){
					mobileOrgStructures.get(i).setUpdateTime(new Date());
				}
				
				if(mobileOrgStructures.get(i).getUpdator() == null){
					mobileOrgStructures.get(i).setUpdator("");
				}
				
				JSONObject jsonObject = JSONObject.fromObject(mobileOrgStructures.get(i));
				jsonObject1.put("attributes", jsonObject);
				
				List<MobileOrgStructure> childMobileOrgStructures = new ArrayList<MobileOrgStructure>();
				for (MobileOrgStructure entity : allMobileOrgStructures) {
					if(entity.getParentCode().equals(mobileOrgStructures.get(i).getCodeName())) {
						childMobileOrgStructures.add(entity);
					}
				}
				
				if (childMobileOrgStructures != null && childMobileOrgStructures.size() > 0) {
					jsonObject1.put("state", "open");
					jsonObject1.put("children", buildEditMobileUserOrgStructureTree(childMobileOrgStructures, allMobileOrgStructures));
				}
				
				jSONArray.add(jsonObject1);
			}
		}
		
		return jSONArray;
	}
}
