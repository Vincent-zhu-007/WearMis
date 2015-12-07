package com.sg.util;

import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MapUtil {
	public static SortedMap<String, Object> mapSortByKey(Map<String, Object> unsort_map) {
		TreeMap<String, Object> result = new TreeMap<String, Object>();
	
	    Object[] unsort_key = unsort_map.keySet().toArray();
	    Arrays.sort(unsort_key);
	
	    for (int i = 0; i < unsort_key.length; i++) {
	    	result.put(unsort_key[i].toString(), unsort_map.get(unsort_key[i]));
	    }
	    return result.tailMap(result.firstKey());
	}
}
