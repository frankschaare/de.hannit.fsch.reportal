package de.hannit.fsch.reportal.model;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@ApplicationScoped
public class Benutzer 
{
private String systemUser = null;	
private String systemDNSDomain = null;
private String systemUserDomain = null;
private String loginName = null;
private String loginPassword = null;
public static final String ROLE_HRG = "hrg";

	public Benutzer() 
	{
	systemUser = System.getenv("USERNAME");
	systemDNSDomain = System.getenv("USERDNSDOMAIN");
	systemUserDomain = System.getenv("USERDOMAIN");
	}

	public String getInfo() 
	{
	return "\\\\" + systemUserDomain + "\\" + systemUser;
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
	
	

}
