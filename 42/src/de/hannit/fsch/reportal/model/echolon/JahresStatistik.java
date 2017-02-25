package de.hannit.fsch.reportal.model.echolon;

import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.application.ProjectStage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import org.primefaces.model.chart.PieChartModel;

import de.hannit.fsch.reportal.model.Chart;
import de.hannit.fsch.reportal.model.DatumsConstants;
import de.hannit.fsch.reportal.model.Quartal;
import de.hannit.fsch.reportal.model.Zeitraum;

public class JahresStatistik extends EcholonStatistik
{
private final static Logger log = Logger.getLogger(JahresStatistik.class.getSimpleName());
private String logPrefix = null;

@ManagedProperty (value = "#{chart}")
private Chart chart;	

private FacesContext fc = null;

@SuppressWarnings("unused")
private int anzahlIncidents = 0;
@SuppressWarnings("unused")
private int anzahlServiceabrufe = 0;

private ArrayList<Quartal> quartale = null;
private ArrayList<Vorgang> vorgaenge = null;
private ArrayList<Vorgang> incidents = new ArrayList<Vorgang>();
private ArrayList<Vorgang> workOrder = new ArrayList<Vorgang>();
private ArrayList<Vorgang> serviceInfo = new ArrayList<Vorgang>();
private ArrayList<Vorgang> serviceAbrufe = new ArrayList<Vorgang>();
private ArrayList<Vorgang> serviceAnfrage = new ArrayList<Vorgang>();
private ArrayList<Vorgang> beschwerden = new ArrayList<Vorgang>();
private ArrayList<Vorgang> customerRequest = new ArrayList<Vorgang>();
private ArrayList<Vorgang> shortCall = new ArrayList<Vorgang>();
private Stream<Vorgang> si = null;
private Vorgang max = null;

private String berichtsJahr = null;
private String quartal = null;
private HashMap<Integer, QuartalsStatistik> quartalsStatistiken = null;
private HashMap<Integer, MonatsStatistik> monatsStatistiken = new HashMap<Integer, MonatsStatistik>();
private TreeMap<LocalDate, TagesStatistik> tagesStatistiken = null;


	/**
	 * Dieser Konstruktor erzeugt die Jahresstatistik und alle dazugehörigen Unterstatisiken
	 * @param vorgaenge
	 * @param berichtsJahr
	 */
	public JahresStatistik(ArrayList<Vorgang> alleVorgaenge, String berichtsJahr) 
	{
	fc = FacesContext.getCurrentInstance();	
	chart = chart != null ? chart : fc.getApplication().evaluateExpressionGet(fc, "#{chart}", Chart.class);	

	this.berichtsJahr = berichtsJahr;	
	berichtsZeitraum = new Zeitraum(berichtsJahr);
	setLabel(berichtsJahr);
	int iBerichtsJahr = Integer.parseInt(berichtsJahr);
		
	si = alleVorgaenge.stream(); 
	vorgaengeBerichtszeitraum = si.filter(v -> v.getBerichtsJahr() == iBerichtsJahr).collect(Collectors.toCollection(ArrayList::new ));
	filterDistinc(vorgaengeBerichtszeitraum);
	anzahlVorgaengeBerichtszeitraum = vorgaengeBerichtszeitraum.size();
		if (getMandant() != null) 
		{
		getMandant().setAnzahlVorgaengeBerichtszeitraum(anzahlVorgaengeBerichtszeitraum);	
		} 
		

		if (vorgaengeBerichtszeitraum.size() > 0) 
		{
			split(vorgaengeBerichtszeitraum);
			/*
			 *  Nachdem Split ausgeführt wurde stehen auch alle Monatsstatistiken zur Verfügung
			 *  Es wird nun für jede Monatsstatistik die Methode setStatistik() aufgerufen.
			 *  Dadurch werden u.a. alle Tagesstatistiken erstellt und jede Tagesstatistik erstellt wiederum 
			 *  die Stundenstatistiken des Tages
			 */
			for (MonatsStatistik ms : monatsStatistiken.values()) 
			{
			ms.setStatistik();	
			}
		setMax(vorgaengeBerichtszeitraum.stream().max(Comparator.comparing(Vorgang::getErstellDatumZeit)).get());	
		setQuartale(iBerichtsJahr);	
		}
	setTagesStatistiken();	
	}

	/*
	 * Erstellt Tagesstatistiken für das Berichtsjahr
	 */
	private void setTagesStatistiken() 
	{
	LocalDate tagesDatum = null;
	tagesStatistiken = new TreeMap<LocalDate, TagesStatistik>();
	TagesStatistik ts = null;
	
		for (Vorgang vorgang : vorgaengeBerichtszeitraum) 
		{
		tagesDatum = vorgang.getErstellDatum();	
			if (tagesStatistiken.containsKey(tagesDatum)) 
			{
			tagesStatistiken.get(tagesDatum).addVorgang(vorgang);	
			} 
			else 
			{
			ts = new TagesStatistik(tagesDatum);
			ts.addVorgang(vorgang);
			tagesStatistiken.put(tagesDatum, ts);
			}
		}
		for (TagesStatistik t : tagesStatistiken.values()) 
		{
		t.setStatistik();	
		}
		if (fc.isProjectStage(ProjectStage.Development)) 
		{
		log.log(Level.INFO, this.getClass().getCanonicalName() + ": Es wurden " + tagesStatistiken.size() + " Tagesstatistiken erstellt.");	
		}	
	}

	private void setQuartale(int iBerichtsJahr) 
	{	
	int maxErstellMonat = getMax().getBerichtsMonat();
	int maxErstellTag = getMax().getErstellDatum().getDayOfMonth();
	
		switch (maxErstellMonat) 
		{
		case 1: break;
		case 2: break;
		case 3: 
			if (maxErstellTag >= 29) 
			{
			quartale = new ArrayList<Quartal>();				
			quartale.add(new Quartal(1,iBerichtsJahr));				
			}
		break;
		case 4:
		quartale = new ArrayList<Quartal>();			
		quartale.add(new Quartal(1,iBerichtsJahr));		
		break;
		case 5: 
		quartale = new ArrayList<Quartal>();		
		quartale.add(new Quartal(1,iBerichtsJahr));		
		break;
		case 6:
		quartale = new ArrayList<Quartal>();
			if (maxErstellTag >= 28) 
			{
			quartale.add(new Quartal(1,iBerichtsJahr));
			quartale.add(new Quartal(4,iBerichtsJahr));			
			}
			else 
			{
			quartale.add(new Quartal(1,iBerichtsJahr));				
			}
		break;
		case 7: 
		quartale = new ArrayList<Quartal>();			
		quartale.add(new Quartal(1,iBerichtsJahr));		
		quartale.add(new Quartal(4,iBerichtsJahr));		
		break;
		case 8: 
		quartale = new ArrayList<Quartal>();
		quartale.add(new Quartal(1,iBerichtsJahr));		
		quartale.add(new Quartal(4,iBerichtsJahr));		
		break;
		case 9:
		quartale = new ArrayList<Quartal>();			
			if (maxErstellTag >= 28) 
			{
			quartale.add(new Quartal(1,iBerichtsJahr));
			quartale.add(new Quartal(4,iBerichtsJahr));
			quartale.add(new Quartal(7,iBerichtsJahr));			
			}
			else 
			{
			quartale.add(new Quartal(1,iBerichtsJahr));
			quartale.add(new Quartal(4,iBerichtsJahr));
			}
				
		break;
		case 10:
		quartale = new ArrayList<Quartal>();			
		quartale.add(new Quartal(1,iBerichtsJahr));		
		quartale.add(new Quartal(4,iBerichtsJahr));		
		quartale.add(new Quartal(7,iBerichtsJahr));		
		break;
		case 11:
		quartale = new ArrayList<Quartal>();			
		quartale.add(new Quartal(1,iBerichtsJahr));		
		quartale.add(new Quartal(4,iBerichtsJahr));		
		quartale.add(new Quartal(7,iBerichtsJahr));		
		break;				
		default:
		quartale = new ArrayList<Quartal>();			
			if (maxErstellTag >= 28) 
			{
			quartale.add(new Quartal(1,iBerichtsJahr));
			quartale.add(new Quartal(4,iBerichtsJahr));
			quartale.add(new Quartal(7,iBerichtsJahr));
			quartale.add(new Quartal(10,iBerichtsJahr));			
			}
			else 
			{
			quartale.add(new Quartal(1,iBerichtsJahr));
			quartale.add(new Quartal(4,iBerichtsJahr));
			quartale.add(new Quartal(7,iBerichtsJahr));
			}
		break;
		}
		
		if (quartale != null) 
		{
		quartalsStatistiken = new HashMap<Integer, QuartalsStatistik>();	
			for (Quartal quartal : quartale) 
			{
				QuartalsStatistik qs =	new QuartalsStatistik(quartal);	
				
				switch (quartal.getQuartalsNummer()) 
				{
				case 1:
					qs.addMonatsstatistik(monatsStatistiken.get(1));
					qs.addMonatsstatistik(monatsStatistiken.get(2));
					qs.addMonatsstatistik(monatsStatistiken.get(3));
					break;
				case 2:
					qs.addMonatsstatistik(monatsStatistiken.get(4));
					qs.addMonatsstatistik(monatsStatistiken.get(5));
					qs.addMonatsstatistik(monatsStatistiken.get(6));
					break;
				case 3:
					qs.addMonatsstatistik(monatsStatistiken.get(7));
					qs.addMonatsstatistik(monatsStatistiken.get(8));
					qs.addMonatsstatistik(monatsStatistiken.get(9));
					break;
				case 4:
					qs.addMonatsstatistik(monatsStatistiken.get(10));
					qs.addMonatsstatistik(monatsStatistiken.get(11));
					qs.addMonatsstatistik(monatsStatistiken.get(12));
					break;
				default:
					break;
				}
				quartalsStatistiken.put(quartal.getQuartalsNummer(), qs);	
			}
		}
	}
	
	public HashMap<Integer, QuartalsStatistik> getQuartalsStatistiken() 
	{
	return quartalsStatistiken;
	}

	public TreeMap<LocalDate, TagesStatistik> getTagesStatistiken() 
	{
	return tagesStatistiken;
	}

	public JahresStatistik(ArrayList<Vorgang> vorgaenge, Zeitraum berichtsZeitraum) 
	{
	//this.abfrageZeitraum = berichtsZeitraum;	
	filterDistinc(vorgaenge);
	anzahlVorgaengeBerichtszeitraum  = vorgaengeBerichtszeitraum.size();
	split(vorgaengeBerichtszeitraum);
	}	

	/*
	 * Für den Fall, das bereits Quartals- oder Monatsdaten gefiltert wurden,
	 * kann hier auf das Gesamtjahr zurückgesetzt werden
	 */
	public void resetFilter()
	{
	setVorgaenge(vorgaengeBerichtszeitraum);	
	}
	
	/*
	 * Hier werden die gesamten Vorgänge nach Incidents und Serviceabrufen gesplittet.
	 * Sobald eine Information verfügbar ist, wird das ChartModel initiiert
	 */
	private void split(ArrayList<Vorgang> vorgaenge) 
	{
	beschwerden.clear();	
	incidents.clear();
	serviceAbrufe.clear();
	serviceAnfrage.clear();
	serviceInfo.clear();
	shortCall.clear();
	workOrder.clear();
	customerRequest.clear();
	
		for (Vorgang v : vorgaenge) 
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
			MonatsStatistik m = new MonatsStatistik(LocalDate.of(berichtsZeitraum.getStartDatum().getYear(), berichtsMonat, 1));
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
	si = vorgaengeBerichtszeitraum.stream();
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
	pieModel.set(EcholonConstants.TYP_CUSTOMERREQUEST, getAnzahlCustomerRequests());
	pieModel.set(EcholonConstants.TYP_WORKORDER, getAnzahlWorkorders());
	pieModel.set(EcholonConstants.TYP_SHORTCALL, getAnzahlShortCalls());
	pieModel.set(EcholonConstants.TYP_SERVICEINFO, getAnzahlServiceinfos());
	pieModel.set(EcholonConstants.TYP_SERVICEANFRAGE, getAnzahlServiceanfragen());
	pieModel.set(EcholonConstants.TYP_SERVICEABRUF, getAnzahlServiceAbrufe());
	pieModel.set(EcholonConstants.TYP_INCIDENT, getAnzahlIncidents());
	pieModel.set(EcholonConstants.TYP_BESCHWERDE, getAnzahlBeschwerden());
	
	pieModel.setTitle("Vorgangsübersicht");
    pieModel.setLegendPosition("w");
    pieModel.setSeriesColors(chart.getDefaultBarColors());
	
	return pieModel;
	}
	

	public String getBerichtsJahr() {return berichtsJahr;}
	
	

	@Override
	public Zeitraum getBerichtsZeitraum() 
	{
	return berichtsZeitraum;
	}



	/*
	 * Die Datenbanksicht gibt für jeden Vorgang ZWEI Datensätze aus.
	 * Anhand der ID wird daher hier recht umständlich sortiert.
	 * Dazu wird eine HasMap mit den eindeutigen ID's erstellt
	 */
	public void filterDistinc(ArrayList<Vorgang> incoming) 
	{
	HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();	
	vorgaengeBerichtszeitraum = new ArrayList<Vorgang>();
	
		for (Vorgang vorgang : incoming) 
		{
			if (!distinctCases.containsKey(vorgang.getId())) 
			{
			distinctCases.put(vorgang.getId(), vorgang);
			vorgaengeBerichtszeitraum.add(vorgang);
			} 
		}
	}
	
	public void setVorgaenge(ArrayList<Vorgang> filtered) 
	{
	this.vorgaenge = filtered;
	anzahlVorgaengeBerichtszeitraum = vorgaenge.size();
	split(vorgaenge);
	}

	public void setBerichtsMonat(int berichtsMonat) 
	{
	si = vorgaengeBerichtszeitraum.stream();
	ArrayList<Vorgang> filtered = null;
	filtered = si.filter(v -> v.getBerichtsMonat() == berichtsMonat).collect(Collectors.toCollection(ArrayList::new ));	
	setVorgaenge(filtered);	
	}

	public int getAnzahlVorgaengeGesamt() 
	{
	return vorgaengeBerichtszeitraum.size();
	}


	public void setAnzahlIncidents(int anzahlIncidents) {
		this.anzahlIncidents = anzahlIncidents;
	}
	
	public long getAnzahlIncidentsServicezeitEingehalten() 
	{
	si = incidents.stream();
	anzahlIncidentsServicezeitEingehalten = si.filter(v -> v.isZielzeitEingehalten()).count();
	return anzahlIncidentsServicezeitEingehalten;
	}

	public long getAnzahlIncidentsServicezeitNichtEingehalten() 
	{
	si = incidents.stream();
	anzahlIncidentsServicezeitNichtEingehalten = si.filter(v -> !v.isZielzeitEingehalten()).count();
	return anzahlIncidentsServicezeitNichtEingehalten;
	}
	
	public long getAnzahlServiceAbrufeServicezeitEingehalten() 
	{
	si = serviceAbrufe.stream();
	anzahlServiceAbrufeServicezeitEingehalten = si.filter(v -> v.isZielzeitEingehalten()).count();
	return anzahlServiceAbrufeServicezeitEingehalten;
	}

	public long getAnzahlServiceAbrufeServicezeitNichtEingehalten() 
	{
	si = serviceAbrufe.stream();
	anzahlServiceAbrufeServicezeitNichtEingehalten = si.filter(v -> !v.isZielzeitEingehalten()).count();
	return anzahlServiceAbrufeServicezeitNichtEingehalten;
	}
	
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
	
	public void setAnzahlServiceabrufe(int anzahlServiceabrufe) 
	{
	this.anzahlServiceabrufe = anzahlServiceabrufe;
	}

	/*
	 * Setzt den letzten Vorgang, für den Daten in der Datenbank vorhanden sind.
	 * Wird benutzt, um zu prüfen, ob die Quartale vollständig sind.
	 */
	public void setMax(Vorgang incoming) 
	{
	logPrefix = this.getClass().getCanonicalName() + ".setMax(Vorgang incoming): ";	
		
	this.max = incoming;
	if (fc.isProjectStage(ProjectStage.Development)) {log.log(Level.INFO, logPrefix + "MaxVorgang wurde gesetzt. Jahresstatistik " + getBerichtsJahr() + " enthält Daten bis " + max.getErstellDatumAsString() );}	
	}
	public Vorgang getMax() {return max;}
	public HashMap<Integer, MonatsStatistik> getMonatsStatistiken() {return monatsStatistiken;}

}
