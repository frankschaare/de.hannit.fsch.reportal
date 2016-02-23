package de.hannit.fsch.reportal.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hannit.fsch.reportal.model.Berichtszeitraum;
import de.hannit.fsch.reportal.model.Zeitraum;
import de.hannit.fsch.reportal.model.echolon.Vorgang;

public class EcholonDumpThread implements Callable<Integer> 
{
private final static Logger log = Logger.getLogger(EcholonDumpThread.class.getSimpleName());	
private Connection localConnection = null;	
private Connection remoteConnection = null;
private PreparedStatement ps = null;
private ResultSet rs = null;
private Zeitraum abfrageZeitraum = null;

private HashMap<String, Vorgang> distinctCases = new HashMap<String, Vorgang>();	

	public EcholonDumpThread() 
	{
	
	}

	public void setAbfrageZeitraum(Zeitraum abfrageZeitraum) 
	{
	this.abfrageZeitraum = abfrageZeitraum;
	}
	
	public Zeitraum getAbfrageZeitraum() {return abfrageZeitraum;}

	@Override
	public Integer call() throws Exception 
	{
	int count = 0;	
	Instant abfrageStart = Instant.now();
	Vorgang v = null;

		switch (abfrageZeitraum.getTyp()) 
		{
		case Berichtszeitraum.BERICHTSZEITRAUM_GESAMT:
		log.log(Level.INFO, "Starte asynchrone Abfrage der EcholonDB für den Gesamtzeitraum" );	
		
		ps = remoteConnection.prepareStatement(PreparedStatements.SELECT_ALLEVORGAENGE);
		break;

		case Berichtszeitraum.BERICHTSZEITRAUM_LETZTES_QUARTAL:
		log.log(Level.INFO, "Starte asynchrone Abfrage der EcholonDB ab dem Ersten des letzten Quartals" );	
		
		ps = remoteConnection.prepareStatement(PreparedStatements.SELECT_ALLEVORGAENGE_AB);
		ps.setDate(1, abfrageZeitraum.getSQLStartDatum());
		break;
		}
		
		try 
		{
		rs = ps.executeQuery();
		
			while (rs.next()) 
			{
			count++;	
			v = new Vorgang();
			v.setId(rs.getString("IncidentId"));
			v.setErstellDatumZeit(rs.getTimestamp("IncidentCreatedOn"));
			v.setErstellZeit(rs.getString("AnfrageZeit"));
			v.setVorgangsNummer(rs.getString("Vorgangsnummer"));
			v.setTyp(rs.getString("Typ").trim());
			v.setStatus(rs.getString("Status"));
			v.setKategorie(rs.getString("Kategorie"));
			v.setPrioritaet(rs.getString("Priorität"));
			v.setOrganisation(rs.getString("OrganizationName"));
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
	log.log(Level.INFO, count + " Datensätze aus der Datenbank geladen. Es wurden " + distinctCases.size() + " eindeutige Vorgänge generiert. Abfagezeit: " + abfrageZeit.toEpochMilli() + " Milisekunden.");
	log.log(Level.INFO, "Aktualisiere lokale EcholonDB.");
	insertAll();
	
	return count;
	}

	private void insertAll() 
	{
		try 
		{
		localConnection.setAutoCommit(false);	
		ps = localConnection.prepareStatement(PreparedStatements.INSERT_ECHOLON_LOKAL, ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			
			for (Vorgang vorgang : distinctCases.values()) 
			{
			ps.setString(1, vorgang.getId());
			ps.setTimestamp(2, Timestamp.valueOf(vorgang.getErstellDatumZeit()));
			ps.setString(3, vorgang.getVorgangsNummer());
			ps.setString(4, vorgang.getStatus());
			ps.setString(5, vorgang.getTyp());
			ps.setString(6, vorgang.getKategorie());
			ps.setString(7, vorgang.getPrioritaetAsString());
			ps.setString(8, vorgang.getOrganisation());
			ps.setString(9, vorgang.getReaktionszeitEingehalten());
			ps.setString(10, vorgang.getZielzeitEingehalten());
			ps.setFloat(11, vorgang.getLoesungszeitMinuten());
			
			ps.addBatch();
			}
		ps.executeBatch();
		localConnection.commit();
		localConnection.close();
		} 
		catch (SQLException e) 
		{
		e.printStackTrace();
			try 
			{
			localConnection.rollback();
			localConnection.close();
			} 
			catch (SQLException e1) 
			{
			e1.printStackTrace();
			}
		}
		
	}

	public void setLocalConnection(Connection localConnection) 
	{
	this.localConnection = localConnection;
	}

	public void setRemoteConnection(Connection remoteConnection) 
	{
	this.remoteConnection = remoteConnection;
	}

	
}
