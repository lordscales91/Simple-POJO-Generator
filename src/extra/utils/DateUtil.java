package extra.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author lordscales91(kosuke_ueki91@hotmail.com)
 * Thread-Safe class that provides functions for date 
 * formatting and age calculations
 */
public class DateUtil {	
	
	public static String MYSQL_DATE= "yyyy-MM-dd";
	public static String MYSQL_DATETIME="yyyy-MM-dd HH:mm:ss";
	public static String DATE_SHOW="dd-MM-yyyy";
	public static String DATETIME_SHOW="dd-MM-yyyy HH:mm:ss";
	
	private static ThreadLocal<SimpleDateFormat> tsdf= 
			new ThreadLocal<SimpleDateFormat>();
	/**
	 * return the instance of the SimpleDateFormat object or null if 
	 * it isn't initialized
	 * @return the instance of the SimpleDateFormat object or null if 
	 * it isn't initialized
	 */
	public static SimpleDateFormat getSdf() {					
		return tsdf.get();
	}
	/**
	 * Initialize a per-thread static instance of a SimpleDateFormat object
	 * @param pattern Date format string
	 */
	public static void initSdf(String pattern) {
		tsdf.set(new SimpleDateFormat(pattern));
	}
	/**
	 * This calculate the age in years by providing the date of birth.
	 * @param birth the date of birth 
	 * @return the age in years or -1 if something goes wrong
	 */
	public static int calculateAgeFromDate(Calendar birth) {		
		int age = -1;
		if(birth!=null) {
			
			Calendar today=Calendar.getInstance();
			//Calculate the difference in years
			age=today.get(Calendar.YEAR)-birth.get(Calendar.YEAR);
			//Check if the birth date has already happened this year
			if(birth.get(Calendar.MONTH)>today.get(Calendar.MONTH)
					|| (birth.get(Calendar.MONTH)==today.get(Calendar.MONTH)
							&& birth.get(Calendar.DAY_OF_MONTH)
									>today.get(Calendar.DAY_OF_MONTH))) {
				age--;//if not, decrease one
			}
		}
		return age;
	}
	/**
	 * This is a convenience method that just calls {@link #calculateAgeFromDate(Calendar)}
	 * @param date_of_birth the date of birth 
	 * @return the age in years or -1 if something goes wrong
	 */
	public static int calculateAgeFromDate(Date date_of_birth) {		
		int age = -1;
		if(date_of_birth!=null) {
			Calendar birth=Calendar.getInstance();
			birth.setTime(date_of_birth);
			age=calculateAgeFromDate(birth);
		}
		return age;
	}
	

}
