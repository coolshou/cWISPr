package idv.coolshou.wispr.util;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/14/12
 * Time: 10:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdCheck
{
    private static Boolean m_adsValue = null;
    private static final String m_adsChecker = "admob"; // example: "admob"
    private static final String m_adsHostFilename = "/etc/hosts";

    public static boolean isAdsDisabled() {
        synchronized(AdCheck.class) {
            if(m_adsValue == null) {
                m_adsValue = Boolean.FALSE;

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(m_adsHostFilename));
                    String line;
                    while((line = reader.readLine()) != null) {
                        if(line.toLowerCase().contains(m_adsChecker)) {
                            m_adsValue = Boolean.TRUE;
                            break;
                        }
                    }
                }
                catch(Exception e) {
                }
                finally {
                    if(reader != null) {
                        try {
                            reader.close();
                        }
                        catch(Exception e) {}
                    }
                }
            }

            return m_adsValue.booleanValue();
        }
    }
}
