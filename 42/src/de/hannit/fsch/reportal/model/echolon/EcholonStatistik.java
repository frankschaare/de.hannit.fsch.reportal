/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

import java.text.DecimalFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
public class EcholonStatistik 
{
protected ArrayList<Vorgang> vorgaengeBerichtszeitraum;
protected ArrayList<SimpleEntry<String, Integer>> zusammenfassung;
protected Zeitraum berichtsZeitraum = null;

protected int anzahlVorgaengeBerichtszeitraum = 0;
protected long anzahlIncidentsServicezeitEingehalten = 0;
protected long anzahlIncidentsServicezeitNichtEingehalten = 0;
protected long anzahlServiceAbrufeServicezeitEingehalten = 0;
protected long anzahlServiceAbrufeServicezeitNichtEingehalten = 0;
protected double prozentanteilIncidentsServicezeitEingehalten = 0;
protected double prozentanteilServiceAbrufeServicezeitEingehalten = 0;
protected float prozentanteilIncidentsServicezeitNichtEingehalten = 0;
protected float prozentanteilServiceAbrufeServicezeitNichtEingehalten = 0;
protected double avgDauerMinutenIncidents = 0;
protected double avgDauerStundenIncidents = 0;
protected double avgDauerTageIncidents = 0;
protected double avgDauerMinutenServiceAbrufe = 0;
protected double avgDauerStundenServiceAbrufe = 0;
protected double avgDauerTageServiceAbrufe = 0;
protected String label = null;

protected DecimalFormat df = new DecimalFormat( "###.##" );


	/**
	 * Superklasse für alle Echolon Statistiken 
	 */
	public EcholonStatistik() 
	{

	}
	
	/*
	 * Berechnet die Statistikwerte für den Auswertungszeitraum
	 * Beim Aufruf muss sichergestellt sein, dass alle Werte enthalten sind ! 
	 */
	public void setStatistik()
	{
		
	}
	
	public int getAnzahlVorgaengeBerichtszeitraum() 
	{
	return anzahlVorgaengeBerichtszeitraum;
	}

	public ArrayList<Vorgang> getVorgaengeBerichtszeitraum() 
	{
	return vorgaengeBerichtszeitraum;
	}

	protected ArrayList<SimpleEntry<String, Integer>> getZusammenfassung() 
	{
	return zusammenfassung;
	}

	public Zeitraum getBerichtsZeitraum() 
	{
	return berichtsZeitraum;
	}

	public void setBerichtsZeitraum(Zeitraum incoming) 
	{
	this.berichtsZeitraum = incoming;
	}

	public String getLabel() 
	{
	return label;
	}

	public void setLabel(String label) 
	{
	this.label = label;
	}
	
	public void setDurchschnittlicheDauerMinutenIncidents(double durchschnittlicheDauerMinutenIncidents) 
	{
	avgDauerMinutenIncidents = durchschnittlicheDauerMinutenIncidents;
	avgDauerStundenIncidents = (avgDauerMinutenIncidents / 60);
	avgDauerTageIncidents = (avgDauerStundenIncidents / 24);
	}

	public void setDurchschnittlicheDauerMinutenServiceAbrufe(double durchschnittlicheDauerMinutenServiceAbrufe) 
	{
	avgDauerMinutenServiceAbrufe = durchschnittlicheDauerMinutenServiceAbrufe;
	avgDauerStundenServiceAbrufe = (avgDauerMinutenServiceAbrufe / 60);
	avgDauerTageServiceAbrufe = (avgDauerStundenServiceAbrufe / 24);
	}

	public double getAvgDauerStundenIncidents() 
	{
	return avgDauerStundenIncidents;
	}

	public String getFormattedAvgDauerStundenIncidents() 
	{
	return df.format(avgDauerStundenIncidents);
	}
	
	public double getAvgDauerTageIncidents() {
		return avgDauerTageIncidents;
	}
	
	public String getFormattedAvgDauerTageIncidents() 
	{
	return df.format(avgDauerTageIncidents);
	}	

	public double getAvgDauerStundenServiceAbrufe() {
		return avgDauerStundenServiceAbrufe;
	}

	public String getFormattedAvgDauerStundenServiceAbrufe() {
		return df.format(avgDauerStundenServiceAbrufe);
	}

	public double getAvgDauerTageServiceAbrufe() {
		return avgDauerTageServiceAbrufe;
	}
	
	public String getFormattedAvgDauerTageServiceAbrufe() {
		return df.format(avgDauerTageServiceAbrufe);
	}	

	public String getFormattedProzentanteilIncidentsServicezeitNichtEingehalten() 
	{
	return df.format(prozentanteilIncidentsServicezeitNichtEingehalten);
	}
	
	public String getFormattedProzentanteilServiceAbrufeServicezeitNichtEingehalten() 
	{
	return df.format(prozentanteilServiceAbrufeServicezeitNichtEingehalten);
	}

	public long getAnzahlIncidentsServicezeitEingehalten() 
	{
	return anzahlIncidentsServicezeitEingehalten;
	}

	public long getAnzahlServiceAbrufeServicezeitEingehalten() 
	{
	return anzahlServiceAbrufeServicezeitEingehalten;
	}

	public String getProzentanteilIncidentsServicezeitEingehalten() 
	{
	return null;
	}

	public String getProzentanteilServiceAbrufeServicezeitEingehalten() 
	{
	return null;
	}

}
