package de.hannit.fsch.reportal.model.echolon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;
import java.util.stream.Stream;


public class MonatsStatistik extends EcholonStatistik
{
private int anzahlVorgaengeGesamt = 0;
private int anzahlIncidents = 0;
private long anzahlIncidentsServicezeitNichtEingehalten = 0;
private int anzahlserviceAbrufe = 0;
private long anzahlServiceAbrufeServicezeitNichtEingehalten = 0;

private String bezeichnungLang = "unbekannt";

private LocalDate berichtsZeitraum = null;
private ArrayList<Vorgang> vorgaengeGesamt = null;
private TreeMap<LocalDateTime, Vorgang> incidents = new TreeMap<LocalDateTime, Vorgang>();
private TreeMap<LocalDateTime, Vorgang> serviceAbrufe = new TreeMap<LocalDateTime, Vorgang>();
private Stream<Vorgang> si = null;

	public MonatsStatistik(LocalDate incoming) 
	{
	this.berichtsZeitraum = incoming;
	vorgaengeGesamt = new ArrayList<Vorgang>();
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
	this.bezeichnungLang = formatter.format(berichtsZeitraum);
	}
	
	public void addVorgang(Vorgang incoming) 
	{
	vorgaengeGesamt.add(incoming);	
	}
	
	/*
	 * Bildet die Summenwerte für den Berichtsmonat
	 */
	public void setMonatswerte()
	{
	this.anzahlVorgaengeGesamt = vorgaengeGesamt.size();
		
	split();
		
	this.anzahlIncidents = incidents.size();
	si = incidents.values().stream();
	this.anzahlIncidentsServicezeitNichtEingehalten = si.filter(v -> !v.isZielzeitEingehalten()).count();
	this.prozentanteilIncidentsServicezeitNichtEingehalten = incidents.size() > 0 ? ((anzahlIncidentsServicezeitNichtEingehalten * 100) / (float)anzahlIncidents) : 0;
	setDurchschnittlicheDauerMinutenIncidents(getDurchschnittlicheDauerMinutenIncidents());
	
	this.anzahlserviceAbrufe = serviceAbrufe.size();
	si = serviceAbrufe.values().stream();
	this.anzahlServiceAbrufeServicezeitNichtEingehalten = si.filter(v -> !v.isZielzeitEingehalten()).count();
	this.prozentanteilServiceAbrufeServicezeitNichtEingehalten = serviceAbrufe.size() > 0 ? ((anzahlServiceAbrufeServicezeitNichtEingehalten * 100) / (float)anzahlserviceAbrufe) : 0;
	setDurchschnittlicheDauerMinutenServiceAbrufe(getDurchschnittlicheDauerMinutenServiceAbrufe());
	}
	
	private void split() 
	{
		for (Vorgang v : vorgaengeGesamt) 
		{
			switch (v.getTyp()) 
			{
			case EcholonConstants.TYP_INCIDENT:
			incidents.put(v.getErstellDatumZeit(), v);
			break;

			case EcholonConstants.TYP_SERVICEABRUF:
			serviceAbrufe.put(v.getErstellDatumZeit(), v);
			break;
			
			default:
			break;
			}	
		}
		
	}
	
	public String getBerichtsMonatAsString()
	{
	return berichtsZeitraum.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());	
	}
	
	public int getAnzahlVorgaengeGesamt()
	{
	return anzahlVorgaengeGesamt;	
	}
	
	public long getAnzahlIncidents() 
	{
	return anzahlIncidents;
	}
	
	public long getAnzahlBeschwerden() 
	{
	si = vorgaengeGesamt.stream();		
	return si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_BESCHWERDE)).count();
	}

	public long getCustomerRequests() 
	{
	si = vorgaengeGesamt.stream();		
	return si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_CUSTOMERREQUEST)).count();
	}
	
	public long getAnzahlServiceAnfragen() 
	{
	si = vorgaengeGesamt.stream();		
	return si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_SERVICEANFRAGE)).count();
	}
	
	public long getAnzahlServiceInfo() 
	{
	si = vorgaengeGesamt.stream();		
	return si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_SERVICEINFO)).count();
	}
	
	public long getAnzahlShortCalls() 
	{
	si = vorgaengeGesamt.stream();		
	return si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_SHORTCALL)).count();
	}
	
	public long getAnzahlWorkOrders() 
	{
	si = vorgaengeGesamt.stream();		
	return si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_WORKORDER)).count();
	}
	
	public long getAnzahlCustomerRequests() 
	{
	si = vorgaengeGesamt.stream();		
	return si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_CUSTOMERREQUEST)).count();
	}
	
	public long getAnzahlIncidentsServicezeitNichtEingehalten() {return anzahlIncidentsServicezeitNichtEingehalten;}
	
	public float getProzentanteilIncidentsServicezeitNichtEingehalten() {return prozentanteilIncidentsServicezeitNichtEingehalten;}
	
	public int getDurchschnittlicheDauerMinutenIncidents() 
	{
	int returnValue = 0;
	
		if (incidents.size() > 0) 
		{
		si = incidents.values().stream();	
		Double d =  si.mapToInt(v -> v.getLoesungszeitMinuten()).average().getAsDouble();
		returnValue = d.intValue();
		} 
		else 
		{
		returnValue = 0;	
		}
	return returnValue;
	}	
	
	public int getDurchschnittlicheDauerMinutenServiceAbrufe() 
	{
	int returnValue = 0;
		
		if (serviceAbrufe.size() > 0) 
		{
		si = serviceAbrufe.values().stream();	
		Double d =  si.mapToInt(v -> v.getLoesungszeitMinuten()).average().getAsDouble();
		returnValue = d.intValue();	
		} 
		else 
		{
		returnValue = 0;
		}
	return returnValue;		
	}		
	
	public int getAnzahlserviceAbrufe() {return anzahlserviceAbrufe;}

	public long getAnzahlServiceAbrufeServicezeitNichtEingehalten() {
		return anzahlServiceAbrufeServicezeitNichtEingehalten;
	}

	public float getProzentanteilServiceAbrufeServicezeitNichtEingehalten() {
		return prozentanteilServiceAbrufeServicezeitNichtEingehalten;
	}

	public String getBezeichnungLang() {
	return bezeichnungLang;
	}

	public LocalDate getBerichtsZeitraum() {
		return berichtsZeitraum;
	}	
	

}
