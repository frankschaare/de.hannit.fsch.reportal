/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDate;
import java.util.TreeMap;
import java.util.stream.Stream;

import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
public class CallcenterMonatsStatistik extends CallcenterStatistik 
{
private TreeMap<LocalDate, CallcenterTagesStatistik> tagesStatistiken = new TreeMap<LocalDate, CallcenterTagesStatistik>();	
private Zeitraum auswertungsZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_MONATLICH);
private Stream<CallcenterTagesStatistik> tagesStatistikenStream = null;

	/**
	 * 
	 */
	public CallcenterMonatsStatistik() 
	{
		// TODO Auto-generated constructor stub
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
	}	

}
