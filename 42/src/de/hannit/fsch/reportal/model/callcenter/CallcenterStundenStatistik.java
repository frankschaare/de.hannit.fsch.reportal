package de.hannit.fsch.reportal.model.callcenter;

import java.text.DecimalFormat;
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
	auswertungsZeitraum = new Zeitraum(Berichtszeitraum.BERICHTSZEITRAUM_STUENDLICH, null);	
	}
	
	public CallcenterStundenStatistik(LocalDateTime startZeit, LocalDateTime endZeit) 
	{
	auswertungsZeitraum = new Zeitraum(Berichtszeitraum.BERICHTSZEITRAUM_STUENDLICH, null);	
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
	
	@Override
	public int getEingehendeAnrufe() 
	{
	stundenStatistikenStream = stundenStatistiken.values().stream();	
	this.eingehendeAnrufe = stundenStatistikenStream.mapToInt(ts -> ts.getEingehendeAnrufe()).sum();
	return eingehendeAnrufe;
	}
	
	@Override
	public int getZugeordneteAnrufe() 
	{
	stundenStatistikenStream = stundenStatistiken.values().stream();
	this.zugeordneteAnrufe = stundenStatistikenStream.mapToInt(ts -> ts.getZugeordneteAnrufe()).sum(); 
	return zugeordneteAnrufe;
	}
	
	@Override
	public int getAngenommeneAnrufe() 
	{
	stundenStatistikenStream = stundenStatistiken.values().stream();
	this.angenommeneAnrufe = stundenStatistikenStream.mapToInt(ts -> ts.getAngenommeneAnrufe()).sum();
	return angenommeneAnrufe;
	}

	/*
	 * (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.callcenter.CallcenterStatistik#getAnrufeInWarteschlange()
	 * 
	 * Summiert die Anrufe des Tages in Warteschlange
	 * Chartseries: Ansagetext
	 */
	@Override
	public int getAnrufeInWarteschlange() 
	{
	stundenStatistikenStream = stundenStatistiken.values().stream();		
	return stundenStatistikenStream.mapToInt(ts -> ts.getAnrufeInWarteschlange()).sum();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.callcenter.CallcenterStatistik#getInWarteschlangeAufgelegt()
	 * 
	 * Summiert die erfolglosen Anrufe des Tages.
	 * Chartseries: Erfolglos
	 */
	@Override
	public int getInWarteschlangeAufgelegt() 
	{
	stundenStatistikenStream = stundenStatistiken.values().stream();
	this.InWarteschlangeAufgelegt = stundenStatistikenStream.mapToInt(ts -> ts.getInWarteschlangeAufgelegt()).sum();
	return InWarteschlangeAufgelegt;
	}
	
	@Override
	public double getInWarteschlangeAufgelegtProzent() 
	{
	return InWarteschlangeAufgelegt != 0 ? ((InWarteschlangeAufgelegt * 100) / (double) eingehendeAnrufe) : 0;
	}

	public String getFormattedInWarteschlangeAufgelegtProzent() 
	{
	DecimalFormat df = new DecimalFormat( "###.##" );
	return InWarteschlangeAufgelegt != 0 ? df.format(((InWarteschlangeAufgelegt * 100) / (double) eingehendeAnrufe)) : "0";
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
