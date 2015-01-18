package idv.coolshou.wispr;

import idv.coolshou.wispr.pref.ServiceItem;
import idv.coolshou.wispr.pref.ServiceItemList;
import idv.coolshou.wispr.service.WISPrLoginService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
//import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/17/12
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class WISPrUtility {
    public static final String SSID_ITAIWAN     = "iTaiwan";
    public static final String SSID_TPE_FREE    = "TPE-Free";
    public static final String SSID_NEW_TAIPEI  = "NewTaipei";
    public static final String SSID_KANSAI      = "Kansai Airport";
    public static final String SSID_WIFLY       = "WIFLY";
    public static final String SSID_FETWIFI     = "FET Wi-Fi";
    public static final String SSID_FET_MOBILE  = "FET Mobile";
    public static final String SSID_CYBER_CITIZENS = "CyberCitizens";
    public static final String SSID_TWNGSM      = "TWM Wi-Fi";
    public static final String SSID_TWROAM      = "TWROAM";
    public static final String SSID_CHT_WIFI      = "CHT Wi-Fi(HiNet)";

    static public final String KEY_IDX      = "idx";
    static public final String KEY_SERVICE  = "service";
    static public final String KEY_SSID     = "ssid";
    static public final String KEY_USER     = "user";
    static public final String KEY_PASS     = "pass";
    static public final String KEY_ITW      = "itw_auto";
    static public final String KEY_URI      = "uri";
    static public final String KEY_DEBUG    = "debug";
    static public final String KEY_ACTION_DEBUG     = "DEBUG";
    static public final String KEY_ACTION_LOG       = "LOG";
    static public final String KEY_ACTION_LOGOFF    = "LOGOFF";

    public static void tryDisconnect(Context context, String ssid) {
        ServiceItemList settingList = new ServiceItemList(context);
        settingList.loadFromPref();
        ServiceItem item = settingList.isMatch(ssid);
        String defaultLogOffUri = "";

        if (item == null && isWiflyService(ssid)) {
            item = settingList.findWiflyService();
            if (item != null)
                defaultLogOffUri = "https://apc.aptilo.com/sci/logoff";
        }

        if (item == null && isTaiwanService(ssid))
            item = settingList.findTaiwanService();

        if (item != null) {
            int index = item.getIndex();
            
            String uri = null;
            if (defaultLogOffUri.length() > 0) {
                uri = defaultLogOffUri;
            }
            else {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                uri = pref.getString(ServiceItemList.KEY_SERVICE_LOGOFF+index, "");
            }

            if (uri != null && uri.length() > 0) {
                Intent logIntent = new Intent(context, WISPrLoginService.class);
                logIntent.setAction(KEY_ACTION_LOGOFF);
                logIntent.putExtra(KEY_SERVICE, item.getServiceName());
                logIntent.putExtra(KEY_IDX, item.getIndex());
                logIntent.putExtra(KEY_URI, uri);
                context.startService(logIntent);
            }
        }
    }
    
    public static boolean tryConnect(Context context, boolean manual) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wm.getConnectionInfo();
        String ssid = connectionInfo.getSSID();
        if (ssid == null || ssid.length() <= 0)
            return false;

        ServiceItemList settingList = new ServiceItemList(context);

        boolean enabled = (manual ? true : settingList.isActive());

        if (enabled) {
            settingList.loadFromPref();

            ServiceItem item = settingList.isMatch(ssid);

            boolean itaiwan_auto = false;
            if (item == null && isTaiwanService(ssid)) {
                item = settingList.findTaiwanService();
                
                if (item != null) {
                    itaiwan_auto = true;
                }
            }

            if (item == null && isWiflyService(ssid))
                item = settingList.findWiflyService();

            if (item != null) {
                Intent logIntent = new Intent(context, WISPrLoginService.class);
                logIntent.setAction(KEY_ACTION_LOG);
                logIntent.putExtra(KEY_IDX, item.getIndex());
                logIntent.putExtra(KEY_SERVICE, item.getServiceName());
                logIntent.putExtra(KEY_SSID, ssid);
                logIntent.putExtra(KEY_USER, item.getUsername());
                logIntent.putExtra(KEY_PASS, item.getPassword());
                logIntent.putExtra(KEY_ITW, itaiwan_auto);
                context.startService(logIntent);
                return true;
            }
            else {
                cleanNotification(context);
            }
        }
        else {
            cleanNotification(context);
        }
        return false;
    }

    static public void cleanNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(1);
    }

    static public boolean isConnectedIntent(Context context, Intent intent) {
        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null) {
            return (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
        }
        else {
            WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            if (wm.isWifiEnabled()) {
                WifiInfo info = wm.getConnectionInfo();
                if (info != null) {
                    String ssid = info.getSSID();
                    if (ssid != null && ssid.length() > 0) {
                        if (info.getIpAddress() > 0)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    static public boolean isDisconnectedIntent(Intent intent) {
        boolean res = false;
        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null) {
            NetworkInfo.State state = networkInfo.getState();
            res = (state.equals(NetworkInfo.State.DISCONNECTING) || state.equals(NetworkInfo.State.DISCONNECTED))
                    && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);

        } else {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            if (wifiState == WifiManager.WIFI_STATE_DISABLED || wifiState == WifiManager.WIFI_STATE_DISABLING) {
                res = true;
            }
        }
        return res;
    }

    static String real_str[] = {"itw", "tpe", "ntpc", "itw"};
                                     /*  0    1      2       3 */
    static String [] t = real_str;
    static String eGov = "eGov";
    static String eGovPrefix = eGov;

    static public String reformatTaiwanLogin(String service, String ssid, String user) {
        if (t == null)
            return user;

        if (ssid.equalsIgnoreCase(SSID_ITAIWAN)) {
            if (service.equalsIgnoreCase(SSID_ITAIWAN))
                return t[0] + "_" + t[0] + "/" + user + "@" + t[3];
                //return "itw_itw/" + user + "@itw";
            else if (service.equalsIgnoreCase(SSID_TPE_FREE))
                return t[1] + "_" + t[0] + "/" + user + "." + t[1] + "@" + t[3];
                //return "tpe_itw/" + user + ".tpe@itw";
            else if (service.equalsIgnoreCase(SSID_NEW_TAIPEI))
                return t[2] + "_" + t[0] + "/" + user + "@" + t[3];
                //return "ntpc_itw/" + user + "@itw";
        }
        else if (ssid.equalsIgnoreCase(SSID_TPE_FREE)) {
            if (service.equalsIgnoreCase(SSID_ITAIWAN))
                return user + "@" + t[3];
                //return eGovPrefix + "/" + fake_tbl[4] + "-" + fake_tbl[5] + "-" + t[3] + "/"+user+"@" + t[3];
            else if (service.equalsIgnoreCase(SSID_TPE_FREE)) {
                return user + "@"+t[1]+"-" + "free" + "." + "tw";
                //return "tpe_tpe/" + user + ".tpe@itw";
            }
            else if (service.equalsIgnoreCase(SSID_NEW_TAIPEI)) {
                return user + "@" + t[2];
                //return eGovPrefix + "/" + fake_tbl[4] + "-" + fake_tbl[5] + "-" + t[2] + "/" + user + "@" + t[2];
                //return "ntpc_tpe/" + user + "@itw";
            }
        }
        else if (ssid.equalsIgnoreCase(SSID_NEW_TAIPEI)) {
            if (service.equalsIgnoreCase(SSID_ITAIWAN))
                return t[0] + "_" + t[2] + "/" + user + "@" + t[3];
                //return "itw_ntpc/" + user + "@itw";
            else if (service.equalsIgnoreCase(SSID_TPE_FREE))
                return t[1] + "_" + t[2] + "/" + user + "." + t[1] + "@" + t[3];
                //return "tpe_ntpc/" + user + ".tpe@itw";
            else if (service.equalsIgnoreCase(SSID_NEW_TAIPEI))
                return t[2] + "_" + t[2] + "/" + user + "@" + t[3];
                //return "ntpc_ntpc/" + user + "@itw";
        }
        else
            return t[0] + user;

        return user;
    }

    public static boolean is_TPE_Free(final String s) {
        return (s.equalsIgnoreCase(SSID_TPE_FREE));
    }

    public static boolean is_iTaiwan(final String s) {
        return (s.equalsIgnoreCase(SSID_ITAIWAN));
    }

    public static boolean is_NewTaipei(final String s) {
        return (s.equalsIgnoreCase(SSID_NEW_TAIPEI));
    }

    public static boolean is_Kansai(final String s) {
        return (s.equalsIgnoreCase(SSID_KANSAI));
    }

    public static boolean isWiflyService(final String ssid) {
        String s = ssid.toLowerCase();
        return s.contains(SSID_WIFLY.toLowerCase()) ||
                s.equalsIgnoreCase(SSID_CYBER_CITIZENS) ||
                s.equalsIgnoreCase(SSID_FET_MOBILE) ||
                s.equalsIgnoreCase(SSID_TWNGSM) ||
                s.equalsIgnoreCase(SSID_KANSAI) ||
                s.equalsIgnoreCase(SSID_TWROAM) ||
                s.equalsIgnoreCase(SSID_KANSAI)
                ;
    }

    public static boolean isTaiwanService(final String s) {
        boolean found = is_iTaiwan(s) || is_TPE_Free(s) || is_NewTaipei(s);

        return found;
    }
}
