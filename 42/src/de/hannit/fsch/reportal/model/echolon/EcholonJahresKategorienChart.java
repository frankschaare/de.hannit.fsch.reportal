/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import de.hannit.fsch.reportal.db.Cache;
import de.hannit.fsch.reportal.db.EcholonDBManager;
import de.hannit.fsch.reportal.model.Chart;

/**
 * @author fsch
 *
 */
@ManagedBean
@SessionScoped
public class EcholonJahresKategorienChart implements Serializable
{
private static final long serialVersionUID = 2732509156820031564L;
@ManagedProperty (value = "#{cache}")
private Cache cache;	
@ManagedProperty (value = "#{chart}")
private Chart chart;	

private FacesContext fc = null;

private final static Logger log = Logger.getLogger(EcholonDBManager.class.getSimpleName());
private TreeMap<Integer, JahresStatistik> jahresStatistiken = null;

/**
	 * Managed Bean für die Darstellung der Echolon-Daten im Chart 
	 */
	public EcholonJahresKategorienChart() 
	{
	fc = FacesContext.getCurrentInstance();
		
		try 
		{
		jahresStatistiken = cache.getJahresStatistiken();
		} 
		catch (NullPointerException e) 
		{
		cache = fc.getApplication().evaluateExpressionGet(fc, "#{cache}", Cache.class);
		jahresStatistiken = cache.getJahresStatistiken();
		}
	chart = chart != null ? chart : fc.getApplication().evaluateExpressionGet(fc, "#{chart}", Chart.class);	
	}

	public BarChartModel getKategorienChartModel() 
	{
	BarChartModel model = new BarChartModel();
	
    ChartSeries sCustomerRequests = new ChartSeries();
    sCustomerRequests.setLabel("Customer Requests");
    
    ChartSeries sWorkOrders = new ChartSeries();
    sWorkOrders.setLabel("Work Orders");
    
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
    	
    	for (JahresStatistik js : jahresStatistiken.values()) 
    	{
    	sCustomerRequests.set(js.getBerichtsJahr() + " (" + js.getAnzahlVorgaengeBerichtszeitraum() + ")", js.getAnzahlCustomerRequests());	
    	sWorkOrders.set(js.getBerichtsJahr() + " (" + js.getAnzahlVorgaengeBerichtszeitraum() + ")", js.getAnzahlWorkorders());
    	sServiceInfos.set(js.getBerichtsJahr() + " (" + js.getAnzahlVorgaengeBerichtszeitraum() + ")", js.getAnzahlServiceinfos());
    	sServiceAnfragen.set(js.getBerichtsJahr() + " (" + js.getAnzahlVorgaengeBerichtszeitraum() + ")", js.getAnzahlServiceanfragen());
    	sServiceAbrufe.set(js.getBerichtsJahr() + " (" + js.getAnzahlVorgaengeBerichtszeitraum() + ")", js.getAnzahlServiceAbrufe());
    	sIncidents.set(js.getBerichtsJahr() + " (" + js.getAnzahlVorgaengeBerichtszeitraum() + ")", js.getAnzahlIncidents());
    	sBeschwerden.set(js.getBerichtsJahr() + " (" + js.getAnzahlVorgaengeBerichtszeitraum() + ")", js.getAnzahlBeschwerden());
		}
 
     model.addSeries(sCustomerRequests);
     model.addSeries(sWorkOrders);
     model.addSeries(sServiceInfos);
     model.addSeries(sServiceAnfragen);
     model.addSeries(sServiceAbrufe);
     model.addSeries(sIncidents);
     model.addSeries(sBeschwerden);
         
    model.setTitle("Jahressummen seit 2010");
    model.setLegendPosition("ne");
    model.setMouseoverHighlight(true);
    model.setShowDatatip(true);
    model.setShowPointLabels(true);
    model.setAnimate(true);
    model.setBarMargin(50);
	model.setSeriesColors(chart.getTopTenBarColors());
    
    return model;
    }	
	
	public Cache getCache() {return cache;}
	public void setCache(Cache cache) {this.cache = cache;}
	public Chart getChart() {return chart;}
	public void setChart(Chart chart) {this.chart = chart;}
}
