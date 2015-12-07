package com.sg.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String getDate(Date date, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String s = sdf.format(date);
		return s;
	}
	
	public static Date getDateFormat(Date date, String format){
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(date);
		ParsePosition pos = new ParsePosition(8);
		Date resultDate = formatter.parse(dateString, pos);
		return resultDate;
	}
}
