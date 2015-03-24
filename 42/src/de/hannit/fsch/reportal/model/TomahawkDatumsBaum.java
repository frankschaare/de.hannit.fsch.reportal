package de.hannit.fsch.reportal.model;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

@ManagedBean(name = "tomahawkBaumModel")
@SessionScoped
public class TomahawkDatumsBaum 
{
private String selectedNode = null;	
private ArrayList<String> jahre = null;
private ArrayList<String> quartale = null;
private ArrayList<String> monate = null;	

	public TomahawkDatumsBaum() 
	{
	createModel();
	}

	private void createModel() 
	{
	jahre = new ArrayList<String>();
	jahre.add("2014");
	jahre.add("2013");
	jahre.add("2012");
	jahre.add("2011");
	
	quartale = new ArrayList<String>();
	quartale.add(DatumsConstants.QUARTAL1);
	quartale.add(DatumsConstants.QUARTAL2);
	quartale.add(DatumsConstants.QUARTAL3);
	quartale.add(DatumsConstants.QUARTAL4);
	
	monate = new ArrayList<String>();
	monate.add("Januar");
	monate.add("Februar");
	monate.add("März");
	monate.add("April");
	monate.add("Mai");
	monate.add("Juni");
	monate.add("Juli");
	monate.add("August");
	monate.add("September");
	monate.add("Oktober");
	monate.add("November");
	monate.add("Dezember");
	}

	public ArrayList<String> getJahre() {
		return jahre;
	}

	public ArrayList<String> getQuartale() {
		return quartale;
	}

	public ArrayList<String> getMonate() {
		return monate;
	}
	
    @SuppressWarnings("unchecked")
	public TreeNode getBaumStruktur()
    {
    TreeNode treeData = new TreeNodeBase("jahrNode", "Inbox", false);
        
        for (String jahr : getJahre()) 
        {
       	TreeNodeBase jahrNode = new TreeNodeBase("jahrNode", jahr, jahr, false);
       		for (String quartal : getQuartale()) 
       		{
       		TreeNodeBase quartalNode = new TreeNodeBase("quartalsNode", "Quartal " + quartal, jahr + "//" + quartal, false);
       		
       			switch (quartal) 
       			{
				case DatumsConstants.QUARTAL1:
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.JANUAR_LANG, jahr + "//" + DatumsConstants.JANUAR_KURZ, false));	
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.FEBRUAR_LANG, jahr + "//" + DatumsConstants.FEBRUAR_KURZ, false));
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.MÄRZ_LANG, jahr + "//" + DatumsConstants.MÄRZ_KURZ, false));
				break;

				case DatumsConstants.QUARTAL2:
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.APRIL_LANG, jahr + "//" + DatumsConstants.APRIL_KURZ, false));	
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.MAI_LANG, jahr + "//" + DatumsConstants.MAI_KURZ, false));
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.JUNI_LANG, jahr + "//" + DatumsConstants.JUNI_KURZ, false));
				break;
				
				case DatumsConstants.QUARTAL3:
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.JULI_LANG, jahr + "//" + DatumsConstants.JULI_KURZ, false));	
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.AUGUST_LANG, jahr + "//" + DatumsConstants.AUGUST_KURZ, false));
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.SEPTEMBER_LANG, jahr + "//" + DatumsConstants.SEPTEMBER_KURZ, false));
				break;
			
				default:
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.OKTOBER_LANG, jahr + "//" + DatumsConstants.OKTOBER_KURZ, false));	
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.NOVEMBER_LANG, jahr + "//" + DatumsConstants.NOVEMBER_KURZ, false));
				quartalNode.getChildren().add(new TreeNodeBase("monatsNode", DatumsConstants.DEZEMBER_LANG, jahr + "//" + DatumsConstants.DEZEMBER_KURZ, false));
				break;
				}
       		jahrNode.getChildren().add(quartalNode);
			}
       	treeData.getChildren().add(jahrNode);
		}
    return treeData;
    }

	public String getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(String selectedNode) 
	{
	this.selectedNode = selectedNode;
	}		
}
