/**
 * 
 */
package de.hannit.fsch.reportal.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
private FacesContext fc = null;
private TimerDeamon timer = null;

private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();
private String lastExecution = null;
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
	fc = FacesContext.getCurrentInstance();
	ServletContext servletContext = (ServletContext) fc.getExternalContext().getContext();

		try 
		{
		timer = (TimerDeamon) servletContext.getAttribute(ServletContextController.TIMER_DEAMON);	
		if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, this.getClass().getCanonicalName() + ": TimerDeamon wurde im Servlet Context gefunden");}	
		
		distinctCases = timer.getDistinctCases();
		anzahlDatensaetzeGesamt = distinctCases.size();
		max = distinctCases.values().stream().max(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
		setJahresStatistiken();
		lastExecution = timer.getLastExecution();
		log.log(Level.INFO, this.getClass().getCanonicalName() + ": Letzte Cache Aktualisierung ist vom " + getLastExecution());	
		} 
		catch (NullPointerException e) 
		{
		log.log(Level.SEVERE, this.getClass().getCanonicalName() + ": TimerDeamon wurde NICHT im Servlet Context gefunden !");
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
	return lastExecution;
	}

	public HashMap<String, Vorgang> getDistinctCases() 
	{
	return distinctCases;
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
