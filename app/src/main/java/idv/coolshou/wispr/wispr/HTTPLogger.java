package idv.coolshou.wispr.wispr;

import android.util.Log;

import idv.coolshou.wispr.MyActivity;
import idv.coolshou.wispr.service.WISPrLoginService;
import idv.coolshou.wispr.util.HttpUtils;

import java.io.IOException;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/18/12
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPLogger extends ServiceLogger {
    
    protected String targetURL;

    @Override
    public LoggerResult login(String user, String password) {
        String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
        String msg = "";
        try {
            if (!haveConnection()) { /* TODO */
                Map<String, String> postParams = getPostParameters(user, password);

                WISPrLoginService.log(Log.INFO, "HTTP Post Login");
                String result = HttpUtils.getUrlByPost(targetURL, postParams);
                
                do {
                    if (result == null)
                        result = "";

                    if (MyActivity.rd_debug) {
                        WISPrLoginService.log(Log.INFO, "vvvvvvvvvv\n" + result + "\n^^^^^^^^^^\n");
                    }

                    if (isRepostNeed(result)) {
                        postParams = getRepostParameters(user, password, result);
                        result = HttpUtils.getUrlByPost(targetURL, postParams);
                        continue;
                    }
                    String r = HttpUtils.getMetaRefresh(result);
                    if (r == null) {
                        r = HttpUtils.getHttpMoved(result);
                        if (r == null)
                            break;
                    }
                    result = HttpUtils.getUrl(r);
                } while(true);

                if (!isLoginSuccess(result)) {
                    WISPrLoginService.log(Log.INFO, "HTTP Post Login FAILED");
                    res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_FAILED;
                    msg = getLoginMsg(result);
                }
                else if (haveConnection()) {
                    WISPrLoginService.log(Log.INFO, "HTTP Post Login SUCCESS");
                    res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED;
                }
            } else {
                WISPrLoginService.log(Log.INFO, "HTTP Post Login: ALREADY Connected");
                res = WISPrConstants.ALREADY_CONNECTED;
            }
        } catch (IOException e) {
            WISPrLoginService.log(Log.INFO, "HTTP Post Login: Internal Error " + e.getMessage());
            res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
        }

        LoggerResult r= new LoggerResult(res, getLogOffUrl());

        if (res.equals(WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED))
            return r;
        
        r.setMsg( msg );
        return r;
    }

    Map<String, String> extraLoginParam;
    
    public void addExtraParam(String param, String value) {
        if (extraLoginParam == null)
            extraLoginParam = new HashMap<String, String>();
        extraLoginParam.put(param, value);
    }

    protected Map<String, String> getRepostParameters(String user, String password, String result) {
        Map<String, String> map = new HashMap<String, String>();
        return map;
    }

    protected Map<String, String> getPostParameters(String user, String password)
    {
        Map<String, String> map = new HashMap<String, String>();

        if (extraLoginParam != null) {
            Set<Map.Entry<String, String>> paramsSet = extraLoginParam.entrySet();
            for (Map.Entry<String, String> entry : paramsSet) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }
    
    protected String getLogOffUrl() {
        return "";
    }
    
    protected boolean isLoginSuccess(String s) {
        return false;
    }

    protected boolean isRepostNeed(String s) {
        return false;
    }

    @Override
    public boolean setServiceSSID(String ssid) {
        return false;
    }
    
    @Override
    public String getLoginMsg(String s) {
        return "";
    }

}
