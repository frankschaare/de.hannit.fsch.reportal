/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDateTime;

import de.hannit.fsch.reportal.model.KalenderWoche;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
public class CallcenterStatistik 
{
protected String id = null;
protected LocalDateTime startZeit = null;
protected LocalDateTime endZeit = null;
protected KalenderWoche kw = null;
protected int eingehendeAnrufe = 0;
protected int wochenNummer = 0;
protected int zugeordneteAnrufe = 0;
protected int angenommeneAnrufe = 0;
protected int anrufeInWarteschlange = 0;
protected int trotzZuordnungAufgelegt = 0;
protected int InWarteschlangeAufgelegt = 0;
protected int avgWarteZeitSekunden = 0;
protected String nodeName = null;
protected Zeitraum auswertungsZeitraum = null;


	/**
	 * Die Stundenstatistik enth�lt die Callcenter Statistik f�r jeweils eine Stunde. 
	 */
	public CallcenterStatistik() 
	{
	auswertungsZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_STUENDLICH);	
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDateTime getStartZeit() {
		return startZeit;
	}

	/*
	 * Ich hab's leider echt nicht gebacken bekommen, die Kalenderwoche sauber im Chart darzustellen.
	 * Daher dieser Hack, der die Kalenderwoche im Minutenfeld unterbringt: 
	 */
	public String getChartZeit() 
	{
	String jahr = String.valueOf(startZeit.getYear());
	String monat = startZeit.getMonthValue() < 10 ? "0" + String.valueOf(startZeit.getMonthValue()) : String.valueOf(startZeit.getMonthValue());
	String tag = startZeit.getDayOfMonth() < 10 ? "0" + String.valueOf(startZeit.getDayOfMonth()) : String.valueOf(startZeit.getDayOfMonth());
	return jahr + "-" + monat + "-" + tag + " 00:" + String.valueOf(getWochenNummer() + "AM");
	}

	public void setStartZeit(LocalDateTime startZeit) 
	{
	this.startZeit = startZeit;
	this.kw = new KalenderWoche(startZeit);
	}

	public LocalDateTime getEndZeit() {
		return endZeit;
	}


	public KalenderWoche getKw() {
		return kw;
	}

	public void setEndZeit(LocalDateTime endZeit) {
		this.endZeit = endZeit;
	}


	public int getWochenNummer() {
		return wochenNummer;
	}


	public void setWochenNummer(int wochenNummer) {
		this.wochenNummer = wochenNummer;
	}



	public int getEingehendeAnrufe() {
		return eingehendeAnrufe;
	}



	public void setEingehendeAnrufe(int eingehendeAnrufe) {
		this.eingehendeAnrufe = eingehendeAnrufe;
	}



	public int getZugeordneteAnrufe() {
		return zugeordneteAnrufe;
	}



	public void setZugeordneteAnrufe(int zugeordneteAnrufe) {
		this.zugeordneteAnrufe = zugeordneteAnrufe;
	}



	public int getAngenommeneAnrufe() {
		return angenommeneAnrufe;
	}



	public void setAngenommeneAnrufe(int angenommeneAnrufe) {
		this.angenommeneAnrufe = angenommeneAnrufe;
	}



	public int getAnrufeInWarteschlange() {
		return anrufeInWarteschlange;
	}



	public void setAnrufeInWarteschlange(int anrufeInWarteschlange) {
		this.anrufeInWarteschlange = anrufeInWarteschlange;
	}



	public int getTrotzZuordnungAufgelegt() {
		return trotzZuordnungAufgelegt;
	}



	public void setTrotzZuordnungAufgelegt(int trotzZuordnungAufgelegt) {
		this.trotzZuordnungAufgelegt = trotzZuordnungAufgelegt;
	}



	public int getInWarteschlangeAufgelegt() {
		return InWarteschlangeAufgelegt;
	}



	public void setInWarteschlangeAufgelegt(int inWarteschlangeAufgelegt) {
		InWarteschlangeAufgelegt = inWarteschlangeAufgelegt;
	}



	public int getAvgWarteZeitSekunden() {
		return avgWarteZeitSekunden;
	}



	public void setAvgWarteZeitSekunden(int avgWarteZeitSekunden) {
		this.avgWarteZeitSekunden = avgWarteZeitSekunden;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public Zeitraum getAuswertungsZeitraum() {
		return auswertungsZeitraum;
	}
	
	

}
