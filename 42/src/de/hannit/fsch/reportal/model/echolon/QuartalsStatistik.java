/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Stream;

import org.primefaces.model.chart.PieChartModel;

import de.hannit.fsch.reportal.model.Quartal;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
public class QuartalsStatistik extends EcholonStatistik
{
private Quartal berichtsQuartal = null;
private ArrayList<Vorgang> incidents = new ArrayList<Vorgang>();
private ArrayList<Vorgang> workOrder = new ArrayList<Vorgang>();
private ArrayList<Vorgang> serviceInfo = new ArrayList<Vorgang>();
private ArrayList<Vorgang> serviceAbrufe = new ArrayList<Vorgang>();
private ArrayList<Vorgang> serviceAnfrage = new ArrayList<Vorgang>();
private ArrayList<Vorgang> beschwerden = new ArrayList<Vorgang>();
private ArrayList<Vorgang> customerRequest = new ArrayList<Vorgang>();
private ArrayList<Vorgang> shortCall = new ArrayList<Vorgang>();
private String bezeichnungLang = "nicht vorhanden !";

private ArrayList<MonatsStatistik> monatsStatistiken = new ArrayList<MonatsStatistik>();

private long anzahlIncidents = 0;
private long anzahlIncidentsServicezeitNichtEingehalten = 0;
private int anzahlserviceAbrufe = 0;
private long anzahlServiceAbrufeServicezeitNichtEingehalten = 0;

private Stream<Vorgang> si = null;

	/**
	 * Im Konstruktor wird das Quartal definiert.
	 * 
	 * Gleichzeitig wird eine TreeMap mit den drei Berichtsmonaten konstruiert, 
	 * welche im Quartal enthalten sind.
	 */
	public QuartalsStatistik(Quartal incoming) 
	{
	this.berichtsQuartal = incoming;	
	this.bezeichnungLang = berichtsQuartal.getBezeichnungLang();
	this.label = berichtsQuartal.getBezeichnung();
	}

	@Override
	public void setStatistik() 
	{
		if (vorgaengeBerichtszeitraum != null) 
		{
		vorgaengeBerichtszeitraum.clear();	
		} 
		else 
		{
		vorgaengeBerichtszeitraum = new ArrayList<>();
		}
	
		for (MonatsStatistik ms : monatsStatistiken) 
		{
		vorgaengeBerichtszeitraum.addAll(ms.getVorgaengeBerichtszeitraum());	
		}
	
	Vorgang max	= vorgaengeBerichtszeitraum.stream().max(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
	Vorgang min = vorgaengeBerichtszeitraum.stream().min(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();	
	berichtsZeitraum = new Zeitraum(min.getErstellDatumZeit(), max.getErstellDatumZeit());	
		
	anzahlVorgaengeBerichtszeitraum = vorgaengeBerichtszeitraum.size();
	
	split();
	
	this.anzahlIncidents = incidents.size();
	si = incidents.stream();
	this.anzahlIncidentsServicezeitNichtEingehalten = si.filter(v -> !v.isZielzeitEingehalten()).count();
	this.prozentanteilIncidentsServicezeitNichtEingehalten = ((anzahlIncidentsServicezeitNichtEingehalten * 100) / (float)anzahlIncidents);
	setDurchschnittlicheDauerMinutenIncidents(getDurchschnittlicheDauerMinutenIncidents());
	
	this.anzahlserviceAbrufe = serviceAbrufe.size();
	si = serviceAbrufe.stream();
	this.anzahlServiceAbrufeServicezeitNichtEingehalten = si.filter(v -> !v.isZielzeitEingehalten()).count();
	this.prozentanteilServiceAbrufeServicezeitNichtEingehalten = ((anzahlServiceAbrufeServicezeitNichtEingehalten * 100) / (float)anzahlserviceAbrufe);
	setDurchschnittlicheDauerMinutenServiceAbrufe(getDurchschnittlicheDauerMinutenServiceAbrufe());
	
		for (MonatsStatistik ms : monatsStatistiken) 
		{
		ms.setStatistik();	
		}
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
	
	public ArrayList<Vorgang> getIncidents() {return incidents;}
	public ArrayList<Vorgang> getServiceAbrufe() {return serviceAbrufe;}
	public ArrayList<Vorgang> getBeschwerden() {return beschwerden;}
	public ArrayList<Vorgang> getServiceanfragen() {return serviceAnfrage;}
	public ArrayList<Vorgang> getServiceinfos() {return serviceInfo;}
	public ArrayList<Vorgang> getShortcalls() {return shortCall;}
	public ArrayList<Vorgang> getWorkorders() {return workOrder;}
	public ArrayList<Vorgang> getCustomerRequests() {return customerRequest;}
	
	public Integer getAnzahlIncidents() {return (incidents != null && incidents.size() > 0) ? incidents.size() : 0;}
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

	public String getBezeichnungLang() {
		return bezeichnungLang;
	}

	public int getAnzahlVorgaengeBerichtszeitraum() 
	{
	anzahlVorgaengeBerichtszeitraum = 0;
	
		try 
		{
			for (MonatsStatistik monatsStatistik : monatsStatistiken) 
			{
			anzahlVorgaengeBerichtszeitraum = anzahlVorgaengeBerichtszeitraum + monatsStatistik.getAnzahlVorgaengeGesamt(); 	
			}
		} 
		catch (NullPointerException e) 
		{
		anzahlVorgaengeBerichtszeitraum = 0;
		}
	return anzahlVorgaengeBerichtszeitraum;
	}
	
	@Override
	public long getAnzahlIncidentsServicezeitEingehalten() 
	{
	si = incidents.stream();
	anzahlIncidentsServicezeitEingehalten = si.filter(v -> v.isZielzeitEingehalten()).count();
	return anzahlIncidentsServicezeitEingehalten;
	}
	
	public long getAnzahlServiceAbrufeServicezeitEingehalten() 
	{
	si = serviceAbrufe.stream();
	anzahlServiceAbrufeServicezeitEingehalten = si.filter(v -> v.isZielzeitEingehalten()).count();
	return anzahlServiceAbrufeServicezeitEingehalten;
	}


	public long getAnzahlIncidentsServicezeitNichtEingehalten() 
	{
	return anzahlIncidentsServicezeitNichtEingehalten;
	}

	@Override
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
	Double d =  incidents.stream().mapToInt(v -> v.getLoesungszeitMinuten()).average().getAsDouble();
	return d.intValue();
	}
	
	public int getDurchschnittlicheDauerMinutenServiceAbrufe() 
	{
	Double d =  serviceAbrufe.stream().mapToInt(v -> v.getLoesungszeitMinuten()).average().getAsDouble();
	return d.intValue();
	}

	public long getAnzahlServiceAbrufeServicezeitNichtEingehalten() {
		return anzahlServiceAbrufeServicezeitNichtEingehalten;
	}
	
	@Override
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
	
	public Object[] getMonate()
	{
	return monatsStatistiken.toArray();	
	}

	public ArrayList<MonatsStatistik> getMonatsStatistiken() 
	{
	return monatsStatistiken;
	}


	public void addMonatsstatistik(MonatsStatistik incoming) 
	{
	monatsStatistiken.add(incoming);
	}	

}
