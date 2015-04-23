/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.hannit.fsch.reportal.db.CallcenterDBThread;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 *
 */
@ManagedBean(eager=true)
@SessionScoped
public class CallcenterAuswertung 
{
private Zeitraum auswertungsZeitraum = null;
private CallcenterDBThread callcenterAbfrage = null;

	/**
	 * Lädt CallCenter-Daten aus der DB und bereitet diese für das Webinterface vor 
	 */
	public CallcenterAuswertung() 
	{
	callcenterAbfrage = new CallcenterDBThread();
	callcenterAbfrage.setAbfrageZeitraum(new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE));
	}

	public Zeitraum getAuswertungsZeitraum() {return auswertungsZeitraum;}

	public void setAuswertungsZeitraum(Zeitraum auswertungsZeitraum) {this.auswertungsZeitraum = auswertungsZeitraum;}
	
	

}
