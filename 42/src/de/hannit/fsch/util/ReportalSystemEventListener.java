package de.hannit.fsch.util;

import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.PreDestroyApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

public class ReportalSystemEventListener implements SystemEventListener 
{
private PostConstructApplicationEvent start = null;

	public ReportalSystemEventListener() 
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isListenerForSource(Object obj) 
	{
	return (obj instanceof Application);
	}

	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException 
	{
		if(event instanceof PostConstructApplicationEvent)
		{
		start = (PostConstructApplicationEvent) event;
	    }
		
	    if(event instanceof PreDestroyApplicationEvent){System.out.println("PreDestroyApplicationEvent occurred. Application is stopping.");
	    }

	}

}
