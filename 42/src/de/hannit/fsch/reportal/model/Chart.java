package de.hannit.fsch.reportal.model;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class Chart implements Serializable 
{
private static final long serialVersionUID = -1835051659312691677L;
private FacesContext fc = null;
private ExternalContext ec = null;

private String colorShortCalls = null;
private String colorCustomerRequests = null;
private String colorWorkOrders = null;
private String colorServiceInfos = null;
private String colorServiceAnfragen = null;
private String colorServiceAbrufe = null;
private String colorIncidents = null;
private String colorBeschwerden = null;

	public Chart() 
	{
	fc = FacesContext.getCurrentInstance();
	ec = fc.getExternalContext();
	
	colorShortCalls = ec.getInitParameter("de.hannit.fsch.reportal.COLOR_SHORTCALLS");
	colorCustomerRequests = ec.getInitParameter("de.hannit.fsch.reportal.COLOR_CUSTOMERREQUESTS");
	colorWorkOrders = ec.getInitParameter("de.hannit.fsch.reportal.COLOR_WORKORDERS");
	colorServiceInfos = ec.getInitParameter("de.hannit.fsch.reportal.COLOR_SERVICEINFOS");
	colorServiceAnfragen = ec.getInitParameter("de.hannit.fsch.reportal.COLOR_SERVICEANFRAGEN");
	colorServiceAbrufe = ec.getInitParameter("de.hannit.fsch.reportal.COLOR_SERVICEABRUFE");
	colorIncidents = ec.getInitParameter("de.hannit.fsch.reportal.COLOR_INCIDENTS");
	colorBeschwerden = ec.getInitParameter("de.hannit.fsch.reportal.COLOR_BESCHWERDEN");
	}

	public String getColorShortCalls() {return colorShortCalls;}
	public String getColorCustomerRequests() {return colorCustomerRequests;}
	public String getColorWorkOrders() {return colorWorkOrders;}
	public String getColorServiceInfos() {return colorServiceInfos;}
	public String getColorServiceAnfragen() {return colorServiceAnfragen;}
	public String getColorServiceAbrufe() {return colorServiceAbrufe;}
	public String getColorIncidents() {return colorIncidents;}
	public String getColorBeschwerden() {return colorBeschwerden;}
	
	public String getDefaultBarColors() 
	{
	return getColorCustomerRequests() + "," + getColorWorkOrders() + "," + getColorShortCalls() + "," + getColorServiceInfos() + "," + getColorServiceAnfragen() + "," + getColorServiceAbrufe() + "," + getColorIncidents() + "," + getColorBeschwerden();
	}
	public String getTopTenBarColors() 
	{
	return getColorCustomerRequests() + "," + getColorWorkOrders() + "," + getColorServiceInfos() + "," + getColorServiceAnfragen() + "," + getColorServiceAbrufe() + "," + getColorIncidents() + "," + getColorBeschwerden();
	}
}
