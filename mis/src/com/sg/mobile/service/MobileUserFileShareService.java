package com.sg.mobile.service;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.sg.mobile.dao.MobileUserFileShareDao;
import com.sg.mobile.entity.MobileUserFileShare;
import com.sg.service.BaseEntityManager;

@Service
public class MobileUserFileShareService implements BaseEntityManager<MobileUserFileShare> {
	@Resource
	private MobileUserFileShareDao mobileUserFileShareDao;

	@Override
	public void create(MobileUserFileShare entity) {
		// TODO Auto-generated method stub
		mobileUserFileShareDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileUserFileShareDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileUserFileShare> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileUserFileShareDao.find(params, pageParams);
	}

	@Override
	public MobileUserFileShare get(String id) {
		// TODO Auto-generated method stub
		return mobileUserFileShareDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileUserFileShare> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileUserFileShareDao.findForUnPage(params);
	}

	@Override
	public void update(MobileUserFileShare entity) {
		// TODO Auto-generated method stub
		mobileUserFileShareDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileUserFileShareDao.getTotalCount(params);
	}
}
