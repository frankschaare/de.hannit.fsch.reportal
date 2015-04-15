package de.hannit.fsch.reportal.model.echolon;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MonatsStatistik
{
private LocalDate berichtsZeitraum = null;
private ArrayList<Vorgang> vorgaengeGesamt = null;
private ArrayList<Vorgang> incidents = null;
private Stream<Vorgang> si = null;

	public MonatsStatistik(LocalDate incoming) 
	{
	this.berichtsZeitraum = incoming;
	vorgaengeGesamt = new ArrayList<Vorgang>();
	}
	
	public void addVorgang(Vorgang incoming) 
	{
	vorgaengeGesamt.add(incoming);	
	}
	
	public String getBerichtsMonatAsString()
	{
	return berichtsZeitraum.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());	
	}
	
	public int getAnzahlVorgaengeGesamt()
	{
	return vorgaengeGesamt.size();	
	}
	
	public long getAnzahlIncidents() 
	{
	si = vorgaengeGesamt.stream();		
	return si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_INCIDENT)).count();
	}
	
	public int getDurchschnittlicheDauerMinutenIncidents() 
	{
	si = vorgaengeGesamt.stream();	
	incidents = si.filter(v -> v.getTyp().equalsIgnoreCase(EcholonConstants.TYP_INCIDENT)).collect(Collectors.toCollection(ArrayList::new ));	
	Double d =  incidents.stream().mapToInt(v -> v.getLoesungszeitMinuten()).average().getAsDouble();
	return d.intValue();
	}	
	
	

}
