package de.hannit.fsch.reportal.model.callcenter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Stream;

import de.hannit.fsch.reportal.model.Zeitraum;

public class CallcenterKWStatistik extends CallcenterStatistik 
{
private TreeMap<LocalDate, CallcenterTagesStatistik> statistikenTag = null;	
private Stream<CallcenterTagesStatistik> tagesStatistikenStream = null;

	public CallcenterKWStatistik() 
	{
	statistikenTag = new TreeMap<LocalDate, CallcenterTagesStatistik>();
	auswertungsZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_KW, null);
	}

	public void addTagesStatistik(CallcenterTagesStatistik incoming) 
	{
	statistikenTag.put(incoming.getAuswertungsTag(), incoming);	
	
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
	
	@Override
	public int getWochenNummer() 
	{
	return this.auswertungsZeitraum.getKw().getKw();
	}

	/*
	 * (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.callcenter.CallcenterStatistik#getEingehendeAnrufe()
	 * 
	 * Summiert die eingehenden Anrufe der KW aus dem Tageswerten
	 */
	@Override
	public int getEingehendeAnrufe() 
	{
	tagesStatistikenStream = statistikenTag.values().stream();	
	this.eingehendeAnrufe = tagesStatistikenStream.mapToInt(ts -> ts.getEingehendeAnrufe()).sum();
	return eingehendeAnrufe;
	}
	
	@Override
	public int getZugeordneteAnrufe() 
	{
	tagesStatistikenStream = statistikenTag.values().stream();
	this.zugeordneteAnrufe = tagesStatistikenStream.mapToInt(ts -> ts.getZugeordneteAnrufe()).sum(); 
	return zugeordneteAnrufe;
	}
	
	@Override
	public int getAngenommeneAnrufe() 
	{
	tagesStatistikenStream = statistikenTag.values().stream();
	this.angenommeneAnrufe = tagesStatistikenStream.mapToInt(ts -> ts.getAngenommeneAnrufe()).sum();
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
	tagesStatistikenStream = statistikenTag.values().stream();		
	return tagesStatistikenStream.mapToInt(ts -> ts.getAnrufeInWarteschlange()).sum();
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
	tagesStatistikenStream = statistikenTag.values().stream();
	this.InWarteschlangeAufgelegt = tagesStatistikenStream.mapToInt(ts -> ts.getInWarteschlangeAufgelegt()).sum();
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


	/*
	 * (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.callcenter.CallcenterStatistik#getAvgWarteZeitSekunden()
	 * 
	 * Bildet den Durchschnitt der stündlichen Durchschnittswerte
	 * Chartseries: Wartezeit
	 */
	@Override
	public int getAvgWarteZeitSekunden() 
	{
	tagesStatistikenStream = statistikenTag.values().stream();
	return tagesStatistikenStream.mapToInt(ts -> ts.getAvgWarteZeitSekunden()).sum() / statistikenTag.size();
	}

	public void setSummen() 
	{
	tagesStatistikenStream = statistikenTag.values().stream();
	this.eingehendeAnrufe = tagesStatistikenStream.mapToInt(cs -> cs.getEingehendeAnrufe()).sum();

	tagesStatistikenStream = statistikenTag.values().stream();
	this.angenommeneAnrufe = tagesStatistikenStream.mapToInt(cs -> cs.getAngenommeneAnrufe()).sum();
	
	tagesStatistikenStream = statistikenTag.values().stream();
	this.zugeordneteAnrufe = tagesStatistikenStream.mapToInt(cs -> cs.getZugeordneteAnrufe()).sum();
	
	tagesStatistikenStream = statistikenTag.values().stream();
	this.anrufeInWarteschlange = tagesStatistikenStream.mapToInt(cs -> cs.getAnrufeInWarteschlange()).sum();
		
	tagesStatistikenStream = statistikenTag.values().stream();
	this.InWarteschlangeAufgelegt = tagesStatistikenStream.mapToInt(cs -> cs.getInWarteschlangeAufgelegt()).sum();
	
	tagesStatistikenStream = statistikenTag.values().stream();
	this.trotzZuordnungAufgelegt = tagesStatistikenStream.mapToInt(cs -> cs.getTrotzZuordnungAufgelegt()).sum();
		
	tagesStatistikenStream = statistikenTag.values().stream();
	this.avgWarteZeitSekunden = tagesStatistikenStream.mapToInt(cs -> cs.getAvgWarteZeitSekunden()).sum() / statistikenTag.size();
		
	this.nodeName = getWochenNummer() < 10  ? "KW 0" + String.valueOf(getWochenNummer()) : "KW " + String.valueOf(getWochenNummer());
		
	}

	public TreeMap<LocalDate, CallcenterTagesStatistik> getStatistikenTag() {
		return statistikenTag;
	}
	
	@Override
	public ArrayList<CallcenterStatistik> getDaten() 
	{
	daten = new ArrayList<CallcenterStatistik>();
	ArrayList<CallcenterStatistik> chs = null;
	
		for (CallcenterTagesStatistik ct : statistikenTag.values()) 
		{
		chs = ct.getDaten();	
			for (CallcenterStatistik ch : chs) 
			{
			daten.add(ch);	
			}
		}
	return daten;
	}
}
