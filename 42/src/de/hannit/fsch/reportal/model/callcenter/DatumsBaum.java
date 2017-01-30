package de.hannit.fsch.reportal.model.callcenter;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import de.hannit.fsch.reportal.db.CallcenterDBThread;
import de.hannit.fsch.reportal.model.Zeitraum;

@ManagedBean(name = "ccBaum")
@SessionScoped
public class DatumsBaum 
{
private TreeNode root;
private CallcenterDBThread callcenterAbfrage = null;
private Future<TreeMap<LocalDateTime, CallcenterStatistik>> result = null;
private Future<Boolean> dbCacheGeschrieben = null;
private ExecutorService executor = Executors.newCachedThreadPool();
private Zeitraum standardZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_GESAMT, null);
private TreeMap<LocalDateTime, CallcenterStatistik> statisikenGesamt;
private CallcenterAuswertung auswertung = null;
private DefaultTreeNode selectedNode = null;
private CallcenterStatistik selected = null;
private int nodeCount = 0;
private String berichtsZeitraum = null;

	public DatumsBaum() 
	{
	root = new DefaultTreeNode("Root", null);
	
	callcenterAbfrage = new CallcenterDBThread();
	dbAbfrage();
	auswertung = new CallcenterAuswertung(statisikenGesamt);
	init();
	setDBCache();
	}
	
	// Nachdem die Datenbank abgefragt wurde, wird eine XML-Datei erzeugt, die den Inhalt der Abfrage cached:	
	private void setDBCache() 
	{
	ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	Path testPath = FileSystems.getDefault().getPath(servletContext.getRealPath("/downloads"),  "CallcenterStatistikenGesamt.xml");
		
	Stream<CallcenterStatistik> csStream = statisikenGesamt.values().stream();	
	CallcenterStatistiken xml = new CallcenterStatistiken();
	xml.setAbsolutePath(testPath);
	xml.setStatistiken(csStream.collect(Collectors.toCollection(ArrayList::new)));
	dbCacheGeschrieben = executor.submit(xml);	
	}

	public void dbAbfrage() 
	{
	callcenterAbfrage.setAbfrageZeitraum(standardZeitraum);
	result = executor.submit(callcenterAbfrage);
		try 
		{
		statisikenGesamt = result.get();
		} 
		catch (InterruptedException | ExecutionException e) 
		{
		e.printStackTrace();
		}		
	}

	@SuppressWarnings("unused")
    public void init() 
    {
	
	
       	for (CallcenterJahresStatistik cj : auswertung.getStatistikenJaehrlich().values()) 
    	{
		TreeNode jahr = new DefaultTreeNode(cj, root);
		nodeCount++;
			for (CallcenterQuartalsStatistik cq : cj.getQuartalsStatistiken().values()) 
			{
			TreeNode quartal = new DefaultTreeNode(cq, jahr);
			nodeCount++;
				for (CallcenterMonatsStatistik cm : cq.getMonatsStatistiken().values()) 
				{
				TreeNode monat = new DefaultTreeNode(cm, quartal);
				nodeCount++;
					for (CallcenterKWStatistik ckw : cm.getStatistikenKW().values()) 
					{
					TreeNode kw = new DefaultTreeNode(ckw, monat);
					nodeCount++;
						for (CallcenterTagesStatistik ct : ckw.getStatistikenTag().values()) 
						{
						TreeNode tag = new DefaultTreeNode(ct, kw);
						nodeCount++;
							for (CallcenterStundenStatistik ch : ct.getStundenStatistiken().values()) 
							{
							TreeNode stunde = new DefaultTreeNode(ch, tag);
							nodeCount++;
							}
						}
					}
				}
			}
		}    
    }

	public int getNodeCount() {
		return nodeCount;
	}
	
	public void onNodeSelect(NodeSelectEvent event) 
	{
	selected = (CallcenterStatistik) event.getTreeNode().getData();
	}	

	public CallcenterStatistik getSelected() {
		return selected;
	}

	public DefaultTreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(DefaultTreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public TreeNode getRoot() 
    {
    return root;
    }    
	
	public int getAnzahlDaten()
	{
	return selected != null ? selected.getDaten().size() : 0;
	}

	/*
	 * Leider bin ich hier auf einen Bug im DateTimeFormatter gestossen.
	 * Das Enddatum wird daher etwas umständlich formatiert:
	 */
	public String getBerichtsZeitraum() 
	{
		if (selected != null) 
		{
		berichtsZeitraum = selected.getAuswertungsZeitraum().getBerichtszeitraum();
		} 
		else 
		{
		berichtsZeitraum = "Bitte im Navigationsbaum einen Berichtszeitraum auswählen. Ihnen stehen " + nodeCount + " Statistiken zur Verfügung.";	
		}
	return berichtsZeitraum;
	}
}
