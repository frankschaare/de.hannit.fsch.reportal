package de.hannit.fsch.reportal.db;

public interface PreparedStatements 
{
public static final String SELECT_ALLEVORGAENGE_AKTUELLES_JAHR = "SELECT * FROM [dbo].[EcholoN_Quartalsbericht] Where [IncidentCreatedOnDate] LIKE ?";
public static final String SELECT_COUNT = "SELECT COUNT(*) AS Anzahl FROM [dbo].[EcholoN_Quartalsbericht]";
}
