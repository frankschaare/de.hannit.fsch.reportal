package de.hannit.fsch.reportal.model;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import de.hannit.fsch.reportal.model.echolon.MonatsStatistik;

public class EcholonChartModel 
{
private final static Logger log = Logger.getLogger(EcholonChartModel.class.getSimpleName());	
private BarChartModel barModel;	
private HashMap<Integer, MonatsStatistik> monatsStatistiken = null;
private int maxValue = 0;
private int actualValue = 0;


	public EcholonChartModel(HashMap<Integer, MonatsStatistik> incoming) 
	{
	this.monatsStatistiken = incoming;	
	createBarModel();
	}
	
    public BarChartModel getBarModel() 
    {
    return barModel;
    }
	
    private void createBarModel() 
    {
    barModel = initBarModel();
         
    barModel.setTitle("Incidents");
    barModel.setLegendPosition("ne");
         
    Axis xAxis = barModel.getAxis(AxisType.X);
    xAxis.setLabel("Gender");
         
    Axis yAxis = barModel.getAxis(AxisType.Y);
    yAxis.setLabel("Births");
    yAxis.setMin(0);
    yAxis.setMax(maxValue + ((maxValue / 100) * 20));
    }
    
    private BarChartModel initBarModel() 
    {
    BarChartModel model = new BarChartModel();
 
    ChartSeries anzahlVorgaenge = new ChartSeries();
    anzahlVorgaenge.setLabel("Anzahl Vorgänge Gesamt");
    
    ChartSeries anzahlIncidents = new ChartSeries();
    anzahlIncidents.setLabel("Anzahl Incidents");
    
    
		for (int i = 1; i <= monatsStatistiken.size(); i++) 
		{
		MonatsStatistik m = monatsStatistiken.get(i);
		log.log(Level.INFO, "Verarbeite Monatsstatistik für den Monat " + m.getBerichtsMonatAsString());
		
		actualValue = m.getAnzahlVorgaengeGesamt();
		log.log(Level.INFO, "Monatsstatistik enthält insgesamt " + actualValue + " Vorgänge.");
		maxValue = actualValue > maxValue ? actualValue : maxValue;
    	anzahlVorgaenge.set(m.getBerichtsMonatAsString(), actualValue);
    	anzahlIncidents.set(m.getBerichtsMonatAsString(), m.getAnzahlIncidents());		
		}

	model.addSeries(anzahlVorgaenge);
	log.log(Level.INFO, "Ergänze Chartmodel um Chartseries 'anzahlVorgaenge'");
    model.addSeries(anzahlIncidents);
    log.log(Level.INFO, "Ergänze Chartmodel um Chartseries 'anzahlIncidents'");
    
    model.setExtender("ext");
    
    return model;
    }    

}
