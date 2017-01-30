/**
 * 
 */
package de.hannit.fsch.reportal.db;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
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
 * Lädt die Daten der letzten vier Quartale aud der Echolon View
 * und bereited diese für die Zusammenfassung auf
 *
 */
@ManagedBean(name = "ez")
@SessionScoped
public class EcholonZusammenfassung 
{
private final static Logger log = Logger.getLogger(EcholonDBManager.class.getSimpleName());		
private String thema = "Zusammenfassung";
private Zeitraum abfrageZeitraum = null;
private String datumsFormat = "dd.MM.yyyy";
private DateTimeFormatter df = DateTimeFormatter.ofPattern(datumsFormat);

private ExecutorService executor = Executors.newCachedThreadPool();
private DataBaseThread dbThread = null;
private Future<HashMap<String, Vorgang>> result = null;
private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();	
private ArrayList<Vorgang> vorgaenge = null;
private TreeMap<Integer, QuartalsStatistik> quartale = new TreeMap<Integer, QuartalsStatistik>();
private ArrayList<String> lines = null;
private Stream<Vorgang> si = null;
private ArrayList<Vorgang> vorgaengeBerichtszeitraum;
private Vorgang max = null;
private Vorgang min = null;

	/**
	 * 
	 */
	public EcholonZusammenfassung() 
	{
	
	dbThread =  new DataBaseThread();
	log.log(Level.INFO, "Lade Daten aus der Datenbank");	

		// DB-Abfrage starten:
		try 
		{
		result = executor.submit(dbThread);			
		distinctCases = result.get();
		setMinMaxVorgang();

		// Standardabfragezeitraum über die letzen vier Quartale:
		abfrageZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE, max);
		LocalDate start = abfrageZeitraum.getStartDatum();
		LocalDate end = abfrageZeitraum.getEndDatum();
	
		log.log(Level.INFO, this.getClass().getName() + ": Filtere Daten für den Abfragezeitraum vom " + Zeitraum.df.format(start) + " bis " + Zeitraum.df.format(end));
		si = distinctCases.values().parallelStream(); 
		vorgaengeBerichtszeitraum = si.filter(v -> (v.getErstellDatum().isAfter(start) || v.getErstellDatum().isEqual(start)) && (v.getErstellDatum().isBefore(end) || v.getErstellDatum().isEqual(end))).collect(Collectors.toCollection(ArrayList::new ));
		log.log(Level.INFO, this.getClass().getName() + ": Für den Abfragezeitraum vom " + Zeitraum.df.format(start) + " bis " + Zeitraum.df.format(end) + " wurden " + vorgaengeBerichtszeitraum.size() + " Vorgänge gefiltert.");
		
		log.log(Level.INFO, this.getClass().getName() + ": Verarbeite Monatsstatistiken für den Abfragezeitraum vom " + Zeitraum.df.format(start) + " bis " + Zeitraum.df.format(end));
		TreeMap<LocalDate, MonatsStatistik> monatsstatistiken = new TreeMap<>();
		LocalDate berichtsMonat = null;
		MonatsStatistik aktuell = null;
		
			for (Vorgang vorgang : vorgaengeBerichtszeitraum) 
			{
			berichtsMonat = LocalDate.of(vorgang.getErstellDatum().getYear(), vorgang.getErstellDatum().getMonthValue(), 1);
				if (monatsstatistiken.containsKey(berichtsMonat)) 
				{
				monatsstatistiken.get(berichtsMonat).addVorgang(vorgang);	
				} 
				else 
				{
				aktuell = new MonatsStatistik(berichtsMonat);	
				aktuell.addVorgang(vorgang);
				monatsstatistiken.put(berichtsMonat, aktuell);
				}
			}
			log.log(Level.INFO, this.getClass().getName() + ": Es wurden " + monatsstatistiken.size() + " Monatsstatistiken für den Abfragezeitraum vom " + Zeitraum.df.format(start) + " bis " + Zeitraum.df.format(end) + " erstellt.");			
		
			log.log(Level.INFO, this.getClass().getName() + ": Erstelle Quartalsstatistiken für den Abfragezeitraum vom " + Zeitraum.df.format(start) + " bis " + Zeitraum.df.format(end));
			for (Quartal q : abfrageZeitraum.getQuartale().values()) 
			{
			QuartalsStatistik qs = new QuartalsStatistik(q);
			Quartal msQ = null;
			
				for (MonatsStatistik ms : monatsstatistiken.values()) 
				{
				ms.setStatistik();
				msQ = ms.getBerichtsZeitraum().getAuswertungsQuartal();
				
					if (msQ.getQuartalsJahr() == q.getQuartalsJahr() && msQ.getQuartalsNummer() == q.getQuartalsNummer()) 
					{
					qs.addMonatsstatistik(ms);	
					}
				}
			qs.setStatistik();	
			quartale.put(q.getIndex(), qs);	
			}
			log.log(Level.INFO, this.getClass().getName() + ": Es wurden " + quartale.size() + " Quartalsstatistiken für den Abfragezeitraum vom " + Zeitraum.df.format(start) + " bis " + Zeitraum.df.format(end) + " erstellt.");
			
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
		catch (InterruptedException | ExecutionException e) 
		{
		e.printStackTrace();
		}
	}
	
	/*
	 * Ermittelt den jüngsten Vorgang und legt fest,
	 * welches der oberste Node im Baum sein wird
	 */
    private void setMinMaxVorgang() 
    {
	max = distinctCases.values().stream().max(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
	min = distinctCases.values().stream().min(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
	}
	
	public StreamedContent getFile() 
	{
	StreamedContent file = null;	
    InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/downloads/EcholonZusammenfassung.csv");
    file = new DefaultStreamedContent(stream, "application/csv", "EcholonZusammenfassung.csv");
	return file;
	}	
	
	public Object[] getQuartale()
	{
	return quartale.values().toArray();	
	}

	public String getThema() {
		return thema;
	}

	public String getSubtitle() 
	{
	return "AuswertungsZeitraum: " + df.format(abfrageZeitraum.getStartDatum()) + " bis " + df.format(abfrageZeitraum.getEndDatum());
	}

}
