/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

import java.text.DecimalFormat;

/**
 * @author fsch
 *
 */
public class EcholonStatistik 
{
protected float prozentanteilIncidentsServicezeitNichtEingehalten = 0;
protected float prozentanteilServiceAbrufeServicezeitNichtEingehalten = 0;
protected double avgDauerMinutenIncidents = 0;
protected double avgDauerStundenIncidents = 0;
protected double avgDauerTageIncidents = 0;
protected double avgDauerMinutenServiceAbrufe = 0;
protected double avgDauerStundenServiceAbrufe = 0;
protected double avgDauerTageServiceAbrufe = 0;

private	DecimalFormat df = new DecimalFormat( "###.##" );

	/**
	 * Superklasse für alle Echolon Statistiken 
	 */
	public EcholonStatistik() 
	{

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

}
