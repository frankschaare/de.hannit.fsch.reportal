/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 * 
 * Bereitet die, aus der Datenbank geladenen, Callcenter-Daten f�r die weitere Verarbeitung auf.
 * Die Datens�tze werden stundenweise geliefert und werden nach:
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
private TreeMap<String, CallcenterKWStatistik> statistikenKW = null;
private TreeMap<LocalDate, CallcenterTagesStatistik> statistikenTag = null;
	/**
	 * L�dt CallCenter-Daten aus der DB und bereitet diese f�r das Webinterface vor 
	 */
	public CallcenterAuswertung(TreeMap<LocalDateTime, CallcenterStatistik> gesamt) 
	{
	this.statistikenGesamt = gesamt;
	
	// Zuerst werden die Abfragewerte nach Tagen sortiert
	setStatistikenTag(gesamt);
	
	// Dann werden die Tagesstatistiken nach Kalenderwochen ausgewertet
	setStatistikenKW();
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
	log.log(Level.INFO, "Es wurden Callcenter-KWStatistiken f�r " + statistikenKW.size() + " Kalenderwochen erstellt.");
	
	}

	public void setStatistikenTag(TreeMap<LocalDateTime, CallcenterStatistik> gesamt) 
	{
	statistikenTag = new TreeMap<LocalDate, CallcenterTagesStatistik>();
	CallcenterTagesStatistik vorhanden = null;
	CallcenterTagesStatistik neu = null;
	LocalDate auswertungTag = null;
	
		for (CallcenterStatistik cs : gesamt.values()) 
		{
		auswertungTag = LocalDate.of(cs.getStartZeit().getYear(), cs.getStartZeit().getMonthValue(), cs.getStartZeit().getDayOfMonth());
			
			if (statistikenTag.containsKey(auswertungTag)) 
			{
			vorhanden = statistikenTag.get(auswertungTag);
			vorhanden.addStundenStatistik(cs);
			} 
			else 
			{
			neu = new CallcenterTagesStatistik();
			neu.addStundenStatistik(cs);
			statistikenTag.put(auswertungTag, neu);
			}
			
		}
	log.log(Level.INFO, "Es wurden Callcenter-Tagesstatistiken f�r " + statistikenTag.size() + " Tage erstellt.");

	}	

	public TreeMap<String, CallcenterKWStatistik> getStatistikenKW() {
		return statistikenKW;
	}

	public Zeitraum getAuswertungsZeitraum() {return auswertungsZeitraum;}

	public void setAuswertungsZeitraum(Zeitraum auswertungsZeitraum) {this.auswertungsZeitraum = auswertungsZeitraum;}
	
	

}
