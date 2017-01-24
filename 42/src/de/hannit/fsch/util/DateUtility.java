package de.hannit.fsch.util;

import java.time.LocalDate;
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
}
