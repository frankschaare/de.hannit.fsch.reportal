package de.hannit.fsch.reportal.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import de.hannit.fsch.reportal.model.echolon.Vorgang;

public class DataBaseThread implements Callable<HashMap<String, Vorgang>> 
{
private final static Logger log = Logger.getLogger(DataBaseThread.class.getSimpleName());	
private InitialContext ic;
private DataSource ds = null;
private Connection con = null;	
private PreparedStatement ps = null;
private ResultSet rs = null;
private String selection = null;
private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();	

	public DataBaseThread() 
	{
		try 
		{
		ic = new InitialContext();
		ds = (DataSource) ic.lookup("java:comp/env/jdbc/echolonDB");
		con = (con != null ) ? con : ds.getConnection();
		
			if (con != null) 
			{
			log.log(Level.INFO, "Verbindung zur Echolon-Datenbank hergestellt");	
			}
			else
			{
			log.log(Level.WARNING, "Keine Verbindung zur Echolon-Datenbank !");	
			}
		// rs = ps.executeQuery();
		// rs.next();
		// anzahlDatenGesamt = rs.getInt("Anzahl");
		} 
		catch (NamingException | SQLException e) 
		{
		e.printStackTrace();
		}		
	}

	@Override
	public HashMap<String, Vorgang> call() throws Exception 
	{
	log.log(Level.INFO, "Starte asynchrone Datenbankabfrage");	
	Instant abfrageStart = Instant.now();
	
	distinctCases = new HashMap<String, Vorgang>();	
	Vorgang v = null;
		try 
		{
		ps = con.prepareStatement(PreparedStatements.SELECT_ALLEVORGAENGE);
		rs = ps.executeQuery();
						
			while (rs.next()) 
			{
			v = new Vorgang();
			v.setId(rs.getString("IncidentId"));
			v.setErstellDatumZeit(rs.getTimestamp("IncidentCreatedOn"));;
			// v.setErstellZeit(rs.getString("AnfrageZeit"));
			v.setVorgangsNummer(rs.getString("Vorgangsnummer"));
			v.setTyp(rs.getString("Typ").trim());
			v.setStatus(rs.getString("Status"));
			v.setKategorie(rs.getString("Kategorie"));
			v.setPrioritaet(rs.getString("Priorität"));
			v.setOrganisation((rs.getString("OrganizationName") != null ? rs.getString("OrganizationName") : "unbekannt"));
			v.setLoesungszeitMinuten((int) rs.getDouble("LösungszeitMinuten"));
			v.setReaktionszeitEingehalten(rs.getString("Reaktionszeit_eingehalten").equalsIgnoreCase("Reaktionszeit eingehalten") ? true : false);
			v.setZielzeitEingehalten(rs.getString("Zielzeit_eingehalten").equalsIgnoreCase("Zielzeit eingehalten") ? true : false);
				
				if (!distinctCases.containsKey(v.getId())) 
				{
				distinctCases.put(v.getId(), v);
				} 
			}
		} 
		catch (SQLException e) 
		{
		e.printStackTrace();
		}		
	Instant abfrageEnde = Instant.now();
	Instant abfrageZeit = abfrageEnde.minusMillis(abfrageStart.toEpochMilli());
	
	/*
	 * Die Vorgänge werden in der Jahresstatistik gespeichert und es wird das Chartmodel generiert.
	 */
	// JahresStatistik js = new JahresStatistik(vorgaenge, selection);
	log.log(Level.INFO, this.getClass().getName() +  ": " + distinctCases.size() + " Datensätze aus der Datenbank geladen. Abfagezeit: " + abfrageZeit.toEpochMilli() + " Milisekunden.");
	return distinctCases;
	}

	public String getSelection() {return selection;}
	public void setSelection(String selection) {this.selection = selection;}

}
