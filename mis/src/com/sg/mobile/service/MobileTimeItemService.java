package com.sg.mobile.service;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.sg.mobile.dao.MobileTimeItemDao;
import com.sg.mobile.entity.MobileTimeItem;
import com.sg.service.BaseEntityManager;

@Service
public class MobileTimeItemService implements BaseEntityManager<MobileTimeItem> {
	@Resource
	private MobileTimeItemDao mobileTimeItemDao;

	@Override
	public void create(MobileTimeItem entity) {
		// TODO Auto-generated method stub
		mobileTimeItemDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileTimeItemDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileTimeItem> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileTimeItemDao.find(params, pageParams);
	}

	@Override
	public MobileTimeItem get(String id) {
		// TODO Auto-generated method stub
		return mobileTimeItemDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileTimeItem> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileTimeItemDao.findForUnPage(params);
	}

	@Override
	public void update(MobileTimeItem entity) {
		// TODO Auto-generated method stub
		mobileTimeItemDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileTimeItemDao.getTotalCount(params);
	}
}
