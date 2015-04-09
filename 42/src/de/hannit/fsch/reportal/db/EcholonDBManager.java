package de.hannit.fsch.reportal.db;

import java.time.LocalDate;
import java.time.Month;
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
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import de.hannit.fsch.reportal.model.DatumsConstants;
import de.hannit.fsch.reportal.model.echolon.JahresStatistik;

@ManagedBean(name = "edb", eager=true)
@ApplicationScoped
public class EcholonDBManager 
{
private final static Logger log = Logger.getLogger(EcholonDBManager.class.getSimpleName());

private ExecutorService executor = Executors.newCachedThreadPool();
private DataBaseThread dbThread = null;
private Future<JahresStatistik> result = null;
private String connectionInfo = "Keine Verbindung zur Echolon Datenbank !";
private boolean connected = false;

private String aktuellesJahr = null;
private String selectedYear = null;
private String berichtsZeitraum = null;
private JahresStatistik aktuelleJahresstatistik = null;
private HashMap<String, JahresStatistik> dbCache = new HashMap<String, JahresStatistik>();
private int anzahlDatenGesamt = 0;
private DefaultTreeNode selectedNode = null;

private boolean loadFinished = false;

	public EcholonDBManager() 
	{
	// Zuerst wird das aktuelle Jahr ermittelt:
	setAktuellesJahr();	

	dbThread =  new DataBaseThread();
	dbThread.setSelection(aktuellesJahr);
	
	connectionInfo = "Verbunden mit Echolon Datenbank an VM-SQLEcholon.hannit.regionhannover.de\\MSSQL";
	}
	
	public boolean isLoadFinished() {return loadFinished;}

	public DefaultTreeNode getSelectedNode() {return selectedNode;}

	public void setSelectedNode(DefaultTreeNode incoming) 
	{
		if (selectedNode != null) 
		{
		this.selectedNode = incoming;
		String test = (String) selectedNode.getData();
		System.out.println(test);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) 
	{
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
	

		if (expandedNode.getParent().getRowKey().equalsIgnoreCase("root")) 
		{
		selectedYear = expandedNode.getData().toString();	
			// Sind die Daten bereits im Cache ?
			if (dbCache.containsKey(selectedYear)) 
			{
			aktuelleJahresstatistik = dbCache.get(selectedYear);
			log.log(Level.INFO, "Lade Daten für das Jahr " + aktuelleJahresstatistik.getBerichtsJahr() + " aus dem Datenbank-Cache...");	
	        FacesMessage message = new FacesMessage("Lade Daten für das Jahr " + selectedYear + " aus dem Datenbank-Cache...");
	        FacesContext.getCurrentInstance().addMessage(null, message);
			
			loadFinished = true;
			} 
			// Nein: starte neuen Thread
			else 
			{
		    FacesMessage message = new FacesMessage("Starte neuen Datenbankthread zum Laden der Daten für das Jahr " + selectedYear );
		    FacesContext.getCurrentInstance().addMessage(null, message);				
			dbThread.setSelection(selectedYear);	
			result = executor.submit(dbThread);
			loadFinished = false;
			}
		}
		else
		{
		String nodeString = expandedNode.getData().toString();
			try 
			{
			aktuelleJahresstatistik = result.get();
				if (!dbCache.containsKey(aktuelleJahresstatistik.getBerichtsJahr())) 
				{
				dbCache.put(aktuelleJahresstatistik.getBerichtsJahr(), aktuelleJahresstatistik);
				}
			filter(nodeString);
			loadFinished = true;
			} 
			catch (InterruptedException | ExecutionException e) 
			{
			e.printStackTrace();
			}
		}
	}	
	
	private void filter(String nodeString) 
	{
	String info = "// Echolon Daten";
	FacesMessage message = null;
		switch (nodeString) 
		{
		case DatumsConstants.QUARTAL1_LANG:
		    message = new FacesMessage("Filtere geladene Daten für das 1. Quartal " + aktuelleJahresstatistik.getBerichtsJahr());
		    FacesContext.getCurrentInstance().addMessage(null, message);					
			aktuelleJahresstatistik.setQuartal(nodeString);
			berichtsZeitraum = "Erstes Quartal " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + DatumsConstants.QUARTAL1_LANG;
			break;
		case DatumsConstants.QUARTAL2_LANG:
		    message = new FacesMessage("Filtere geladene Daten für das 2. Quartal " + aktuelleJahresstatistik.getBerichtsJahr());
		    FacesContext.getCurrentInstance().addMessage(null, message);								
			aktuelleJahresstatistik.setQuartal(nodeString);
			berichtsZeitraum = "Zweites Quartal " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + DatumsConstants.QUARTAL2_LANG;
			break;		
		case DatumsConstants.QUARTAL3_LANG:
		    message = new FacesMessage("Filtere geladene Daten für das 3. Quartal " + aktuelleJahresstatistik.getBerichtsJahr());
		    FacesContext.getCurrentInstance().addMessage(null, message);								
			aktuelleJahresstatistik.setQuartal(nodeString);	
			berichtsZeitraum = "Drittes Quartal " + aktuelleJahresstatistik.getBerichtsJahr();		
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + DatumsConstants.QUARTAL3_LANG;
			break;		
		case DatumsConstants.QUARTAL4_LANG:
		    message = new FacesMessage("Filtere geladene Daten für das 4. Quartal " + aktuelleJahresstatistik.getBerichtsJahr());
		    FacesContext.getCurrentInstance().addMessage(null, message);								
			aktuelleJahresstatistik.setQuartal(nodeString);	
			berichtsZeitraum = "Viertes Quartal " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + DatumsConstants.QUARTAL4_LANG;
			break;
		case DatumsConstants.JANUAR_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(1);
			berichtsZeitraum = DatumsConstants.JANUAR_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL1 + " // " + DatumsConstants.JANUAR_LANG;
			break;
		case DatumsConstants.FEBRUAR_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(2);
			berichtsZeitraum = DatumsConstants.FEBRUAR_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL1 + " // " + DatumsConstants.FEBRUAR_LANG;
			break;		
		case DatumsConstants.MÄRZ_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(3);
			berichtsZeitraum = DatumsConstants.MÄRZ_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL1 + " // " + DatumsConstants.MÄRZ_LANG;
			break;		
		case DatumsConstants.APRIL_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(4);
			berichtsZeitraum = DatumsConstants.APRIL_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL2 + " // " + DatumsConstants.APRIL_LANG;
			break;		
		case DatumsConstants.MAI_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(5);
			berichtsZeitraum = DatumsConstants.MAI_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL2 + " // " + DatumsConstants.MAI_LANG;
			break;		
		case DatumsConstants.JUNI_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(6);
			berichtsZeitraum = DatumsConstants.JUNI_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL2 + " // " + DatumsConstants.JUNI_LANG;
			break;		
		case DatumsConstants.JULI_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(7);
			berichtsZeitraum = DatumsConstants.JULI_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL3 + " // " + DatumsConstants.JULI_LANG;
			break;		
		case DatumsConstants.AUGUST_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(8);
			berichtsZeitraum = DatumsConstants.AUGUST_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL3 + " // " + DatumsConstants.AUGUST_LANG;		    
			break;		
		case DatumsConstants.SEPTEMBER_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(9);
			berichtsZeitraum = DatumsConstants.SEPTEMBER_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL3 + " // " + DatumsConstants.SEPTEMBER_LANG;
			break;		
		case DatumsConstants.OKTOBER_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(10);
			berichtsZeitraum = DatumsConstants.OKTOBER_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL4 + " // " + DatumsConstants.OKTOBER_LANG;
			break;		
		case DatumsConstants.NOVEMBER_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(11);
			berichtsZeitraum = DatumsConstants.NOVEMBER_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL4 + " // " + DatumsConstants.NOVEMBER_LANG;
			break;		
		case DatumsConstants.DEZEMBER_LANG:
			aktuelleJahresstatistik.setBerichtsMonat(12);
			berichtsZeitraum = DatumsConstants.DEZEMBER_LANG + " " + aktuelleJahresstatistik.getBerichtsJahr();
			info = info + " // " + aktuelleJahresstatistik.getBerichtsJahr() + " // " + "Quartal " + DatumsConstants.QUARTAL4 + " // " + DatumsConstants.DEZEMBER_LANG;
			break;	
		
		// Sind weder Quartals- noch Monatsdaten gefragt, wird angenommen, dass das Gesamtjahr abgerufen wird.	
		default:
			// Sind die Jahresdaten bereits im Cache ?
			if (dbCache.containsKey(nodeString)) 
			{
			message = new FacesMessage("Filtere geladene Daten für das Gesamtjahr " + nodeString);
			FacesContext.getCurrentInstance().addMessage(null, message);				
			aktuelleJahresstatistik = dbCache.get(nodeString);
			berichtsZeitraum = "Gesamtjahr " +  aktuelleJahresstatistik.getBerichtsJahr();
			aktuelleJahresstatistik.resetFilter();
			}
			else
			{
			message = new FacesMessage("Lade Daten für das Gesamtjahr " + nodeString);
			FacesContext.getCurrentInstance().addMessage(null, message);				
			dbThread =  new DataBaseThread();
			dbThread.setSelection(nodeString);
			result = executor.submit(dbThread);
				try 
				{
				aktuelleJahresstatistik = result.get();
				berichtsZeitraum = "Gesamtjahr " +  aktuelleJahresstatistik.getBerichtsJahr();
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
		
		break;
		}
	connectionInfo = info;		
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
	public JahresStatistik getJahresstatistik(){return aktuelleJahresstatistik;}

	public void setJahresstatistik(JahresStatistik incoming) {this.aktuelleJahresstatistik = incoming;}

	public boolean isConnected() {return connected;}

	public int getAnzahlDatenGesamt() {return anzahlDatenGesamt;}
}
