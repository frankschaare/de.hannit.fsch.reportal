/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Stream;

import de.hannit.fsch.reportal.model.Berichtszeitraum;
import de.hannit.fsch.reportal.model.Quartal;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
public class CallcenterJahresStatistik extends CallcenterStatistik 
{
private TreeMap<LocalDate, CallcenterMonatsStatistik> monatsStatistiken = new TreeMap<LocalDate, CallcenterMonatsStatistik>();
private TreeMap<Integer, CallcenterQuartalsStatistik> quartalsStatistiken = new TreeMap<Integer, CallcenterQuartalsStatistik>();
private Stream<CallcenterMonatsStatistik> monatsStatistikenStream = null;
private int auswertungsJahr = 0;

	/**
	 * 
	 */
	public CallcenterJahresStatistik() 
	{
	auswertungsZeitraum = new Zeitraum(Berichtszeitraum.BERICHTSZEITRAUM_JAEHRLICH, null);
	}

	public void addMonatsStatistik(CallcenterMonatsStatistik incoming) 
	{
	monatsStatistiken.put(incoming.getAuswertungsMonat(), incoming);
	
		if (auswertungsZeitraum.getStartDatum() == null)
		{
		auswertungsZeitraum.setStartDatum(incoming.getAuswertungsZeitraum().getStartDatum());	
		} 
		else 
		{
			if (incoming.getAuswertungsZeitraum().getStartDatum().isBefore(auswertungsZeitraum.getStartDatum())) 
			{
			auswertungsZeitraum.setStartDatum(incoming.getAuswertungsZeitraum().getStartDatum());	
			}
		}
		if (auswertungsZeitraum.getEndDatum() == null) 
		{
		auswertungsZeitraum.setEndDatum(incoming.getAuswertungsZeitraum().getEndDatum());	
		} 
		else 
		{
			if (incoming.getAuswertungsZeitraum().getEndDatum().isAfter(auswertungsZeitraum.getEndDatum())) 
			{
			auswertungsZeitraum.setEndDatum(incoming.getAuswertungsZeitraum().getEndDatum());	
			}
		}	
		
	}
	
	public void setJahresSummen() 
	{
	monatsStatistikenStream = monatsStatistiken.values().stream();
	this.eingehendeAnrufe = monatsStatistikenStream.mapToInt(cs -> cs.getEingehendeAnrufe()).sum();
	
	monatsStatistikenStream = monatsStatistiken.values().stream();
	this.angenommeneAnrufe = monatsStatistikenStream.mapToInt(cs -> cs.getAngenommeneAnrufe()).sum();
	
	monatsStatistikenStream = monatsStatistiken.values().stream();
	this.zugeordneteAnrufe = monatsStatistikenStream.mapToInt(cs -> cs.getZugeordneteAnrufe()).sum();
	
	monatsStatistikenStream = monatsStatistiken.values().stream();
	this.anrufeInWarteschlange = monatsStatistikenStream.mapToInt(cs -> cs.getAnrufeInWarteschlange()).sum();
	
	monatsStatistikenStream = monatsStatistiken.values().stream();
	this.InWarteschlangeAufgelegt = monatsStatistikenStream.mapToInt(cs -> cs.getInWarteschlangeAufgelegt()).sum();
	
	monatsStatistikenStream = monatsStatistiken.values().stream();
	this.trotzZuordnungAufgelegt = monatsStatistikenStream.mapToInt(cs -> cs.getTrotzZuordnungAufgelegt()).sum();
	
	monatsStatistikenStream = monatsStatistiken.values().stream();
	this.avgWarteZeitSekunden = monatsStatistikenStream.mapToInt(cs -> cs.getAvgWarteZeitSekunden()).sum() / monatsStatistiken.size();
	
	setNodeName(String.valueOf(auswertungsZeitraum.getStartDatum().getYear()));
	}

	
	public TreeMap<LocalDate, CallcenterMonatsStatistik> getMonatsStatistiken() {
		return monatsStatistiken;
	}

	public int getAuswertungsJahr() {return auswertungsJahr;}

	/*
	 * Sortiert die Monatsstatistiken nach Quartalen
	 */
	public void setQuartalsStatistiken() 
	{
	quartalsStatistiken = new TreeMap<Integer, CallcenterQuartalsStatistik>();
	CallcenterQuartalsStatistik vorhanden = null;
	CallcenterQuartalsStatistik neu = null;
		
		for (Quartal q : auswertungsZeitraum.getQuartale().values()) 
		{
			for (CallcenterMonatsStatistik cm : monatsStatistiken.values()) 
			{
				if (cm.getAuswertungsZeitraum().getStartDatum().isAfter(q.getStartDatum().minusDays(1)) && cm.getAuswertungsZeitraum().getEndDatum().isBefore(q.getEndDatum().plusDays(1))) 
				{
					if (quartalsStatistiken.containsKey(q.getQuartalsNummer())) 
					{
					vorhanden = quartalsStatistiken.get(q.getQuartalsNummer());
					vorhanden.addMonatsStatistik(cm);
					} 
					else 
					{
					neu = new CallcenterQuartalsStatistik();
					neu.setQuartal(q);
					neu.addMonatsStatistik(cm);
					quartalsStatistiken.put(q.getQuartalsNummer(), neu);
					}
				}
			}
		}
		for (CallcenterQuartalsStatistik cq : quartalsStatistiken.values()) 
		{
		cq.setQuartalsSummen();	
		}
		
	}

	public TreeMap<Integer, CallcenterQuartalsStatistik> getQuartalsStatistiken() {
		return quartalsStatistiken;
	}
	
	@Override
	public ArrayList<CallcenterStatistik> getDaten() 
	{
	daten = new ArrayList<CallcenterStatistik>();
	ArrayList<CallcenterStatistik> chs = null;
	
		for (CallcenterMonatsStatistik cm : monatsStatistiken.values()) 
		{
		chs = cm.getDaten();
			for (CallcenterStatistik ch : chs) 
			{
			daten.add(ch);	
			}
		}
	return daten;
	}
}
