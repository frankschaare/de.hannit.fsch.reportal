/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
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
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import de.hannit.fsch.reportal.db.CallcenterDBThread;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
@ManagedBean
@SessionScoped
public class CallcenterChart 
{
private CallcenterDBThread callcenterAbfrage = null;
private Future<TreeMap<LocalDateTime, CallcenterStatistik>> result = null;
private ExecutorService executor = Executors.newCachedThreadPool();
private Zeitraum standardZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE);
private int selectedZeitraum = 0;
private LineChartModel lineModel;	
private long maxValue = 0;
private String ticks = null;
private String seriesEingehendeAnrufe = null;
private String seriesAnsagetext = null;
private String seriesErfolglos = null;
private String chartTitle = null;

private TreeMap<LocalDateTime, CallcenterStatistik> statisiken;
private TreeMap<String, CallcenterStatistik> kwStatisiken;

	/**
	 * Managed Bean für die Darstellung der Echolon-Daten im Chart 
	 */
	public CallcenterChart() 
	{
	// Standardmässig werden die Echolon Daten der vergangenen vier Quartale abgefragt.
	callcenterAbfrage = new CallcenterDBThread();
	setSelectedZeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE);
	setKWStatistiken();
	createLineModel();
	}
	
	/*
	 * Erstellt eine Statistik der Callcenter-Auswertungen nach Kalenderwochen
	 */
	private void setKWStatistiken() 
	{
	TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
	int wochenNummer = 0;
	String key = null;
	
	kwStatisiken = new TreeMap<String, CallcenterStatistik>();
	CallcenterStatistik vorhanden = null;
		for (CallcenterStatistik cs : statisiken.values()) 
		{
		wochenNummer = cs.getStartZeit().get(woy);
		cs.setWochenNummer(wochenNummer);
		/*
		 * Die KW 1 bereitet Kummer.
		 * Sie kann sowohl am Anfang, als auch am Ende der Serie liegen.
		 * 
		 * Handelt es sich also um einen Januarwert, wird das aktuelle Jahr zum Index hinzugefügt.
		 * Falls nicht, wird das Folgejahr zum Index hinzugefügt.
		 */
			switch (wochenNummer) 
			{
			case 1:
				if (cs.getStartZeit().getMonthValue() == 1) 
				{
				key = (String.valueOf(cs.getStartZeit().getYear() + String.valueOf(wochenNummer)));
				}
				else
				{
				key = (String.valueOf(cs.getStartZeit().plusYears(1).getYear() + "0" + String.valueOf(wochenNummer)));	
				}
			break;

			default:
			key = (String.valueOf(cs.getStartZeit().getYear() + String.valueOf(wochenNummer)));
			break;
			}
			
			if (! kwStatisiken.containsKey(key)) 
			{
			kwStatisiken.put(key, cs);	
			} 
			else 
			{
			vorhanden = kwStatisiken.get(key);
				if (cs.getStartZeit().isBefore(vorhanden.getStartZeit())) 
				{
				vorhanden.setStartZeit(cs.getStartZeit());	
				}
				if (cs.getEndZeit().isAfter(vorhanden.getEndZeit())) 
				{
				vorhanden.setEndZeit(cs.getEndZeit());	
				}
			vorhanden.setEingehendeAnrufe((vorhanden.getEingehendeAnrufe() + cs.getEingehendeAnrufe()));
			vorhanden.setAnrufeInWarteschlange((vorhanden.getAnrufeInWarteschlange() + cs.getAnrufeInWarteschlange()));
			vorhanden.setInWarteschlangeAufgelegt((vorhanden.getInWarteschlangeAufgelegt() + cs.getInWarteschlangeAufgelegt()));
			}
		}
	}

    public LineChartModel getLineModel() {return lineModel;}
	
	private void createLineModel() 
    {
	lineModel = new LineChartModel();
    lineModel.setTitle("Linear Chart");
    lineModel.setLegendPosition("ne");
    
    LineChartSeries eingehendeAnrufe = new LineChartSeries();
    eingehendeAnrufe.setLabel("Anrufe");
    
    	
    	for (CallcenterStatistik cs : kwStatisiken.values()) 
    	{
    	String test = cs.getChartZeit();	
    	eingehendeAnrufe.set(cs.getChartZeit(), cs.getEingehendeAnrufe());	
		}
    
    lineModel.addSeries(eingehendeAnrufe);
    // DateAxis axis = new DateAxis();
    // lineModel.getAxes().put(AxisType.X, axis);
    
    lineModel.setExtender("ext");	
    }
    
    private void createBarModel() 
    {    
    // barModel.setTitle("Auswertungszeitraum: " + echolonAbfrage.getAbfrageZeitraum().getBerichtszeitraumStart() + " bis " + echolonAbfrage.getAbfrageZeitraum().getBerichtszeitraumEnde());

    // yAxis.setMax(maxValue + ((maxValue / 100) * 10));
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
		selected = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE);
		break;

		default:
			break;
		}

	callcenterAbfrage.setAbfrageZeitraum(selected);
	result = executor.submit(callcenterAbfrage);
		try 
		{
		statisiken = result.get();
		this.chartTitle = "'Auswertungszeitraum:  " + selected.getBerichtszeitraumStart() + " bis " + selected.getBerichtszeitraumEnde() + "'";
		} 
		catch (InterruptedException | ExecutionException e) 
		{
		e.printStackTrace();
		}		
	}
	
	public String getChartTitle() {return chartTitle;}

	public String getTicks() 
	{
	ticks = "[";	
    	for (CallcenterStatistik cs : kwStatisiken.values()) 
    	{
    	ticks = ticks + (cs.getWochenNummer() < 10 ? "0" + cs.getWochenNummer() : cs.getWochenNummer()) + ",";	
		}
    ticks = ticks + "]";
    
    return ticks;
	}

	public String getSeriesEingehendeAnrufe() 
	{
	seriesEingehendeAnrufe = "[";	
    	for (CallcenterStatistik cs : kwStatisiken.values()) 
    	{
    	seriesEingehendeAnrufe = seriesEingehendeAnrufe + cs.getEingehendeAnrufe() + ",";	
		}
    seriesEingehendeAnrufe = seriesEingehendeAnrufe + "]";
	
	return seriesEingehendeAnrufe;
	}
	
	public String getSeriesAnsagetext() 
	{
	seriesAnsagetext = "[";	
    	for (CallcenterStatistik cs : kwStatisiken.values()) 
    	{
    	seriesAnsagetext = seriesAnsagetext + cs.getAnrufeInWarteschlange() + ",";	
		}
    seriesAnsagetext = seriesAnsagetext + "]";
    
	return seriesAnsagetext;
	}

	public String getSeriesErfolglos() 
	{
	seriesErfolglos = "[";	
    	for (CallcenterStatistik cs : kwStatisiken.values()) 
    	{
    	seriesErfolglos = seriesErfolglos + cs.getInWarteschlangeAufgelegt() + ",";	
		}
    seriesErfolglos = seriesErfolglos + "]";	
	
	return seriesErfolglos;
	}

	public long getMaxValue() {return maxValue;}

	public int getAnzahlVorgaengeGesamt()
	{
	return statisiken.size();	
	}

}
