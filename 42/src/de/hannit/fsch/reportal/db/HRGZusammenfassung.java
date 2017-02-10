/**
 * 
 */
package de.hannit.fsch.reportal.db;

import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.application.ProjectStage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import de.hannit.fsch.reportal.model.Quartal;
import de.hannit.fsch.reportal.model.Zeitraum;
import de.hannit.fsch.reportal.model.echolon.EcholonZusammenfassungCSV;
import de.hannit.fsch.reportal.model.echolon.MonatsStatistik;
import de.hannit.fsch.reportal.model.echolon.QuartalsStatistik;
import de.hannit.fsch.reportal.model.echolon.Vorgang;

/**
 * @author fsch
 * 
 * Lädt die Daten der letzten drei Monate für die HRG aus der Echolon View
 * und bereitet diese für die Zusammenfassung auf
 *
 */
@ManagedBean(name = "hrg")
@SessionScoped
public class HRGZusammenfassung implements Serializable
{
@ManagedProperty (value = "#{cache}")
private Cache cache;	
private static final long serialVersionUID = 1L;

private final static Logger log = Logger.getLogger(HRGZusammenfassung.class.getSimpleName());
private String logPrefix = this.getClass().getCanonicalName() + ": ";
private FacesContext fc = FacesContext.getCurrentInstance();

private String thema = "Zusammenfassung";
private Zeitraum abfrageZeitraum = null;

private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();
private ArrayList<Vorgang> vorgaengeBerichtszeitraum;
private TreeMap<Integer, QuartalsStatistik> quartale = new TreeMap<Integer, QuartalsStatistik>();
private HashMap<Integer, MonatsStatistik> monatsStatistiken = null;
private ArrayList<String> lines = null;
private Vorgang max = null;

private Stream<Vorgang> si = null;

	/**
	 * 
	 */
	public HRGZusammenfassung() 
	{
		if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Lade alle Vorgänge aus dem Cache.");}	
		try 
		{
		distinctCases = cache.getDistinctCases();
		} 
		catch (NullPointerException e) 
		{
		FacesContext fc = FacesContext.getCurrentInstance();
		cache = fc.getApplication().evaluateExpressionGet(fc, "#{cache}", Cache.class);
		distinctCases = cache.getDistinctCases();
		}
	setMinMaxVorgang();
	
	// Standardabfragezeitraum über die letzen drei Monate:
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Setze AbfrageZeitraum auf 'Zeitraum.BERICHTSZEITRAUM_LETZTES_QUARTAL'.");}	
	abfrageZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTES_QUARTAL, max);
			

			for (Quartal q : abfrageZeitraum.getQuartale().values()) 
			{
			QuartalsStatistik qs = new QuartalsStatistik(q);

			vorgaengeBerichtszeitraum = filter(q);

			setMonatsStatistiken(vorgaengeBerichtszeitraum); 

				for (MonatsStatistik ms : monatsStatistiken.values()) 
				{
				ms.setStatistik();	
				qs.addMonatsstatistik(ms);
				}			
			qs.setStatistik();	
			quartale.put(q.getIndex(), qs);	
			}
			
			// Dateidownload vorbereiten:
			lines = new ArrayList<String>();
			String einheiten = "";
			String lineGesamt = "Anzahl Vorgänge Gesamt:";
			String lineAnzahlIncidents = "Anzahl Incidents:";
			String lineAnzahlIncidentsServicezeitNichtEingehalten = "Servicezeit nicht eingehalten:";
			String lineAnzahlIncidentsServicezeitNichtEingehaltenProzent = "Servicezeit nicht eingehalten (%):";
			String lineDauerIncidentMinuten = "Dauer Incident (Minuten):";
			String lineDauerIncidentStunden = "Dauer Incident (Stunden):";
			String lineDauerIncidentTage = "Dauer Incident (Tage):";
			String lineAnzahlServiceabrufe = "Anzahl Serviceabrufe:";
			String lineAnzahlServiceabrufeServicezeitNichtEingehalten = "Servicezeit nicht eingehalten:";
			String lineAnzahlServiceabrufeServicezeitNichtEingehaltenProzent = "Servicezeit nicht eingehalten (%):";
			String lineDauerServiceabrufeMinuten = "Dauer Serviceabrufe (Minuten):";
			String lineDauerServiceabrufeStunden = "Dauer Serviceabrufe (Stunden):";
			String lineDauerServiceabrufeTage = "Dauer Serviceabrufe (Tage):";
			
			for (QuartalsStatistik qs : quartale.values()) 
			{
				for (MonatsStatistik m : qs.getMonatsStatistiken()) 
				{
				einheiten = einheiten + ";" + m.getBezeichnungLang();	
				lineGesamt = lineGesamt + ";" + m.getAnzahlVorgaengeGesamt();
				lineAnzahlIncidents = lineAnzahlIncidents + ";" + m.getAnzahlIncidents();
				lineAnzahlIncidentsServicezeitNichtEingehalten = lineAnzahlIncidentsServicezeitNichtEingehalten + ";" + m.getAnzahlIncidentsServicezeitNichtEingehalten();
				lineAnzahlIncidentsServicezeitNichtEingehaltenProzent = lineAnzahlIncidentsServicezeitNichtEingehaltenProzent + ";" + m.getFormattedProzentanteilIncidentsServicezeitNichtEingehalten() + " %";
				lineDauerIncidentMinuten = lineDauerIncidentMinuten + ";" + m.getDurchschnittlicheDauerMinutenIncidents();
				lineDauerIncidentStunden = lineDauerIncidentStunden + ";" + m.getFormattedAvgDauerStundenIncidents();
				lineDauerIncidentTage = lineDauerIncidentTage + ";" + m.getFormattedAvgDauerTageIncidents();
				
				lineAnzahlServiceabrufe = lineAnzahlServiceabrufe + ";" + m.getAnzahlServiceAbrufe();
				lineAnzahlServiceabrufeServicezeitNichtEingehalten = lineAnzahlServiceabrufeServicezeitNichtEingehalten + ";" + m.getAnzahlServiceAbrufeServicezeitNichtEingehalten();
				lineAnzahlServiceabrufeServicezeitNichtEingehaltenProzent = lineAnzahlServiceabrufeServicezeitNichtEingehaltenProzent + ";" + m.getFormattedProzentanteilServiceAbrufeServicezeitNichtEingehalten() + " %";
				lineDauerServiceabrufeMinuten = lineDauerServiceabrufeMinuten + ";" + m.getDurchschnittlicheDauerMinutenServiceAbrufe();
				lineDauerServiceabrufeStunden = lineDauerServiceabrufeStunden + ";" + m.getFormattedAvgDauerStundenServiceAbrufe();
				lineDauerServiceabrufeTage = lineDauerServiceabrufeTage + ";" + m.getFormattedAvgDauerTageServiceAbrufe();

				}
			}
			for (QuartalsStatistik qs : quartale.values()) 
			{
			einheiten = einheiten + ";" + qs.getBezeichnungLang();	
			lineGesamt = lineGesamt + ";" + qs.getAnzahlVorgaengeBerichtszeitraum();
			lineAnzahlIncidents = lineAnzahlIncidents + ";" + qs.getAnzahlIncidents();
			lineAnzahlIncidentsServicezeitNichtEingehalten = lineAnzahlIncidentsServicezeitNichtEingehalten + ";" + qs.getAnzahlIncidentsServicezeitNichtEingehalten();
			lineAnzahlIncidentsServicezeitNichtEingehaltenProzent = lineAnzahlIncidentsServicezeitNichtEingehaltenProzent + ";" + qs.getFormattedProzentanteilIncidentsServicezeitNichtEingehalten() + " %";
			lineDauerIncidentMinuten = lineDauerIncidentMinuten + ";" + qs.getDurchschnittlicheDauerMinutenIncidents();
			lineDauerIncidentStunden = lineDauerIncidentStunden + ";" + qs.getFormattedAvgDauerStundenIncidents();
			lineDauerIncidentTage = lineDauerIncidentTage + ";" + qs.getFormattedAvgDauerTageIncidents();
			
			lineAnzahlServiceabrufe = lineAnzahlServiceabrufe + ";" + qs.getAnzahlServiceAbrufe();
			lineAnzahlServiceabrufeServicezeitNichtEingehalten = lineAnzahlServiceabrufeServicezeitNichtEingehalten + ";" + qs.getAnzahlServiceAbrufeServicezeitNichtEingehalten();
			lineAnzahlServiceabrufeServicezeitNichtEingehaltenProzent = lineAnzahlServiceabrufeServicezeitNichtEingehaltenProzent + ";" + qs.getFormattedProzentanteilServiceAbrufeServicezeitNichtEingehalten() + " %";
			lineDauerServiceabrufeMinuten = lineDauerServiceabrufeMinuten + ";" + qs.getDurchschnittlicheDauerMinutenServiceAbrufe();
			lineDauerServiceabrufeStunden = lineDauerServiceabrufeStunden + ";" + qs.getFormattedAvgDauerStundenServiceAbrufe();
			lineDauerServiceabrufeTage = lineDauerServiceabrufeTage + ";" + qs.getFormattedAvgDauerTageServiceAbrufe();
			}
			lines.add(einheiten);
			lines.add(lineGesamt);
			lines.add(lineAnzahlIncidents);
			lines.add(lineAnzahlIncidentsServicezeitNichtEingehalten);
			lines.add(lineAnzahlIncidentsServicezeitNichtEingehaltenProzent);
			lines.add(lineDauerIncidentMinuten);
			lines.add(lineDauerIncidentStunden);
			lines.add(lineDauerIncidentTage);
			lines.add(lineAnzahlServiceabrufe);
			lines.add(lineAnzahlServiceabrufeServicezeitNichtEingehalten);
			lines.add(lineAnzahlServiceabrufeServicezeitNichtEingehaltenProzent);
			lines.add(lineDauerServiceabrufeMinuten);
			lines.add(lineDauerServiceabrufeStunden);
			lines.add(lineDauerServiceabrufeTage);

		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String dateiName = "EcholonZusammenfassung.csv";
		String dateiPfad = servletContext.getRealPath("/downloads");
		EcholonZusammenfassungCSV csv = new EcholonZusammenfassungCSV(dateiPfad);
		csv.setLines(lines);
		csv.createCSVDatei(dateiPfad, dateiName);
	}
	
	/*
	 * Aufteilung der gesamten Vorgänge nach Monaten	
	 */
	private void setMonatsStatistiken(ArrayList<Vorgang> incoming) 
	{
	monatsStatistiken =	new HashMap<Integer, MonatsStatistik>();
	int berichtsMonat = 0;
	
		for (Vorgang vorgang : incoming) 
		{
			berichtsMonat = vorgang.getBerichtsMonat();
			if (monatsStatistiken.containsKey(berichtsMonat)) 
			{
			monatsStatistiken.get(berichtsMonat).addVorgang(vorgang);	
			} 
			else 
			{
			MonatsStatistik m = new MonatsStatistik(LocalDate.of(vorgang.getBerichtsJahr(), berichtsMonat, 1));
			m.addVorgang(vorgang);
			monatsStatistiken.put(berichtsMonat, m);
			}		
			
		}
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Es wurden " + monatsStatistiken.size() + " Monatsstatisken erstellt.");}				
	}

	/*
	 * Filtert die Vorgänge deren Organisation mit HRG beginnt und die im Berichtszeitraum liegen
	 */
	private ArrayList<Vorgang> filter(Quartal incoming) 
	{
	vorgaengeBerichtszeitraum = new ArrayList<>();
	final LocalDateTime start = incoming.getStartDatumUhrzeit();
	final LocalDateTime ende = incoming.getEndDatumUhrzeit();
	
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Verarbeite Quartal " + incoming.getBezeichnungLang() + " für die Zeit vom " + Zeitraum.dfDatumUhrzeit.format(start) + " bis " + Zeitraum.dfDatumUhrzeit.format(ende));}	
	
	si = distinctCases.values().stream();
	vorgaengeBerichtszeitraum = si.filter(v -> v.getOrganisation().startsWith("HRG") && v.getErstellDatumZeit().isAfter(start) && v.getErstellDatumZeit().isBefore(ende)).collect(Collectors.toCollection(ArrayList::new ));
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Es wurden " + vorgaengeBerichtszeitraum.size() + " Vorgänge für die Zeit vom " + Zeitraum.dfDatumUhrzeit.format(start) + " bis " + Zeitraum.dfDatumUhrzeit.format(ende) + " gefiltert");}
	
	return vorgaengeBerichtszeitraum;
	}


	/*
	 * Ermittelt den jüngsten Vorgang und legt fest,
	 * welches der oberste Node im Baum sein wird
	 */
    private void setMinMaxVorgang() 
    {
	max = distinctCases.values().stream().max(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
	}
    
	public StreamedContent getFile() 
	{
	StreamedContent file = null;	
    InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/downloads/EcholonZusammenfassung.csv");
    file = new DefaultStreamedContent(stream, "application/csv", "EcholonZusammenfassung.csv");
	return file;
	}	
	
	public Cache getCache() {return cache;}
	public void setCache(Cache cache) {this.cache = cache;}

	public Object[] getQuartale()
	{
	return quartale.values().toArray();	
	}

	public String getThema() {
		return thema;
	}

	public String getSubtitle() 
	{
	return "AuswertungsZeitraum: " + Zeitraum.df.format(abfrageZeitraum.getStartDatum()) + " bis " + Zeitraum.df.format(abfrageZeitraum.getEndDatum());
	}

}
