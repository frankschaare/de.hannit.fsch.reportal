/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.stream.Stream;

import de.hannit.fsch.reportal.model.Quartal;

/**
 * @author fsch
 *
 */
public class QuartalsStatistik extends EcholonStatistik
{
private Quartal berichtsQuartal = null;
private TreeMap<LocalDateTime, Vorgang> vorgaenge = new TreeMap<LocalDateTime, Vorgang>();
private TreeMap<LocalDateTime, Vorgang> incidents = new TreeMap<LocalDateTime, Vorgang>();
private TreeMap<LocalDateTime, Vorgang> serviceAbrufe = new TreeMap<LocalDateTime, Vorgang>();
private String bezeichnungLang = "nicht vorhanden !";

private TreeMap<LocalDate, MonatsStatistik> monatsStatistiken = new TreeMap<LocalDate, MonatsStatistik>();

private int vorgaengeGesamt = 0;
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
	
	LocalDate berichtsMonat = berichtsQuartal.getStartDatum();
	
		while (berichtsMonat.isBefore(berichtsQuartal.getEndDatum())) 
		{
		monatsStatistiken.put(berichtsMonat, new MonatsStatistik(berichtsMonat));
		berichtsMonat = berichtsMonat.plusMonths(1);	
		}
	}

	/*
	 * Hier wird ein Vorgang einem Quartal zugeordnet.
	 * Gleichzeitig wird er in die Monatsstatistiken einsortiert.
	 */
	public void addVorgang(Vorgang incoming) 
	{
	LocalDateTime erstellDatumUhrzeit = incoming.getErstellDatumZeit(); 
	LocalDate erstellMonat = LocalDate.of(erstellDatumUhrzeit.getYear(), erstellDatumUhrzeit.getMonthValue(), 1);
	
		if (monatsStatistiken.containsKey(erstellMonat)) 
		{
		monatsStatistiken.get(erstellMonat).addVorgang(incoming);	
		}
	
	vorgaenge.put(erstellDatumUhrzeit, incoming);		
	}
	
	
	/*
	 * Bildet die Summenwerte des Quartals.
	 * Gleichzeit werden auch die Summen der enthaltenen Berichtsmonate gebildet
	 */
	public void setQuartalswerte()
	{
	this.vorgaengeGesamt = vorgaenge.size();
	
	split();
	
	this.anzahlIncidents = incidents.size();
	si = incidents.values().stream();
	this.anzahlIncidentsServicezeitNichtEingehalten = si.filter(v -> !v.isZielzeitEingehalten()).count();
	this.prozentanteilIncidentsServicezeitNichtEingehalten = ((anzahlIncidentsServicezeitNichtEingehalten * 100) / (float)anzahlIncidents);
	setDurchschnittlicheDauerMinutenIncidents(getDurchschnittlicheDauerMinutenIncidents());
	
	this.anzahlserviceAbrufe = serviceAbrufe.size();
	si = serviceAbrufe.values().stream();
	this.anzahlServiceAbrufeServicezeitNichtEingehalten = si.filter(v -> !v.isZielzeitEingehalten()).count();
	this.prozentanteilServiceAbrufeServicezeitNichtEingehalten = ((anzahlServiceAbrufeServicezeitNichtEingehalten * 100) / (float)anzahlserviceAbrufe);
	setDurchschnittlicheDauerMinutenServiceAbrufe(getDurchschnittlicheDauerMinutenServiceAbrufe());
	
		for (MonatsStatistik ms : monatsStatistiken.values()) 
		{
		ms.setMonatswerte();	
		}
	}

	private void split() 
	{
		for (Vorgang v : vorgaenge.values()) 
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

	public String getBezeichnungLang() {
		return bezeichnungLang;
	}

	public int getVorgaengeGesamt() 
	{
	return vorgaengeGesamt;
	}
	
	public long getAnzahlIncidents() 
	{
	return anzahlIncidents;
	}
	
	public long getAnzahlIncidentsServicezeitNichtEingehalten() 
	{
	return anzahlIncidentsServicezeitNichtEingehalten;
	}

	public float getProzentanteilIncidentsServicezeitNichtEingehalten() {
		return prozentanteilIncidentsServicezeitNichtEingehalten;
	}	
	
	public int getDurchschnittlicheDauerMinutenIncidents() 
	{
	Double d =  incidents.values().stream().mapToInt(v -> v.getLoesungszeitMinuten()).average().getAsDouble();
	return d.intValue();
	}
	
	public int getDurchschnittlicheDauerMinutenServiceAbrufe() 
	{
	Double d =  serviceAbrufe.values().stream().mapToInt(v -> v.getLoesungszeitMinuten()).average().getAsDouble();
	return d.intValue();
	}
	
	public int getAnzahlserviceAbrufe() {return anzahlserviceAbrufe;}

	public long getAnzahlServiceAbrufeServicezeitNichtEingehalten() {
		return anzahlServiceAbrufeServicezeitNichtEingehalten;
	}

	public float getProzentanteilServiceAbrufeServicezeitNichtEingehalten() {
		return prozentanteilServiceAbrufeServicezeitNichtEingehalten;
	}
	
	public Object[] getMonate()
	{
	return monatsStatistiken.values().toArray();	
	}

	public TreeMap<LocalDate, MonatsStatistik> getMonatsStatistiken() {
		return monatsStatistiken;
	}	

}
