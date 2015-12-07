package com.sg.mobile.service;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.sg.mobile.dao.MobileAppItemDao;
import com.sg.mobile.entity.MobileAppItem;
import com.sg.service.BaseEntityManager;

@Service
public class MobileAppItemService implements BaseEntityManager<MobileAppItem> {
	@Resource
	private MobileAppItemDao mobileAppItemDao;

	@Override
	public void create(MobileAppItem entity) {
		// TODO Auto-generated method stub
		mobileAppItemDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileAppItemDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileAppItem> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileAppItemDao.find(params, pageParams);
	}

	@Override
	public MobileAppItem get(String id) {
		// TODO Auto-generated method stub
		return mobileAppItemDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileAppItem> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileAppItemDao.findForUnPage(params);
	}

	@Override
	public void update(MobileAppItem entity) {
		// TODO Auto-generated method stub
		mobileAppItemDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileAppItemDao.getTotalCount(params);
	}
}
