package com.sg.mobile.dao;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.sg.core.AbstractGenericDao;
import com.sg.mobile.entity.MobileUser;

@Repository
public class MobileUserDao extends AbstractGenericDao<MobileUser> {
	@SuppressWarnings("unchecked")
	public List<MobileUser> findUnJoin(Map<String, Object> params) {
		return (List<MobileUser>)getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".findunjoin", params);
	}
	
	@SuppressWarnings("unchecked")
	public List<MobileUser> findUnPageIn(Map<String, Object> params) {
		return (List<MobileUser>)getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".findunpagein", params);
	}
	
	@SuppressWarnings("unchecked")
	public List<MobileUser> findUnPageByCheckDisplayName(Map<String, Object> params) {
		return (List<MobileUser>)getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".findunpagebycheckdisplayname", params);
	}
}
