/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.hannit.fsch.reportal.db.CallcenterDBThread;

/**
 * @author fsch
 * JAXB Wrapper zur Generierung von XML aus den Callcenterstatistiken
 */
@XmlRootElement(namespace = "de.hannit.xml.callcenter")
public class CallcenterStatistiken implements Callable<Boolean>
{
@XmlElement(name = "Callcenterstatistik")	
private ArrayList<CallcenterStatistik> statistiken = null;
private Path absolutePath = null;
private final static Logger log = Logger.getLogger(CallcenterStatistiken.class.getSimpleName());
	/**
	 * 
	 */
	public CallcenterStatistiken() 
	{
	statistiken = new ArrayList<CallcenterStatistik>();	
	}

	public void setAbsolutePath(Path absolutePath) {
		this.absolutePath = absolutePath;
	}


	public void setStatistiken(ArrayList<CallcenterStatistik> statistiken) 
	{
	this.statistiken = statistiken;
    
	}

	@Override
	public Boolean call() 
	{
	log.log(Level.INFO, "Starte neuen Thread um die Datenbankinhalte in die Datei " + absolutePath.toString() + " zu cachen.");	
		try 
		{
		JAXBContext context = JAXBContext.newInstance(this.getClass());
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
		m.marshal(this, Files.newOutputStream(absolutePath));
		BasicFileAttributes attr = Files.readAttributes(absolutePath, BasicFileAttributes.class);
		log.log(Level.INFO, "DBCache " + absolutePath.toString() + " erfolgreich geschrieben. Dateigrösse ist " + (attr.size() / (float)1048567) + " MB");
		} 
		catch (JAXBException | IOException e) 
		{
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}

}
