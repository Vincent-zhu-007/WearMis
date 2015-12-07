package com.sg.service;

import java.util.List;
import java.util.Map;

public interface BaseEntityManager<T> {
	
	void create(T entity);

	void delete(String id);

    @SuppressWarnings("rawtypes")
    List<T> findForPage(Map params,int... pageParams);

    T get(String id);

    @SuppressWarnings("rawtypes")
	List<T> findForUnPage(Map params);

	void update(T entity);
	
	@SuppressWarnings("rawtypes")
	Long getTotalCount(Map params);
}
