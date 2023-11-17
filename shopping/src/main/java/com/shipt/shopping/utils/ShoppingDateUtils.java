package com.shipt.shopping.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;

/*Utility class for time related calculations. Java 8 Date/Time API is used for time calculations
 * 
 * */
public class ShoppingDateUtils {

	//get current time in milliseconds
	public static Long getCurrentDateTimeInMillis() throws Exception {	  
		LocalDateTime currentTimeObj = LocalDateTime.now(); 
		ZonedDateTime zdt = ZonedDateTime.of(currentTimeObj, ZoneId.systemDefault());
		long currentTime = zdt.toInstant().toEpochMilli();
		return currentTime;
	}

	//convert milliseconds to date as a string in yyyy-MM-dd HH:mm:ss format
	public static String convertMillistoDateTimeString(long epoch) throws Exception{
		LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault());
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDateTime = dateTime.format(dateTimeFormatter);
		return formattedDateTime;
	}

	//convert milliseconds to date as a string in yyyy-MM-dd format
	public static String convertMillistoDateString(long epoch) throws Exception{
		LocalDate date = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()).toLocalDate();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedDate = date.format(dateTimeFormatter);
		return formattedDate;
	}

	//get number of days between start and end date
	public static long getDaysBetween(long startDate, long endDate) {
		LocalDate startDateLD = LocalDate.ofInstant(Instant.ofEpochMilli(startDate), ZoneId.systemDefault());
		LocalDate endDateLD = LocalDate.ofInstant(Instant.ofEpochMilli(endDate), ZoneId.systemDefault());
		long days = ChronoUnit.DAYS.between(startDateLD, endDateLD);
		return days;
	}

	//get week number for a date from the epoch day
	public static long getWeekFromEpoch(long timeMillis) throws Exception{
		LocalDate localDate = LocalDate.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
		LocalDate epochDate = LocalDate.ofEpochDay(0);
		long weeks = ChronoUnit.WEEKS.between(epochDate, localDate);
		return weeks;
	}

	//get month number for a date from the epoch day
	public static long getMonthFromEpoch(long timeMillis) throws Exception{
		LocalDate localDate = LocalDate.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
		LocalDate epochDate = LocalDate.ofEpochDay(0);
		long months = ChronoUnit.MONTHS.between(epochDate, localDate);
		return months;
	}

	//convert date to milliseconds
	public static long convertDatetoMillis(String date) throws Exception{
		LocalDateTime localDateTime = LocalDate.parse(date,DateTimeFormatter.ofPattern("yyyy/MM/dd")).atStartOfDay();
		long millis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return millis;
	}

	//check if the given date range is valid
	public static boolean checkIfDateRangeisvalid(String startDate, String endDate) throws Exception{
		LocalDate localStartDate = LocalDate.parse(startDate,DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		LocalDate localEndDate = LocalDate.parse(endDate,DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		if(localStartDate.isAfter(localEndDate)) {
			return false;
		}
		return true;
	}
}
