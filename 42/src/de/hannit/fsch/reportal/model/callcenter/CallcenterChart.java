/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.hannit.fsch.reportal.db.CallcenterDBThread;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
@ManagedBean
@SessionScoped
public class CallcenterChart 
{
private CallcenterDBThread callcenterAbfrage = null;
private Future<TreeMap<LocalDateTime, CallcenterStatistik>> result = null;
private ExecutorService executor = Executors.newCachedThreadPool();
private Zeitraum standardZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE, null);
private int selectedZeitraum = 0;
private long maxValue = 0;
private String ticks = null;
private String tickTyp = null;
private String ticksStuendlich = null;
private String ticksMonatlich = null;
private String seriesEingehendeAnrufeMonatlich = null;
private String seriesAnsagetextMonatlich = null;
private String seriesErfolglosMonatlich = null;
private String seriesWartezeitMonatlich = null;
private String seriesEingehendeAnrufeStuendlich = null;
private String seriesAnsagetextStuendlich = null;
private String seriesErfolglosStuendlich = null;
private String seriesWartezeitStuendlich = null;
private String seriesEingehendeAnrufe = null;
private String seriesAnsagetext = null;
private String seriesErfolglos = null;
private String seriesWartezeit = null;
private String chartTitle = "'Anrufe je Kalenderwoche'";
private String chartSubTitle = null;

private TreeMap<LocalDateTime, CallcenterStatistik> statisiken;
private CallcenterAuswertung auswertung = null;

	/**
	 * Managed Bean für die Darstellung der Echolon-Daten im Chart 
	 */
	public CallcenterChart() 
	{
	// Standardmässig werden die Callcenter Daten der vergangenen vier Quartale abgefragt.
	callcenterAbfrage = new CallcenterDBThread();
	setSelectedZeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE);
	auswertung = new CallcenterAuswertung(statisiken);
	}
	
	public int getSelectedZeitraum() {return selectedZeitraum;}

	public void setSelectedZeitraum(int selectedZeitraum) 
	{
	this.selectedZeitraum = selectedZeitraum;
	Zeitraum selected = null;
		switch (selectedZeitraum) 
		{
		case Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE:
		selected = standardZeitraum;
		break;

		case Zeitraum.BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE:
		selected = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE, null);
		break;

		default:
			break;
		}

	callcenterAbfrage.setAbfrageZeitraum(selected);
	result = executor.submit(callcenterAbfrage);
		try 
		{
		statisiken = result.get();
		this.chartSubTitle = "'Auswertungszeitraum:  " + selected.getBerichtszeitraumStart() + " bis " + selected.getBerichtszeitraumEnde() + "'";
		} 
		catch (InterruptedException | ExecutionException e) 
		{
		e.printStackTrace();
		}		
	}
	
	public String getChartTitle() {return chartTitle;}
	public String getChartSubTitle() {return chartSubTitle;}

	public String getTicks() 
	{
	ticks = "[";	
    	for (CallcenterKWStatistik kw : auswertung.getStatistikenKW().values()) 
    	{
    	ticks = ticks + (kw.getWochenNummer() < 10 ? "0" + kw.getWochenNummer() : kw.getWochenNummer()) + ",";	
		}
    ticks = ticks + "]";
    
    return ticks;
	}
	
	public String getTicksStuendlich() 
	{
	ticksStuendlich = "[";	
    	for (String t : auswertung.getStatistikenStuendlich().keySet()) 
    	{
    	ticksStuendlich = ticksStuendlich + "'" + t + "',";	
		}
    ticksStuendlich = ticksStuendlich + "]";
    
	return ticksStuendlich;
	}
	
	public String getTicksMonatlich() 
	{
	DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM yy");
	
	ticksMonatlich = "[";	
    	for (LocalDate monat : auswertung.getStatistikenMonatlich().keySet()) 
    	{
    	ticksMonatlich = ticksMonatlich + "'" + df.format(monat) + "',";	
		}
    ticksMonatlich = ticksMonatlich + "]";
    
	return ticksMonatlich;
	}	

	public String getSeriesEingehendeAnrufe() 
	{
	seriesEingehendeAnrufe = "[";	
		for (CallcenterKWStatistik kw : auswertung.getStatistikenKW().values()) 
		{
    	seriesEingehendeAnrufe = seriesEingehendeAnrufe + kw.getEingehendeAnrufe() + ",";	
		}
    seriesEingehendeAnrufe = seriesEingehendeAnrufe + "]";
	
	return seriesEingehendeAnrufe;
	}
	
	public String getSeriesEingehendeAnrufeStuendlich() 
	{
	seriesEingehendeAnrufeStuendlich = "[";	
		for (CallcenterStundenStatistik ch : auswertung.getStatistikenStuendlich().values()) 
		{
		seriesEingehendeAnrufeStuendlich = seriesEingehendeAnrufeStuendlich + ch.getEingehendeAnrufe() + ",";	
		}
	seriesEingehendeAnrufeStuendlich = seriesEingehendeAnrufeStuendlich + "]";
	
	return seriesEingehendeAnrufeStuendlich;
	}	

	public String getSeriesEingehendeAnrufeMonatlich() 
	{
	seriesEingehendeAnrufeMonatlich = "[";	
		for (CallcenterMonatsStatistik cm : auswertung.getStatistikenMonatlich().values()) 
		{
		seriesEingehendeAnrufeMonatlich = seriesEingehendeAnrufeMonatlich + cm.getEingehendeAnrufe() + ",";	
		}
		seriesEingehendeAnrufeMonatlich = seriesEingehendeAnrufeMonatlich + "]";
	
	return seriesEingehendeAnrufeMonatlich;
	}	
	
	
	public String getSeriesAnsagetextStuendlich() 
	{
	seriesAnsagetextStuendlich = "[";	
		for (CallcenterStundenStatistik ch : auswertung.getStatistikenStuendlich().values()) 
		{
		seriesAnsagetextStuendlich = seriesAnsagetextStuendlich + ch.getAnrufeInWarteschlange() + ",";	
		}
	seriesAnsagetextStuendlich = seriesAnsagetextStuendlich + "]";		
	
	return seriesAnsagetextStuendlich;
	}
	
	public String getSeriesAnsagetextMonatlich() 
	{
	seriesAnsagetextMonatlich = "[";	
		for (CallcenterMonatsStatistik cm : auswertung.getStatistikenMonatlich().values()) 
		{
		seriesAnsagetextMonatlich = seriesAnsagetextMonatlich + cm.getAnrufeInWarteschlange() + ",";	
		}
	seriesAnsagetextMonatlich = seriesAnsagetextMonatlich + "]";
	
	return seriesAnsagetextMonatlich;
	}	

	public String getSeriesErfolglosStuendlich() 
	{
	seriesErfolglosStuendlich = "[";	
		for (CallcenterStundenStatistik ch : auswertung.getStatistikenStuendlich().values()) 
		{
		seriesErfolglosStuendlich = seriesErfolglosStuendlich + ch.getInWarteschlangeAufgelegt() + ",";	
		}
	seriesErfolglosStuendlich = seriesErfolglosStuendlich + "]";			
		
	return seriesErfolglosStuendlich;
	}
	
	public String getSeriesErfolglosMonatlich() 
	{
	seriesErfolglosMonatlich = "[";	
		for (CallcenterMonatsStatistik cm : auswertung.getStatistikenMonatlich().values()) 
		{
		seriesErfolglosMonatlich = seriesErfolglosMonatlich + cm.getInWarteschlangeAufgelegt() + ",";	
		}
	seriesErfolglosMonatlich = seriesErfolglosMonatlich + "]";
	
	return seriesErfolglosMonatlich;
	}		

	public String getSeriesWartezeitStuendlich() 
	{
	seriesWartezeitStuendlich = "[";	
		for (CallcenterStundenStatistik ch : auswertung.getStatistikenStuendlich().values()) 
		{
		seriesWartezeitStuendlich = seriesWartezeitStuendlich + ch.getAvgWarteZeitSekunden() + ",";	
		}
	seriesWartezeitStuendlich = seriesWartezeitStuendlich + "]";			
		
	return seriesWartezeitStuendlich;
	}

	public String getSeriesWartezeitMonatlich() 
	{
	seriesWartezeitMonatlich = "[";	
		for (CallcenterMonatsStatistik cm : auswertung.getStatistikenMonatlich().values()) 
		{
		seriesWartezeitMonatlich = seriesWartezeitMonatlich + cm.getAvgWarteZeitSekunden() + ",";	
		}
	seriesWartezeitMonatlich = seriesWartezeitMonatlich + "]";
	
	return seriesWartezeitMonatlich;
	}
	
	public String getSeriesAnsagetext() 
	{
	seriesAnsagetext = "[";	
		for (CallcenterKWStatistik kw : auswertung.getStatistikenKW().values()) 
		{
    	seriesAnsagetext = seriesAnsagetext + kw.getAnrufeInWarteschlange() + ",";	
		}
    seriesAnsagetext = seriesAnsagetext + "]";
    
	return seriesAnsagetext;
	}

	public String getSeriesErfolglos() 
	{
	seriesErfolglos = "[";	
		for (CallcenterKWStatistik kw : auswertung.getStatistikenKW().values()) 
		{
    	seriesErfolglos = seriesErfolglos + kw.getInWarteschlangeAufgelegt() + ",";	
		}
    seriesErfolglos = seriesErfolglos + "]";	
	
	return seriesErfolglos;
	}

	public String getSeriesWartezeit() 
	{
	seriesWartezeit = "[";	
    	for (CallcenterKWStatistik kw : auswertung.getStatistikenKW().values()) 
    	{
    	seriesWartezeit = seriesWartezeit + kw.getAvgWarteZeitSekunden() + ",";	
		}
    seriesWartezeit = seriesWartezeit + "]";	
		
	return seriesWartezeit;
	}
	
	/**
	 * Liefert die Statistik 'Wartezeit nach KW' welche die Detaildatensätze für den Export nach Excel enthält
	 */
	public Collection<CallcenterKWStatistik> getSumKW() 
	{
	return auswertung.getStatistikenKW().values();
	}

	public Collection<CallcenterStundenStatistik> getStundenStatistiken() 
	{
	return auswertung.getStatistikenStuendlich().values();
	}
	
	public long getMaxValue() {return maxValue;}

	public int getAnzahlVorgaengeGesamt()
	{
	return statisiken.size();	
	}

	public String setTickTyp(String aufloesung)
	{
	this.tickTyp = aufloesung;	
	this.chartTitle = "'Anrufe je Monat'";	
	return "/charts/callcenter/ccm";	
	}
}
