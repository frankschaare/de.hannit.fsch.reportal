package de.hannit.fsch.jsf.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class HalloWeltBean 
{
private String hello = "Daten aus der Managed Bean !";	


	public HalloWeltBean() 
	{
	}

	public String getHello() 
	{
	return hello;
	}
}
