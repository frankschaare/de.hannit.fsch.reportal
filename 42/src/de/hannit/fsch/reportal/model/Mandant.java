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
private int anzahlVorgaengeGesamt = 0;
private int anzahlVorgaengeInServiceZeit = 0;
private int anzahlVorgaengeInOrganisation = 0;
private int anzahlVorgaengeInServiceKategorien = 0;
private int anzahlVorgaengeBerichtszeitraum = 0;


	public Mandant() 
	{
		// TODO Auto-generated constructor stub
	}
	
	public String getDebugInfo() 
	{
	String info = "Filterhistorie: Vorgänge Gesamt = " + getAnzahlVorgaengeGesamt() + " >> Vorgänge für Servicezeit = " + getAnzahlVorgaengeInServiceZeit() + " >> Vorgänge für Organisation = " + getAnzahlVorgaengeInOrganisation() + " >> Vorgänge in Servicekategorien = " + getAnzahlVorgaengeInServiceKategorien() + " >> Vorgänge im Berichtszeitraum = " + getAnzahlVorgaengeBerichtszeitraum(); 	
	return info; 	
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
	public int getAnzahlVorgaengeGesamt() {return anzahlVorgaengeGesamt;}
	public void setAnzahlVorgaengeGesamt(int anzahlVorgaengeGesamt) {this.anzahlVorgaengeGesamt = anzahlVorgaengeGesamt;}
	public int getAnzahlVorgaengeInServiceZeit() {return anzahlVorgaengeInServiceZeit;}
	public void setAnzahlVorgaengeInServiceZeit(int anzahlVorgaengeInServiceZeit) {this.anzahlVorgaengeInServiceZeit = anzahlVorgaengeInServiceZeit;}
	public int getAnzahlVorgaengeInOrganisation() {return anzahlVorgaengeInOrganisation;}
	public void setAnzahlVorgaengeInOrganisation(int anzahlVorgaengeInOrganisation) {this.anzahlVorgaengeInOrganisation = anzahlVorgaengeInOrganisation;}
	public int getAnzahlVorgaengeInServiceKategorien() {return anzahlVorgaengeInServiceKategorien;}
	public void setAnzahlVorgaengeInServiceKategorien(int anzahlVorgaengeInServiceKategorien) {this.anzahlVorgaengeInServiceKategorien = anzahlVorgaengeInServiceKategorien;}
	public int getAnzahlVorgaengeBerichtszeitraum() {return anzahlVorgaengeBerichtszeitraum;}
	public void setAnzahlVorgaengeBerichtszeitraum(int anzahlVorgaengeBerichtszeitraum) {this.anzahlVorgaengeBerichtszeitraum = anzahlVorgaengeBerichtszeitraum;}
	
}
