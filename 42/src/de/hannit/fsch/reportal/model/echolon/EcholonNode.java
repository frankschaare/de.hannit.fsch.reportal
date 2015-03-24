package de.hannit.fsch.reportal.model.echolon;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import de.hannit.fsch.reportal.model.Berichtszeitraum;

public class EcholonNode extends DefaultTreeNode implements Berichtszeitraum 
{
private String berichtsZeitraum = null;
private String berichtsJahr = null;
private String berichtsQuartal = null;
private String berichtsMonat = null;


	public EcholonNode() 
	{
		// TODO Auto-generated constructor stub
	}

	public EcholonNode(Object data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	public EcholonNode(Object data, TreeNode parent) {
		super(data, parent);
		// TODO Auto-generated constructor stub
	}

	public EcholonNode(String type, Object data, TreeNode parent) {
		super(type, data, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getBerichtsJahr() {return berichtsJahr;}

	@Override
	public void setBerichtsJahr(String berichtsJahr) {this.berichtsJahr = berichtsJahr;}

	@Override
	public String getBerichtsQuartal() {return berichtsQuartal;}

	@Override
	public void setBerichtsQuartal(String berichtsQuartal) {this.berichtsQuartal = berichtsQuartal;}

	@Override
	public String getBerichtsMonat() {return berichtsMonat;}

	@Override
	public void setBerichtsMonat(String berichtsMonat) {this.berichtsMonat = berichtsMonat;}

	@Override
	public String getBerichtszeitraum() {return berichtsZeitraum;}

	@Override
	public void setBerichtszeitraum(String berichtsZeitraum) {this.berichtsZeitraum = berichtsZeitraum;}

}
