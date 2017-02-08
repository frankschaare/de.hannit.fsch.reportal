package de.hannit.fsch.reportal.model.echolon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

import org.primefaces.model.chart.PieChartModel;

import de.hannit.fsch.reportal.model.Zeitraum;


public class TagesStatistik extends EcholonStatistik
{
private LocalDate berichtsTag = null;	
private int anzahlVorgaengeGesamt = 0;
private int anzahlIncidents = 0;
private long anzahlIncidentsServicezeitNichtEingehalten = 0;
private int anzahlserviceAbrufe = 0;
private long anzahlServiceAbrufeServicezeitNichtEingehalten = 0;

private String bezeichnungLang = "unbekannt";
private String bezeichnungkurz = "unbekannt";

private ArrayList<Vorgang> incidents = new ArrayList<Vorgang>();
private ArrayList<Vorgang> workOrder = new ArrayList<Vorgang>();
private ArrayList<Vorgang> serviceInfo = new ArrayList<Vorgang>();
private ArrayList<Vorgang> serviceAbrufe = new ArrayList<Vorgang>();
private ArrayList<Vorgang> serviceAnfrage = new ArrayList<Vorgang>();
private ArrayList<Vorgang> beschwerden = new ArrayList<Vorgang>();
private ArrayList<Vorgang> customerRequest = new ArrayList<Vorgang>();
private ArrayList<Vorgang> shortCall = new ArrayList<Vorgang>();

private Stream<Vorgang> si = null;

	public TagesStatistik(LocalDate incoming) 
	{
	vorgaengeBerichtszeitraum = new ArrayList<Vorgang>();
	this.berichtsTag = incoming;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
	this.bezeichnungLang = formatter.format(incoming);
	formatter = DateTimeFormatter.ofPattern("MMMM");
	setLabel(formatter.format(incoming));
	}
	
	public void addVorgang(Vorgang incoming) 
	{
	vorgaengeBerichtszeitraum.add(incoming);	
	}
	
	public ArrayList<Vorgang> getIncidents() {return incidents;}
	public ArrayList<Vorgang> getServiceAbrufe() {return serviceAbrufe;}
	public ArrayList<Vorgang> getBeschwerden() {return beschwerden;}
	public ArrayList<Vorgang> getServiceanfragen() {return serviceAnfrage;}
	public ArrayList<Vorgang> getServiceinfos() {return serviceInfo;}
	public ArrayList<Vorgang> getShortcalls() {return shortCall;}
	public ArrayList<Vorgang> getWorkorders() {return workOrder;}
	public ArrayList<Vorgang> getCustomerRequests() {return customerRequest;}
	
	public int getAnzahlIncidents() {return (incidents != null && incidents.size() > 0) ? incidents.size() : 0;}
	public int getAnzahlServiceAbrufe() {return (serviceAbrufe != null && serviceAbrufe.size() > 0) ? serviceAbrufe.size() : 0;}
	public int getAnzahlBeschwerden() {return (beschwerden != null && beschwerden.size() > 0) ? beschwerden.size() : 0;}
	public int getAnzahlServiceanfragen() {return (serviceAnfrage != null && serviceAnfrage.size() > 0) ? serviceAnfrage.size() : 0;}
	public int getAnzahlServiceinfos() {return (serviceInfo != null && serviceInfo.size() > 0) ? serviceInfo.size() : 0;}
	public int getAnzahlShortCalls() {return (shortCall != null && shortCall.size() > 0) ? shortCall.size() : 0;}
	public int getAnzahlWorkorders() {return (workOrder != null && workOrder.size() > 0) ? workOrder.size() : 0;}
	public int getAnzahlCustomerRequests() {return (customerRequest != null && customerRequest.size() > 0) ? customerRequest.size() : 0;}
	
	public int getSummeVorgaenge () 
	{
	int summe = 0;	
		
	summe += getAnzahlIncidents();
	summe += getAnzahlServiceAbrufe();
	summe += getAnzahlBeschwerden();
	summe += getAnzahlServiceanfragen();
	summe += getAnzahlServiceinfos();
	summe += getAnzahlShortCalls();
	summe += getAnzahlWorkorders();
	summe += getAnzahlCustomerRequests();
	
	return summe;
	}
	
	@Override
	public ArrayList<SimpleEntry<String, Integer>> getZusammenfassung() 
	{
	zusammenfassung = new ArrayList<>();
	
	zusammenfassung.add(new SimpleEntry<String, Integer>(EcholonConstants.TYP_INCIDENT, getAnzahlIncidents()));
	zusammenfassung.add(new SimpleEntry<String, Integer>(EcholonConstants.TYP_SERVICEABRUF, getAnzahlServiceAbrufe()));
	zusammenfassung.add(new SimpleEntry<String, Integer>(EcholonConstants.TYP_SERVICEANFRAGE, getAnzahlServiceanfragen()));
	zusammenfassung.add(new SimpleEntry<String, Integer>(EcholonConstants.TYP_SERVICEINFO, getAnzahlServiceinfos()));
	zusammenfassung.add(new SimpleEntry<String, Integer>(EcholonConstants.TYP_BESCHWERDE, getAnzahlBeschwerden()));
	zusammenfassung.add(new SimpleEntry<String, Integer>(EcholonConstants.TYP_SHORTCALL, getAnzahlShortCalls()));
	zusammenfassung.add(new SimpleEntry<String, Integer>(EcholonConstants.TYP_WORKORDER, getAnzahlWorkorders()));
	zusammenfassung.add(new SimpleEntry<String, Integer>(EcholonConstants.TYP_CUSTOMERREQUEST, getAnzahlCustomerRequests()));
	
	return zusammenfassung;
	}

	@Override
	public PieChartModel getPieModel() 
	{
	pieModel = new PieChartModel();
	pieModel.set(EcholonConstants.TYP_INCIDENT, getAnzahlIncidents());
	pieModel.set(EcholonConstants.TYP_SERVICEABRUF, getAnzahlServiceAbrufe());
	pieModel.set(EcholonConstants.TYP_SERVICEANFRAGE, getAnzahlServiceanfragen());
	pieModel.set(EcholonConstants.TYP_SERVICEINFO, getAnzahlServiceinfos());
	pieModel.set(EcholonConstants.TYP_BESCHWERDE, getAnzahlBeschwerden());
	pieModel.set(EcholonConstants.TYP_SHORTCALL, getAnzahlShortCalls());
	pieModel.set(EcholonConstants.TYP_WORKORDER, getAnzahlWorkorders());
	pieModel.set(EcholonConstants.TYP_CUSTOMERREQUEST, getAnzahlCustomerRequests());
	
	 pieModel.setTitle("Vorgangsübersicht");
     pieModel.setLegendPosition("w");
	
	return pieModel;
	}
	
	@Override
	public ArrayList<Vorgang> getVorgaengeBerichtszeitraum() 
	{
	return vorgaengeBerichtszeitraum;
	}
	
	@Override
	public void setStatistik() 
	{
	this.anzahlVorgaengeGesamt = vorgaengeBerichtszeitraum.size();
	
	Vorgang max	= vorgaengeBerichtszeitraum.stream().max(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
	Vorgang min = vorgaengeBerichtszeitraum.stream().min(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();	
	berichtsZeitraum = new Zeitraum(min.getErstellDatumZeit(), max.getErstellDatumZeit());	
	
	split();
		
	this.anzahlIncidents = incidents.size();
	si = incidents.stream();
	this.anzahlIncidentsServicezeitNichtEingehalten = si.filter(v -> !v.isZielzeitEingehalten()).count();
	this.prozentanteilIncidentsServicezeitNichtEingehalten = incidents.size() > 0 ? ((anzahlIncidentsServicezeitNichtEingehalten * 100) / (float)anzahlIncidents) : 0;
	setDurchschnittlicheDauerMinutenIncidents(getDurchschnittlicheDauerMinutenIncidents());
		
	this.anzahlserviceAbrufe = serviceAbrufe.size();
	si = serviceAbrufe.stream();
	this.anzahlServiceAbrufeServicezeitNichtEingehalten = si.filter(v -> !v.isZielzeitEingehalten()).count();
	this.prozentanteilServiceAbrufeServicezeitNichtEingehalten = serviceAbrufe.size() > 0 ? ((anzahlServiceAbrufeServicezeitNichtEingehalten * 100) / (float)anzahlserviceAbrufe) : 0;
	setDurchschnittlicheDauerMinutenServiceAbrufe(getDurchschnittlicheDauerMinutenServiceAbrufe());
	}


	private void split() 
	{
	beschwerden.clear();	
	incidents.clear();
	serviceAbrufe.clear();
	serviceAnfrage.clear();
	serviceInfo.clear();
	shortCall.clear();
	workOrder.clear();
	customerRequest.clear();
	
		for (Vorgang v : vorgaengeBerichtszeitraum) 
		{
			switch (v.getTyp()) 
			{
			case EcholonConstants.TYP_BESCHWERDE:
			beschwerden.add(v);
			break;		
			
			case EcholonConstants.TYP_INCIDENT:
			incidents.add(v);
			break;

			case EcholonConstants.TYP_SERVICEABRUF:
			serviceAbrufe.add(v);
			break;
			
			case EcholonConstants.TYP_SERVICEANFRAGE:
			serviceAnfrage.add(v);
			break;			
			
			case EcholonConstants.TYP_SERVICEINFO:
			serviceInfo.add(v);
			break;		
			
			case EcholonConstants.TYP_SHORTCALL:
			shortCall.add(v);
			break;			
			
			case EcholonConstants.TYP_WORKORDER:
			workOrder.add(v);
			break;
			
			case EcholonConstants.TYP_CUSTOMERREQUEST:
			customerRequest.add(v);
			break;	
			
			default:
			break;
			}	
		}
		
	}
	
	public String getBerichtsMonatAsString()
	{
	return berichtsZeitraum.getBerichtsMonat();	
	}
	
	public int getAnzahlVorgaengeBerichtszeitraum() 
	{
	return vorgaengeBerichtszeitraum.size();	
	}
	
	public int getAnzahlVorgaengeGesamt()
	{
	return anzahlVorgaengeGesamt == 0 ? vorgaengeBerichtszeitraum.size() : anzahlVorgaengeGesamt;	
	}
	
	public long getAnzahlServiceAnfragen() 
	{
	si = vorgaengeBerichtszeitraum.stream();		
	return si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_SERVICEANFRAGE)).count();
	}
	
	public long getAnzahlServiceInfo() 
	{
	si = vorgaengeBerichtszeitraum.stream();		
	return si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_SERVICEINFO)).count();
	}
	
	public long getAnzahlWorkOrders() 
	{
	si = vorgaengeBerichtszeitraum.stream();		
	return si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_WORKORDER)).count();
	}
	
	public long getAnzahlIncidentsServicezeitEingehalten() 
	{
	si = incidents.stream();
	anzahlIncidentsServicezeitEingehalten = si.filter(v -> v.isZielzeitEingehalten()).count();
	return anzahlIncidentsServicezeitEingehalten;
	}
	
	public long getAnzahlIncidentsServicezeitNichtEingehalten() {return anzahlIncidentsServicezeitNichtEingehalten;}
	
	public String getProzentanteilIncidentsServicezeitEingehalten() 
	{
	prozentanteilIncidentsServicezeitEingehalten = (anzahlIncidentsServicezeitEingehalten * 100) / ((double)incidents.size());	
	return df.format(prozentanteilIncidentsServicezeitEingehalten);
	}
	
	public String getProzentanteilIncidentsServicezeitNichtEingehalten() 
	{
	prozentanteilIncidentsServicezeitNichtEingehalten = ((anzahlIncidentsServicezeitNichtEingehalten * 100) / ((float)incidents.size()));	
	return df.format(prozentanteilIncidentsServicezeitNichtEingehalten);
	}
	
	public int getDurchschnittlicheDauerMinutenIncidents() 
	{
	int returnValue = 0;
	
		if (incidents.size() > 0) 
		{
		si = incidents.stream();	
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
		si = serviceAbrufe.stream();	
		Double d =  si.mapToInt(v -> v.getLoesungszeitMinuten()).average().getAsDouble();
		returnValue = d.intValue();	
		} 
		else 
		{
		returnValue = 0;
		}
	return returnValue;		
	}		
	
	@Override
	public long getAnzahlServiceAbrufeServicezeitEingehalten() 
	{
	si = serviceAbrufe.stream();
	anzahlServiceAbrufeServicezeitEingehalten = si.filter(v -> v.isZielzeitEingehalten()).count();
	return anzahlServiceAbrufeServicezeitEingehalten;
	}

	public long getAnzahlServiceAbrufeServicezeitNichtEingehalten() {
		return anzahlServiceAbrufeServicezeitNichtEingehalten;
	}
	
	public String getProzentanteilServiceAbrufeServicezeitEingehalten() 
	{
	prozentanteilServiceAbrufeServicezeitEingehalten = (anzahlServiceAbrufeServicezeitEingehalten * 100) / ((float)serviceAbrufe.size());	
	return df.format(prozentanteilServiceAbrufeServicezeitEingehalten);
	}

	public String getProzentanteilServiceAbrufeServicezeitNichtEingehalten() 
	{
	prozentanteilServiceAbrufeServicezeitNichtEingehalten = (anzahlServiceAbrufeServicezeitNichtEingehalten * 100) / ((float)serviceAbrufe.size());	
	return df.format(prozentanteilServiceAbrufeServicezeitNichtEingehalten);
	}

	public String getBezeichnungLang() {
	return bezeichnungLang;
	}

	public Zeitraum getBerichtsZeitraum() 
	{
		return berichtsZeitraum;
	}

	public LocalDate getBerichtsTag() {
		return berichtsTag;
	}	

}
