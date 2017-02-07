package de.hannit.fsch.util;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class SessionController 
{
private FacesContext fc = null; 	
private ExternalContext ec = null;

	public SessionController() 
	{
	fc = FacesContext.getCurrentInstance();
	ec = fc.getExternalContext();
	}

	public String home()
	{
	return "/";
	}
	
	public String signout()
	{
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

}
