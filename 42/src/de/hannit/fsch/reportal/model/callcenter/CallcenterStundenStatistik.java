package de.hannit.fsch.reportal.model.callcenter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.stream.Stream;

import de.hannit.fsch.reportal.model.Zeitraum;

public class CallcenterStundenStatistik extends CallcenterStatistik 
{
private TreeMap<String, CallcenterStatistik> stundenStatistiken = new TreeMap<String, CallcenterStatistik>();
private Zeitraum auswertungsZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_TAG);
private Stream<CallcenterStatistik> stundenStatistikenStream = null;

public CallcenterStundenStatistik() 
	{

	}
	
	/*
	 * Fügt dem Auswertungstag eine neue Stundenstatistik hinzu
	 */
	public void addStundenStatistik(CallcenterStatistik incoming) 
	{
	stundenStatistiken.put(incoming.getId(), incoming);	
		
		if (auswertungsZeitraum.getStartDatumUhrzeit() == null)
		{
		auswertungsZeitraum.setStartDatumUhrzeit(incoming.getStartZeit());	
		} 
		else 
		{
			if (incoming.getStartZeit().isBefore(auswertungsZeitraum.getStartDatumUhrzeit())) 
			{
			auswertungsZeitraum.setStartDatumUhrzeit(incoming.getStartZeit());	
			}
		}
		if (auswertungsZeitraum.getEndDatumUhrzeit() == null) 
		{
		auswertungsZeitraum.setEndDatumUhrzeit(incoming.getEndZeit());	
		} 
		else 
		{
			if (incoming.getEndZeit().isAfter(auswertungsZeitraum.getEndDatumUhrzeit())) 
			{
			auswertungsZeitraum.setEndDatumUhrzeit(incoming.getEndZeit());	
			}
		}	
	}

	/*
	 * Summiert die Werte aller enthaltenen Datensätze
	 */
	public void setSummenWerte() 
	{
	stundenStatistikenStream = stundenStatistiken.values().stream();
	this.eingehendeAnrufe = stundenStatistikenStream.mapToInt(cs -> cs.getEingehendeAnrufe()).sum();
		
	stundenStatistikenStream = stundenStatistiken.values().stream();
	this.anrufeInWarteschlange = stundenStatistikenStream.mapToInt(cs -> cs.getAnrufeInWarteschlange()).sum();
		
	stundenStatistikenStream = stundenStatistiken.values().stream();
	this.InWarteschlangeAufgelegt = stundenStatistikenStream.mapToInt(cs -> cs.getInWarteschlangeAufgelegt()).sum();
		
	stundenStatistikenStream = stundenStatistiken.values().stream();
	this.avgWarteZeitSekunden = stundenStatistikenStream.mapToInt(cs -> cs.getAvgWarteZeitSekunden()).sum() / stundenStatistiken.size();	
	}	

}
