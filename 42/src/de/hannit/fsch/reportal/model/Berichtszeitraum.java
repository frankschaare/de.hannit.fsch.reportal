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
public static final int BERICHTSZEITRAUM_MONATLICH = 30;
public static final int BERICHTSZEITRAUM_TAG = 24;
public static final int BERICHTSZEITRAUM_Stunde = 60;
public static final int BERICHTSZEITRAUM_KW = 7;
public static final int BERICHTSZEITRAUM_LETZTE_VIER_QUARTALE = 4;
public static final int BERICHTSZEITRAUM_LETZTE_ZWOELF_MONATE = 12;

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
