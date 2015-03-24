package de.hannit.fsch.reportal.model.echolon;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.hannit.fsch.reportal.model.DatumsConstants;

public class JahresStatistik 
{
private int anzahlVorgaengeGesamt = 0;
private int anzahlIncidents = 0;
private int durchschnittlicheDauerMinutenIncidents = 0;
private int durchschnittlicheDauerMinutenServiceAbrufe = 0;

private int anzahlServiceabrufe = 0;

private ArrayList<Vorgang> vorgaengeGesamt = null;
private ArrayList<Vorgang> vorgaenge = null;
private ArrayList<Vorgang> incidents = new ArrayList<Vorgang>();
private ArrayList<Vorgang> serviceAbrufe = new ArrayList<Vorgang>();
private Stream<Vorgang> si = null;

private String berichtsJahr = null;
private String quartal = null;
private int berichtsMonat = 0;

	public JahresStatistik(ArrayList<Vorgang> vorgaenge, String berichtsJahr) 
	{
	this.vorgaengeGesamt = vorgaenge;
	setBerichtsJahr(berichtsJahr);
	setAnzahlVorgaengeGesamt(vorgaengeGesamt.size());
	split(vorgaengeGesamt);
	}
	
	/*
	 * Für den Fall, das bereits Quartals- oder Monatsdaten gefiltert wurden,
	 * kann hier auf das Gesamtjahr zurückgesetzt werden
	 */
	public void resetFilter()
	{
	setVorgaenge(vorgaengeGesamt);	
	}
	
	private void split(ArrayList<Vorgang> vorgaenge) 
	{
	incidents.clear();
	serviceAbrufe.clear();
	
		for (Vorgang v : vorgaenge) 
		{
			switch (v.getTyp()) 
			{
			case EcholonConstants.TYP_INCIDENT:
			incidents.add(v);
			break;

			case EcholonConstants.TYP_SERVICEABRUF:
			serviceAbrufe.add(v);
			break;
			
			default:
			break;
			}
		}
	setAnzahlIncidents(incidents.size());
	setAnzahlServiceabrufe(serviceAbrufe.size());
	}
	
	public String getQuartal() {return quartal;}

	public void setQuartal(String quartal) 
	{
	this.quartal = quartal;
	si = vorgaengeGesamt.stream();
	ArrayList<Vorgang> filtered = null;
	
		switch (quartal) 
		{
		case DatumsConstants.QUARTAL1_LANG:
		filtered = si.filter(v -> v.getBerichtsMonat() == 1 || v.getBerichtsMonat() == 2 || v.getBerichtsMonat() == 3).collect(Collectors.toCollection(ArrayList::new ));	
		break;
		case DatumsConstants.QUARTAL2_LANG:
		filtered = si.filter(v -> v.getBerichtsMonat() == 4 || v.getBerichtsMonat() == 5 || v.getBerichtsMonat() == 6).collect(Collectors.toCollection(ArrayList::new ));	
		break;
		case DatumsConstants.QUARTAL3_LANG:
		filtered = si.filter(v -> v.getBerichtsMonat() == 7 || v.getBerichtsMonat() == 8 || v.getBerichtsMonat() == 9).collect(Collectors.toCollection(ArrayList::new ));	
		break;		
		default:
		filtered = si.filter(v -> v.getBerichtsMonat() == 10 || v.getBerichtsMonat() == 11 || v.getBerichtsMonat() == 12).collect(Collectors.toCollection(ArrayList::new ));			
		break;
		}
	setVorgaenge(filtered);	
	}

	public ArrayList<Vorgang> getVorgaenge() {return vorgaenge;}
	public ArrayList<Vorgang> getIncidents() {return incidents;}
	public ArrayList<Vorgang> getServiceabrufe() {return serviceAbrufe;}
	public String getBerichtsJahr() {return berichtsJahr;}
	public void setBerichtsJahr(String berichtsJahr) {this.berichtsJahr = berichtsJahr;}

	public void setVorgaenge(ArrayList<Vorgang> filtered) 
	{
	this.vorgaenge = filtered;
	setAnzahlVorgaengeGesamt(vorgaenge.size());
	split(vorgaenge);
	}

	public void setBerichtsMonat(int berichtsMonat) 
	{
	this.berichtsMonat = berichtsMonat;
	si = vorgaengeGesamt.stream();
	ArrayList<Vorgang> filtered = null;
	filtered = si.filter(v -> v.getBerichtsMonat() == berichtsMonat).collect(Collectors.toCollection(ArrayList::new ));	
	setVorgaenge(filtered);	
	}

	public int getAnzahlVorgaengeGesamt() {
		return anzahlVorgaengeGesamt;
	}

	public void setAnzahlVorgaengeGesamt(int anzahlVorgaengeGesamt) {
		this.anzahlVorgaengeGesamt = anzahlVorgaengeGesamt;
	}

	public long getAnzahlIncidents() {return anzahlIncidents;}

	public void setAnzahlIncidents(int anzahlIncidents) {
		this.anzahlIncidents = anzahlIncidents;
	}

	public long getAnzahlIncidentsServicezeitEingehalten() 
	{
	si = incidents.parallelStream();		
	return si.filter(v -> v.isZielzeitEingehalten()).count();
	}
	
	public long getAnzahlServiceAbrufeServicezeitEingehalten() 
	{
	si = serviceAbrufe.parallelStream();		
	return si.filter(v -> v.isZielzeitEingehalten()).count();
	}
	
	public long getAnzahlIncidentsServicezeitNichtEingehalten() 
	{
	si = incidents.stream();		
	return si.filter(v -> !v.isZielzeitEingehalten()).count();
	}

	public long getAnzahlServiceAbrufeServicezeitNichtEingehalten() 
	{
	si = serviceAbrufe.stream();		
	return si.filter(v -> !v.isZielzeitEingehalten()).count();
	}
	
	public double getProzentanteilIncidentsServicezeitEingehalten() 
	{
	return (getAnzahlIncidentsServicezeitEingehalten() * 100) / incidents.size();
	}

	public double getProzentanteilServiceAbrufeServicezeitEingehalten() 
	{
	return (getAnzahlServiceAbrufeServicezeitEingehalten() * 100) / serviceAbrufe.size();
	}
	
	public double getProzentanteilIncidentsServicezeitNichtEingehalten() 
	{
	return (getAnzahlIncidentsServicezeitNichtEingehalten() * 100) / incidents.size();
	}

	public double getProzentanteilServiceAbrufeServicezeitNichtEingehalten() 
	{
	return (getAnzahlServiceAbrufeServicezeitNichtEingehalten() * 100) / serviceAbrufe.size();
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
	public int getAnzahlServiceabrufe() {return anzahlServiceabrufe;}

	public void setAnzahlServiceabrufe(int anzahlServiceabrufe) 
	{
	this.anzahlServiceabrufe = anzahlServiceabrufe;
	}

}
