/**
 * 
 */
package de.hannit.fsch.reportal.db;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
 * Lädt die Daten der letzten drei Monate für die HRG aus der Echolon View
 * und bereitet diese für die Zusammenfassung auf
 *
 */
@ManagedBean(name = "hrg")
@SessionScoped
public class HRGZusammenfassung 
{
private String thema = "Zusammenfassung";
private Zeitraum abfrageZeitraum = null;
private String datumsFormat = "dd.MM.yyyy";
private DateTimeFormatter df = DateTimeFormatter.ofPattern(datumsFormat);

private ExecutorService executor = Executors.newCachedThreadPool();
private HRGDBThread dbThread = null;
private Future<HashMap<String, Vorgang>> result = null;

private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();
private TreeMap<Integer, QuartalsStatistik> quartale = new TreeMap<Integer, QuartalsStatistik>();
private ArrayList<String> lines = null;

	/**
	 * 
	 */
	public HRGZusammenfassung() 
	{
	// Standardabfragezeitraum über die letzen drei Monate:
	abfrageZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTES_QUARTAL, null);
	
	dbThread = new HRGDBThread();
	dbThread.setAbfrageZeitraum(abfrageZeitraum);
	
		// DB-Abfrage starten:
		try 
		{
		result = executor.submit(dbThread);			
		distinctCases = result.get();
		
			for (Quartal q : abfrageZeitraum.getQuartale().values()) 
			{
			QuartalsStatistik qs = new QuartalsStatistik(q);
			
				for (Vorgang v : distinctCases.values()) 
				{
					if (v.getErstellDatumZeit().isAfter(q.getStartDatumUhrzeit()) && v.getErstellDatumZeit().isBefore(q.getEndDatumUhrzeit())) 
					{
					// qs.addVorgang(v);	
					}
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
		catch (InterruptedException | ExecutionException e) 
		{
		e.printStackTrace();
		}
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
