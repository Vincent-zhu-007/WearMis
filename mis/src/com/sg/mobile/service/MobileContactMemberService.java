package com.sg.mobile.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sg.mobile.dao.MobileContactMemberDao;
import com.sg.mobile.entity.MobileContactMember;
import com.sg.service.BaseEntityManager;

@Service
public class MobileContactMemberService implements BaseEntityManager<MobileContactMember> {
	@Resource
	private MobileContactMemberDao mobileContactMemberDao;

	@Override
	public void create(MobileContactMember entity) {
		// TODO Auto-generated method stub
		mobileContactMemberDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		mobileContactMemberDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileContactMember> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return mobileContactMemberDao.find(params, pageParams);
	}

	@Override
	public MobileContactMember get(String id) {
		// TODO Auto-generated method stub
		return mobileContactMemberDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MobileContactMember> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return mobileContactMemberDao.findForUnPage(params);
	}

	@Override
	public void update(MobileContactMember entity) {
		// TODO Auto-generated method stub
		mobileContactMemberDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return mobileContactMemberDao.getTotalCount(params);
	}
}
