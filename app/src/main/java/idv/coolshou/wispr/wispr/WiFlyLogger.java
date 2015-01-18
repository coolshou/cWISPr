package idv.coolshou.wispr.wispr;

//import android.content.Context;
//import android.database.DatabaseErrorHandler;
import android.util.Log;
//import android.view.Display;
//import android.view.WindowManager;

import idv.coolshou.wispr.MyActivity;
import idv.coolshou.wispr.WISPrUtility;
import idv.coolshou.wispr.service.WISPrLoginService;
import idv.coolshou.wispr.util.HttpUtils;
import idv.coolshou.wispr.util.ScreenUtils;

//import java.io.IOException;
//import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 3/18/12
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class WiFlyLogger extends HTTPLogger {

    enum ServiceProvider {WIFLY, TPE_FREE, ITAIWAN, NEWTAIPEI, FET_MOBILE, FET_FIBER, TWNGSM, TWROAM,
        WIRELESSGATE, CYBERCITIZENS, MAX};
    final static String[] postLoginURL = {
            "http://www.wifly.com.tw/mWifly6/login.aspx", /* WiFly */
            "https://member.wifly.com.tw/WiflyConn/LoginS1.aspx", /* TPE-Free */
            "https://member.wifly.com.tw/WiflyConn/LoginS1.aspx", /* iTaiwan */
            "https://member.wifly.com.tw/WiflyConn/LoginS1.aspx", /* NEWTAIPEI */
            "http://www.wifly.com.tw/mWifly6/login.aspx", /* FET_MOBILE */
            "https://service.seed.net.tw/register-cgi/login", /* FET ADSL */
            "https://www.catch.net.tw/auth/wifi/login.jsp", /* TWNGSM */
            "https://login.twroam.org.tw/wifly", /* TWROAM */
            "https://apc.aptilo.com/cgi-bin/login", /*WIRELESSGATE*/
            "https://cas.taipei.gov.tw/cas-web/login?service=http://www.wifly.com.tw/wiflylogin/CyberCitizens.aspx",
            ""
    };
    /*
     "https://www.tpe-free.tw/TPE/tpe_login.aspx", /* TPE-Free
    "https://www.tpe-free.tw/TPE/tpe_login.aspx", /* ITAIWAN
            "https://www.tpe-free.tw/TPE/tpe_login.aspx", /* NEWTAIPEI

    TPE CyberCitizens
        http://www.wifly.com.tw/wiflylogin/CyberCitizens.aspx
        https://cas.taipei.gov.tw/cas-web/login?service=http://www.wifly.com.tw/wiflylogin/CyberCitizens.aspx
     */

    private ServiceProvider sp = ServiceProvider.WIFLY;

    @Override
    public boolean setServiceSSID(String ssid) {
        boolean is_debug = MyActivity.rd_debug;
        String s = ssid.toLowerCase();
        if (s.contains(WISPrUtility.SSID_WIFLY.toLowerCase())) {
            sp = ServiceProvider.WIFLY;
        }
        else if (s.contains(WISPrUtility.SSID_CYBER_CITIZENS.toLowerCase())) {
            sp = ServiceProvider.CYBERCITIZENS;
        }
        else if (s.contains(WISPrUtility.SSID_FET_MOBILE.toLowerCase())) {
            sp = ServiceProvider.FET_MOBILE;
        }
        else if (s.contains(WISPrUtility.SSID_TWNGSM.toLowerCase())) {
            sp = ServiceProvider.TWNGSM;
        }
        else if (s.contains(WISPrUtility.SSID_TWROAM.toLowerCase())) {
            sp = ServiceProvider.TWROAM;
        }
        else if (s.contains(WISPrUtility.SSID_TPE_FREE.toLowerCase())) {
            sp = ServiceProvider.TPE_FREE;
        }
        else if (s.contains(WISPrUtility.SSID_ITAIWAN.toLowerCase())) {
            sp = ServiceProvider.ITAIWAN;
        }
        else if (s.contains(WISPrUtility.SSID_NEW_TAIPEI.toLowerCase())) {
            sp = ServiceProvider.NEWTAIPEI;
        }
        else
            return false;
        return true;
    }
    
    public void setServiceProvider (ServiceProvider _sp) {
        sp = _sp;
    }

    @Override
    protected Map<String, String> getPostParameters(String user, String password)
    {
        Map<String, String> map = super.getPostParameters(user, password);

        int n = sp.ordinal();
        if (n >= ServiceProvider.MAX.ordinal()) {
            n = 0;
        }
        targetURL = postLoginURL[n];
        switch(sp) {
            case FET_FIBER:
                SimpleDateFormat df = new SimpleDateFormat("yyyMMddHHmmss");
                map.put("FUNC", "Verify");
                map.put("Scode", "WA");
                map.put("Target", "https://www.wifly.com.tw/wiflylogin/SeedNet_Roaming.aspx");
                map.put("Key", df.format(Calendar.getInstance().getTime()));
                map.put("Method", "post");
                map.put("PATTERN", "default");
                map.put("UserNo", user);
                map.put("UserPwd", password);
                break;
            case TWNGSM:
                map.put("msisdn",user);
                map.put("passwd", password);
                map.put("from_page","member_login");
                map.put("return_url","");
                break;
            case TWROAM:
                map.put("network","tw.wifly");
                map.put("action","https://apc.aptilo.com/cgi-bin/login");
                map.put("otherparams","");
                map.put("username",user);
                map.put("password",password);
                break;
            case WIRELESSGATE:
                map.put("acceptedurl", "http://www.wifly.com.tw");
                map.put("username", "aicent/wig/" + user);
                map.put("password", password);
                break;
            case CYBERCITIZENS:
                String lt = "";
                try {
                    String form = HttpUtils.getUrl(targetURL);
                    if (form != null) {
                        targetURL = "https://cas.taipei.gov.tw" + getKeyValue(form, "action=\"", '"');
                        lt = getKeyValue(form, "name=\"lt\" value=\"", '"');
                    }
                }
                catch (Exception e) {
                }
                map.put("lt", lt);
                map.put("username",user);
                map.put("password", password);
                map.put("_eventId", "submit");
                break;
            case FET_MOBILE:
                map.put("username", user);
                map.put("password", password);
                map.put("UserRole", "44");
                map.put("ScreenResolution", ScreenUtils.getWidth() + "*" + ScreenUtils.getHeight());
                map.put("CookieUse", "F");
                map.put("DevicePortal", "mtpe");
                map.put("ua", "Mozilla/5.0 (Linux; U; Android 4.0; en-us) AppleWebKit/533+ (KHTML, like Gecko) Safari/533.1");
                map.put("key", "");
                break;
            case ITAIWAN:
            case TPE_FREE:
            case NEWTAIPEI:
                map.put("username", user);
                map.put("password", password);
                map.put("ScreenResolution", ScreenUtils.getWidth() + "*" + ScreenUtils.getHeight());
                map.put("CookieUse", "F");
                map.put("DevicePortal", "TPE");
                map.put("ua", "Mozilla/5.0 (Linux; U; Android 4.0; en-us) AppleWebKit/533+ (KHTML, like Gecko) Safari/533.1");
                map.put("Key", "");
                map.put("sUrl", "http://www.taipei.gov.tw");
                map.put("lang","tw");
                map.put("IdentityID","");
                if (sp == ServiceProvider.TPE_FREE)
                    map.put("UserRole", "58");
                else if (sp == ServiceProvider.ITAIWAN)
                    map.put("UserRole", "59");
                else if (sp == ServiceProvider.NEWTAIPEI)
                    map.put("UserRole", "60");

                /*
                String state = "";
                try {
                    String form = HttpUtils.getUrl(targetURL);
                    if (form != null) {
                        state = getKeyValue(form, "name=\"__VIEWSTATE\" value=\"", '"');
                    }
                }
                catch (Exception e) {
                }
                map.put("__EVENTTARGET","btn_login");
                map.put("__EVENTARGUMENT","");
                map.put("__VIEWSTATE",state);
                map.put("pno", user);
                map.put("passwd", password);
                map.put("keepuser","");
                map.put("autologin","");
                */
                break;
            case WIFLY:
            default:
                map.put("username", user);
                map.put("password", password);
                map.put("UserRole", "1");
                map.put("ScreenResolution", ScreenUtils.getWidth() + "*" + ScreenUtils.getHeight());
                map.put("CookieUse", "F");
                map.put("DevicePortal", "mtpe");
                map.put("ua", "Mozilla/5.0 (Linux; U; Android 4.0; en-us) AppleWebKit/533+ (KHTML, like Gecko) Safari/533.1");
                map.put("key", "");
                break;
        }
        return map;
    }

    private static String getKeyValue(String xml, String key, int end_ch) {
        int start, end;
        String value = "";
        start = xml.indexOf(key);
        if (start != -1) {
            start += key.length();
            
            end = xml.indexOf(end_ch, start);
            
            if(end != -1)
                value = xml.substring(start, end);
        }
        return value;
    }
    
    @Override
    protected Map<String, String> getRepostParameters(String user, String password, String result) {
        Map<String, String> map = super.getPostParameters(user, password);

        if (result.contains("method=post action='https://apc.aptilo.com/cgi-bin/login'")) {
            user = getKeyValue(result, "name=username value='", '\'');
            password = getKeyValue(result, "name=password value='", '\'');
            map.put("username", user);
            map.put("password", password);
            map.put("acceptedurl", getKeyValue(result, "name=acceptedurl value='", '\''));

            WISPrLoginService.log(Log.INFO, "WISPr username=" + user);

            targetURL = getKeyValue(result, "method=post action='", '\'');
        }
        else if (result.contains("<title>WIFLY - CyberCitizens</title>")) {
            password = getKeyValue(result, "name=\"password\" value=\"", '"');

            map.put("username", user);
            map.put("password", password);
            map.put("UserRole", getKeyValue(result, "name=\"UserRole\" value=\"", '"'));
            map.put("ScreenResolution", ScreenUtils.getWidth() + "*" + ScreenUtils.getHeight());
            map.put("CookieUse", "F");
            map.put("DevicePortal", "");
            map.put("ua", "Mozilla/5.0 (Linux; U; Android 4.0; en-us) AppleWebKit/533+ (KHTML, like Gecko) Safari/533.1");
            map.put("key", "");
            map.put("sUrl", "http://www.wifly.com.tw");
            map.put("lang","tw");
            map.put("ticket", getKeyValue(result, "name=\"ticket\" value=\"", '"'));
            map.put("casn","");

            targetURL = getKeyValue(result, "Form1.action = \"", '"');
        }
        else if (result.contains("name=auth_form method=post action='/auth/wifi/loginMessage.jsp'")) {
            targetURL = "https://www.catch.net.tw" + getKeyValue(result, "method=post action='", '\'');
            
            map.put("next_url", getKeyValue(result, "name='next_url' value='", '\''));
            map.put("from_ch", "");
            map.put("message", getKeyValue(result, "name='message' value='", '\''));
            map.put("ret_code", getKeyValue(result, "name='ret_code' value='", '\''));
            map.put("messageType", getKeyValue(result, "name='messageType' value='", '\''));
            map.put("need_captcha", getKeyValue(result, "name='need_captcha' value='", '\''));
        }

        return map;
    }

    @Override
    protected boolean isRepostNeed(String s) {
        /* CyberCitizen login re-post */
        if (s.contains("https://member.wifly.com.tw/WiflyConn/LoginS1.aspx"))
            return true;
        /* CyberCitizen login re-post */
        if (s.contains("method=post action='https://apc.aptilo.com/cgi-bin/login'"))
            return true;
        /* TWNGSM login re-post */
        if (s.contains("name=auth_form method=post action='/auth/wifi/loginMessage.jsp'"))
            return true;
        return false;
    }

    @Override
    protected boolean isLoginSuccess(String s) {
        if (s.contains("_Er1_"))
            return false;
        if (s.contains("無法以門號登入"))
            return false;
        if (s.contains("帳號或密碼錯誤"))
            return false;
        if (s.contains("無權使用之區域"))
            return false;
        return true;
    }

    @Override
    protected String getLogOffUrl() {
        return "https://apc.aptilo.com/sci/logoff";
    }

    @Override
    public String getLoginMsg(String s) {
        if (s.contains("_Er1_"))
            return "帳號密碼錯誤";
        if (s.contains("無法以門號登入"))
            return "無法以門號登入";
        if (s.contains("帳號或密碼錯誤"))
            return "帳號或密碼錯誤";
        if (s.contains("無權使用之區域"))
            return "無權使用之區域";
        return "";
    }
}
