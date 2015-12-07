package com.sg.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sg.dao.UserInRoleDao;
import com.sg.entity.UserInRole;

@Service
public class UserInRoleService implements BaseEntityManager<UserInRole> {
	@Resource
	private UserInRoleDao userInRoleDao;

	@Override
	public void create(UserInRole entity) {
		// TODO Auto-generated method stub
		userInRoleDao.create(entity);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		userInRoleDao.delete(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<UserInRole> findForPage(Map params, int... pageParams) {
		// TODO Auto-generated method stub
		return userInRoleDao.find(params, pageParams);
	}

	@Override
	public UserInRole get(String id) {
		// TODO Auto-generated method stub
		return userInRoleDao.get(id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<UserInRole> findForUnPage(Map params) {
		// TODO Auto-generated method stub
		return userInRoleDao.findForUnPage(params);
	}

	@Override
	public void update(UserInRole entity) {
		// TODO Auto-generated method stub
		userInRoleDao.update(entity);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Long getTotalCount(Map params) {
		// TODO Auto-generated method stub
		return userInRoleDao.getTotalCount(params);
	}
	
}
