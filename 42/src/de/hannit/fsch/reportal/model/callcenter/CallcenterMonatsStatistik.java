/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Stream;

import de.hannit.fsch.reportal.model.Berichtszeitraum;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
public class CallcenterMonatsStatistik extends CallcenterStatistik 
{
private TreeMap<LocalDate, CallcenterTagesStatistik> tagesStatistiken = new TreeMap<LocalDate, CallcenterTagesStatistik>();	
private TreeMap<String, CallcenterKWStatistik> statistikenKW = null;
private Stream<CallcenterTagesStatistik> tagesStatistikenStream = null;

	/**
	 * 
	 */
	public CallcenterMonatsStatistik() 
	{
	auswertungsZeitraum = new Zeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONATLICH);
	}

	public void addTagesStatistik(CallcenterTagesStatistik ct) 
	{
	tagesStatistiken.put(ct.getAuswertungsTag(), ct);
	
		if (auswertungsZeitraum.getStartDatumUhrzeit() == null)
		{
		auswertungsZeitraum.setStartDatumUhrzeit(ct.getStartZeit());	
		} 
		else 
		{
			if (ct.getStartZeit().isBefore(auswertungsZeitraum.getStartDatumUhrzeit())) 
			{
			auswertungsZeitraum.setStartDatumUhrzeit(ct.getStartZeit());	
			}
		}
		if (auswertungsZeitraum.getEndDatumUhrzeit() == null) 
		{
		auswertungsZeitraum.setEndDatumUhrzeit(ct.getEndZeit());	
		} 
		else 
		{
			if (ct.getEndZeit().isAfter(auswertungsZeitraum.getEndDatumUhrzeit())) 
			{
			auswertungsZeitraum.setEndDatumUhrzeit(ct.getEndZeit());	
			}
		}		
	}
	
	public void setMonatsSummen() 
	{
	tagesStatistikenStream = tagesStatistiken.values().stream();
	this.eingehendeAnrufe = tagesStatistikenStream.mapToInt(cs -> cs.getEingehendeAnrufe()).sum();
	
	tagesStatistikenStream = tagesStatistiken.values().stream();
	this.anrufeInWarteschlange = tagesStatistikenStream.mapToInt(cs -> cs.getAnrufeInWarteschlange()).sum();
	
	tagesStatistikenStream = tagesStatistiken.values().stream();
	this.InWarteschlangeAufgelegt = tagesStatistikenStream.mapToInt(cs -> cs.getInWarteschlangeAufgelegt()).sum();
	
	tagesStatistikenStream = tagesStatistiken.values().stream();
	this.avgWarteZeitSekunden = tagesStatistikenStream.mapToInt(cs -> cs.getAvgWarteZeitSekunden()).sum() / tagesStatistiken.size();
	
	DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM");
	this.nodeName = df.format(auswertungsZeitraum.getStartDatum());
	
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
	
		for (CallcenterTagesStatistik tag : tagesStatistiken.values()) 
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
		for (CallcenterKWStatistik cw : statistikenKW.values()) 
		{
		cw.setSummen();	
		}
	}
	
	public TreeMap<String, CallcenterKWStatistik> getStatistikenKW() {
		return statistikenKW;
	}

	public Zeitraum getAuswertungsZeitraum() 
	{
	return auswertungsZeitraum;
	}

	public LocalDate getAuswertungsMonat()
	{
	return auswertungsZeitraum.getStartDatum();	
	}
}
