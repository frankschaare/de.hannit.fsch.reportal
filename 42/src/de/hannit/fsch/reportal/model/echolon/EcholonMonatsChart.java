/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
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

import de.hannit.fsch.reportal.db.DataBaseThread;
import de.hannit.fsch.reportal.db.EcholonDBManager;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
@ManagedBean
@SessionScoped
public class EcholonMonatsChart 
{
private final static Logger log = Logger.getLogger(EcholonDBManager.class.getSimpleName());
private Stream<Vorgang> si = null;
private DataBaseThread echolonAbfrage = null;
private Future<HashMap<String, Vorgang>> result = null;
private ExecutorService executor = Executors.newCachedThreadPool();
private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();
private ArrayList<Vorgang> vorgaengeBerichtszeitraum = null;
private TreeMap<LocalDate, MonatsStatistik> monatsStatistiken = new TreeMap<LocalDate, MonatsStatistik>();
private Zeitraum standardZeitraum = null;
private Zeitraum abfrageZeitraum = null;
private int selectedZeitraum = 0;
private long maxValue = 0;
private String datumsFormat = "dd.MM.yyyy";
private DateTimeFormatter df = DateTimeFormatter.ofPattern(datumsFormat);
private String ticks = null;
private String seriesGesamt = null;
private String seriesIncidents  = null;
private String seriesServiceAbruf  = null;
private String seriesServiceAnfragen  = null;
private String seriesServiceInfo  = null;
private String seriesBeschwerden  = null;
private String seriesCustomerRequest  = null;
private String seriesShortCall  = null;
private String seriesWorkOrder  = null;
private String seriesAVGWartezeit = null;
private Vorgang max = null;
private Vorgang min = null;

	/**
	 * Managed Bean für die Darstellung der Echolon-Daten im Chart 
	 */
	public EcholonMonatsChart() 
	{
	// Standardmässig werden die Echolon Daten der vergangenen vier Quartale abgefragt.
	echolonAbfrage = new DataBaseThread();
	result = executor.submit(echolonAbfrage);
		try 
		{
		distinctCases = result.get();
		setMinMaxVorgang();
		} 
		catch (InterruptedException | ExecutionException e) 
		{
		e.printStackTrace();
		}		
	setSelectedZeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE);
	
	LocalDate start = abfrageZeitraum.getStartDatum();
	LocalDate end = abfrageZeitraum.getEndDatum();

	log.log(Level.INFO, this.getClass().getName() + ": Filtere Daten für den Abfragezeitraum vom " + Zeitraum.df.format(start) + " bis " + Zeitraum.df.format(end));
	si = distinctCases.values().parallelStream(); 
	vorgaengeBerichtszeitraum = si.filter(v -> (v.getErstellDatum().isAfter(start) || v.getErstellDatum().isEqual(start)) && (v.getErstellDatum().isBefore(end) || v.getErstellDatum().isEqual(end))).collect(Collectors.toCollection(ArrayList::new ));
	log.log(Level.INFO, this.getClass().getName() + ": Für den Abfragezeitraum vom " + Zeitraum.df.format(start) + " bis " + Zeitraum.df.format(end) + " wurden " + vorgaengeBerichtszeitraum.size() + " Vorgänge gefiltert.");

	setMonatsstatistiken();
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
	
	/*
	 * Aufteilung der gesamten Vorgänge nach Monaten,
	 * der der Chart die Werte monatsweise ausgibt.	
	 */
	private void setMonatsstatistiken() 
	{
	LocalDateTime erstellDatum;
	LocalDate monat;	
	TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(); 
	
		for (Vorgang v : vorgaengeBerichtszeitraum) 
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
		m.setStatistik();	
		}
	}
	
    public int getSelectedZeitraum() {return selectedZeitraum;}

	public void setSelectedZeitraum(int selectedZeitraum) 
	{
	this.selectedZeitraum = selectedZeitraum;
	
		switch (selectedZeitraum) 
		{
		case Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE:
		abfrageZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE, max);
		break;

		case Zeitraum.BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE:
		abfrageZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE, null);
		break;

		default:
			break;
		}

	// echolonAbfrage.setAbfrageZeitraum(abfrageZeitraum);
	}

	public long getMaxValue() {return maxValue;}

	public int getAnzahlVorgaengeGesamt()
	{
	return distinctCases.size();	
	}
	
	public String getTicks() 
	{
	ticks = "[";	
    	for (MonatsStatistik m : monatsStatistiken.values()) 
    	{
    	ticks = ticks + "'" + m.getBerichtsZeitraum().getBerichtsMonat() + "',";	
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

	public String getSeriesBeschwerden() 
	{
	seriesBeschwerden = "[";	
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		seriesBeschwerden = seriesBeschwerden + m.getAnzahlBeschwerden() + ",";	
		}
	seriesBeschwerden = seriesBeschwerden + "]";	
	
	return seriesBeschwerden;
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
	
	public String getSeriesServiceabrufe() 
	{
	seriesServiceAbruf = "[";	
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		seriesServiceAbruf = seriesServiceAbruf + m.getAnzahlServiceAbrufe() + ",";	
		}
	seriesServiceAbruf = seriesServiceAbruf + "]";	
	
	return seriesServiceAbruf;
	}
	
	public String getSeriesServiceAnfragen() 
	{
	seriesServiceAnfragen = "[";	
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		seriesServiceAnfragen = seriesServiceAnfragen + m.getAnzahlServiceAnfragen() + ",";	
		}
	seriesServiceAnfragen = seriesServiceAnfragen + "]";	
	
	return seriesServiceAnfragen;
	}		

	public String getSeriesServiceInfos() 
	{
	seriesServiceInfo = "[";	
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		seriesServiceInfo = seriesServiceInfo + m.getAnzahlServiceInfo() + ",";	
		}
	seriesServiceInfo = seriesServiceInfo + "]";	
	
	return seriesServiceInfo;
	}
	
	public String getSeriesShortCalls() 
	{
	seriesShortCall = "[";	
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		seriesShortCall = seriesShortCall + m.getAnzahlShortCalls() + ",";	
		}
	seriesShortCall = seriesShortCall + "]";	
	
	return seriesShortCall;
	}
	
	public String getSeriesWorkOrders() 
	{
	seriesWorkOrder = "[";	
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		seriesWorkOrder = seriesWorkOrder + m.getAnzahlWorkOrders() + ",";	
		}
	seriesWorkOrder = seriesWorkOrder + "]";	
	
	return seriesWorkOrder;
	}
	
	public String getSeriesCustomerRequests() 
	{
	seriesCustomerRequest = "[";	
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		seriesCustomerRequest = seriesCustomerRequest + m.getAnzahlCustomerRequests() + ",";	
		}
	seriesCustomerRequest = seriesCustomerRequest + "]";	
	
	return seriesCustomerRequest;
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
