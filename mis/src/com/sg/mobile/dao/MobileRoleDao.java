package com.sg.mobile.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sg.core.AbstractGenericDao;
import com.sg.mobile.entity.MobileRole;

@Repository
public class MobileRoleDao extends AbstractGenericDao<MobileRole> {
	@SuppressWarnings("unchecked")
	public List<MobileRole> findPermissionByRoles(Map<String, Object> params) {
		return (List<MobileRole>)getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".findpermissionbyroles", params);
	}
}
