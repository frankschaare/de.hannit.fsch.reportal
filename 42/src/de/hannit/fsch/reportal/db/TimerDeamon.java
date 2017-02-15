package de.hannit.fsch.reportal.db;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hannit.fsch.reportal.model.Zeitraum;
import de.hannit.fsch.reportal.model.echolon.Vorgang;

public class TimerDeamon extends TimerTask 
{
private final static Logger log = Logger.getLogger(TimerDeamon.class.getSimpleName());
private LocalDateTime lastExecution = LocalDateTime.now();
private ExecutorService executor = Executors.newCachedThreadPool();
private DataBaseThread dbThread = null;
private Future<HashMap<String, Vorgang>> result = null;
private HashMap<String, Vorgang> distinctCases = null;
	

	public TimerDeamon() 
	{
	Timer timer = new Timer(true);
	LocalDate startDay = LocalDate.now().plusDays(1);
	LocalDateTime start = LocalDateTime.of(startDay.getYear(), startDay.getMonthValue(), startDay.getDayOfMonth(), 6, 30);
	Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
		
	timer.scheduleAtFixedRate(this, startDate, 86400000);
	log.log(Level.INFO, this.getClass().getCanonicalName() + ": TimerDeamon wurde erfolgreich initialisiert");
	}

	@Override
	public void run() 
	{
	long test = System.currentTimeMillis() - lastExecution.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();	
		if (test > 1000) 
		{
		loadData();
		}
		else 
		{
		log.log(Level.INFO, this.getClass().getCanonicalName() + ": Timer wurde nicht ausgeführt, da die erneute Anforderung unter einer Sekunde lag.");	
		}		
	}

	private HashMap<String, Vorgang> loadData() 
	{
	dbThread =  new DataBaseThread();
	result = executor.submit(dbThread);	
	distinctCases = new HashMap<>();
	
		try 
		{
			if (distinctCases != null) 
			{
			distinctCases.clear();	
			}
		distinctCases = result.get();
		lastExecution = LocalDateTime.now();
		log.log(Level.INFO, this.getClass().getCanonicalName() + ": Letzte Cache Aktualisierung ist vom " + getLastExecution());	
		} 
		catch (InterruptedException e) 
		{
		e.printStackTrace();
		} 
		catch (ExecutionException e) 
		{
		e.printStackTrace();
		}
	return distinctCases;	
	}
	
	public HashMap<String, Vorgang> getDistinctCases() 
	{
	return distinctCases == null ? loadData() : distinctCases;
	}
	
	public String getLastExecution() {return Zeitraum.dfDatumUhrzeitMax.format(lastExecution);}	
	
}
