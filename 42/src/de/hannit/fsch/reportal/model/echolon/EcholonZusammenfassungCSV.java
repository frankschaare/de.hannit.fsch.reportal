/**
 * 
 */
package de.hannit.fsch.reportal.model.echolon;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hannit.fsch.reportal.db.EcholonDBManager;

/**
 * @author fsch
 *
 */
public class EcholonZusammenfassungCSV extends File 
{
private final static Logger log = Logger.getLogger(EcholonZusammenfassungCSV.class.getSimpleName());
private ArrayList<String> lines = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4996042199957252137L;

	/**
	 * @param pathname
	 */
	public EcholonZusammenfassungCSV(String pathname) {
		super(pathname);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param uri
	 */
	public EcholonZusammenfassungCSV(URI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param parent
	 * @param child
	 */
	public EcholonZusammenfassungCSV(String parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param parent
	 * @param child
	 */
	public EcholonZusammenfassungCSV(File parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}
	
	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}

	public void createCSVDatei(String dateiPfad, String dateiName)
	{
	Path testPath = FileSystems.getDefault().getPath(dateiPfad, dateiName);
		if (Files.exists(testPath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS}))
		{
		log.log(Level.WARNING, "CSV-Datei " + testPath.toString() + " existiert bereits ! Versuche, vorhandene Datei zu löschen...");	
			try 
			{
			Files.delete(testPath);
			Files.createFile(testPath);
			Files.write(testPath, lines, Charset.forName("ISO-8859-15"), StandardOpenOption.WRITE);
			log.log(Level.INFO, "Datei " + testPath.toString() + " wurde erfolgreich angelegt.");
			} 
			catch (IOException e) 
			{
			log.log(Level.WARNING, "CSV-Datei " + testPath.toString() + " Konnte nicht gelöscht werden ! Bitte Berechtigungen prüfen.");				
			e.printStackTrace();
			}	
		}
		else
		{
			try
			{
			Files.createFile(testPath);
			Files.write(testPath, lines, Charset.forName("ISO-8859-15"), StandardOpenOption.WRITE);
			log.log(Level.INFO, "Datei " + testPath.toString() + " wurde erfolgreich angelegt.");
			}
			catch (IOException e)
			{
			log.log(Level.WARNING, "Datei " + testPath.toString() + " konnte nicht erstellt werden. !");					
			e.printStackTrace();
			}	
		}
	}
}
