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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import de.hannit.fsch.reportal.model.Berichtszeitraum;
import de.hannit.fsch.reportal.model.Mandant;
import de.hannit.fsch.reportal.model.Zeitraum;
import de.hannit.fsch.reportal.model.echolon.EcholonNode;
import de.hannit.fsch.reportal.model.echolon.JahresStatistik;
import de.hannit.fsch.reportal.model.echolon.MonatsStatistik;
import de.hannit.fsch.reportal.model.echolon.QuartalsStatistik;
import de.hannit.fsch.reportal.model.echolon.Vorgang;
import de.hannit.fsch.util.ServletContextController;

/**
 * @author hit
 *
 */
@ManagedBean
@SessionScoped
public class Cache implements Serializable
{
@ManagedProperty (value = "#{edb}")
private EcholonDBManager edb;	
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
private TreeMap<String, TreeNode> trees = null;

private ArrayList<Vorgang> vorgaengeBerichtszeitraum;
private Stream<Vorgang> si = null;
private String restrictedAccess = null;

private boolean timerOK = false;
private boolean backendOK = false;

private Mandant mandant = null;
/**
	 * 
	 */
	public Cache() 
	{
	fc = FacesContext.getCurrentInstance();
	servletContext = (ServletContext) fc.getExternalContext().getContext();
	edb = edb != null ? edb : fc.getApplication().evaluateExpressionGet(fc, "#{edb}", EcholonDBManager.class);	

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
		setTreeModels();
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
	
    public TreeNode getRoot() 
    {
    	if (getMandant() != null) 
    	{
		return trees.get(mandant.getBezeichnung());
		}
    	else
    	{
   		return null;
    	}
    }
	
	/*
	 * Cached die Baummodelle für alle vorhandenen Mandanten
	 */
	private void setTreeModels() 
	{
	fc = fc != null ? fc : FacesContext.getCurrentInstance(); 	
	logPrefix = this.getClass().getCanonicalName() + ".setTreeModels(): ";
	trees = new TreeMap<>();
	HashMap<String, Vorgang> filteredCases = null;
	Vorgang maxMandant = null;
	Vorgang minMandant = null;
	String berichtsJahr = null;
	int aktuellesBerichtsJahr = 0;
	JahresStatistik js = null;
	EcholonNode aktuellerJahresknoten = null;
	EcholonNode aktuellerQuartalssknoten = null;
	EcholonNode aktuellerMonatssknoten = null;
	TreeNode root = null;
		
	
		for (Mandant m : edb.getMandanten().values()) 
		{
		setMandant(m);
		filteredCases = getDistinctCases();
		maxMandant = filteredCases.values().stream().max(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
		minMandant = filteredCases.values().stream().min(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
		aktuellesBerichtsJahr = maxMandant.getBerichtsJahr();	
		root = new DefaultTreeNode("Root", null);
		
			// Erstelle Jahresknoten bis zum ältesten Vorgang:
			while (aktuellesBerichtsJahr >= minMandant.getBerichtsJahr()) 
			{
			berichtsJahr = String.valueOf(aktuellesBerichtsJahr);
			aktuellerJahresknoten = new EcholonNode(berichtsJahr, root);
			aktuellerJahresknoten.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_JAHR);
			aktuellerJahresknoten.setBerichtsJahr(berichtsJahr);
			
			js = new JahresStatistik(filteredCases.values().stream().collect(Collectors.toCollection(ArrayList<Vorgang>::new)), berichtsJahr);
			aktuellerJahresknoten.setData(js);
				
				// Jahresknoten/Quartalsknoten
				// Gibt es schon QuartalsStatistiken ?
				if (js.getQuartalsStatistiken() != null && !js.getQuartalsStatistiken().isEmpty()) 
				{
				if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Jahresstatisik " + js.getBerichtsJahr() + " enthält "+ js.getQuartalsStatistiken().size() + " Quartalsstatistiken");	}
					
					for (QuartalsStatistik qs : js.getQuartalsStatistiken().values()) 
					{
						if (qs.getAnzahlVorgaengeBerichtszeitraum() > 0) 
						{
						qs.setStatistik();
						aktuellerQuartalssknoten = new EcholonNode(qs,aktuellerJahresknoten);
						aktuellerQuartalssknoten.setData(qs);
							
							// Jahresknoten/Quartalsknoten/Monatsknoten
							for (MonatsStatistik ms : qs.getMonatsStatistiken()) 
							{
								if (ms.getAnzahlVorgaengeGesamt() > 0) 
								{
								ms.setStatistik();	
								aktuellerMonatssknoten = new EcholonNode(ms, aktuellerQuartalssknoten);	
								aktuellerMonatssknoten.setData(ms);
								}
							}
						}
					}
				} 
				else 
				{
				if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.WARNING, logPrefix + "Jahresstatisik " + js.getBerichtsJahr() + " enthält keine Quartalsstatistiken");}
				
					if (js.getMonatsStatistiken().size() > 0) 
					{
					if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.WARNING, logPrefix + "Jahresstatisik " + js.getBerichtsJahr() + " enthält " + js.getMonatsStatistiken().size() + " Monatsstatistiken. Diese werden dem Jahresknoten hinzugefügt.");}
						for (MonatsStatistik ms : js.getMonatsStatistiken().values()) 
						{
							if (ms.getAnzahlVorgaengeGesamt() > 0) 
							{
							ms.setStatistik();	
							aktuellerMonatssknoten = new EcholonNode(ms, aktuellerJahresknoten);	
							aktuellerMonatssknoten.setData(ms);
							}
						}					
						
					}
				}
			aktuellesBerichtsJahr--;	
			}
		trees.put(m.getBezeichnung(), root);	
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
	
	public void filter(ActionEvent event) 
	{
    String mandant = (String) event.getComponent().getAttributes().get("Mandant");
    setMandant(edb.getMandanten().get(mandant));
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
	HashMap<String, Vorgang> result = null;	
	
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
		
		if (mandant != null) 
		{
		ArrayList<Vorgang> inServiceZeit = distinctCases.values().stream().filter(v -> v.getErstellZeit().isAfter(mandant.getServicezeitStart()) && v.getErstellZeit().isBefore(mandant.getServicezeitEnde())).collect(Collectors.toCollection(ArrayList<Vorgang>::new));	
			
			// Muss nach Organisationen UND Servicekategorien gefiltert werden ?
			if (mandant.getOrganisationenFilter() && mandant.getServiceKategorienFilter()) 
			{
			HashMap<String, Vorgang> filteredCases = new HashMap<String, Vorgang>();	
			
				for (Vorgang v : inServiceZeit) 
				{
					for (String organisation : mandant.getOrganisationen().values()) 
					{
						if (v.getOrganisation().equalsIgnoreCase(organisation)) 
						{
						filteredCases.put(v.getId(), v);	
						}
					}
					for (String serviceKategorie : mandant.getServiceKategorien().values()) 
					{
						if (v.getKategorie().equalsIgnoreCase(serviceKategorie)) 
						{
						filteredCases.put(v.getId(), v);	
						}
					}
				}
			if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": Gesamtvorgänge für den Mandanten " + mandant.getBezeichnung() + " wurden nach Organisation und Servicekategorien auf " + filteredCases.size() + " Vorgänge gefiltert.");}
			result = filteredCases;	
			}
	
			// Muss nach Organisationen gefiltert werden ?
			if (mandant.getOrganisationenFilter() && ! mandant.getServiceKategorienFilter()) 
			{
			HashMap<String, Vorgang> filteredCases = new HashMap<String, Vorgang>();	
			
				for (Vorgang v : inServiceZeit) 
				{
					for (String organisation : mandant.getOrganisationen().values()) 
					{
						if (v.getOrganisation().equalsIgnoreCase(organisation)) 
						{
						filteredCases.put(v.getId(), v);	
						}
					}
				}
			if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": Gesamtvorgänge für den Mandanten " + mandant.getBezeichnung() + " wurden nach Organisation auf " + filteredCases.size() + " Vorgänge gefiltert.");}
			result = filteredCases;	
			}
			// Muss Servicekategorien gefiltert werden ?
			if (mandant.getServiceKategorienFilter() && ! mandant.getOrganisationenFilter()) 
			{
			HashMap<String, Vorgang> filteredCases = new HashMap<String, Vorgang>();	
			
				for (Vorgang v : inServiceZeit) 
				{
					for (String serviceKategorie : mandant.getServiceKategorien().values()) 
					{
						if (v.getKategorie().equalsIgnoreCase(serviceKategorie)) 
						{
						filteredCases.put(v.getId(), v);	
						}
					}
				}
			if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": Gesamtvorgänge für den Mandanten " + mandant.getBezeichnung() + " wurden nach Servicekategorien auf " + filteredCases.size() + " Vorgänge gefiltert.");}
			result = filteredCases;	
			}
			// Kein Filter, also HannIT
			if (! mandant.getServiceKategorienFilter() && ! mandant.getOrganisationenFilter()) 
			{
			if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": Gesamtvorgänge für den Mandanten " + mandant.getBezeichnung() + " wurden nicht gefiltert.");}
			result = distinctCases;	
			}			
		}
		else
		{
		result =  distinctCases;
		}
	return result;	
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
	public EcholonDBManager getEdb() {return edb;}
	public void setEdb(EcholonDBManager edb) {this.edb = edb;}
	public Mandant getMandant() {return mandant;}
	public void setMandant(Mandant mandant) {this.mandant = mandant;}

	public void setMandant(String incoming) 
	{
	this.mandant = edb.getMandanten().get(incoming);		
	}
		
}
