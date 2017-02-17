/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import de.hannit.fsch.reportal.db.Cache;
import de.hannit.fsch.reportal.model.Chart;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
@ManagedBean
@RequestScoped
public class EcholonMonatsChart implements Serializable
{
private static final long serialVersionUID = -7758525667371440750L;

@ManagedProperty (value = "#{cache}")
private Cache cache;	
@ManagedProperty (value = "#{chart}")
private Chart chart;	

private FacesContext fc = null;

private final static Logger log = Logger.getLogger(EcholonMonatsChart.class.getSimpleName());
private String logPrefix = this.getClass().getCanonicalName() + ": ";

private Stream<Vorgang> si = null;
private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();
private ArrayList<Vorgang> vorgaengeBerichtszeitraum = null;
private TreeMap<LocalDate, MonatsStatistik> monatsStatistiken = new TreeMap<LocalDate, MonatsStatistik>();
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
/**
	 * Managed Bean für die Darstellung der Echolon-Daten im Chart 
	 */
	public EcholonMonatsChart() 
	{
	fc = FacesContext.getCurrentInstance();
		try 
		{
		distinctCases = cache.getDistinctCases();
		} 
		catch (NullPointerException e) 
		{
		cache = fc.getApplication().evaluateExpressionGet(fc, "#{cache}", Cache.class);
		distinctCases = cache.getDistinctCases();
		}
		
	chart = chart != null ? chart : fc.getApplication().evaluateExpressionGet(fc, "#{chart}", Chart.class);	
		
	setMinMaxVorgang();
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
	}	
	
	/*
	 * Aufteilung der gesamten Vorgänge nach Monaten,
	 * der der Chart die Werte monatsweise ausgibt.	
	 */
	private void setMonatsstatistiken() 
	{
	LocalDateTime erstellDatum;
	LocalDate monat;	
	
		for (Vorgang v : vorgaengeBerichtszeitraum) 
		{
		erstellDatum = v.getErstellDatumZeit();
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
	
	public LineChartModel getSLAStatusChartModel() 
	{
	LineChartModel model = new LineChartModel();
	model.setSeriesColors(chart.getColorIncidents() + "," + chart.getColorServiceAbrufe());
	model.setAnimate(true);
	
	LineChartSeries sIncidents = new LineChartSeries();
	sIncidents.setLabel("Incidents");
    
	LineChartSeries sServiceAbrufe = new LineChartSeries();
	sServiceAbrufe.setLabel("ServiceAbrufe");

    	for (MonatsStatistik m : monatsStatistiken.values()) 
    	{
    	sIncidents.set(m.getBerichtsMonatAsString(), m.getProzentanteilIncidentsServicezeitNichtEingehaltenAsFloat());	
    	sServiceAbrufe.set(m.getBerichtsMonatAsString(), m.getProzentanteilServiceAbrufeServicezeitNichtEingehaltenAsFloat());
		}
 
    model.addSeries(sIncidents);
    model.addSeries(sServiceAbrufe);
         
    model.setTitle(getPrimeFacesSubtitle());
    model.setLegendPosition("ne");
    model.setMouseoverHighlight(true);
    model.setShowDatatip(true);
    model.setShowPointLabels(true);
    
    Axis xAxis = new CategoryAxis("Berichtmonat");
    model.getAxes().put(AxisType.X, xAxis);
    Axis yAxis = model.getAxis(AxisType.Y);
    yAxis.setLabel("Prozentanteil Servicezeit nicht eingehalten");
    
    return model;
    }
	
	public CartesianChartModel getTageszeitCombinedModel() 
	{
	TreeMap<Integer, StundenStatistik> stundenStatistiken = new TreeMap<>();
	Integer berichtsStunde = 0;
	int anzahlSortierterVorgaenge = 0;
	int maxY = 0;

	
	CartesianChartModel  model = new BarChartModel();
	
	BarChartSeries sVorgaengeGesamt = new BarChartSeries();
	sVorgaengeGesamt.setLabel("Vorgänge gesamt");

    LineChartSeries sAVGLoesungszeitIncidents = new LineChartSeries();
    sAVGLoesungszeitIncidents.setLabel("&#216; Dauer Incidents");

    LineChartSeries sAVGLoesungszeitSA = new LineChartSeries();
    sAVGLoesungszeitSA.setLabel("&#216; Dauer Serviceabrufe");
    
    	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Starte Sortierung der vorhandenen " +  monatsStatistiken.size() + " Monatsstatistiken nach Uhrzeit");}
    	for (MonatsStatistik m : monatsStatistiken.values()) 
    	{
    	anzahlSortierterVorgaenge += m.getVorgaengeBerichtszeitraum().size();	
    	
    		for (Vorgang vorgang : m.getVorgaengeBerichtszeitraum()) 
    		{
			berichtsStunde = vorgang.getErstellZeit().getHour();
			
				if (stundenStatistiken.containsKey(berichtsStunde)) 
				{
				stundenStatistiken.get(berichtsStunde).addVorgang(vorgang);	
				} 
				else 
				{
					// Es werden nur Stundenstatistiken zwischen 06:00 und 18:00 Uhr erstellt
					if (berichtsStunde > 5 && berichtsStunde < 19) 
					{
					StundenStatistik ss = new StundenStatistik(vorgang.getErstellDatumZeit());
					ss.addVorgang(vorgang);
					stundenStatistiken.put(berichtsStunde, ss);
						
					}
				}	
			}
		}
       	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Sortierung abgeschlossen. Es wurden " +  anzahlSortierterVorgaenge + " Vorgänge in " + stundenStatistiken.size() + " Stundenstatistiken sortiert.");}    	
    	
       	int avg = 0;
       	for (StundenStatistik ss : stundenStatistiken.values()) 
       	{
		ss.setStatistik();
		sVorgaengeGesamt.set(ss.getLabel(), ss.getAnzahlVorgaengeBerichtszeitraum());	

		avg = ss.getDurchschnittlicheDauerMinutenIncidents();
		sAVGLoesungszeitIncidents.set(ss.getLabel(), avg);
		maxY = avg > maxY ? avg : maxY;
		
		avg = ss.getDurchschnittlicheDauerMinutenServiceAbrufe();
		sAVGLoesungszeitSA.set(ss.getLabel(), avg);
		maxY = avg > maxY ? avg : maxY;
		
		}
 
     model.addSeries(sVorgaengeGesamt);
     model.addSeries(sAVGLoesungszeitIncidents);
     model.addSeries(sAVGLoesungszeitSA);
         
    model.setTitle("Gesamtaufträge nach Tageszeit " + getPrimeFacesSubtitle() + " (" + anzahlSortierterVorgaenge + " Vorgänge)");
    model.setLegendPosition("nw");
    model.setMouseoverHighlight(true);
    model.setShowDatatip(true);
    model.setShowPointLabels(true);
    model.setAnimate(true);
    model.setSeriesColors(chart.getColorServiceInfos() + "," + chart.getColorIncidents() + "," + chart.getColorServiceAbrufe() );

    Axis yAxis = model.getAxis(AxisType.Y);
    yAxis.setMin(0);
    yAxis.setMax((maxY + 200));

    return model;
    }	
	
	public BarChartModel getKategorienChartKWModel() 
	{
	TreeMap<String, KWStatistik> kwStatistiken = new TreeMap<>();
	String kwIndex = null;
	int anzahlSortierterVorgaenge = 0;
	
	BarChartModel model = new BarChartModel();
	
    ChartSeries sCustomerRequests = new ChartSeries();
    sCustomerRequests.setLabel("Customer Requests");
    
    ChartSeries sWorkOrders = new ChartSeries();
    sWorkOrders.setLabel("Work Orders");
    
    ChartSeries sShortCalls = new ChartSeries();
    sShortCalls.setLabel("Short Calls");

    ChartSeries sServiceInfos = new ChartSeries();
    sServiceInfos.setLabel("Service Infos");
    
    ChartSeries sServiceAnfragen = new ChartSeries();
    sServiceAnfragen.setLabel("Service Anfragen");

    ChartSeries sServiceAbrufe = new ChartSeries();
    sServiceAbrufe.setLabel("Service Abrufe");
    
    ChartSeries sIncidents = new ChartSeries();
    sIncidents.setLabel("Incidents");
    
    ChartSeries sBeschwerden = new ChartSeries();
    sBeschwerden.setLabel("Beschwerden");
    
    	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Starte Sortierung der vorhandenen " +  monatsStatistiken.size() + " Monatsstatistiken nach Kalenderwoche");}
    	for (MonatsStatistik m : monatsStatistiken.values()) 
    	{
    	anzahlSortierterVorgaenge += m.getVorgaengeBerichtszeitraum().size();	
    	
    		for (Vorgang vorgang : m.getVorgaengeBerichtszeitraum()) 
    		{
			kwIndex = vorgang.getKw().getIndex();
			
				if (kwStatistiken.containsKey(kwIndex)) 
				{
				kwStatistiken.get(kwIndex).addVorgang(vorgang);	
				} 
				else 
				{
				KWStatistik ks = new KWStatistik(vorgang.getErstellDatum(), vorgang.getKw());
				ks.addVorgang(vorgang);
				kwStatistiken.put(kwIndex, ks);
				}	
			}
		}
       	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Sortierung abgeschlossen. Es wurden " +  anzahlSortierterVorgaenge + " Vorgänge in " + kwStatistiken.size() + " KWstatistiken sortiert.");}    	
    	
       	for (KWStatistik ks : kwStatistiken.values()) 
       	{
		ks.setStatistik();
		
		sCustomerRequests.set(ks.getLabel(), ks.getAnzahlCustomerRequests());	
		sWorkOrders.set(ks.getLabel(), ks.getAnzahlWorkorders());
		sShortCalls.set(ks.getLabel(), ks.getAnzahlShortCalls());
		sServiceInfos.set(ks.getLabel(), ks.getAnzahlServiceinfos());
		sServiceAnfragen.set(ks.getLabel(), ks.getAnzahlServiceanfragen());
		sServiceAbrufe.set(ks.getLabel(), ks.getAnzahlServiceAbrufe());
		sIncidents.set(ks.getLabel(), ks.getAnzahlIncidents());
		sBeschwerden.set(ks.getLabel(), ks.getAnzahlBeschwerden());
		}
 
     model.addSeries(sCustomerRequests);
     model.addSeries(sWorkOrders);
     model.addSeries(sShortCalls);
     model.addSeries(sServiceInfos);
     model.addSeries(sServiceAnfragen);
     model.addSeries(sServiceAbrufe);
     model.addSeries(sIncidents);
     model.addSeries(sBeschwerden);
         
    model.setTitle("Gesamtaufträge nach Kalenderwoche " + getPrimeFacesSubtitle() + " (" + anzahlSortierterVorgaenge + " Vorgänge)");
    model.setStacked(true);
    model.setLegendPosition("ne");
    model.setMouseoverHighlight(true);
    model.setShowDatatip(true);
    model.setShowPointLabels(true);
    model.setAnimate(true);
    model.setBarMargin(10);
	model.setSeriesColors(chart.getDefaultBarColors());
	Axis xAxis = model.getAxis(AxisType.X);
	xAxis.setTickAngle(-90);
    //yAxis.setMin(0);
    // yAxis.setMax(200);
    
    return model;
    }		
	
	public BarChartModel  getKategorienChartModel() 
	{
	BarChartModel model = new BarChartModel();
	
    ChartSeries sCustomerRequests = new ChartSeries();
    sCustomerRequests.setLabel("Customer Requests");
    
    ChartSeries sWorkOrders = new ChartSeries();
    sWorkOrders.setLabel("Work Orders");
    
    ChartSeries sShortCalls = new ChartSeries();
    sShortCalls.setLabel("Short Calls");

    ChartSeries sServiceInfos = new ChartSeries();
    sServiceInfos.setLabel("Service Infos");
    
    ChartSeries sServiceAnfragen = new ChartSeries();
    sServiceAnfragen.setLabel("Service Anfragen");

    ChartSeries sServiceAbrufe = new ChartSeries();
    sServiceAbrufe.setLabel("Service Abrufe");
    
    ChartSeries sIncidents = new ChartSeries();
    sIncidents.setLabel("Incidents");
    
    ChartSeries sBeschwerden = new ChartSeries();
    sBeschwerden.setLabel("Beschwerden");
    
    	for (MonatsStatistik m : monatsStatistiken.values()) 
    	{
    	sCustomerRequests.set(m.getBerichtsMonatAsString(), m.getAnzahlCustomerRequests());	
    	sWorkOrders.set(m.getBerichtsMonatAsString(), m.getAnzahlWorkorders());
    	sShortCalls.set(m.getBerichtsMonatAsString(), m.getAnzahlShortCalls());
    	sServiceInfos.set(m.getBerichtsMonatAsString(), m.getAnzahlServiceinfos());
    	sServiceAnfragen.set(m.getBerichtsMonatAsString(), m.getAnzahlServiceanfragen());
    	sServiceAbrufe.set(m.getBerichtsMonatAsString(), m.getAnzahlServiceAbrufe());
    	sIncidents.set(m.getBerichtsMonatAsString(), m.getAnzahlIncidents());
    	sBeschwerden.set(m.getBerichtsMonatAsString(), m.getAnzahlBeschwerden());
		}
 
     model.addSeries(sCustomerRequests);
     model.addSeries(sWorkOrders);
     model.addSeries(sShortCalls);
     model.addSeries(sServiceInfos);
     model.addSeries(sServiceAnfragen);
     model.addSeries(sServiceAbrufe);
     model.addSeries(sIncidents);
     model.addSeries(sBeschwerden);
         
    model.setTitle("Gesamtaufträge nach Kategorien " + getPrimeFacesSubtitle());
    model.setStacked(true);
    model.setLegendPosition("ne");
    model.setMouseoverHighlight(true);
    model.setShowDatatip(true);
    model.setShowPointLabels(true);
    model.setAnimate(true);
    model.setBarMargin(50);
	model.setSeriesColors(chart.getDefaultBarColors());

    //Axis yAxis = model.getAxis(AxisType.Y);
    //yAxis.setMin(0);
    // yAxis.setMax(200);
    
    return model;
    }	

	public Cache getCache() {return cache;}	
	public void setCache(Cache cache) {this.cache = cache;}
	public Chart getChart() {return chart;}
	public void setChart(Chart chart) {this.chart = chart;}

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
	
	public String getPrimeFacesSubtitle() 
	{
	return "Auswertungszeitraum: " + df.format(abfrageZeitraum.getStartDatum()) + " bis " + df.format(abfrageZeitraum.getEndDatum()) ;
	}
	
	public String getSubtitle() 
	{
	return "'Auswertungszeitraum: " + df.format(abfrageZeitraum.getStartDatum()) + " bis " + df.format(abfrageZeitraum.getEndDatum()) + "'";
	}

	public BarChartModel getMonatsChartModel() 
	{
	BarChartModel model = new BarChartModel();
	model.setSeriesColors("00b0f0, 1f497d, bebebe");
	model.setAnimate(true);
	
	ChartSeries sVorgaengeGesamt = new ChartSeries();
	sVorgaengeGesamt.setLabel("Anzahl Vorgänge Gesamt");
	
	ChartSeries sAnzahlIncidents = new ChartSeries();
	sAnzahlIncidents.setLabel("Anzahl Incidents");
	
	ChartSeries sAVGDauer = new ChartSeries();
	sAVGDauer.setLabel("&#216; Dauer Incident");
	
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		sVorgaengeGesamt.set(m.getBerichtsMonatAsString(), m.getAnzahlVorgaengeBerichtszeitraum());	
		sAnzahlIncidents.set(m.getBerichtsMonatAsString(), m.getAnzahlIncidents());
		sAVGDauer.set(m.getBerichtsMonatAsString(), m.getDurchschnittlicheDauerMinutenIncidents());
		}
	
	 model.addSeries(sVorgaengeGesamt);
	 model.addSeries(sAnzahlIncidents);
	 model.addSeries(sAVGDauer);
	     
	model.setTitle("Gesamtaufträge " + getPrimeFacesSubtitle());
	model.setLegendPosition("ne");
	model.setMouseoverHighlight(true);
	model.setShowDatatip(true);
	model.setShowPointLabels(true);
	//Axis yAxis = model.getAxis(AxisType.Y);
	//yAxis.setMin(0);
	// yAxis.setMax(200);
	
	return model;
	}

	public BarChartModel getKategorienChartTageszeitModel() 
	{
	TreeMap<Integer, StundenStatistik> stundenStatistiken = new TreeMap<>();
	Integer berichtsStunde = 0;
	int anzahlSortierterVorgaenge = 0;
	
	BarChartModel model = new BarChartModel();
	
	ChartSeries sCustomerRequests = new ChartSeries();
	sCustomerRequests.setLabel("Customer Requests");
	
	ChartSeries sWorkOrders = new ChartSeries();
	sWorkOrders.setLabel("Work Orders");
	
	ChartSeries sShortCalls = new ChartSeries();
	sShortCalls.setLabel("Short Calls");
	
	ChartSeries sServiceInfos = new ChartSeries();
	sServiceInfos.setLabel("Service Infos");
	
	ChartSeries sServiceAnfragen = new ChartSeries();
	sServiceAnfragen.setLabel("Service Anfragen");
	
	ChartSeries sServiceAbrufe = new ChartSeries();
	sServiceAbrufe.setLabel("Service Abrufe");
	
	ChartSeries sIncidents = new ChartSeries();
	sIncidents.setLabel("Incidents");
	
	ChartSeries sBeschwerden = new ChartSeries();
	sBeschwerden.setLabel("Beschwerden");
	
		if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Starte Sortierung der vorhandenen " +  monatsStatistiken.size() + " Monatsstatistiken nach Uhrzeit");}
		for (MonatsStatistik m : monatsStatistiken.values()) 
		{
		anzahlSortierterVorgaenge += m.getVorgaengeBerichtszeitraum().size();	
		
			for (Vorgang vorgang : m.getVorgaengeBerichtszeitraum()) 
			{
			berichtsStunde = vorgang.getErstellZeit().getHour();
			
				if (stundenStatistiken.containsKey(berichtsStunde)) 
				{
				stundenStatistiken.get(berichtsStunde).addVorgang(vorgang);	
				} 
				else 
				{
					// Es werden nur Stundenstatistiken zwischen 06:00 und 18:00 Uhr erstellt
					if (berichtsStunde > 5 && berichtsStunde < 19) 
					{
					StundenStatistik ss = new StundenStatistik(vorgang.getErstellDatumZeit());
					ss.addVorgang(vorgang);
					stundenStatistiken.put(berichtsStunde, ss);
						
					}
				}	
			}
		}
	   	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Sortierung abgeschlossen. Es wurden " +  anzahlSortierterVorgaenge + " Vorgänge in " + stundenStatistiken.size() + " Stundenstatistiken sortiert.");}    	
		
	   	for (StundenStatistik ss : stundenStatistiken.values()) 
	   	{
		ss.setStatistik();
		
		sCustomerRequests.set(ss.getLabel(), ss.getAnzahlCustomerRequests());	
		sWorkOrders.set(ss.getLabel(), ss.getAnzahlWorkorders());
		sShortCalls.set(ss.getLabel(), ss.getAnzahlShortCalls());
		sServiceInfos.set(ss.getLabel(), ss.getAnzahlServiceinfos());
		sServiceAnfragen.set(ss.getLabel(), ss.getAnzahlServiceanfragen());
		sServiceAbrufe.set(ss.getLabel(), ss.getAnzahlServiceAbrufe());
		sIncidents.set(ss.getLabel(), ss.getAnzahlIncidents());
		sBeschwerden.set(ss.getLabel(), ss.getAnzahlBeschwerden());
		}
	
	 model.addSeries(sCustomerRequests);
	 model.addSeries(sWorkOrders);
	 model.addSeries(sShortCalls);
	 model.addSeries(sServiceInfos);
	 model.addSeries(sServiceAnfragen);
	 model.addSeries(sServiceAbrufe);
	 model.addSeries(sIncidents);
	 model.addSeries(sBeschwerden);
	     
	model.setTitle("Gesamtaufträge nach Tageszeit " + getPrimeFacesSubtitle() + " (" + anzahlSortierterVorgaenge + " Vorgänge)");
	model.setStacked(true);
	model.setLegendPosition("ne");
	model.setMouseoverHighlight(true);
	model.setShowDatatip(true);
	model.setShowPointLabels(true);
	model.setAnimate(true);
	model.setBarMargin(50);
	model.setSeriesColors(chart.getDefaultBarColors());
	
	//Axis yAxis = model.getAxis(AxisType.Y);
	//yAxis.setMin(0);
	// yAxis.setMax(200);
	
	return model;
	}

}
