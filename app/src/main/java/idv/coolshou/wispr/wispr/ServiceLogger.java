package idv.coolshou.wispr.wispr;


//import idv.coolshou.wispr.WhisperUtility;
//import idv.coolshou.wispr.pref.ServiceItemList;
import idv.coolshou.wispr.util.HttpUtils;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/18/12
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceLogger {
    public static final String CONNECTED = "CONNECTED";

    public static final String BLOCKED_URL = "http://www.google.com";

    public static final String WISPR_TAG_NAME = "WISPAccessGatewayParam";

    public String getBlockedUrl() {
        return "http://www.google.com/xhtml";
    }

    public LoggerResult login(String user, String password)
    {
        return null;
    }

    public boolean isConnected(String html) {
        return html.contains("<title>Google</title>") ||
                html.contains("<title> Google </title>");
    }

    public static boolean haveConnection() throws IOException {
        String blockedUrlText = HttpUtils.getUrl(BLOCKED_URL);
        if (blockedUrlText.equals(CONNECTED))
            return true;
        return blockedUrlText.contains("<title>Google</title>") ||
                blockedUrlText.contains("<title> Google </title>");
    }

    public static String getWISPrXML(String source) {
        String res = null;
        int start = source.indexOf("<" + WISPR_TAG_NAME);
        int end = source.indexOf("</" + WISPR_TAG_NAME + ">", start) + WISPR_TAG_NAME.length() + 3;
        if (start > -1 && end > -1) {
            res = new String(source.substring(start, end));
            if (!res.contains("&amp;")) {
                res = res.replace("&", "&amp;");
            }
        }

        return res;
    }

    public boolean setServiceSSID(String ssid) {
        return true;
    }
    
    public String getLoginMsg(String s) {
        return "";
    }
}
