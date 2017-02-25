package de.hannit.fsch.reportal.db;

public interface PreparedStatements 
{
public static final String SELECT_ALLEVORGAENGE = "SELECT * FROM [dbo].[EcholoN_Quartalsbericht]";	
public static final String SELECT_ALLEVORGAENGE_AKTUELLES_JAHR = "SELECT * FROM [dbo].[EcholoN_Quartalsbericht] Where [IncidentCreatedOnDate] LIKE ?";
public static final String SELECT_ALLEVORGAENGE_AB = "SELECT * FROM [dbo].[EcholoN_Quartalsbericht] Where [IncidentCreatedOnDate] > ?";
public static final String SELECT_ALLEVORGAENGE_LETZTE_12_MONATE = "SELECT * FROM [dbo].[EcholoN_Quartalsbericht] Where [IncidentCreatedOn] >= ? AND [IncidentCreatedOn] < ?";
public static final String SELECT_ALLEVORGAENGE_LETZTES_QUARTAL_HRG = "SELECT * FROM [dbo].[EcholoN_Quartalsbericht] Where [OrganizationName] Like 'HRG%' AND [IncidentCreatedOn] >= ? AND [IncidentCreatedOn] < ?";
public static final String SELECT_COUNT = "SELECT COUNT(*) AS Anzahl FROM [dbo].[EcholoN_Quartalsbericht]";
public static final String INSERT_ECHOLON_LOKAL = "INSERT INTO [dbo].[EcholoN_Quartalsbericht] ([IncidentId],[IncidentCreatedOn],[Vorgangsnummer],[Status],[Typ],[Kategorie],[Priorit�t],[OrganizationName],[Reaktionszeit_eingehalten],[Zielzeit_eingehalten],[L�sungszeitMinuten]) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

public static final String SELECT_MANDANTEN = "SELECT * FROM [dbo].[Mandanten]";	

public static final String SELECT_CALLCENTER_ZEITRAUM = "SELECT * FROM [dbo].[CallcenterMonitoring] Where [Datum] >= ? AND [Datum] < ?";

}
