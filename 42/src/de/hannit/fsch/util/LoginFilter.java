package de.hannit.fsch.util;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginFilter implements Filter 
{

	public LoginFilter() 
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
	HttpServletResponse res = (HttpServletResponse) response;

	// pre login action
	      
	// get username 
	String username = req.getParameter("j_username");
	String password = req.getParameter("j_password");

	// res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
	
	// Nächsten Filter in der Kette aufrufen: Benutzer mit j_security_check authentifizieren
	chain.doFilter(request, response);

	// post login action

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException 
	{
		// TODO Auto-generated method stub

	}

}
