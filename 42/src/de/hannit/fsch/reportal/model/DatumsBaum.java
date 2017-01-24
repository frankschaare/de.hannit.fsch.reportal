package de.hannit.fsch.reportal.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import de.hannit.fsch.reportal.db.DataBaseThread;
import de.hannit.fsch.reportal.db.EcholonDBManager;
import de.hannit.fsch.reportal.model.echolon.EcholonNode;
import de.hannit.fsch.reportal.model.echolon.JahresStatistik;
import de.hannit.fsch.reportal.model.echolon.MonatsStatistik;
import de.hannit.fsch.reportal.model.echolon.QuartalsStatistik;
import de.hannit.fsch.reportal.model.echolon.Vorgang;

@ManagedBean(name = "baumModel")
@SessionScoped
public class DatumsBaum 
{
private final static Logger log = Logger.getLogger(EcholonDBManager.class.getSimpleName());		
private TreeNode root;
private HashMap<String, EcholonNode> jahresNodes = new HashMap<String, EcholonNode>();
private ExecutorService executor = Executors.newCachedThreadPool();
private DataBaseThread dbThread = null;
private Future<ArrayList<Vorgang>> result = null;
private ArrayList<Vorgang> vorgaenge = null;
private Vorgang max = null;
private Vorgang min = null;

	public DatumsBaum() 
	{
	dbThread =  new DataBaseThread();
	log.log(Level.INFO, "Lade Daten aus der Datenbank");	
	result = executor.submit(dbThread);	
		try 
		{
		vorgaenge = result.get();
		} 
		catch (InterruptedException e) 
		{
		e.printStackTrace();
		} 
		catch (ExecutionException e) 
		{
		e.printStackTrace();
		}
	root = new DefaultTreeNode("Root", null);
	setMinMaxNode();
	setJahresNodes();
	// setQuartalsNodes();
	}
	
	/*
	 * Ermittelt den jüngsten Vorgang und legt fest,
	 * welches der oberste Node im Baum sein wird
	 */
    private void setMinMaxNode() 
    {
	max = vorgaenge.stream().max(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
	min = vorgaenge.stream().min(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
	}

	@SuppressWarnings("unused")
	@PostConstruct
    public void init() 
    {
     
    
    }

	
	private void setJahresNodes() 
	{
	String berichtsJahr = null;
	int aktuellesBerichtsJahr = max.getBerichtsJahr();
	JahresStatistik js = null;
			
	EcholonNode aktuellerJahresknoten = null;
	EcholonNode aktuellerQuartalssknoten = null;
	EcholonNode aktuellerMonatssknoten = null;

		
		// Erstelle Jahresknoten bis zum ältesten Vorgang:
		while (aktuellesBerichtsJahr >= min.getBerichtsJahr()) 
		{
		berichtsJahr = String.valueOf(aktuellesBerichtsJahr);
		aktuellerJahresknoten = new EcholonNode(berichtsJahr, root);
		aktuellerJahresknoten.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_JAHR);
		aktuellerJahresknoten.setBerichtsJahr(berichtsJahr);
			
		js = new JahresStatistik(vorgaenge, berichtsJahr);
		aktuellerJahresknoten.setData(js);
			
			// Jahresknoten/Quartalsknoten
			for (QuartalsStatistik qs : js.getQuartalsStatistiken().values()) 
			{
				if (qs.getAnzahlVorgaengeBerichtszeitraum() > 0) 
				{
				qs.setStatistik();
				aktuellerQuartalssknoten = new EcholonNode(qs,aktuellerJahresknoten);
				aktuellerQuartalssknoten.setData(qs);
				
					// Jahresknoten/Quartalsknoten/Monatsknoten
					for (MonatsStatistik ms : qs.getMonatsStatistiken()) 
					{
						if (ms.getAnzahlVorgaengeGesamt() > 0) 
						{
						ms.setStatistik();	
						aktuellerMonatssknoten = new EcholonNode(ms, aktuellerQuartalssknoten);	
						aktuellerMonatssknoten.setData(ms);
						}
					}
				}
			}
			
		jahresNodes.put(berichtsJahr, aktuellerJahresknoten);
			
		aktuellesBerichtsJahr--;	
		}
	}    
    
    public TreeNode getRoot() 
    {
    return root;
    }

	@SuppressWarnings("unused")
	private void setManualNodes()
	{
	    TreeNode node2014 = new DefaultTreeNode("2014", root);
	    
	    TreeNode node2014Q1 = new DefaultTreeNode(DatumsConstants.QUARTAL1_LANG, node2014);
	    TreeNode node201401 = new DefaultTreeNode(DatumsConstants.JANUAR_LANG, node2014Q1);
	    TreeNode node201402 = new DefaultTreeNode(DatumsConstants.FEBRUAR_LANG, node2014Q1);
	    TreeNode node201403 = new DefaultTreeNode(DatumsConstants.MÄRZ_LANG, node2014Q1);
	    
	    TreeNode node2014Q2 = new DefaultTreeNode(DatumsConstants.QUARTAL2_LANG, node2014);
	    TreeNode node201404 = new DefaultTreeNode(DatumsConstants.APRIL_LANG, node2014Q2);
	    TreeNode node201405 = new DefaultTreeNode(DatumsConstants.MAI_LANG, node2014Q2);
	    TreeNode node201406 = new DefaultTreeNode(DatumsConstants.JUNI_LANG, node2014Q2);
	        
	    TreeNode node2014Q3 = new DefaultTreeNode(DatumsConstants.QUARTAL3_LANG, node2014);
	    TreeNode node201407 = new DefaultTreeNode(DatumsConstants.JULI_LANG, node2014Q3);
	    TreeNode node201408 = new DefaultTreeNode(DatumsConstants.AUGUST_LANG, node2014Q3);
	    TreeNode node201409 = new DefaultTreeNode(DatumsConstants.SEPTEMBER_LANG, node2014Q3);
	
	    TreeNode node2014Q4 = new DefaultTreeNode(DatumsConstants.QUARTAL4_LANG, node2014);
	    TreeNode node201410 = new DefaultTreeNode(DatumsConstants.OKTOBER_LANG, node2014Q4);
	    TreeNode node201411 = new DefaultTreeNode(DatumsConstants.NOVEMBER_LANG, node2014Q4);
	    TreeNode node201412 = new DefaultTreeNode(DatumsConstants.DEZEMBER_LANG, node2014Q4);
	    
	    TreeNode node2013 = new DefaultTreeNode("2013", root);
	    TreeNode node2013Q1 = new DefaultTreeNode(DatumsConstants.QUARTAL1_LANG, node2013);
	    TreeNode node201301 = new DefaultTreeNode(DatumsConstants.JANUAR_LANG, node2013Q1);
	    TreeNode node201302 = new DefaultTreeNode(DatumsConstants.FEBRUAR_LANG, node2013Q1);
	    TreeNode node201303 = new DefaultTreeNode(DatumsConstants.MÄRZ_LANG, node2013Q1);
	    
	    TreeNode node2013Q2 = new DefaultTreeNode(DatumsConstants.QUARTAL2_LANG, node2013);
	    TreeNode node201304 = new DefaultTreeNode(DatumsConstants.APRIL_LANG, node2013Q2);
	    TreeNode node201305 = new DefaultTreeNode(DatumsConstants.MAI_LANG, node2013Q2);
	    TreeNode node201306 = new DefaultTreeNode(DatumsConstants.JUNI_LANG, node2013Q2);
	        
	    TreeNode node2013Q3 = new DefaultTreeNode(DatumsConstants.QUARTAL3_LANG, node2013);
	    TreeNode node201307 = new DefaultTreeNode(DatumsConstants.JULI_LANG, node2013Q3);
	    TreeNode node201308 = new DefaultTreeNode(DatumsConstants.AUGUST_LANG, node2013Q3);
	    TreeNode node201309 = new DefaultTreeNode(DatumsConstants.SEPTEMBER_LANG, node2013Q3);
	
	    TreeNode node2013Q4 = new DefaultTreeNode(DatumsConstants.QUARTAL4_LANG, node2013);
	    TreeNode node201310 = new DefaultTreeNode(DatumsConstants.OKTOBER_LANG, node2013Q4);
	    TreeNode node201311 = new DefaultTreeNode(DatumsConstants.NOVEMBER_LANG, node2013Q4);
	    TreeNode node201312 = new DefaultTreeNode(DatumsConstants.DEZEMBER_LANG, node2013Q4);    
	    
	    TreeNode node2012 = new DefaultTreeNode("2012", root);
	    TreeNode node2012Q1 = new DefaultTreeNode(DatumsConstants.QUARTAL1_LANG, node2012);
	    TreeNode node201201 = new DefaultTreeNode(DatumsConstants.JANUAR_LANG, node2012Q1);
	    TreeNode node201202 = new DefaultTreeNode(DatumsConstants.FEBRUAR_LANG, node2012Q1);
	    TreeNode node201203 = new DefaultTreeNode(DatumsConstants.MÄRZ_LANG, node2012Q1);
	    
	    TreeNode node2012Q2 = new DefaultTreeNode(DatumsConstants.QUARTAL2_LANG, node2012);
	    TreeNode node201204 = new DefaultTreeNode(DatumsConstants.APRIL_LANG, node2012Q2);
	    TreeNode node201205 = new DefaultTreeNode(DatumsConstants.MAI_LANG, node2012Q2);
	    TreeNode node201206 = new DefaultTreeNode(DatumsConstants.JUNI_LANG, node2012Q2);
	        
	    TreeNode node2012Q3 = new DefaultTreeNode(DatumsConstants.QUARTAL3_LANG, node2012);
	    TreeNode node201207 = new DefaultTreeNode(DatumsConstants.JULI_LANG, node2012Q3);
	    TreeNode node201208 = new DefaultTreeNode(DatumsConstants.AUGUST_LANG, node2012Q3);
	    TreeNode node201209 = new DefaultTreeNode(DatumsConstants.SEPTEMBER_LANG, node2012Q3);
	
	    TreeNode node2012Q4 = new DefaultTreeNode(DatumsConstants.QUARTAL4_LANG, node2012);
	    TreeNode node201210 = new DefaultTreeNode(DatumsConstants.OKTOBER_LANG, node2012Q4);
	    TreeNode node201211 = new DefaultTreeNode(DatumsConstants.NOVEMBER_LANG, node2012Q4);
	    TreeNode node201212 = new DefaultTreeNode(DatumsConstants.DEZEMBER_LANG, node2012Q4);     
	    
	    TreeNode node2011 = new DefaultTreeNode("2011", root);
	    TreeNode node2011Q1 = new DefaultTreeNode(DatumsConstants.QUARTAL1_LANG, node2011);
	    TreeNode node201101 = new DefaultTreeNode(DatumsConstants.JANUAR_LANG, node2011Q1);
	    TreeNode node201102 = new DefaultTreeNode(DatumsConstants.FEBRUAR_LANG, node2011Q1);
	    TreeNode node201103 = new DefaultTreeNode(DatumsConstants.MÄRZ_LANG, node2011Q1);
	    
	    TreeNode node2011Q2 = new DefaultTreeNode(DatumsConstants.QUARTAL2_LANG, node2011);
	    TreeNode node201104 = new DefaultTreeNode(DatumsConstants.APRIL_LANG, node2011Q2);
	    TreeNode node201105 = new DefaultTreeNode(DatumsConstants.MAI_LANG, node2011Q2);
	    TreeNode node201106 = new DefaultTreeNode(DatumsConstants.JUNI_LANG, node2011Q2);
	        
	    TreeNode node2011Q3 = new DefaultTreeNode(DatumsConstants.QUARTAL3_LANG, node2011);
	    TreeNode node201107 = new DefaultTreeNode(DatumsConstants.JULI_LANG, node2011Q3);
	    TreeNode node201108 = new DefaultTreeNode(DatumsConstants.AUGUST_LANG, node2011Q3);
	    TreeNode node201109 = new DefaultTreeNode(DatumsConstants.SEPTEMBER_LANG, node2011Q3);
	
	    TreeNode node2011Q4 = new DefaultTreeNode(DatumsConstants.QUARTAL4_LANG, node2011);
	    TreeNode node201110 = new DefaultTreeNode(DatumsConstants.OKTOBER_LANG, node2011Q4);
	    TreeNode node201111 = new DefaultTreeNode(DatumsConstants.NOVEMBER_LANG, node2011Q4);
	    TreeNode node201112 = new DefaultTreeNode(DatumsConstants.DEZEMBER_LANG, node2011Q4);		
	}  
	
	private void setQuartalsNodes()
	{
	EcholonNode tempQuartal = null;	
	EcholonNode tempMonat = null;
	
		// Für alle vorhandenen Jahresknoten werden Quartalsknoten eingehängt
		for (EcholonNode jahresNode : jahresNodes.values()) 
		{

			for (int i = 1; i <= 4; i++) 
			{
				
				switch (i) 
				{
				// Erstes Quartal
				case 1:
				tempQuartal = new EcholonNode(DatumsConstants.QUARTAL1_LANG, jahresNode);
				tempQuartal.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_Quartal);
				tempQuartal.setBerichtsQuartal(DatumsConstants.QUARTAL1);
				
				tempMonat = new EcholonNode(DatumsConstants.JANUAR_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.JANUAR_KURZ);
				
				tempMonat = new EcholonNode(DatumsConstants.FEBRUAR_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.FEBRUAR_KURZ);
				
				tempMonat = new EcholonNode(DatumsConstants.MÄRZ_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.MÄRZ_KURZ);
				break;
				
				// Zweites Quartal
				case 2:
				tempQuartal = new EcholonNode(DatumsConstants.QUARTAL2_LANG, jahresNode);
				tempQuartal.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_Quartal);
				tempQuartal.setBerichtsQuartal(DatumsConstants.QUARTAL2);
				
				tempMonat = new EcholonNode(DatumsConstants.APRIL_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.APRIL_KURZ);
				
				tempMonat = new EcholonNode(DatumsConstants.MAI_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.MAI_KURZ);
				
				tempMonat = new EcholonNode(DatumsConstants.JUNI_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.JUNI_KURZ);
				break;

				// Drittes Quartal
				case 3:
				tempQuartal = new EcholonNode(DatumsConstants.QUARTAL3_LANG, jahresNode);
				tempQuartal.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_Quartal);
				tempQuartal.setBerichtsQuartal(DatumsConstants.QUARTAL3);
				
				tempMonat = new EcholonNode(DatumsConstants.JULI_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.JULI_KURZ);
				
				tempMonat = new EcholonNode(DatumsConstants.AUGUST_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.AUGUST_KURZ);
				
				tempMonat = new EcholonNode(DatumsConstants.SEPTEMBER_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.SEPTEMBER_KURZ);
				break;				
				
				// Viertes Quartal
				case 4:
				tempQuartal = new EcholonNode(DatumsConstants.QUARTAL4_LANG, jahresNode);
				tempQuartal.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_Quartal);
				tempQuartal.setBerichtsQuartal(DatumsConstants.QUARTAL4);
				
				tempMonat = new EcholonNode(DatumsConstants.OKTOBER_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.OKTOBER_KURZ);
				
				tempMonat = new EcholonNode(DatumsConstants.NOVEMBER_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.NOVEMBER_KURZ);
				
				tempMonat = new EcholonNode(DatumsConstants.DEZEMBER_LANG, tempQuartal);
				tempMonat.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_MONAT);
				tempMonat.setBerichtsMonat(DatumsConstants.DEZEMBER_KURZ);
				break;				
				default:
					break;
				}
				
			}
		}
	}
	
}
