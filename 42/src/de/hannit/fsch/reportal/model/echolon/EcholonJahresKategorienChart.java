/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

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

/**
 * @author fsch
 *
 */
@ManagedBean
@SessionScoped
public class EcholonJahresKategorienChart 
{
@ManagedProperty (value = "#{cache}")
private Cache cache;	

private final static Logger log = Logger.getLogger(EcholonDBManager.class.getSimpleName());
private TreeMap<Integer, JahresStatistik> jahresStatistiken = null;

/**
	 * Managed Bean für die Darstellung der Echolon-Daten im Chart 
	 */
	public EcholonJahresKategorienChart() 
	{
		try 
		{
		jahresStatistiken = cache.getJahresStatistiken();
		} 
		catch (NullPointerException e) 
		{
		FacesContext fc = FacesContext.getCurrentInstance();
		cache = fc.getApplication().evaluateExpressionGet(fc, "#{cache}", Cache.class);
		jahresStatistiken = cache.getJahresStatistiken();
		}
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
	model.setSeriesColors("32cd32, 698b22, 008b8b, 0000ff, 00008b, ee7600, ff0000");
    
    return model;
    }	

	public Cache getCache() {
		return cache;
	}
	
	public void setCache(Cache cache) {
		this.cache = cache;
	}

}
