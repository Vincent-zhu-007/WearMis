package com.sg.core;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;

/**
 * @author zhe.wang
 *
 * @param <T>
 * 
 * Base
 */
@SuppressWarnings("unchecked")
public abstract class AbstractGenericDao<T> extends SqlMapClientDaoSupport {
	protected Class<T> clazz;

	/**
	 * @param params
	 * @param pageParams
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<T> find(Map params, int... pageParams) {
		if ((pageParams != null) && (pageParams.length > 0)) { 
			/*如果页面参数为空*/
			int rowStart = 0;
			int pageSize = 0;
			rowStart = Math.max(0, pageParams[0]);
			if (pageParams.length > 1) {
				pageSize = Math.max(0, pageParams[1]);
			}
			return this.findForPage(params, rowStart, pageSize);
		} else {
			return this.getSqlMapClientTemplate().queryForList(
					getNameSpace() + ".find", params);
		}
	}

	/**
	 * 分页查询函数
	 * 
	 * @param pageNo页号,从1开始.
	 *            
	 * @return 含总记录数和当前页数据的Page对象.
	 */
	@SuppressWarnings("rawtypes")
	private List findForPage(Map params, int rowStart, int pageSize) {
		params.put("rowStart", rowStart);
		params.put("pageSize", pageSize);
		Assert.isTrue(rowStart >= 0, "rowStart should start from 0");
		List list = getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".find", params);
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	public List<T> findForUnPage(Map params) {
		return this.getSqlMapClientTemplate().queryForList(
				getNameSpace() + ".findunpage", params);
	}

	/**
	 * @param id
	 * @return
	 */
	public T get(Serializable id) {
		T entity = (T) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".get", id);
		return entity;
	}

	/**
	 * 通过范型反射，取得在子类中定义的class.
	 * 
	 * @return
	 */
	protected String getNameSpace() {
		clazz = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		return clazz.getSimpleName();
	}

	/**
	 * 查询总数
	 */
	@SuppressWarnings("rawtypes")
	public Long getTotalCount(Map params) {
		Long count = (Long) getSqlMapClientTemplate().queryForObject(
				getNameSpace() + ".totalCount", params);
		return count;
	}

	/**
	 * 根据ID删除对象
	 */
	public void delete(Serializable id) {
		getSqlMapClientTemplate().delete(getNameSpace() + ".delete", id);
	}

	/**
	 * 新增对象
	 */
	public T create(T entity) {
		getSqlMapClientTemplate().insert(getNameSpace() + ".create", entity);
		return entity;
	}

	/**
	 * 更新对象
	 */
	public T update(T entity) {
		getSqlMapClientTemplate().update(getNameSpace() + ".update", entity);
		return entity;
	}
}
