package com.hack;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DateProgram {

	static long MILLISECS_IN_DAY = 24L * 3600L * 1000L;
    static class Holiday {
	    public int month;
	    public int day;

	    Holiday(String m, String d) {
	      month = Integer.parseInt(m);
	      day = Integer.parseInt(d);
	    }
	  }

	  static class Options {
	    public String shipDate = "2018-01-02";
	    public int deliveryEstimateInBusinessDays = 50;
	    public Holiday[] holidays = {
	      new Holiday("01", "01"), // New Year's Day
	      new Holiday("01", "15"), // MLK Day
	      new Holiday("05", "28"), // Memorial Day
	      new Holiday("07", "04"), // Independence Day
	      new Holiday("09", "03"), // Labor Day
	      new Holiday("11", "12"), // Veterans Day
	      new Holiday("11", "22"), // Thanksgiving
	      new Holiday("12", "25"), // Xmas Day
	    };
	    public Date getShipDate() {
	    	return getDate(shipDate);
	    }
	    public boolean isHoliday(int month, int day) {
	    	for(Holiday h : holidays) {
	    		if(h.month == month && h.day == day) {
	    			log("Holiday " + month + " " + day);
	    			return true;
	    		}
	    	}
	    	return false;
	    }    
	  }
	  
	  
	  static void log(String s) {
		  System.out.println(s);
	  }
	  
	  static boolean isWeekDay(Date d) {
		  return 1 <= d.getDay() && d.getDay() <= 5; 
	  }
	  
	  static Date getDate(String text) {
		  Calendar cal =  Calendar.getInstance();
		  List<Integer> numbers = Arrays.asList(text.split("-")).stream()
				  .map(Integer::parseInt)
				  .collect(Collectors.toList());
		  cal.set(numbers.get(0), numbers.get(1)-1, numbers.get(2));
		  Date d = cal.getTime();
		  return d;
	  }
	  
	  static String format(Date d) {
		  return (1900 + d.getYear()) + "-" + (d.getMonth() +1) + "-" + d.getDate();
		  
	  }

	  public static String findEstimatedDateOfDelivery(Options options) {
	      Date shipped = options.getShipDate();
	      int totalBusiness = 0;
	      Date day = shipped;
	      while (totalBusiness < 50) {
	    	  boolean isHoliday = options.isHoliday(day.getMonth() + 1, day.getDate());
	    	  boolean isWeekday = isWeekDay(day);
	    	  log(format(day) + " ");
	    	  day = new Date(day.getTime() + MILLISECS_IN_DAY);

	    	  if(!isHoliday && isWeekday) {
	    		  totalBusiness ++;
	    		  if(totalBusiness == 50) break;
	    	  }
	      }
	      return (1900 + day.getYear()) + "-0" + (day.getMonth()+1) + "-" + day.getDate();
	  }

	  public static void main(String[] args) {
	    String result = findEstimatedDateOfDelivery(new Options());
	    log("Result=" + result);
	    assert result == "2018-03-14";
	  }
	
}
