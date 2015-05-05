/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.hannit.fsch.reportal.db.EcholonDBThread;
import de.hannit.fsch.reportal.model.Zeitraum;
import de.hannit.fsch.reportal.model.callcenter.CallcenterKWStatistik;

/**
 * @author fsch
 *
 */
@ManagedBean
@SessionScoped
public class EcholonMonatsChart 
{
private EcholonDBThread echolonAbfrage = null;
private Future<HashMap<String, Vorgang>> result = null;
private ExecutorService executor = Executors.newCachedThreadPool();
private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();
private TreeMap<LocalDate, MonatsStatistik> monatsStatistiken = new TreeMap<LocalDate, MonatsStatistik>();
private Zeitraum standardZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE);
private Zeitraum abfrageZeitraum = null;
private int selectedZeitraum = 0;
private long maxValue = 0;
private String datumsFormat = "dd.MM.yyyy";
private DateTimeFormatter df = DateTimeFormatter.ofPattern(datumsFormat);
private String ticks = null;
private String seriesGesamt = null;
private String seriesIncidents  = null;
private String seriesAVGWartezeit = null;

	/**
	 * Managed Bean für die Darstellung der Echolon-Daten im Chart 
	 */
	public EcholonMonatsChart() 
	{
	// Standardmässig werden die Echolon Daten der vergangenen vier Quartale abgefragt.
	echolonAbfrage = new EcholonDBThread();
	setSelectedZeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE);
	}
	
	/*
	 * Aufteilung der gesamten Vorgänge nach Monaten,
	 * der der Chart die Werte monatsweise ausgibt.	
	 */
	private void setMonatsstatistiken() 
	{
	LocalDateTime erstellDatum;
	LocalDate monat;	
	TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(); 
	
		for (Vorgang v : distinctCases.values()) 
		{
		erstellDatum = v.getErstellDatumZeit();
		int weekNumber = erstellDatum.get(woy);
		monat = LocalDate.of(erstellDatum.getYear(), erstellDatum.getMonthValue(), 1);
			if (monatsStatistiken.containsKey(monat)) 
			{
			monatsStatistiken.get(monat).addVorgang(v);	
			} 
			else 
			{
			MonatsStatistik m = new MonatsStatistik(monat);
			m.addVorgang(v);
			monatsStatistiken.put(monat, m);
			}
		}
		
		// Nachdem alle Vorgänge sortiert sind, werden die Monatswerte berechnet:
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		m.setMonatswerte();	
		}
	}
	
    public int getSelectedZeitraum() {return selectedZeitraum;}

	public void setSelectedZeitraum(int selectedZeitraum) 
	{
	this.selectedZeitraum = selectedZeitraum;
	
		switch (selectedZeitraum) 
		{
		case Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE:
		abfrageZeitraum = standardZeitraum;
		break;

		case Zeitraum.BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE:
		abfrageZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE);
		break;

		default:
			break;
		}

	echolonAbfrage.setAbfrageZeitraum(abfrageZeitraum);
	result = executor.submit(echolonAbfrage);
		try 
		{
		distinctCases = result.get();
		setMonatsstatistiken();
		} 
		catch (InterruptedException | ExecutionException e) 
		{
		e.printStackTrace();
		}		
	}

	public long getMaxValue() {return maxValue;}

	public int getAnzahlVorgaengeGesamt()
	{
	return distinctCases.size();	
	}
	
	public String getTicks() 
	{
	DateTimeFormatter mf = DateTimeFormatter.ofPattern("MMMM yy"); 	
	ticks = "[";	
    	for (MonatsStatistik m : monatsStatistiken.values()) 
    	{
    	ticks = ticks + "'" + mf.format(m.getBerichtsZeitraum()) + "',";	
		}
    ticks = ticks + "]";
    
    return ticks;
	}
	
	public String getSeriesGesamt() 
	{
	seriesGesamt = "[";	
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		seriesGesamt = seriesGesamt + m.getAnzahlVorgaengeGesamt() + ",";	
		}
	seriesGesamt = seriesGesamt + "]";	
	
	return seriesGesamt;
	}	
	
	public String getSeriesIncidents() 
	{
	seriesIncidents = "[";	
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		seriesIncidents = seriesIncidents + m.getAnzahlIncidents() + ",";	
		}
	seriesIncidents = seriesIncidents + "]";	
	
	return seriesIncidents;
	}	
	
	public String getseriesAVGWartezeit() 
	{
	seriesAVGWartezeit = "[";	
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		seriesAVGWartezeit = seriesAVGWartezeit + m.getDurchschnittlicheDauerMinutenIncidents() + ",";	
		}
	seriesAVGWartezeit = seriesAVGWartezeit + "]";	
	
	return seriesAVGWartezeit;
	}		
	
	public String getSubtitle() 
	{
	return "'Auswertungszeitraum: " + df.format(abfrageZeitraum.getStartDatum()) + " bis " + df.format(abfrageZeitraum.getEndDatum()) + "'";
	}

}
