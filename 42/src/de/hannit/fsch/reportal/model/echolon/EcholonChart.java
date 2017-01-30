/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import de.hannit.fsch.reportal.db.EcholonDBThread;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
@ManagedBean
@SessionScoped
public class EcholonChart 
{
private EcholonDBThread echolonAbfrage = null;
private Future<HashMap<String, Vorgang>> result = null;
private ExecutorService executor = Executors.newCachedThreadPool();
private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();
private TreeMap<LocalDate, MonatsStatistik> monatsStatistiken = new TreeMap<LocalDate, MonatsStatistik>();
private Zeitraum standardZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE, null);
private int selectedZeitraum = 0;
private BarChartModel barModel;	
private long maxValue = 0;
private int anzVorgaenge = 0;
private long anzIncidents = 0;
private int avgDauerIncidents = 0;

	/**
	 * Managed Bean für die Darstellung der Echolon-Daten im Chart 
	 */
	public EcholonChart() 
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
	}
	
    public BarChartModel getBarModel() 
    {
    return barModel;
    }
	
    private void createBarModel() 
    {
    barModel = initBarModel();
         
    barModel.setTitle("Auswertungszeitraum: " + echolonAbfrage.getAbfrageZeitraum().getBerichtszeitraumStart() + " bis " + echolonAbfrage.getAbfrageZeitraum().getBerichtszeitraumEnde());
    barModel.setLegendPosition("ne");
          
    Axis yAxis = barModel.getAxis(AxisType.Y);
    yAxis.setMin(0);
    yAxis.setMax(maxValue + ((maxValue / 100) * 10));
    }
    
    private BarChartModel initBarModel() 
    {
    BarChartModel model = new BarChartModel();
 
    ChartSeries anzahlVorgaenge = new ChartSeries();
    anzahlVorgaenge.setLabel("Anzahl Vorgänge Gesamt");
    
    ChartSeries anzahlIncidents = new ChartSeries();
    anzahlIncidents.setLabel("Anzahl Incidents");
    
    ChartSeries durchschnittlicheWartezeit = new ChartSeries();
    durchschnittlicheWartezeit.setLabel("durchschnittliche Dauer Incident (Minuten)");
    
    	for (MonatsStatistik m : monatsStatistiken.values()) 
    	{
    	String berichtsMonat = m.getBerichtsMonatAsString();
    			
   		anzVorgaenge = m.getAnzahlVorgaengeGesamt();
   		maxValue = anzVorgaenge > maxValue ? anzVorgaenge : maxValue;
   		
   		anzIncidents = m.getAnzahlIncidents();
   		maxValue = anzIncidents > maxValue ? anzIncidents : maxValue;
   		
   		avgDauerIncidents = m.getDurchschnittlicheDauerMinutenIncidents();
   		maxValue = avgDauerIncidents > maxValue ? avgDauerIncidents : maxValue;
   		
   		anzahlVorgaenge.set(berichtsMonat, anzVorgaenge);
   		anzahlIncidents.set(berichtsMonat, anzIncidents);
   		durchschnittlicheWartezeit.set(berichtsMonat, avgDauerIncidents);
		}

	model.addSeries(anzahlVorgaenge);
	model.addSeries(anzahlIncidents);
	model.addSeries(durchschnittlicheWartezeit);
        
    model.setExtender("ext");
    
    return model;
    }  	
    
	public int getSelectedZeitraum() {return selectedZeitraum;}

	public void setSelectedZeitraum(int selectedZeitraum) 
	{
	this.selectedZeitraum = selectedZeitraum;
	Zeitraum selected = null;
		switch (selectedZeitraum) 
		{
		case Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE:
		selected = standardZeitraum;
		break;

		case Zeitraum.BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE:
		selected = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE, null);
		break;

		default:
			break;
		}

	echolonAbfrage.setAbfrageZeitraum(selected);
	result = executor.submit(echolonAbfrage);
		try 
		{
		distinctCases = result.get();
		setMonatsstatistiken();
		createBarModel();
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

}
