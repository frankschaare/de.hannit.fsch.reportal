package de.hannit.fsch.reportal.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import de.hannit.fsch.reportal.db.Cache;
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
@ManagedProperty (value = "#{cache}")
private Cache cache;

private final static Logger log = Logger.getLogger(EcholonDBManager.class.getSimpleName());		
private TreeNode root;
private HashMap<String, EcholonNode> jahresNodes = new HashMap<String, EcholonNode>();
private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();
private ArrayList<Vorgang> vorgaenge = null;
private Vorgang max = null;
private Vorgang min = null;

	public DatumsBaum() 
	{
	// setQuartalsNodes();
	}

	@PostConstruct
	public void init() 
	{
		try 
		{
		distinctCases = cache.getDistinctCases();
		} 
		catch (NullPointerException e) 
		{
		FacesContext fc = FacesContext.getCurrentInstance();
		cache = fc.getApplication().evaluateExpressionGet(fc, "#{cache}", Cache.class);
		distinctCases = cache.getDistinctCases();
		}
	
	root = new DefaultTreeNode("Root", null);
	setMinMaxNode();
	setJahresNodes();
	}
	
	/*
	 * Ermittelt den j�ngsten Vorgang und legt fest,
	 * welches der oberste Node im Baum sein wird
	 */
    private void setMinMaxNode() 
    {
	max = distinctCases.values().stream().max(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
	min = distinctCases.values().stream().min(Comparator.comparing(Vorgang::getErstellDatumZeit)).get();
	}

	public Cache getCache() 
	{
	return cache;
	}

	public void setCache(Cache cache) 
	{
	this.cache = cache;
	}

	private void setJahresNodes() 
	{
	String berichtsJahr = null;
	int aktuellesBerichtsJahr = max.getBerichtsJahr();
	JahresStatistik js = null;
			
	EcholonNode aktuellerJahresknoten = null;
	EcholonNode aktuellerQuartalssknoten = null;
	EcholonNode aktuellerMonatssknoten = null;

		
		// Erstelle Jahresknoten bis zum �ltesten Vorgang:
		while (aktuellesBerichtsJahr >= min.getBerichtsJahr()) 
		{
		berichtsJahr = String.valueOf(aktuellesBerichtsJahr);
		aktuellerJahresknoten = new EcholonNode(berichtsJahr, root);
		aktuellerJahresknoten.setBerichtszeitraum(Berichtszeitraum.BERICHTSZEITRAUM_JAHR);
		aktuellerJahresknoten.setBerichtsJahr(berichtsJahr);
		
			vorgaenge = new ArrayList<>();
			for (Vorgang vorgang : distinctCases.values()) 
			{
			vorgaenge.add(vorgang);	
			}
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
	    TreeNode node201403 = new DefaultTreeNode(DatumsConstants.M�RZ_LANG, node2014Q1);
	    
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
	    TreeNode node201303 = new DefaultTreeNode(DatumsConstants.M�RZ_LANG, node2013Q1);
	    
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
	    TreeNode node201203 = new DefaultTreeNode(DatumsConstants.M�RZ_LANG, node2012Q1);
	    
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
	    TreeNode node201103 = new DefaultTreeNode(DatumsConstants.M�RZ_LANG, node2011Q1);
	    
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
}
