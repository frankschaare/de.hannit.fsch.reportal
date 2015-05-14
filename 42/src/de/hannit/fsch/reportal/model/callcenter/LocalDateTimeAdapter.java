/**
 * 
 */
package de.hannit.fsch.reportal.model.callcenter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author fsch
 *
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> 
{
private DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm");	

	@Override
	public LocalDateTime unmarshal(String v) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String marshal(LocalDateTime t) throws Exception 
	{
	return timestampFormatter.format(t);
	}

}
