package de.hannit.fsch.reportal.db;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import de.hannit.fsch.reportal.model.DatumsConstants;
import de.hannit.fsch.reportal.model.echolon.EcholonNode;
import de.hannit.fsch.reportal.model.echolon.EcholonStatistik;
import de.hannit.fsch.reportal.model.echolon.JahresStatistik;
import de.hannit.fsch.reportal.model.echolon.Vorgang;

@ManagedBean(name = "edb")
@ApplicationScoped
public class EcholonDBManager 
{
private final static Logger log = Logger.getLogger(EcholonDBManager.class.getSimpleName());

private ExecutorService executor = Executors.newCachedThreadPool();
private String connectionInfo = "Keine Verbindung zur Echolon Datenbank !";
private boolean connected = false;

private String aktuellesJahr = null;
private String berichtsZeitraum = null;
private ArrayList<Vorgang> vorgaenge = new ArrayList<Vorgang>();
private EcholonStatistik aktuelleStatistik = null;
private HashMap<String, JahresStatistik> dbCache = new HashMap<String, JahresStatistik>();
private int anzahlDatenGesamt = 0;
private EcholonNode selectedNode = null;

private boolean loadFinished = false;

	public EcholonDBManager() 
	{
	// Zuerst wird das aktuelle Jahr ermittelt:
	setAktuellesJahr();	


 

	
	connectionInfo = "Verbunden mit Echolon Datenbank";
	}
	
	public EcholonDBManager getEcholonDBManager()
	{
	return this;	
	}

	
	public ArrayList<Vorgang> getVorgaenge() 
	{
	return vorgaenge;
	}
		
	public void setVorgaenge(ArrayList<Vorgang> vorgaenge) 
	{
	this.vorgaenge = vorgaenge;
	}

	public boolean isLoadFinished() {return loadFinished;}

	public DefaultTreeNode getSelectedNode() {return selectedNode;}

	public void setSelectedNode(DefaultTreeNode incoming) 
	{
		if (selectedNode != null) 
		{

		}
	}

	public void onNodeSelect(NodeSelectEvent event) 
	{
	selectedNode =  (EcholonNode) event.getTreeNode();	
	setJahresstatistik((EcholonStatistik) selectedNode.getData());
	/*
	String nodeString = event.getTreeNode().getData().toString();
		if (aktuelleJahresstatistik == null) 
		{
			try 
			{
			aktuelleJahresstatistik = result.get();
				if (!dbCache.containsKey(aktuelleJahresstatistik.getBerichtsJahr())) 
				{
				dbCache.put(aktuelleJahresstatistik.getBerichtsJahr(), aktuelleJahresstatistik);
				}
			loadFinished = true;
			}
			catch (InterruptedException | ExecutionException e) 
			{
			e.printStackTrace();
			}	
		}
	filter(nodeString);
	*/
	}
	
	/*
	 * Der Navigationsbaum sendet beim Öffnen eines Knotens einen 
	 * AJAX-NodeExpand Event, welcher mit:
	 * <p:ajax event="expand" listener="#{edb.onNodeExpand}" />
	 * registriert wurde.
	 * 
	 * Eine gute Gelegenheit, vorab Daten aus der langsamen Echolon-DB zu cachen,
	 * sofern es sich um einen Jahresknoten (Elternknoten = "root") handelt. 
	 */
	public void onNodeExpand(NodeExpandEvent event) 
	{
	TreeNode expandedNode = event.getTreeNode();
	}	

	/*
	 * Ermittelt das aktuelle Jahr.
	 * 
	 * Es werden zuerst nur die Vorgänge für das aktuelle Jahr geladen, um die Datenbank zu entlasten,
	 * da die Abfrage recht lange dauert.
	 * 
	 * Solange das aktuelle Datum im ersten Quartal liegt, wird das Vorjahr als aktuelles Jahr angenommen 
	 */
	private void setAktuellesJahr() 
	{
	LocalDate heute = LocalDate.now();
	aktuellesJahr = String.valueOf(heute.getYear());
	
	// Liegt das heutige Datum im ersten Quartal, wird das Vorjahr geladen
	Month aktuellerMonat = heute.getMonth();
		if (aktuellerMonat.getValue() < 4) 
		{
		aktuellesJahr = String.valueOf(heute.minusYears(1).getYear());	
		} 		
	}

	
	public String getConnectionInfo() {return connectionInfo;}

	public String getBerichtsZeitraum() {return berichtsZeitraum;}

	public String getAktuellesJahr() {return aktuellesJahr;}
	public EcholonStatistik getJahresstatistik(){return aktuelleStatistik;}

	public void setJahresstatistik(EcholonStatistik incoming) {this.aktuelleStatistik = incoming;}

	public boolean isConnected() {return connected;}

	public int getAnzahlDatenGesamt() {return anzahlDatenGesamt;}
}
