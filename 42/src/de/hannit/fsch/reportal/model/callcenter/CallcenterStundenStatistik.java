package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Stream;

import de.hannit.fsch.reportal.model.Berichtszeitraum;
import de.hannit.fsch.reportal.model.Zeitraum;

public class CallcenterStundenStatistik extends CallcenterStatistik 
{
private TreeMap<String, CallcenterStatistik> stundenStatistiken = new TreeMap<String, CallcenterStatistik>();
private Stream<CallcenterStatistik> stundenStatistikenStream = null;
private	DateTimeFormatter df = DateTimeFormatter.ofPattern("HH");

	public CallcenterStundenStatistik() 
	{
	auswertungsZeitraum = new Zeitraum(Berichtszeitraum.BERICHTSZEITRAUM_STUENDLICH);	
	}
	
	public CallcenterStundenStatistik(LocalDateTime startZeit, LocalDateTime endZeit) 
	{
	auswertungsZeitraum = new Zeitraum(Berichtszeitraum.BERICHTSZEITRAUM_STUENDLICH);	
	auswertungsZeitraum.setStartDatumUhrzeit(startZeit);
	auswertungsZeitraum.setEndDatumUhrzeit(endZeit);
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
	
	public void setNodeName() 
	{
	this.nodeName = df.format(auswertungsZeitraum.getStartDatumUhrzeit()) + "-" + df.format(auswertungsZeitraum.getEndDatumUhrzeit()) + " Uhr";	
	}

	@Override
	public LocalDateTime getStartZeit() 
	{
	return auswertungsZeitraum.getStartDatumUhrzeit();
	}

	@Override
	public LocalDateTime getEndZeit() 
	{
	return auswertungsZeitraum.getEndDatumUhrzeit();
	}

	@Override
	public String getNodeName() 
	{
	return this.nodeName != null ? this.nodeName : df.format(auswertungsZeitraum.getStartDatumUhrzeit()) + "-" + df.format(auswertungsZeitraum.getEndDatumUhrzeit()) + " Uhr";
	}

	@Override
	public ArrayList<CallcenterStatistik> getDaten() 
	{
	daten = new ArrayList<CallcenterStatistik>();
	
		for (CallcenterStatistik cs : stundenStatistiken.values()) 
		{
		daten.add(cs);	
		}
	return daten;
	}
}
