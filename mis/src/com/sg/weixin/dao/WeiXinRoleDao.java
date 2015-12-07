package com.sg.weixin.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sg.core.AbstractGenericDao;
import com.sg.weixin.entity.WeiXinRole;

@Repository
public class WeiXinRoleDao extends AbstractGenericDao<WeiXinRole> {
	@SuppressWarnings("unchecked")
	public List<WeiXinRole> findPermissionByRoles(Map<String, Object> params) {
		return (List<WeiXinRole>)getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".findpermissionbyroles", params);
	}
}
