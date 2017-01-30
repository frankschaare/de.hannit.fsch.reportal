package de.hannit.fsch.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;

public class DateUtility 
{
	
	private DateUtility() {}
	
	/*
	 * Monatzletzten ermitteln
	 */
	public static LocalDate getEndOfCurrentMonth() 
	{
	return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
	}
	
	 /**
	  * Method calculates last working day for last day of month as input
	  * @param lastDayOfMonth
	  * @return LocalDate instance containing last working day
	  */
	 public static LocalDate getLastWorkingDayOfMonth(LocalDate incoming) 
	 {
	 LocalDate lastWorkingDayofMonth;
	 	switch (DayOfWeek.of(incoming.get(ChronoField.DAY_OF_WEEK))) 
	 	{
	    case SATURDAY:
	    lastWorkingDayofMonth = incoming.minusDays(1);
	    break;
	    case SUNDAY:
	    lastWorkingDayofMonth = incoming.minusDays(2);
	    break;
	    default:
	    lastWorkingDayofMonth = incoming;
	   }
	 return lastWorkingDayofMonth;
	 }
}
