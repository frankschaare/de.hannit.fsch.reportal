/**
 * 
 */
package de.hannit.fsch.reportal.db;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.hannit.fsch.reportal.model.Quartal;
import de.hannit.fsch.reportal.model.Zeitraum;
import de.hannit.fsch.reportal.model.echolon.QuartalsStatistik;
import de.hannit.fsch.reportal.model.echolon.Vorgang;

/**
 * @author fsch
 * 
 * Lädt die Daten der letzten vier Quartale aud der Echolon View
 * und bereited diese für die Zusammenfassung auf
 *
 */
@ManagedBean(name = "ez")
@SessionScoped
public class EcholonZusammenfassung 
{
private String thema = "Zusammenfassung";
private Zeitraum abfrageZeitraum = null;
private String datumsFormat = "dd.MM.yyyy";
private DateTimeFormatter df = DateTimeFormatter.ofPattern(datumsFormat);

private ExecutorService executor = Executors.newCachedThreadPool();
private EcholonDBThread dbThread = null;
private Future<HashMap<String, Vorgang>> result = null;

private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();
private TreeMap<Integer, QuartalsStatistik> quartale = new TreeMap<Integer, QuartalsStatistik>();

	/**
	 * 
	 */
	public EcholonZusammenfassung() 
	{
	// Standardabfragezeitraum über die letzen vier Quartale:
	abfrageZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE);
	
	dbThread = new EcholonDBThread();
	dbThread.setAbfrageZeitraum(abfrageZeitraum);
	
		// DB-Abfrage starten:
		try 
		{
		result = executor.submit(dbThread);			
		distinctCases = result.get();
		
			for (Quartal q : abfrageZeitraum.getQuartale().values()) 
			{
			QuartalsStatistik qs = new QuartalsStatistik(q);
			
				for (Vorgang v : distinctCases.values()) 
				{
					if (v.getErstellDatumZeit().isAfter(q.getStartDatumUhrzeit()) && v.getErstellDatumZeit().isBefore(q.getEndDatumUhrzeit())) 
					{
					qs.addVorgang(v);	
					}
				}
			qs.setQuartalswerte();	
			quartale.put(q.getIndex(), qs);	
			}
		} 
		catch (InterruptedException | ExecutionException e) 
		{
		e.printStackTrace();
		}
	}
	
	public Object[] getQuartale()
	{
	return quartale.values().toArray();	
	}

	public String getThema() {
		return thema;
	}

	public String getSubtitle() 
	{
	return "AuswertungsZeitraum: " + df.format(abfrageZeitraum.getStartDatum()) + " bis " + df.format(abfrageZeitraum.getEndDatum());
	}

}
