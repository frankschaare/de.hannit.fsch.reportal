/**
 * 
 */
package de.hannit.fsch.reportal.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * @author fsch
 * �berpr�ft die Verbindungen zu den Datenbanken und stellt Connections bereit
 */
@ManagedBean(name = "dbs")
@SessionScoped
public class DBStatus 
{
private InitialContext ic;
private DataSource ds = null;
private Connection con = null;
private Exception exception = null;

	/**
	 * 
	 */
	public DBStatus() 
	{
		try 
		{
		ic = new InitialContext();
		ds = (DataSource) ic.lookup("java:comp/env/jdbc/echolonDB");			
		con = ds.getConnection();
		} 
		catch (NamingException | SQLException e) 
		{
		exception = e;	
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
	
	

}
