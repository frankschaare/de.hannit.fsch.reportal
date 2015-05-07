/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

import de.hannit.fsch.reportal.model.Berichtszeitraum;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
public class CallcenterTagesStatistik extends CallcenterStatistik 
{
private TreeMap<LocalDateTime, CallcenterStundenStatistik> stundenStatistiken = new TreeMap<LocalDateTime, CallcenterStundenStatistik>();
private Stream<CallcenterStundenStatistik> stundenStatistikenStream = null;


	/**
	 * Entspricht einem Tag.
	 * Enth�lt wiederum eine TreeMap mit den Stundenstatistiken.
	 */
	public CallcenterTagesStatistik() 
	{
	auswertungsZeitraum = new Zeitraum(Berichtszeitraum.BERICHTSZEITRAUM_TAG);
	}
	
	/*
	 * F�gt dem Auswertungstag eine neue Stundenstatistik hinzu
	 */
	public void addStundenStatistik(CallcenterStundenStatistik incoming) 
	{
	stundenStatistiken.put(incoming.getStartZeit(), incoming);	
		
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
		
		/*
		 * In aller Regel gibt es 10 Datens�tze pro Tag.
		 * Sind alle vorhanden, werden die Tagessummen gebildet:
		 */
		if (stundenStatistiken.size() >= 10) 
		{
		setTagessummen();	
		}
	}
	
	public void setTagessummen() 
	{
	stundenStatistikenStream = stundenStatistiken.values().stream();
	this.eingehendeAnrufe = stundenStatistikenStream.mapToInt(cs -> cs.getEingehendeAnrufe()).sum();
	
	stundenStatistikenStream = stundenStatistiken.values().stream();
	this.anrufeInWarteschlange = stundenStatistikenStream.mapToInt(cs -> cs.getAnrufeInWarteschlange()).sum();
	
	stundenStatistikenStream = stundenStatistiken.values().stream();
	this.InWarteschlangeAufgelegt = stundenStatistikenStream.mapToInt(cs -> cs.getInWarteschlangeAufgelegt()).sum();
	
	stundenStatistikenStream = stundenStatistiken.values().stream();
	this.avgWarteZeitSekunden = stundenStatistikenStream.mapToInt(cs -> cs.getAvgWarteZeitSekunden()).sum() / stundenStatistiken.size();
	
	DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.YYYY");
	this.nodeName = df.format(auswertungsZeitraum.getAuswertungsTag());
	}

	@Override
	public LocalDateTime getStartZeit() 
	{
	return this.getAuswertungsZeitraum().getStartDatumUhrzeit();
	}

	@Override
	public LocalDateTime getEndZeit() 
	{
	return this.getAuswertungsZeitraum().getEndDatumUhrzeit();
	}

	/*
	 * (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.callcenter.CallcenterStatistik#getEingehendeAnrufe()
	 * 
	 * Summiert die eingehenden Anrufe des Tages aus dem Stundenwerten
	 */
	@Override
	public int getEingehendeAnrufe() 
	{
	return this.eingehendeAnrufe;
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
	return this.anrufeInWarteschlange;
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
	return this.InWarteschlangeAufgelegt;
	}

	/*
	 * (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.callcenter.CallcenterStatistik#getAvgWarteZeitSekunden()
	 * 
	 * Bildet den Durchschnitt der st�ndlichen Durchschnittswerte
	 * Chartseries: Wartezeit
	 */
	@Override
	public int getAvgWarteZeitSekunden() 
	{
	return this.avgWarteZeitSekunden; 
	}

	public Zeitraum getAuswertungsZeitraum() {
		return auswertungsZeitraum;
	}
	
	public LocalDate getAuswertungsTag() 
	{
	return auswertungsZeitraum.getAuswertungsTag();
	}

	public TreeMap<LocalDateTime, CallcenterStundenStatistik> getStundenStatistiken() {
		return stundenStatistiken;
	}
	
}
