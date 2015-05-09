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
public class CallcenterQuartalsStatistik extends CallcenterStatistik 
{
private TreeMap<LocalDate, CallcenterMonatsStatistik> monatsStatistiken = new TreeMap<LocalDate, CallcenterMonatsStatistik>();	
private Stream<CallcenterMonatsStatistik> monatsStatistikenStream = null;
private Quartal quartal = null;

	/**
	 * 
	 */
	public CallcenterQuartalsStatistik() 
	{
	auswertungsZeitraum = new Zeitraum(Berichtszeitraum.BERICHTSZEITRAUM_QUARTALSWEISE);
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

	public void setQuartal(Quartal q) 
	{
	this.quartal = q;
	auswertungsZeitraum.setAuswertungsQuartal(q);
	}

	public void setQuartalsSummen() 
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
		
	setNodeName(quartal.getBezeichnung());
	}

	public TreeMap<LocalDate, CallcenterMonatsStatistik> getMonatsStatistiken() {
		return monatsStatistiken;
	}	

	@Override
	public ArrayList<CallcenterStatistik> getDaten() 
	{
	daten = new ArrayList<CallcenterStatistik>();
	ArrayList<CallcenterStatistik> cms = null;
	
		for (CallcenterMonatsStatistik cm : monatsStatistiken.values()) 
		{
		cms = cm.getDaten();
			for (CallcenterStatistik ch : cms) 
			{
			daten.add(ch);	
			}
		}
	return daten;
	}
}
