package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
private ExecutorService executor = Executors.newCachedThreadPool();
private Zeitraum standardZeitraum = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_GESAMT);
private TreeMap<LocalDateTime, CallcenterStatistik> statisikenGesamt;
private CallcenterAuswertung auswertung = null;
private DefaultTreeNode selectedNode = null;

	public DatumsBaum() 
	{
	root = new DefaultTreeNode("Root", null);
	
	callcenterAbfrage = new CallcenterDBThread();
	setSelectedZeitraum(Zeitraum.BERICHTSZEITRAUM_GESAMT);
	auswertung = new CallcenterAuswertung(statisikenGesamt);
	init();
	}
	
	public void setSelectedZeitraum(int selectedZeitraum) 
	{
	Zeitraum selected = null;
		switch (selectedZeitraum) 
		{
		case Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE:
		selected = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE);
		break;

		case Zeitraum.BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE:
		selected = new Zeitraum(Zeitraum.BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE);
		break;

		default:
		selected = standardZeitraum;			
		break;
		}

	callcenterAbfrage.setAbfrageZeitraum(selected);
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
		
			for (CallcenterQuartalsStatistik cq : cj.getQuartalsStatistiken().values()) 
			{
			TreeNode quartal = new DefaultTreeNode(cq, jahr);
				for (CallcenterMonatsStatistik cm : cq.getMonatsStatistiken().values()) 
				{
				TreeNode monat = new DefaultTreeNode(cm, quartal);
					for (CallcenterKWStatistik ckw : cm.getStatistikenKW().values()) 
					{
					TreeNode kw = new DefaultTreeNode(ckw, monat);
						for (CallcenterTagesStatistik ct : ckw.getStatistikenTag().values()) 
						{
						TreeNode tag = new DefaultTreeNode(ct, kw);
							for (CallcenterStundenStatistik ch : ct.getStundenStatistiken().values()) 
							{
							TreeNode stunde = new DefaultTreeNode(ch, tag);	
							}
						}
					}
				}
			}
		}    
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
}
