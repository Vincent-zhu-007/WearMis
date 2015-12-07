package com.sg.mobile.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sg.mobile.dao.MobileUserSosLocationDao;
import com.sg.mobile.entity.MobileUserSosLocation;
import com.sg.service.BaseEntityManager;

@Service
public class MobileUserSosLocationService implements BaseEntityManager<MobileUserSosLocation> {
	@Resource
	private MobileUserSosLocationDao mobileUserSosLocationDao;
	
	@Override
	public void create(MobileUserSosLocation entity) {
		// TODO Auto-generated method stub
		mobileUserSosLocationDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileUserSosLocationDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileUserSosLocation> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileUserSosLocationDao.find(params, pageParams);
	}

	@Override
	public MobileUserSosLocation get(String id) {
		// TODO Auto-generated method stub
		return mobileUserSosLocationDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileUserSosLocation> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileUserSosLocationDao.findForUnPage(params);
	}

	@Override
	public void update(MobileUserSosLocation entity) {
		// TODO Auto-generated method stub
		mobileUserSosLocationDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileUserSosLocationDao.getTotalCount(params);
	}
}
