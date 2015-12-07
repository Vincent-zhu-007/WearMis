package com.sg.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.sg.core.AbstractGenericDao;
import com.sg.entity.Role;

@Repository
public class RoleDao extends AbstractGenericDao<Role> {
	@SuppressWarnings("unchecked")
	public List<Role> findPermissionByRoles(Map<String, Object> params) {
		return (List<Role>)getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".findpermissionbyroles", params);
	}
}
