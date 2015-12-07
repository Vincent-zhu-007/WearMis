package com.sg.mobile.dao;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.sg.core.AbstractGenericDao;
import com.sg.mobile.entity.WebSiteRole;

@Repository
public class WebSiteRoleDao extends AbstractGenericDao<WebSiteRole> {
	@SuppressWarnings("unchecked")
	public List<WebSiteRole> findPermissionByRoles(Map<String, Object> params) {
		return (List<WebSiteRole>)getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".findpermissionbyroles", params);
	}
}
