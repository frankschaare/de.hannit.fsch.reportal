package de.hannit.fsch.util;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import de.hannit.fsch.reportal.model.Zeitraum;

@ManagedBean
@SessionScoped
public class SessionController implements HttpSessionListener, Serializable
{
private static final long serialVersionUID = -8034012295853674691L;
private final static Logger log = Logger.getLogger(SessionController.class.getSimpleName());	
private String logPrefix = this.getClass().getCanonicalName() + ": ";	
private FacesContext fc = null; 	
private ExternalContext ec = null;
private HttpSession session = null;
private LocalDateTime creationTime = null;
private String sessionID = null;

	public SessionController() 
	{
	}

	public String home()
	{
	return "/";
	}
	
	public String signout()
	{
	fc = FacesContext.getCurrentInstance();
	ec = fc.getExternalContext();
	String navCase = null;	
		try 
		{
		ec.getFlash().setKeepMessages(true);	
		fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolgreich abgemeldet.", "Bitte melden Sie sich erneut an."));
		fc.getExternalContext().invalidateSession();
		
		navCase = "/index.xhtml?faces-redirect=true";	
		} 
		catch (Exception e) 
		{
		e.printStackTrace();	
		navCase = "/index.xhtml?faces-redirect=true";
		}
	
	return navCase;
	}

	@Override
	public void sessionCreated(HttpSessionEvent sessionEvent) 
	{
	session = sessionEvent.getSession();
	creationTime =  LocalDateTime.ofInstant(Instant.ofEpochMilli(session.getCreationTime()), ZoneId.systemDefault());
	sessionID = session.getId();
	log.log(Level.INFO, logPrefix + "Neue Session [" + sessionID + "] wurde am " + Zeitraum.dfDatumUhrzeit.format(creationTime) + " Uhr erstellt");		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) 
	{
	fc = FacesContext.getCurrentInstance();
	log.log(Level.INFO, logPrefix + "SessionController.sessionDestroyed wurde ausgeführt");
		
	}

}
