/**
 * 
 */
package de.hannit.fsch.reportal.model;

/**
 * @author fsch
 *
 */
public interface Berichtszeitraum 
{
public static final String BERICHTSZEITRAUM_JAHR = "Jahr";
public static final String BERICHTSZEITRAUM_Quartal = "Quartal";
public static final String BERICHTSZEITRAUM_MONAT = "Monat";

public String getBerichtszeitraum();
public void setBerichtszeitraum(String berichtsZeitraum);

public String getBerichtsJahr();
public void setBerichtsJahr(String berichtsJahr);

public String getBerichtsQuartal();
public void setBerichtsQuartal(String berichtsQuartal);

public String getBerichtsMonat();
public void setBerichtsMonat(String berichtsMonat);
}