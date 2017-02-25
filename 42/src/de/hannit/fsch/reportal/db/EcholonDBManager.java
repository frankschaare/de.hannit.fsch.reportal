package de.hannit.fsch.reportal.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import de.hannit.fsch.reportal.model.Mandant;
import de.hannit.fsch.reportal.model.echolon.EcholonNode;
import de.hannit.fsch.reportal.model.echolon.EcholonStatistik;
import de.hannit.fsch.reportal.model.echolon.Vorgang;

@ManagedBean(name = "edb")
@ApplicationScoped
public class EcholonDBManager 
{
private final static Logger log = Logger.getLogger(EcholonDBManager.class.getSimpleName());
private String logPrefix = null;
private FacesContext fc = null;
private InitialContext ic;
private DataSource ds = null;
private Connection con = null;	
private PreparedStatement ps = null;
private ResultSet rs = null;
private String mandantenInfo = "";
private boolean connected = false;
private	Properties propsMandanten = null;;	

private String aktuellesJahr = null;
private String berichtsZeitraum = null;
private ArrayList<Vorgang> vorgaenge = new ArrayList<Vorgang>();
private EcholonStatistik aktuelleStatistik = null;
private int anzahlDatenGesamt = 0;
private TreeMap<String, Mandant> mandanten = null;
private boolean loadFinished = false;
private EcholonNode selectedNode = null;


	public EcholonDBManager() 
	{
	// Zuerst wird das aktuelle Jahr ermittelt:
	setAktuellesJahr();	
	setConnection();
	}
	
	/*
	 * Initialisiert Verbindung zur Datenbank und lädt bei der Gelegenheit gleich die Mandanten
	 */
	private void setConnection() 
	{
	logPrefix = this.getClass().getCanonicalName() + "setConnection(): ";
	fc = FacesContext.getCurrentInstance();
	propsMandanten = new Properties();
	Mandant mandant = null;
	mandanten = new TreeMap<>();
	
		try 
		{
		InputStream in = fc.getExternalContext().getResourceAsStream("META-INF/Mandanten.xml");			
		propsMandanten.loadFromXML(in);
			
		ic = new InitialContext();
		ds = (DataSource) ic.lookup("java:comp/env/jdbc/echolonDB");
		con = (con != null ) ? con : ds.getConnection();
			
			if (con != null) 
			{
			if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": Verbindung zur Echolon-Datenbank hergestellt.");}	
			}
			else
			{
			log.log(Level.WARNING, "Keine Verbindung zur Echolon-Datenbank !");	
			}
		ps = con.prepareStatement(PreparedStatements.SELECT_MANDANTEN);
		rs = ps.executeQuery();
			while (rs.next()) 
			{
			mandant = new Mandant();	
			mandant.setId(rs.getString("ID"));
			mandant.setBezeichnung(rs.getString("Name"));
			setMandant(mandant);
			mandanten.put(mandant.getBezeichnung(), mandant);
			}
		if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": Mandantgenliste enthält " + mandanten.size() + " Mandanten");}			
			
		} 
		catch (NamingException | SQLException | IOException e) 
		{
		e.printStackTrace();
		}		
	}
	
	public String getMandantenInfo() 
	{
	mandantenInfo = "Die Mandanten ";	
		for (Mandant m : mandanten.values()) 
		{
		mandantenInfo += "[" + m.getBezeichnung() + "] "; 	
		}
	mandantenInfo += "sind verfügbar.";
	
	return mandantenInfo;
	}
	
	public TreeMap<String, Mandant> getMandanten() {return mandanten;}

	private void setMandant(Mandant mandant) 
	{
	String bezeichnung = mandant.getBezeichnung();	
	if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": Konfiguriere Mandant: " + bezeichnung);}
	
	int startServiceZeit = Integer.parseInt(propsMandanten.getProperty(bezeichnung + "StartServicezeit"));
	int endeServiceZeit = Integer.parseInt(propsMandanten.getProperty(bezeichnung + "EndeServicezeit"));
	mandant.setServicezeitStart(LocalTime.of(startServiceZeit, 0));
	
		switch (endeServiceZeit) 
		{
		case 23:
		mandant.setServicezeitEnde(LocalTime.of(endeServiceZeit, 59, 59));
		break;

		default:
		mandant.setServicezeitEnde(LocalTime.of(endeServiceZeit, 0));			
		break;
		}
	
	int anzahlServiceKategorien = Integer.parseInt(propsMandanten.getProperty(bezeichnung + "AnzahlServiceKategorien"));
		switch (anzahlServiceKategorien) 
		{
		case 0:
		mandant.setServiceKategorienFilter(false);	
		break;

		default:
		mandant.setServiceKategorienFilter(true);
		
		TreeMap<Integer, String> serviceKategorien = new TreeMap<>();
			for (int i = 1; i <= anzahlServiceKategorien; i++)
			{
			serviceKategorien.put(i, propsMandanten.getProperty(bezeichnung + "ServiceKategorie" + i));	
			}
			if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": Für Mandant: " + bezeichnung + " wurde ein ServicekategorieFilter mit " + serviceKategorien.size() + " Einträgen erstellt.");}
			mandant.setServiceKategorien(serviceKategorien);
		break;
		}
		
	int anzahlOrganisationen = Integer.parseInt(propsMandanten.getProperty(bezeichnung + "AnzahlOrganisationen"));
		switch (anzahlOrganisationen) 
		{
		case 0:
		mandant.setOrganisationenFilter(false);	
		break;

		default:
		mandant.setOrganisationenFilter(true);
		
		TreeMap<Integer, String> organisationen = new TreeMap<>();
			for (int i = 1; i <= anzahlOrganisationen; i++)
			{
			organisationen.put(i, propsMandanten.getProperty(bezeichnung + "Organisation" + i));	
			}
			if (fc.isProjectStage(ProjectStage.Development)){log.log(Level.INFO, logPrefix + ": Für Mandant: " + bezeichnung + " wurde ein OrganisationsFilter mit " + organisationen.size() + " Einträgen erstellt.");}
			mandant.setOrganisationen(organisationen);
		break;
		}		
	
	}

	public EcholonDBManager getEcholonDBManager()
	{
	return this;	
	}
	
    public void onTabChange(TabChangeEvent event) 
    {
    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "BITTE BEACHTEN !", "Aktiver Mandant ist jetzt: " + event.getTab().getTitle() + ". Der Wechsel des Mandanten verändert aber nicht die Auswahl im Hauptbildschirm. Dazu müssen Sie zuerst eine neue Auswahl im Navigationsbaum treffen.");
    FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	

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
	@SuppressWarnings("unused")
	public void onNodeExpand(NodeExpandEvent event) 
	{
	TreeNode expandedNode = event.getTreeNode();
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

	
	public String getBerichtsZeitraum() {return berichtsZeitraum;}

	public String getAktuellesJahr() {return aktuellesJahr;}
	public EcholonStatistik getJahresstatistik(){return aktuelleStatistik;}

	public void setJahresstatistik(EcholonStatistik incoming) {this.aktuelleStatistik = incoming;}

	public boolean isConnected() {return connected;}

	public int getAnzahlDatenGesamt() {return anzahlDatenGesamt;}
}
