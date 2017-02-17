/**
 * 
 */
package de.hannit.fsch.reportal.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import de.hannit.fsch.reportal.model.Zeitraum;
import de.hannit.fsch.reportal.model.echolon.JahresStatistik;
import de.hannit.fsch.reportal.model.echolon.Vorgang;
import de.hannit.fsch.util.ServletContextController;

/**
 * @author hit
 *
 */
@ManagedBean
@ApplicationScoped
public class Cache implements Serializable
{
private static final long serialVersionUID = -5351191156760420453L;
private final static Logger log = Logger.getLogger(Cache.class.getSimpleName());
private String logPrefix = this.getClass().getCanonicalName() + ": ";
private FacesContext fc = null;
private ServletContext servletContext = null;
private TimerDeamon timer = null;

private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();
private String lastExecution = null;
private int anzahlDatensaetzeGesamt = 0;
private Vorgang max = null;

private TreeMap<Integer, JahresStatistik> jahresStatistiken = null;
private ArrayList<Vorgang> vorgaengeBerichtszeitraum;
private Stream<Vorgang> si = null;
private String restrictedAccess = null;

private boolean timerOK = false;
private boolean backendOK = false;


	/**
	 * 
	 */
	public Cache() 
	{
	fc = FacesContext.getCurrentInstance();
	servletContext = (ServletContext) fc.getExternalContext().getContext();

		try 
		{
		timer = (TimerDeamon) servletContext.getAttribute(ServletContextController.TIMER_DEAMON);	
		if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": TimerDeamon wurde im Servlet Context gefunden");}	
		timerOK = true;
		backendOK = true;
		
		distinctCases = timer.getDistinctCases();
		anzahlDatensaetzeGesamt = distinctCases.size();
		max = distinctCases.values().stream().max(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
		setJahresStatistiken();
		lastExecution = timer.getLastExecution();
		log.log(Level.INFO, logPrefix + ": Letzte Cache Aktualisierung ist vom " + getLastExecution());	
		} 
		catch (NullPointerException e) 
		{
		log.log(Level.SEVERE, logPrefix + ": TimerDeamon wurde NICHT im Servlet Context gefunden !");
		timerOK = false;
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
	return timer.getLastExecution() != null ? timer.getLastExecution() : lastExecution;
	}

	/*
	 * Liefert die Auswertungsdaten.
	 * Hier wird alles mögliche versucht, um an die Daten zu kommen
	 */
	public HashMap<String, Vorgang> getDistinctCases() 
	{
	fc = FacesContext.getCurrentInstance();
	servletContext = (ServletContext) fc.getExternalContext().getContext();
	
		if (fc.isProjectStage(ProjectStage.Development)) 
		{
			if (distinctCases == null || distinctCases.size() < 1) 
			{
			log.log(Level.WARNING, logPrefix + "Es befinden sich keine Daten im Cache !" );
			}
		}	
		
		if (timer == null) 
		{
		log.log(Level.WARNING, logPrefix + "Timer wurde nicht gefunden, versuche Timer aus dem Servlet Context zu laden..." );
		timer = getTimerDeamon();
		
			if (timer != null) 
			{
			if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": TimerDeamon wurde im Servlet Context gefunden. Letzte Ausführung ist vom " + getLastExecution());}
			distinctCases = timer.getDistinctCases();
			timerOK = true;
			} 
			else 
			{
			log.log(Level.SEVERE, logPrefix + "Timer wurde auch nach wiederholter Suche nicht gefunden, versuche Daten über die Fallback Funktion zu laden..." );
			timerOK = false;
				try 
				{
				distinctCases = fallback();
				backendOK = true;
				log.log(Level.SEVERE, logPrefix + "Die Daten konnten über die Fallback Funktion geladen werden. Die Anwendung muss dennoch überprüft werden." );
				} 
				catch (NullPointerException e) 
				{
				log.log(Level.SEVERE, logPrefix + "Daten konnten weder vom Timer noch über die Fallback Funktion geladen werden. Die Applikation wird heruntergefahren !" );
				FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Echolon Daten sind nicht verfügbar !", "Es besteht ein schwerwiegendes Problem bei der Verbindung zur Datenbank. Bitte wenden Sie sich an den Systemadministrator.");
				fc.addMessage(null, error);
				timerOK = false;
				backendOK = false;
				e.printStackTrace();
				}
			}
		}
		else
		{
		distinctCases = timer.getDistinctCases();	
		timerOK = true;
		backendOK = true;
		}
	return distinctCases;
	}

	/*
	 * Diese Notfallfunktion versucht Daten direkt aus der Datenbank zu laden
	 */
	private HashMap<String, Vorgang> fallback() 
	{
		try 
		{
		ExecutorService executor = Executors.newCachedThreadPool();			
		distinctCases = executor.submit(new DataBaseThread()).get();		
		} 
		catch (NullPointerException e) 
		{
		e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
		e.printStackTrace();
		} 
		catch (ExecutionException e) 
		{
		e.printStackTrace();
		}
		return null;
	}

	private TimerDeamon getTimerDeamon() 
	{
		try 
		{
		timer = (TimerDeamon) servletContext.getAttribute(ServletContextController.TIMER_DEAMON);	
		if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": TimerDeamon wurde im Servlet Context gefunden");}	
	
		return timer;
		} 
		catch (NullPointerException e) 
		{
		log.log(Level.SEVERE, logPrefix + ": TimerDeamon wurde NICHT im Servlet Context gefunden !");
		e.printStackTrace();
		return null;
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
	public boolean isTimerOK() {return timerOK;}
	public boolean isBackendOK() {return backendOK;}
	
	
	
}
