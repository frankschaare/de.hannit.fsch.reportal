package de.hannit.fsch.reportal.model.echolon;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Vorgang 
{
private LocalDate erstellDatum = null;
private int berichtsJahr = 0;
private int berichtsMonat = 0;
private int berichtsQuartal = 0;
private LocalTime erstellZeit = null;
private String vorgangsNummer = null;
private String status = null;
private String typ = "unbekannt";
private String kategorie = null;
private int prioritaet = 0;
private boolean reaktionszeitEingehalten = false;
private boolean zielzeitEingehalten = false;
private int loesungszeitMinuten = 0;



	public Vorgang() 
	{
	
	}

	public LocalDate getErstellDatum() {
		return erstellDatum;
	}

	public void setErstellDatum(String dbValue) 
	{
	this.erstellDatum = LocalDate.parse(dbValue, DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.GERMAN));
	this.berichtsJahr = erstellDatum.getYear();
	this.berichtsMonat = erstellDatum.getMonthValue();
	
		switch (this.berichtsMonat) 
		{
		case 1: this.berichtsQuartal = 1; break;
		case 2: this.berichtsQuartal = 1; break;
		case 3: this.berichtsQuartal = 1; break;
		case 4: this.berichtsQuartal = 2; break;
		case 5: this.berichtsQuartal = 2; break;
		case 6: this.berichtsQuartal = 2; break;
		case 7: this.berichtsQuartal = 3; break;
		case 8: this.berichtsQuartal = 3; break;
		case 9: this.berichtsQuartal = 3; break;		

		default: this.berichtsQuartal = 4; break;
		}
	}

	public LocalTime getErstellZeit() {
		return erstellZeit;
	}

	public int getBerichtsJahr() {
		return berichtsJahr;
	}

	public int getBerichtsMonat() {
		return berichtsMonat;
	}

	public int getBerichtsQuartal() {
		return berichtsQuartal;
	}

	public void setErstellZeit(String dbValue) 
	{
	this.erstellZeit = LocalTime.parse(dbValue, DateTimeFormatter.ofPattern("HH:mm:ss").withLocale(Locale.GERMAN));
	}

	public String getVorgangsNummer() {
		return vorgangsNummer;
	}



	public void setVorgangsNummer(String vorgangsNummer) {
		this.vorgangsNummer = vorgangsNummer;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getTyp() {
		return typ;
	}



	public void setTyp(String typ) {
		this.typ = typ;
	}



	public String getKategorie() {
		return kategorie;
	}



	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
	}



	public int getPrioritaet() 
	{
	return prioritaet;
	}



	public void setPrioritaet(String prioritaet) 
	{
		switch (prioritaet) 
		{
		case EcholonConstants.PRIORITAET1: this.prioritaet = 1; break;
		case EcholonConstants.PRIORITAET2: this.prioritaet = 2; break;
		case EcholonConstants.PRIORITAET3: this.prioritaet = 3; break;
		default: this.prioritaet = 4; break;
		}
	}

	public boolean isReaktionszeitEingehalten() {
		return reaktionszeitEingehalten;
	}

	public void setReaktionszeitEingehalten(boolean reaktionszeitEingehalten) {
		this.reaktionszeitEingehalten = reaktionszeitEingehalten;
	}
	
	/*
	 * Gibt an, ob die Servicezeit eingehalten wurde.
	 * Auch Fälle, in denen die Servicezeit nicht eingehalten wurde, werden gezählt, wenn:
	 * - die Priortät = 4 (keine Eskalation) ist
	 * - die Lösungszeit = 0 ist
	 */
	public boolean isZielzeitEingehalten()
	{
	boolean result;
		if (zielzeitEingehalten) 
		{
		result = true;	
		}
		else
		{
		result = false;	
			if (loesungszeitMinuten == 0) 
			{
			result = true;
			}
			else 
			{
				if (prioritaet == 4) 
				{
				result = true;	
				}
			}
		}
	return result;
	}

	public void setZielzeitEingehalten(boolean zielzeitEingehalten) 
	{
		this.zielzeitEingehalten = zielzeitEingehalten;
	}



	public int getLoesungszeitMinuten() {
		return loesungszeitMinuten;
	}



	public void setLoesungszeitMinuten(int loesungszeitMinuten) {
		this.loesungszeitMinuten = loesungszeitMinuten;
	}

}
