package de.hannit.fsch.reportal.model;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class Benutzer implements Serializable
{
private static final long serialVersionUID = 7041864354727039773L;
private String systemUser = null;	
private String systemDNSDomain = null;
private String systemUserDomain = null;
private String loginName = null;
private String loginPassword = null;
private int screenHeight = 0;
private int screenWidth  = 0;
private int initialChartHeight = 0;

public static final String ROLE_HRG = "hrg";

	public Benutzer() 
	{
	systemUser = System.getenv("USERNAME");
	systemDNSDomain = System.getenv("USERDNSDOMAIN");
	systemUserDomain = System.getenv("USERDOMAIN");
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	screenHeight = screenSize.height;
	screenWidth = screenSize.width;
	}

	public String getInfo() 
	{
	return "\\\\" + systemUserDomain + "\\" + systemUser;
	}
	
	public String getTitle() 
	{
	return "\\\\" + systemUserDomain + "\\" + systemUser + " (" + getScreenWidth() + "x" + getScreenHeight() + ")";
	}	
	
	public int getChartHeight()
	{
		switch (screenHeight) 
		{
		// Wahrscheinlich VM oder virtueller Desktop
		case 768:
		initialChartHeight = 800;	
		break;
		case 1080:
		initialChartHeight = 800;	
		break;		

		default:
		initialChartHeight = (getScreenHeight() - 280);	
		break;
		}
	return initialChartHeight;	
	}

	public String getLoginName() {return loginName;}
	public void setLoginName(String loginName) {this.loginName = loginName;}
	public String getLoginPassword() {return loginPassword;}
	public void setLoginPassword(String loginPassword) {this.loginPassword = loginPassword;}
	public String getSystemUser() {return systemUser;}
	public void setSystemUser(String systemUser) {this.systemUser = systemUser;}
	public String getSystemDNSDomain() {return systemDNSDomain;}
	public void setSystemDNSDomain(String systemDNSDomain) {this.systemDNSDomain = systemDNSDomain;}
	public String getSystemUserDomain() {return systemUserDomain;}
	public void setSystemUserDomain(String systemUserDomain) {this.systemUserDomain = systemUserDomain;}
	public int getScreenHeight() {return screenHeight;}
	public int getScreenWidth() {return screenWidth;}
	
	

}
