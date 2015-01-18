package idv.coolshou.wispr.wispr;

import android.util.Log;
import org.xml.sax.SAXException;

import idv.coolshou.wispr.service.WISPrLoginService;
import idv.coolshou.wispr.util.HttpUtils;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/16/12
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class WISPrLogger extends ServiceLogger {

    private static final String DEFAULT_LOGOFF_URL = "http://88.wifi";

    protected String userParam = "UserName";

    protected String passwordParam = "Password";

    public LoggerResult login(String user, String password) {
        LoggerResult res = new LoggerResult(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR, null);
        try {
            String blockedUrlText = HttpUtils.getUrl(getBlockedUrl());
            WISPrLoginService.log(Log.DEBUG, "TRY: " + getBlockedUrl());
            if (!blockedUrlText.equalsIgnoreCase(CONNECTED) &&
                    !isConnected(blockedUrlText)) {
                String WISPrXML = getWISPrXML(blockedUrlText);
                if (WISPrXML != null) {
                    WISPrLoginService.log(Log.DEBUG, "XML: " + WISPrXML);
                    WISPrInfoHandler wisprInfo = new WISPrInfoHandler();
                    android.util.Xml.parse(WISPrXML, wisprInfo);

                    if (wisprInfo.getMessageType().equals(WISPrConstants.WISPR_MESSAGE_TYPE_INITIAL)
                            && wisprInfo.getResponseCode().equals(WISPrConstants.WISPR_RESPONSE_CODE_NO_ERROR)) {
                        res = tryToLogin(user, password, wisprInfo);
                    }
                } else {
                    WISPrLoginService.log(Log.DEBUG, "XML NOT FOUND : " + blockedUrlText);
                    res = new LoggerResult(WISPrConstants.WISPR_NOT_PRESENT, null);
                }
            } else {
                WISPrLoginService.log(Log.DEBUG, "ALREADY CONNECTED");
                res = new LoggerResult(WISPrConstants.ALREADY_CONNECTED, DEFAULT_LOGOFF_URL);
            }
        } catch (Exception e) {
            WISPrLoginService.log(Log.ERROR, "Internal Error " + e.getMessage());
            res = new LoggerResult(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR, null);
        }
        WISPrLoginService.log(Log.DEBUG, "WISPr Result: " + res);

        return res;
    }

    private LoggerResult tryToLogin(String user, String password, WISPrInfoHandler wisprInfo) throws IOException,
            ParserConfigurationException, FactoryConfigurationError {
        String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
        String logOffUrl = null;
        String targetURL = wisprInfo.getLoginURL();
        
        WISPrLoginService.log(Log.INFO, "Trying to Log " + targetURL);

        Map<String, String> data = new HashMap<String, String>();
        data.put(userParam, user);
        data.put(passwordParam, password);

        String htmlResponse = HttpUtils.getUrlByPost(targetURL, data);
        WISPrLoginService.log(Log.DEBUG, "HTML Reponse:" + htmlResponse);
        if (htmlResponse != null) {
            String response = getWISPrXML(htmlResponse);
            if (response != null) {
                WISPrLoginService.log(Log.DEBUG, "WISPr Response:" + response);
                WISPrResponseHandler wrh = new WISPrResponseHandler();
                try {
                    android.util.Xml.parse(response, wrh);
                    res = wrh.getResponseCode();
                    logOffUrl = wrh.getLogoffURL();
                } catch (SAXException saxe) {
                    res = WISPrConstants.WISPR_NOT_PRESENT;
                }
            } else {
                res = WISPrConstants.WISPR_NOT_PRESENT;
            }
        }

        // If we dont find the WISPR Response or we cannot parse it, we check if we have connection
        if (res.equals(WISPrConstants.WISPR_NOT_PRESENT)) {
            if (haveConnection()) {
                res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED;
            }
        }

        return new LoggerResult(res, logOffUrl);
    }

    @Override
    public boolean setServiceSSID(String ssid) {
        return true;
    }

}
