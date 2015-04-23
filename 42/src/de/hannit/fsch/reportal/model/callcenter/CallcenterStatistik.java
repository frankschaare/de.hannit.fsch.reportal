/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDateTime;

/**
 * @author fsch
 *
 */
public class CallcenterStatistik 
{
private String id = null;
private LocalDateTime startZeit = null;
private LocalDateTime endZeit = null;
private int eingehendeAnrufe = 0;
private int wochenNummer = 0;
private int zugeordneteAnrufe = 0;
private int angenommeneAnrufe = 0;
private int anrufeInWarteschlange = 0;
private int trotzZuordnungAufgelegt = 0;
private int InWarteschlangeAufgelegt = 0;
private int avgWarteZeitSekunden = 0;



	/**
	 * Die Stundenstatistik enthält die Callcenter Statistik für jeweils eine Stunde. 
	 */
	public CallcenterStatistik() 
	{
		
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

	public void setStartZeit(LocalDateTime startZeit) {
		this.startZeit = startZeit;
	}

	public LocalDateTime getEndZeit() {
		return endZeit;
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
	
	

}
