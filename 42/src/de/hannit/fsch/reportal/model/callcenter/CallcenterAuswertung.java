/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 * 
 * Bereitet die, aus der Datenbank geladenen, Callcenter-Daten für die weitere Verarbeitung auf.
 * Die Datensätze werden stundenweise geliefert und werden nach:
 * - Monat
 * - Kalenderwoche
 * - Stunde
 * sortiert.
 *
 */
public class CallcenterAuswertung 
{
private final static Logger log = Logger.getLogger(CallcenterAuswertung.class.getSimpleName());		
private Zeitraum auswertungsZeitraum = null;
private TreeMap<LocalDateTime, CallcenterStatistik> statistikenGesamt = null;
private TreeMap<Integer, CallcenterJahresStatistik> statistikenJaehrlich = null;
private TreeMap<LocalDate, CallcenterMonatsStatistik> statistikenMonatlich = null;
private TreeMap<String, CallcenterKWStatistik> statistikenKW = null;
private TreeMap<LocalDate, CallcenterTagesStatistik> statistikenTag = null;
private TreeMap<String, CallcenterStundenStatistik> statistikenStuendlich = null;

	/**
	 * Lädt CallCenter-Daten aus der DB und bereitet diese für das Webinterface vor 
	 */
	public CallcenterAuswertung(TreeMap<LocalDateTime, CallcenterStatistik> gesamt) 
	{
	this.statistikenGesamt = gesamt;
	
	// Zuerst werden die Abfragewerte nach Tagen sortiert
	setStatistikenTag(gesamt);
	
	// Dann werden die Tagesstatistiken nach Kalenderwochen ausgewertet
	setStatistikenKW();
	
	// Sortierung nach Tageszeit
	setStundenStatistiken(statistikenGesamt);
	
	// Sortierung nach Monaten
	setMonatsStatistiken();
	
	// Sind mehr als 12 Monatsstatistiken geladen, werden auch die Jahresstatistiken generiert:
		if (statistikenMonatlich.size() > 12) 
		{
		setJahresStatistiken();			
		}
	}

	private void setJahresStatistiken() 
	{
	statistikenJaehrlich = new TreeMap<Integer, CallcenterJahresStatistik>();
	CallcenterJahresStatistik vorhanden = null;
	CallcenterJahresStatistik neu = null;
	int key = 0;
		
		for (CallcenterMonatsStatistik cm : statistikenMonatlich.values()) 
		{
		key = cm.getAuswertungsMonat().getYear();
			if (statistikenJaehrlich.containsKey(key)) 
			{
			vorhanden = statistikenJaehrlich.get(key);
			vorhanden.addMonatsStatistik(cm);
			} 
			else 
			{
			neu = new CallcenterJahresStatistik();
			neu.addMonatsStatistik(cm);
			statistikenJaehrlich.put(key, neu);
			}
		}
		
		for (CallcenterJahresStatistik cj : statistikenJaehrlich.values()) 
		{
		cj.getAuswertungsZeitraum().setQuartale();
		cj.setQuartalsStatistiken();
		cj.setJahresSummen();	
		}
	}

	/*
	 * Sortiert die Datensätze nach Monaten
	 */
	private void setMonatsStatistiken() 
	{
	statistikenMonatlich = new TreeMap<LocalDate, CallcenterMonatsStatistik>();
	LocalDate key = null;
	CallcenterMonatsStatistik vorhanden = null;
	CallcenterMonatsStatistik neu = null;
	
		for (CallcenterTagesStatistik ct : statistikenTag.values()) 
		{
		key = LocalDate.of(ct.getStartZeit().getYear(), ct.getStartZeit().getMonthValue(), 1);
		
			if (statistikenMonatlich.containsKey(key)) 
			{
			vorhanden = statistikenMonatlich.get(key);
			vorhanden.addTagesStatistik(ct);
			} 
			else 
			{
			neu = new CallcenterMonatsStatistik();	
			neu.addTagesStatistik(ct);
			statistikenMonatlich.put(key, neu);
			}
		}
		
		for (CallcenterMonatsStatistik cm : statistikenMonatlich.values()) 
		{
		cm.setMonatsSummen();	
		}
		
	}

	/*
	 * Sortiert die Datensätze nach Uhrzeit
	 */
	private void setStundenStatistiken(TreeMap<LocalDateTime, CallcenterStatistik> incoming) 
	{
	statistikenStuendlich = new TreeMap<String, CallcenterStundenStatistik>();
	CallcenterStundenStatistik vorhanden = null;
	CallcenterStundenStatistik neu = null;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
	String key = null;
		
			for (CallcenterStatistik cs : statistikenGesamt.values()) 
			{
			key = cs.getStartZeit().format(formatter) + "-" + cs.getEndZeit().format(formatter);	
				if (statistikenStuendlich.containsKey(key)) 
				{
				vorhanden = statistikenStuendlich.get(key);
				vorhanden.addStundenStatistik(cs);
				} 
				else 
				{
				neu = new CallcenterStundenStatistik();	
				neu.addStundenStatistik(cs);
				statistikenStuendlich.put(key, neu);
				}
			}
	log.log(Level.INFO, "Es wurden " + statistikenStuendlich.size() + " stündliche Auswertungen erstellt. Beginne mit dem Summieren der Werte.");
	
		for (CallcenterStundenStatistik ch : statistikenStuendlich.values()) 
		{
		ch.setSummenWerte();
		ch.setNodeName();
		}
		
	}
	
	public TreeMap<String, CallcenterStundenStatistik> getStatistikenStuendlich() 
	{
	return statistikenStuendlich;
	}

	public TreeMap<LocalDate, CallcenterMonatsStatistik> getStatistikenMonatlich() 
	{
	return statistikenMonatlich;
	}
	
	public TreeMap<Integer, CallcenterJahresStatistik> getStatistikenJaehrlich() 
	{
	return statistikenJaehrlich;
	}

	/*
	 * Sortiert die Tagesstatistiken nach Kalenderwoche
	 */
	private void setStatistikenKW() 
	{
	statistikenKW = new TreeMap<String, CallcenterKWStatistik>();
	CallcenterKWStatistik vorhanden = null;
	CallcenterKWStatistik neu = null;
	String key = null;
	
		for (CallcenterTagesStatistik tag : statistikenTag.values()) 
		{
		key = tag.getAuswertungsZeitraum().getKw().getIndex();	
			if (statistikenKW.containsKey(key)) 
			{
			vorhanden = statistikenKW.get(key);
			vorhanden.addTagesStatistik(tag);
			} 
			else 
			{
			neu = new CallcenterKWStatistik();
			neu.addTagesStatistik(tag);
			
			statistikenKW.put(key, neu);
			}
		}
	log.log(Level.INFO, "Es wurden Callcenter-KWStatistiken für " + statistikenKW.size() + " Kalenderwochen erstellt.");
	
	}

	public void setStatistikenTag(TreeMap<LocalDateTime, CallcenterStatistik> gesamt) 
	{
	statistikenTag = new TreeMap<LocalDate, CallcenterTagesStatistik>();
	CallcenterTagesStatistik vorhanden = null;
	CallcenterTagesStatistik neu = null;
	CallcenterStundenStatistik ch = null;
	LocalDate auswertungTag = null;
	
		for (CallcenterStatistik cs : gesamt.values()) 
		{
		auswertungTag = LocalDate.of(cs.getStartZeit().getYear(), cs.getStartZeit().getMonthValue(), cs.getStartZeit().getDayOfMonth());
		ch = new CallcenterStundenStatistik(cs.getStartZeit(), cs.getEndZeit());
		ch.setId(cs.getId());
		ch.setAngenommeneAnrufe(cs.getAngenommeneAnrufe());
		ch.setAnrufeInWarteschlange(cs.getAnrufeInWarteschlange());
		ch.setAvgWarteZeitSekunden(cs.getAvgWarteZeitSekunden());
		ch.setEingehendeAnrufe(cs.getEingehendeAnrufe());
		ch.setInWarteschlangeAufgelegt(cs.getInWarteschlangeAufgelegt());
		ch.setTrotzZuordnungAufgelegt(cs.getTrotzZuordnungAufgelegt());
		ch.setZugeordneteAnrufe(cs.getZugeordneteAnrufe());
			
			if (statistikenTag.containsKey(auswertungTag)) 
			{
			vorhanden = statistikenTag.get(auswertungTag);
			vorhanden.addStundenStatistik(ch);
			} 
			else 
			{
			neu = new CallcenterTagesStatistik();
			neu.addStundenStatistik(ch);
			statistikenTag.put(auswertungTag, neu);
			}
			
		}
		for (CallcenterTagesStatistik ct : statistikenTag.values()) 
		{
		ct.setTagessummen();	
		}
	log.log(Level.INFO, "Es wurden Callcenter-Tagesstatistiken für " + statistikenTag.size() + " Tage erstellt.");

	}	

	public TreeMap<String, CallcenterKWStatistik> getStatistikenKW() {
		return statistikenKW;
	}

	public Zeitraum getAuswertungsZeitraum() {return auswertungsZeitraum;}

	public void setAuswertungsZeitraum(Zeitraum auswertungsZeitraum) {this.auswertungsZeitraum = auswertungsZeitraum;}
	
	

}
