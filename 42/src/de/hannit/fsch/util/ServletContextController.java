package de.hannit.fsch.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import de.hannit.fsch.reportal.db.TimerDeamon;

@WebListener
public class ServletContextController implements ServletContextListener 
{
private final static Logger log = Logger.getLogger(ServletContextController.class.getSimpleName());	
public final static String TIMER_DEAMON = "de.hannit.fsch.reportal.TimerDeamon";	


	public ServletContextController() 
	{
	}

	@Override
	public void contextDestroyed(ServletContextEvent stop) 
	{
		try 
		{
		log.log(Level.INFO, this.getClass().getCanonicalName() + ": Versuche TimerDeamon zu stoppen...");	
		TimerDeamon td = (TimerDeamon) stop.getServletContext().getAttribute(TIMER_DEAMON); 	
			if (td.cancel()) 
			{
			log.log(Level.INFO, this.getClass().getCanonicalName() + ": TimerDeamon wurde gestoppt");	
			} 
			else 
			{
			log.log(Level.WARNING, this.getClass().getCanonicalName() + ": TimerDeamon konnte nicht gestoppt werden !");	
			}
		} 
		catch (NullPointerException e) 
		{
		log.log(Level.SEVERE, this.getClass().getCanonicalName() + ": TimerDeamon wurde nicht im Servlet Context gefunden. !");	
		
		}

	}

	@Override
	public void contextInitialized(ServletContextEvent start) 
	{
	log.log(Level.INFO, this.getClass().getCanonicalName() + ": Initialisiere TimerDeamon im Servlet Context");
	start.getServletContext().setAttribute(TIMER_DEAMON, new TimerDeamon());
	}

}
