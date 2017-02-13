/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.faces.application.ProjectStage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import de.hannit.fsch.reportal.db.Cache;
import de.hannit.fsch.reportal.model.Benutzer;
import de.hannit.fsch.reportal.model.Chart;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
@ManagedBean
@SessionScoped
public class EcholonTopTenChart implements Serializable
{
private static final long serialVersionUID = 1726331639816105369L;
@ManagedProperty (value = "#{cache}")
private Cache cache;	
@ManagedProperty (value = "#{benutzer}")
private Benutzer benutzer;
@ManagedProperty (value = "#{chart}")
private Chart chart;	

private final static Logger log = Logger.getLogger(EcholonTopTenChart.class.getSimpleName());
private String logPrefix = this.getClass().getCanonicalName() + ": ";
private FacesContext fc = FacesContext.getCurrentInstance();
private TreeMap<Integer, JahresStatistik> jahresStatistiken = null;
private TreeMap<LocalDate, TagesStatistik> tagesStatistiken = null;
private TreeMap<Integer, TagesStatistik> helperTree = null;
private TreeMap<Integer, TagesStatistik> top10 = null;
private Stream<TagesStatistik> si = null;
private Integer vorJahr = 0;
private String avgLabel = "";

/**
	 * Managed Bean für die Darstellung der Echolon-Daten im Chart 
	 */
	public EcholonTopTenChart() 
	{
		try 
		{
		jahresStatistiken = cache.getJahresStatistiken();
		} 
		catch (NullPointerException e) 
		{
		cache = fc.getApplication().evaluateExpressionGet(fc, "#{cache}", Cache.class);
		jahresStatistiken = cache.getJahresStatistiken();
		}
	
	// Top 10 wird für das Vorjahr erstellt
	benutzer = benutzer != null ? benutzer : fc.getApplication().evaluateExpressionGet(fc, "#{benutzer}", Benutzer.class);	
	chart = chart != null ? chart : fc.getApplication().evaluateExpressionGet(fc, "#{chart}", Chart.class);	

	vorJahr = benutzer.getSystemUserDomain().equals("ENTHOO") ? 2014 : LocalDate.now().minusYears(1).getYear(); 
		
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "Erstelle Top10 Statistik für das Jahr " + vorJahr);}			
	JahresStatistik auswertungsStatistik = jahresStatistiken.get(vorJahr); 
	avgLabel = "Tagesdurchschnitt " + auswertungsStatistik.getBerichtsJahr();
	tagesStatistiken = auswertungsStatistik.getTagesStatistiken();
	setTop10();
	}

	/*
	 * Berechnet die 10 Tage des Jahres mit den meisten eingegangenen Vorgängen
	 */
	private void setTop10() 
	{
	top10 = new TreeMap<>();
	helperTree = new TreeMap<>();
	
		for (TagesStatistik t : tagesStatistiken.values()) 
		{
		helperTree.put(t.getAnzahlVorgaengeBerichtszeitraum(), t);	
		}	
		
		TagesStatistik tmp = null;
		for (int i = 0; i < 10; i++) 
		{
		tmp = helperTree.pollLastEntry().getValue();
		top10.put(tmp.getAnzahlVorgaengeBerichtszeitraum(), tmp);	
		}
	}

	public BarChartModel getChartModel() 
	{
	BarChartModel model = new BarChartModel();
	Double d = null;
	si = tagesStatistiken.values().stream();	
	d =  si.mapToInt(ts -> ts.getAnzahlVorgaengeBerichtszeitraum()).average().getAsDouble();
	String avgVorgaengeGesamt = String.valueOf(d.intValue());
	
    ChartSeries sCustomerRequests = new ChartSeries();
    sCustomerRequests.setLabel("Customer Requests");
	si = tagesStatistiken.values().stream();	
	d =  si.mapToInt(ts -> ts.getAnzahlCustomerRequests()).average().getAsDouble();	
	sCustomerRequests.set(avgLabel + " (" + avgVorgaengeGesamt + ")", d.intValue());
	
    ChartSeries sWorkOrders = new ChartSeries();
    sWorkOrders.setLabel("Work Orders");
	si = tagesStatistiken.values().stream();	
	d =  si.mapToInt(ts -> ts.getAnzahlWorkorders()).average().getAsDouble();	
	sWorkOrders.set(avgLabel + " (" + avgVorgaengeGesamt + ")", d.intValue());
    
    ChartSeries sServiceInfos = new ChartSeries();
    sServiceInfos.setLabel("Service Infos");
	si = tagesStatistiken.values().stream();	
	d =  si.mapToInt(ts -> ts.getAnzahlServiceinfos()).average().getAsDouble();	
	sServiceInfos.set(avgLabel + " (" + avgVorgaengeGesamt + ")", d.intValue());
	
    ChartSeries sServiceAnfragen = new ChartSeries();
    sServiceAnfragen.setLabel("Service Anfragen");
	si = tagesStatistiken.values().stream();	
	d =  si.mapToInt(ts -> ts.getAnzahlServiceanfragen()).average().getAsDouble();	
	sServiceAnfragen.set(avgLabel + " (" + avgVorgaengeGesamt + ")", d.intValue());    

    ChartSeries sServiceAbrufe = new ChartSeries();
    sServiceAbrufe.setLabel("Service Abrufe");
	si = tagesStatistiken.values().stream();	
	d =  si.mapToInt(ts -> ts.getAnzahlServiceAbrufe()).average().getAsDouble();	
	sServiceAbrufe.set(avgLabel + " (" + avgVorgaengeGesamt + ")", d.intValue());
	
    ChartSeries sIncidents = new ChartSeries();
    sIncidents.setLabel("Incidents");
	si = tagesStatistiken.values().stream();	
	d =  si.mapToInt(ts -> ts.getAnzahlIncidents()).average().getAsDouble();	
	sIncidents.set(avgLabel + " (" + avgVorgaengeGesamt + ")", d.intValue());
	
    ChartSeries sBeschwerden = new ChartSeries();
    sBeschwerden.setLabel("Beschwerden");
	si = tagesStatistiken.values().stream();	
	d =  si.mapToInt(ts -> ts.getAnzahlBeschwerden()).average().getAsDouble();	
	sBeschwerden.set(avgLabel + " (" + avgVorgaengeGesamt + ")", d.intValue());    
    	
    	for (TagesStatistik ts : top10.values()) 
    	{
    	sCustomerRequests.set(Zeitraum.df.format(ts.getBerichtsTag()) + " (" + ts.getAnzahlVorgaengeBerichtszeitraum() + ")", ts.getAnzahlCustomerRequests());	
    	sWorkOrders.set(Zeitraum.df.format(ts.getBerichtsTag()) + " (" + ts.getAnzahlVorgaengeBerichtszeitraum() + ")", ts.getAnzahlWorkorders());
    	sServiceInfos.set(Zeitraum.df.format(ts.getBerichtsTag()) + " (" + ts.getAnzahlVorgaengeBerichtszeitraum() + ")", ts.getAnzahlServiceinfos());
    	sServiceAnfragen.set(Zeitraum.df.format(ts.getBerichtsTag()) + " (" + ts.getAnzahlVorgaengeBerichtszeitraum() + ")", ts.getAnzahlServiceanfragen());
    	sServiceAbrufe.set(Zeitraum.df.format(ts.getBerichtsTag()) + " (" + ts.getAnzahlVorgaengeBerichtszeitraum() + ")", ts.getAnzahlServiceAbrufe());
    	sIncidents.set(Zeitraum.df.format(ts.getBerichtsTag()) + " (" + ts.getAnzahlVorgaengeBerichtszeitraum() + ")", ts.getAnzahlIncidents());
    	sBeschwerden.set(Zeitraum.df.format(ts.getBerichtsTag()) + " (" + ts.getAnzahlVorgaengeBerichtszeitraum() + ")", ts.getAnzahlBeschwerden());
		}
 
     model.addSeries(sCustomerRequests);
     model.addSeries(sWorkOrders);
     model.addSeries(sServiceInfos);
     model.addSeries(sServiceAnfragen);
     model.addSeries(sServiceAbrufe);
     model.addSeries(sIncidents);
     model.addSeries(sBeschwerden);
         
    model.setTitle("Top 10 Eingänge im abgelaufenem Jahr");
    model.setLegendPosition("nw");
    model.setMouseoverHighlight(true);
    model.setShowDatatip(true);
    model.setShowPointLabels(true);
    model.setAnimate(true);
    model.setBarMargin(5);
	model.setSeriesColors(chart.getTopTenBarColors());
    
    return model;
    }	

	public Cache getCache() {return cache;}
	public void setCache(Cache cache) {this.cache = cache;}
	public Benutzer getBenutzer() {return benutzer;}
	public void setBenutzer(Benutzer benutzer) {this.benutzer = benutzer;}
	public Chart getChart() {return chart;}
	public void setChart(Chart chart) {this.chart = chart;}
	

}
