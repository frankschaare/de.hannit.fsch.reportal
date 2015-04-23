package de.hannit.fsch.reportal.model.echolon;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.hannit.fsch.reportal.model.DatumsConstants;
import de.hannit.fsch.reportal.model.Zeitraum;

public class JahresStatistik 
{
private int anzahlVorgaengeGesamt = 0;
private int anzahlIncidents = 0;
private int anzahlServiceabrufe = 0;

private ArrayList<Vorgang> vorgaengeGesamt = null;
private ArrayList<Vorgang> vorgaenge = null;
private ArrayList<Vorgang> incidents = new ArrayList<Vorgang>();
private ArrayList<Vorgang> serviceAbrufe = new ArrayList<Vorgang>();
private Stream<Vorgang> si = null;

private LocalDate berichtsZeitraum = null;
private String berichtsJahr = null;
private String quartal = null;
private HashMap<Integer, MonatsStatistik> monatsStatistiken = new HashMap<Integer, MonatsStatistik>();

private Zeitraum abfrageZeitraum = null;

	public JahresStatistik(ArrayList<Vorgang> vorgaenge, String berichtsJahr) 
	{
	filterDistinc(vorgaenge);
	setBerichtsJahr(berichtsJahr);
	setAnzahlVorgaengeGesamt(vorgaengeGesamt.size());
	split(vorgaengeGesamt);
	}
	
	public JahresStatistik(ArrayList<Vorgang> vorgaenge, Zeitraum berichtsZeitraum) 
	{
	this.abfrageZeitraum = berichtsZeitraum;	
	filterDistinc(vorgaenge);
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
	
	/*
	 * Hier werden die gesamten Vorgänge nach Incidents und Serviceabrufen gesplittet.
	 * Sobald eine Information verfügbar ist, wird das ChartModel initiiert
	 */
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
		
		/*
		 * Aufteilung der gesamten Vorgänge nach Monaten	
		 */
		int berichtsMonat = v.getBerichtsMonat();
			if (monatsStatistiken.containsKey(berichtsMonat)) 
			{
			monatsStatistiken.get(berichtsMonat).addVorgang(v);	
			} 
			else 
			{
			MonatsStatistik m = new MonatsStatistik(LocalDate.of(berichtsZeitraum.getYear(), berichtsMonat, 1));
			m.addVorgang(v);
			monatsStatistiken.put(berichtsMonat, m);
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
	
	public void setBerichtsJahr(String berichtsJahr) 
	{
	this.berichtsJahr = berichtsJahr;
	this.berichtsZeitraum = LocalDate.of(Integer.parseInt(berichtsJahr), 1, 1);
	}

	/*
	 * Die Datenbanksicht gibt für jeden Vorgang ZWEI Datensätze aus.
	 * Anhand der ID wird daher hier recht umständlich sortiert.
	 * Dazu wird eine HasMap mit den eindeutigen ID's erstellt
	 */
	public void filterDistinc(ArrayList<Vorgang> incoming) 
	{
	HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();	
	vorgaengeGesamt = new ArrayList<Vorgang>();
	
		for (Vorgang vorgang : incoming) 
		{
			if (!distinctCases.containsKey(vorgang.getId())) 
			{
			distinctCases.put(vorgang.getId(), vorgang);
			vorgaengeGesamt.add(vorgang);
			} 
		}
	}
	
	public void setVorgaenge(ArrayList<Vorgang> filtered) 
	{
	this.vorgaenge = filtered;
	setAnzahlVorgaengeGesamt(vorgaenge.size());
	split(vorgaenge);
	}

	public void setBerichtsMonat(int berichtsMonat) 
	{
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
