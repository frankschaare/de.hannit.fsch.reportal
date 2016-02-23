/**
 * 
 */
package de.hannit.fsch.reportal.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import de.hannit.fsch.reportal.model.Berichtszeitraum;
import de.hannit.fsch.reportal.model.Zeitraum;

/**
 * @author fsch
 * Überprüft die Verbindungen zu den Datenbanken und stellt Connections bereit
 */
@ManagedBean(name = "dbs")
@SessionScoped
public class DBStatus 
{
private final static Logger log = Logger.getLogger(DBStatus.class.getSimpleName());		
private InitialContext ic;
private DataSource ds = null;
private Connection con = null;
private DataSource dsLocal = null;
private Connection conLocal = null;
private Exception exception = null;
private String version = "Release Candidate 1.2";

private ExecutorService executor = Executors.newCachedThreadPool();
private Future<Integer> result = null;

	/**
	 * 
	 */
	public DBStatus() 
	{
		try 
		{
		ic = new InitialContext();
		ds = (DataSource) ic.lookup("java:comp/env/jdbc/echolonDB");			
		dsLocal = (DataSource) ic.lookup("java:comp/env/jdbc/callcenterDB");
		con = ds.getConnection();
		conLocal = dsLocal.getConnection();
		
		// dumpEcholonDB();
		} 
		catch (NamingException | SQLException e) 
		{
		exception = e;	
		e.printStackTrace();
		}
	}
	
	/*
	 * prüft, ob Daten aus dem Echolon-View in die lokale EcholonDB zu laden sind
	 */
	private void dumpEcholonDB() 
	{
	EcholonDumpThread dump = new EcholonDumpThread();
	dump.setRemoteConnection(con);
	dump.setLocalConnection(conLocal);
	
		try 
		{
		PreparedStatement ps = conLocal.prepareStatement(PreparedStatements.SELECT_COUNT);
		ResultSet rs = ps.executeQuery();
		rs.next();
		int anzahlDatensätze = rs.getInt("Anzahl");
		
			switch (anzahlDatensätze) 
			{
			case 0: // lokale Datenbank ist leer, es wird ein kompletter Dump durchgeführt
			log.log(Level.INFO, "Keine Daten in lokaler EcholonDB gefunden. Starte Komplettdump der Echolon-View. ");
			dump.setAbfrageZeitraum(new Zeitraum(Berichtszeitraum.BERICHTSZEITRAUM_GESAMT));
			
			result = executor.submit(dump);
			break;

			default: // lokale Datenbank ist nicht leer, es werden nur Daten nach dem Ersten des letzten Quartals gedumpt.
			log.log(Level.INFO, "Es wurden " + anzahlDatensätze + " Datensätze in lokaler EcholonDB gefunden. Starte Dump der Echolon-View ab dem Ersten des letzten Quartals. ");
			dump.setAbfrageZeitraum(new Zeitraum(Berichtszeitraum.BERICHTSZEITRAUM_LETZTES_QUARTAL));
				
			break;
			}
		} 
		catch (SQLException e) 
		{
		e.printStackTrace();
		}
		
	}

	public Exception getException() 
	{
	return exception;
	}

	public boolean getEcholonDBConnected() 
	{
	return con != null ? true : false;
	}
	
	public String getEcholonDBStatus() 
	{
	return "EcholonDB";
	}
	
	public DataSource getEcholonDS() 
	{
	return ds;
	}

	public String getVersion() {
		return version;
	}
	

}
