/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.hannit.fsch.reportal.model.Berichtszeitraum;
import de.hannit.fsch.reportal.model.KalenderWoche;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
@XmlType (propOrder = { "id", "startZeit", "endZeit", "eingehendeAnrufe", "angenommeneAnrufe", "zugeordneteAnrufe", "anrufeInWarteschlange", "inWarteschlangeAufgelegt", "trotzZuordnungAufgelegt", "avgWarteZeitSekunden" })
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
protected String berichtsZeitraum = "Berichtszeitraum: ";
protected Zeitraum auswertungsZeitraum = null;
protected ArrayList<CallcenterStatistik> daten = new ArrayList<CallcenterStatistik>();
protected DateTimeFormatter timeStampFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY mm:HH");


	/**
	 * Die Stundenstatistik enthält die Callcenter Statistik für jeweils eine Stunde. 
	 */
	public CallcenterStatistik() 
	{
	auswertungsZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_STUENDLICH, null);	
	}

	public String getBerichtsZeitraum() 
	{
		if (auswertungsZeitraum != null) 
		{
			switch (auswertungsZeitraum.getTyp())
			{
			case Berichtszeitraum.BERICHTSZEITRAUM_JAEHRLICH:
			berichtsZeitraum = "Berichtszeitraum: Gesamtjahr " + auswertungsZeitraum.getBerichtsJahr() + " (" + auswertungsZeitraum.getBerichtszeitraum() + ")";
			break;

			default:
			break;
			}
		}
	return berichtsZeitraum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getStartZeit() 
	{
	return auswertungsZeitraum.getStartDatumUhrzeit();
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
	auswertungsZeitraum.setStartDatumUhrzeit(startZeit);
	this.kw = new KalenderWoche(startZeit);
	}

	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getEndZeit() 
	{
	return auswertungsZeitraum.getEndDatumUhrzeit();
	}

	public String getFormattedDatum() 
	{
	DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.YYYY");
	
	return df.format(auswertungsZeitraum.getStartDatumUhrzeit());
	}
	
	public String getFormattedStartZeit() 
	{
	DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm");
	
	return df.format(auswertungsZeitraum.getStartDatumUhrzeit()) + " Uhr";
	}
	
	public String getFormattedEndZeit() 
	{
	DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm");
		
	return df.format(auswertungsZeitraum.getEndDatumUhrzeit()) + " Uhr";
	}

	public int getKw() 
	{
	return kw != null ? kw.getKw() : auswertungsZeitraum.getKw().getKw();
	}

	public void setEndZeit(LocalDateTime endZeit) 
	{
	this.endZeit = endZeit;
	auswertungsZeitraum.setEndDatumUhrzeit(endZeit);
	}

	@XmlTransient
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

	public double getInWarteschlangeAufgelegtProzent() 
	{
	return InWarteschlangeAufgelegt != 0 ? ((InWarteschlangeAufgelegt * 100) / eingehendeAnrufe) : 0;
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

	@XmlTransient
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public Zeitraum getAuswertungsZeitraum() {
		return auswertungsZeitraum;
	}
	
	@XmlTransient
	public ArrayList<CallcenterStatistik> getDaten() {
	return daten == null ? new ArrayList<CallcenterStatistik>() : daten;
	}
	
	public int getAnzahlDaten() {
	return daten == null ? 0 : daten.size();
	}
	
	public void setDaten(ArrayList<CallcenterStatistik> daten) 
	{
		this.daten = daten;
	}
}
