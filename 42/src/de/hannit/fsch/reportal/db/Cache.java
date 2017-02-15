/**
 * 
 */
package de.hannit.fsch.reportal.db;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.hannit.fsch.reportal.model.Zeitraum;
import de.hannit.fsch.reportal.model.echolon.JahresStatistik;
import de.hannit.fsch.reportal.model.echolon.Vorgang;

/**
 * @author hit
 *
 */
@ManagedBean
@ApplicationScoped
public class Cache extends TimerTask 
{
private final static Logger log = Logger.getLogger(EcholonDBManager.class.getSimpleName());
private ExecutorService executor = Executors.newCachedThreadPool();
private DataBaseThread dbThread = null;
private Future<HashMap<String, Vorgang>> result = null;
private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();
private LocalDateTime lastExecution = LocalDateTime.now();
private int anzahlDatensaetzeGesamt = 0;
private Vorgang max = null;

private TreeMap<Integer, JahresStatistik> jahresStatistiken = null;
private ArrayList<Vorgang> vorgaengeBerichtszeitraum;
private Stream<Vorgang> si = null;
private String restrictedAccess = null;


	/**
	 * 
	 */
	public Cache() 
	{
	Timer timer = new Timer(true);
	LocalDate startDay = LocalDate.now().plusDays(1);
	LocalDateTime start = LocalDateTime.of(startDay.getYear(), startDay.getMonthValue(), startDay.getDayOfMonth(), 6, 30);
	Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
	
	timer.scheduleAtFixedRate(this, startDate, 86400000);
	log.log(Level.INFO, this.getClass().getCanonicalName() + ": Lade initiale Daten aus der Datenbank");	
	loadData();	
	
	}
	
	private void loadData() 
	{
	dbThread =  new DataBaseThread();
	result = executor.submit(dbThread);	
		try 
		{
			if (distinctCases != null) 
			{
			distinctCases.clear();	
			}
		distinctCases = result.get();
		anzahlDatensaetzeGesamt = distinctCases.size();
		max = distinctCases.values().stream().max(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
		setJahresStatistiken();
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
	}
	
	/*
	 * Sortiert alle Vorgänge ab 2010 nach Jahren
	 */
	private void setJahresStatistiken() 
	{
	int aktuellesBerichtsJahr = max.getBerichtsJahr();
	int erstesBerichtsJahr = 2009;
	JahresStatistik js = null;
	jahresStatistiken = new TreeMap<>();
		
		// Erstelle Jahresstatistiken bis 2010:
		while (aktuellesBerichtsJahr > erstesBerichtsJahr) 
		{
		js = new JahresStatistik(getVorgaengeBerichtsJahr(aktuellesBerichtsJahr), String.valueOf(aktuellesBerichtsJahr));
		js.setStatistik();
		jahresStatistiken.put(aktuellesBerichtsJahr, js);
			
		aktuellesBerichtsJahr--;	
		}
	}  	
	
	private ArrayList<Vorgang> getVorgaengeBerichtsJahr(int incoming)
	{
	vorgaengeBerichtszeitraum = new ArrayList<>();
	si = distinctCases.values().stream(); 
	vorgaengeBerichtszeitraum = si.filter(v -> v.getBerichtsJahr() == incoming).collect(Collectors.toCollection(ArrayList::new ));
	
	return vorgaengeBerichtszeitraum;	
	}

	public String getLastExecution() 
	{
	return Zeitraum.dfDatumUhrzeitMax.format(lastExecution);
	}

	public HashMap<String, Vorgang> getDistinctCases() 
	{
		if (distinctCases == null) 
		{
		log.log(Level.INFO, this.getClass().getCanonicalName() + ": Lade Daten aus der Datenbank");	
		loadData();	
		} 
		else
		{
		log.log(Level.INFO, this.getClass().getCanonicalName() + ": Es sind bereits Daten geladen, liefere Daten aus dem Cache.");	
		}
	return distinctCases;
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

	public int getAnzahlDatensaetzeGesamt() {return anzahlDatensaetzeGesamt;}
	
	public String getMaxDate ()  
	{
	return max != null ? "Aktuellster Vorgang im Cache (" + max.getVorgangsNummer() + ") ist vom " + Zeitraum.dfDatumUhrzeitMax.format(max.getErstellDatumZeit()) : null;	
	}

	public TreeMap<Integer, JahresStatistik> getJahresStatistiken() {return jahresStatistiken;}
	public String getRestrictedAccess() {return restrictedAccess;}
	public void setRestrictedAccess(String restrictedAccess) {this.restrictedAccess = restrictedAccess;}
	
	
}
