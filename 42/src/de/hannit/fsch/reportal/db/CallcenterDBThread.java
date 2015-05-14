package de.hannit.fsch.reportal.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import de.hannit.fsch.reportal.model.Zeitraum;
import de.hannit.fsch.reportal.model.callcenter.CallcenterStatistik;

public class CallcenterDBThread implements Callable<TreeMap<LocalDateTime, CallcenterStatistik>> 
{
private final static Logger log = Logger.getLogger(CallcenterDBThread.class.getSimpleName());	
private InitialContext ic;
private DataSource ds = null;
private Connection con = null;	
private PreparedStatement ps = null;
private ResultSet rs = null;
private Zeitraum abfrageZeitraum = null;

private TreeMap<LocalDateTime, CallcenterStatistik> statisiken;

	public CallcenterDBThread() 
	{
		try 
		{
		ic = new InitialContext();
		ds = (DataSource) ic.lookup("java:comp/env/jdbc/callcenterDB");
		con = (con != null ) ? con : ds.getConnection();
		
			if (con != null) 
			{
			log.log(Level.INFO, "Verbindung zur Callcenter-Datenbank hergestellt");	
			}
			else
			{
			log.log(Level.WARNING, "Keine Verbindung zur Callcenter-Datenbank !");	
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

	public void setAbfrageZeitraum(Zeitraum abfrageZeitraum) 
	{
	this.abfrageZeitraum = abfrageZeitraum;
	//this.abfrageZeitraum = new Zeitraum(LocalDate.of(2014, 4, 1), LocalDate.of(2014, 4, 1));	
	}
	
	public Zeitraum getAbfrageZeitraum() {return abfrageZeitraum;}

	@Override
	public TreeMap<LocalDateTime, CallcenterStatistik> call() throws Exception 
	{
	log.log(Level.INFO, "Starte asynchrone Abfrage der CallcenterDB für die Zeit vom " + abfrageZeitraum.getBerichtszeitraumStart() + " bis " + abfrageZeitraum.getBerichtszeitraumEnde() );	
	Instant abfrageStart = Instant.now();
	
	CallcenterStatistik cs = null;
	LocalDate tmpDate = null;
	LocalDateTime tmpTime = null;
	
	statisiken = new TreeMap<LocalDateTime, CallcenterStatistik>();
		try 
		{
		ps = con.prepareStatement(PreparedStatements.SELECT_CALLCENTER_ZEITRAUM);
		ps.setDate(1, abfrageZeitraum.getSQLStartDatum());
		ps.setDate(2, abfrageZeitraum.getSQLEndDatum());
		rs = ps.executeQuery();
		
			while (rs.next()) 
			{
			cs = new CallcenterStatistik();
			cs.setId(rs.getString("ID"));
			
			tmpDate = rs.getDate("Datum").toLocalDate();
			tmpTime = rs.getTimestamp("ZeitVon").toLocalDateTime();
			cs.setStartZeit(LocalDateTime.of(tmpDate.getYear(), tmpDate.getMonthValue(), tmpDate.getDayOfMonth(), tmpTime.getHour(), tmpTime.getMinute(), tmpTime.getSecond()));
			tmpTime = rs.getTimestamp("ZeitBis").toLocalDateTime();
			cs.setEndZeit(LocalDateTime.of(tmpDate.getYear(), tmpDate.getMonthValue(), tmpDate.getDayOfMonth(), tmpTime.getHour(), tmpTime.getMinute(), tmpTime.getSecond()));
			
			cs.setEingehendeAnrufe(rs.getInt("EingehendeAnrufe"));
			cs.setZugeordneteAnrufe(rs.getInt("ZugeordneteAnrufe"));
			cs.setAngenommeneAnrufe(rs.getInt("AngenommeneAnrufe"));
			cs.setAnrufeInWarteschlange(rs.getInt("AnrufeInWarteschlange"));
			cs.setTrotzZuordnungAufgelegt(rs.getInt("TrotzZuordnungAufgelegt"));
			cs.setInWarteschlangeAufgelegt(rs.getInt("InWarteschlangeAufgelegt"));
			cs.setAvgWarteZeitSekunden(rs.getInt("DuschnittlicheWarteZeitSekunden"));
			
			statisiken.put(cs.getStartZeit(), cs);
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
	log.log(Level.INFO, statisiken.size() + " Datensätze aus der Datenbank geladen. Abfagezeit: " + abfrageZeit.toEpochMilli() + " Milisekunden.");
	return statisiken;
	}

}
