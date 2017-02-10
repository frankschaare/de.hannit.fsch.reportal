package de.hannit.fsch.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import de.hannit.fsch.reportal.db.Cache;
import de.hannit.fsch.reportal.model.Benutzer;

public class EcholonDatenFilter implements Filter 
{
private final static Logger log = Logger.getLogger(EcholonDatenFilter.class.getSimpleName());
private String logPrefix = this.getClass().getCanonicalName() + ": ";

	public EcholonDatenFilter() 
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void destroy() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
	HttpServletRequest req = (HttpServletRequest) request;

		if (req.isUserInRole(Benutzer.ROLE_HRG)) 
		{
		req.getSession().setAttribute("restrictedAccess", Benutzer.ROLE_HRG);	
		}

	// res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
	
	// Nächsten Filter in der Kette aufrufen
	chain.doFilter(request, response);

	// post login action

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException 
	{
		// TODO Auto-generated method stub

	}

}
