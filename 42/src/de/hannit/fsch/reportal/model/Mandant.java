package de.hannit.fsch.reportal.model;

import java.time.LocalTime;
import java.util.TreeMap;

public class Mandant
{
private static final long serialVersionUID = 8763641729926262209L;
private LocalTime servicezeitStart = null;
private LocalTime servicezeitEnde = null;
private String id = null;
private String bezeichnung = null;
private boolean serviceKategorienFilter = false;
private TreeMap<Integer, String> serviceKategorien = null;
private boolean organisationenFilter = false;
private TreeMap<Integer, String> organisationen = null;

	public Mandant() 
	{
		// TODO Auto-generated constructor stub
	}

	public TreeMap<Integer, String> getOrganisationen() {return organisationen;}
	public void setOrganisationen(TreeMap<Integer, String> organisationen) {this.organisationen = organisationen;}
	public TreeMap<Integer, String> getServiceKategorien() {return serviceKategorien;}
	public void setServiceKategorien(TreeMap<Integer, String> serviceKategorien) {this.serviceKategorien = serviceKategorien;}
	public boolean getServiceKategorienFilter() {return serviceKategorienFilter;}
	public void setServiceKategorienFilter(boolean serviceKategorienFilter) {this.serviceKategorienFilter = serviceKategorienFilter;}
	public boolean getOrganisationenFilter() {return organisationenFilter;}
	public void setOrganisationenFilter(boolean organisationenFilter) {this.organisationenFilter = organisationenFilter;}
	public LocalTime getServicezeitStart() {return servicezeitStart;}
	public void setServicezeitStart(LocalTime servicezeitStart) {this.servicezeitStart = servicezeitStart;}
	public LocalTime getServicezeitEnde() {return servicezeitEnde;}
	public void setServicezeitEnde(LocalTime servicezeitEnde) {this.servicezeitEnde = servicezeitEnde;}
	public String getId() {return id;}
	public void setId(String id) {this.id = id;}
	public String getBezeichnung() {return bezeichnung;}
	public void setBezeichnung(String bezeichnung) {this.bezeichnung = bezeichnung;}
	public static long getSerialversionuid() {return serialVersionUID;}
	
}
