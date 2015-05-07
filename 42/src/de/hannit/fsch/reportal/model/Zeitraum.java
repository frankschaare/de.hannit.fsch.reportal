/**
 * 
 */
package de.hannit.fsch.reportal.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

/**
 * @author fsch
 * Die Klasse Zeitraum berechnet, ausgehend vom heutigen Datum,
 * die Abfragezeitraum für die Daten
 */
public class Zeitraum implements Berichtszeitraum 
{
private LocalDate heute = LocalDate.now();	
private LocalDate startDatum = null;
private LocalDate endDatum = null;
private LocalDateTime startDatumUhrzeit = null;
private LocalDateTime endDatumUhrzeit = null;
private String datumsFormat = "dd.MM.yyyy";
private DateTimeFormatter df = DateTimeFormatter.ofPattern(datumsFormat);

private KalenderWoche kw = null;
private LocalDate auswertungsTag = null;
private TreeMap<Integer, Quartal> quartale = null;


	/**
	 * Der Konstruktor empfängt Start- und Endzeitpunkt als LocalDate
	 * Konstanten für mögliche Berichtszeiträume
	 */
	public Zeitraum(LocalDate startZeit, LocalDate endZeit) 
	{
	this.startDatum = startZeit;	
	this.endDatum = endZeit;
	}
	
	/**
	 * Der Konstruktor empfängt einen der im Interface Berichtszeitraum vordefinierten
	 * Konstanten für mögliche Berichtszeiträume
	 */
	public Zeitraum(int zeitRaum) 
	{
		switch (zeitRaum) 
		{
		// Berechnung der letzten vier Quartale
		case BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE:
			switch (heute.getMonthValue()) 
			{
			// Heute ist im 1. Quartal: Abfrage Quartale 1-4 des Vorjahres
			case 1:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.JANUARY, 1);
			this.endDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.DECEMBER, 31);
			break;
			case 2:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.JANUARY, 1);
			this.endDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.DECEMBER, 31);
			break;
			case 3:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.JANUARY, 1);
			this.endDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.DECEMBER, 31);
			break;
			// Heute ist im 2. Quartal: Abfrage Quartale 2-4 des Vorjahres und Quartal 1 des aktuellen Jahres			
			case 4:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.APRIL, 1);
			this.endDatum = LocalDate.of(heute.getYear(), Month.MARCH, 31);
			break;
			case 5:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.APRIL, 1);
			this.endDatum = LocalDate.of(heute.getYear(), Month.MARCH, 31);
			break;			
			case 6:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.APRIL, 1);
			this.endDatum = LocalDate.of(heute.getYear(), Month.MARCH, 31);
			break;
			// Heute ist im 3. Quartal: Abfrage Quartale 3-4 des Vorjahres und Quartal 1-2 des aktuellen Jahres			
			case 7:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.JULY, 1);
			this.endDatum = LocalDate.of(heute.getYear(), Month.JUNE, 30);
			break;
			case 8:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.JULY, 1);
			this.endDatum = LocalDate.of(heute.getYear(), Month.JUNE, 30);
			break;			
			case 9:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.JULY, 1);
			this.endDatum = LocalDate.of(heute.getYear(), Month.JUNE, 30);
			break;
			// Heute ist im 4. Quartal: Abfrage Quartal 4 des Vorjahres und Quartal 1-3 des aktuellen Jahres			
			case 10:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.OCTOBER, 1);
			this.endDatum = LocalDate.of(heute.getYear(), Month.SEPTEMBER, 30);
			break;
			case 11:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.OCTOBER, 1);
			this.endDatum = LocalDate.of(heute.getYear(), Month.SEPTEMBER, 30);
			break;			
			case 12:
			this.startDatum = LocalDate.of(heute.minusYears(1).getYear(), Month.OCTOBER, 1);
			this.endDatum = LocalDate.of(heute.getYear(), Month.SEPTEMBER, 30);
			break;						
			default:
			break;
			}
		
		// Da hier die letzten vier Quartale abgefragt werden, werden diese auch automatisch berechnet:
		setQuartale();
		
		break;
		
		// Berechnung der letzten zwölf Monate
		case BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE:
		LocalDate monatsErster = LocalDate.of(heute.getYear(), heute.getMonthValue(), 1);
		this.startDatum = monatsErster.minusMonths(12);
		this.endDatum = monatsErster.minusDays(1);
		break;

		// Berechnung Gesamtzeitraum. Daten liegen erst ab dem 01.01.2013 vor
		case BERICHTSZEITRAUM_GESAMT:
		this.startDatum = LocalDate.of(2013, 1, 1);
		this.endDatum = heute;
		break;
		
		default:
		break;
		}
	
	}
	
	/*
	 * Berechnet die enthaltenen Quartale und liegt diese in der TreeMap ab.
	 * 
	 * ACHTUNG ! 
	 * Der Index ist die Reihenfolge der Quartale im Abfragezeitraum,
	 * NICHT die Quartalsnummer ! 
	 * ACHTUNG ! 
	 *  
	 */
	public void setQuartale() 
	{
	int index = 1;	
	LocalDate tmp = startDatum;
	Quartal q = null;
	quartale = new TreeMap<Integer, Quartal>();
	
		while (tmp.isBefore(endDatum)) 
		{
		q = new Quartal(tmp.getMonthValue(), tmp.getYear());
			if (!quartale.containsKey(index)) 
			{
			q.setIndex(index);	
			quartale.put(index, q);	
			}
		index +=1;	
		tmp = tmp.plusMonths(3);	
		}
	}
	
	public TreeMap<Integer, Quartal> getQuartale() 
	{
	return quartale;
	}

	public LocalDate getStartDatum() 
	{
		if (startDatum == null) 
		{
		startDatum = startDatumUhrzeit != null ? LocalDate.of(startDatumUhrzeit.getYear(), startDatumUhrzeit.getMonthValue(), startDatumUhrzeit.getDayOfMonth()) : null;	
		}
	return startDatum;
	}
	
	public void setStartDatum(LocalDate startDatum) 
	{
	this.startDatum = startDatum;
	}

	public void setEndDatum(LocalDate endDatum) 
	{
	this.endDatum = endDatum;
	}

	public Date getSQLStartDatum() {return Date.valueOf(startDatum);}
	public LocalDate getEndDatum() 
	{
		if (endDatum == null) 
		{
		endDatum = endDatumUhrzeit != null ? LocalDate.of(endDatumUhrzeit.getYear(), endDatumUhrzeit.getMonthValue(), endDatumUhrzeit.getDayOfMonth()) : null;	
		}
	return endDatum;
	}
	
	/*
	 * ACHTUNG: Um Fallstricke bei der Datumskonvertierung der Datenbank zu vermeiden,
	 * liefert diese Methode den FOLGETAG des Enddatums. 
	 * 
	 * Das Abfragestatement werden dann die Daten abgefragt, die KLEINER als das hier gelieferte Datum sind !
	 */
	public Date getSQLEndDatum() 
	{
	LocalDate sqlDatum = endDatum.plusDays(1);	
	return Date.valueOf(sqlDatum);
	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#getBerichtszeitraum()
	 */
	@Override
	public String getBerichtszeitraum() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getBerichtszeitraumStart() 
	{
	return df.format(startDatum);
	}

	public String getBerichtszeitraumEnde() 
	{
	return df.format(endDatum);
	}
	
	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#setBerichtszeitraum(java.lang.String)
	 */
	@Override
	public void setBerichtszeitraum(String berichtsZeitraum) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#getBerichtsJahr()
	 */
	@Override
	public String getBerichtsJahr() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#setBerichtsJahr(java.lang.String)
	 */
	@Override
	public void setBerichtsJahr(String berichtsJahr) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#getBerichtsQuartal()
	 */
	@Override
	public String getBerichtsQuartal() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#setBerichtsQuartal(java.lang.String)
	 */
	@Override
	public void setBerichtsQuartal(String berichtsQuartal) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#getBerichtsMonat()
	 */
	@Override
	public String getBerichtsMonat() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hannit.fsch.reportal.model.Berichtszeitraum#setBerichtsMonat(java.lang.String)
	 */
	@Override
	public void setBerichtsMonat(String berichtsMonat) {
		// TODO Auto-generated method stub

	}

	public LocalDateTime getStartDatumUhrzeit() {
		return startDatumUhrzeit;
	}

	public void setStartDatumUhrzeit(LocalDateTime startDatumUhrzeit) 
	{
	this.startDatumUhrzeit = startDatumUhrzeit;
	this.kw = new KalenderWoche(startDatumUhrzeit);
	this.auswertungsTag = LocalDate.of(startDatumUhrzeit.getYear(), startDatumUhrzeit.getMonthValue(), startDatumUhrzeit.getDayOfMonth());
	}
	
	public LocalDate getAuswertungsTag() {
		return auswertungsTag;
	}

	public KalenderWoche getKw() {
		return kw;
	}

	public LocalDateTime getEndDatumUhrzeit() {
		return endDatumUhrzeit;
	}

	public void setEndDatumUhrzeit(LocalDateTime endDatumUhrzeit) {
		this.endDatumUhrzeit = endDatumUhrzeit;
	}

}
