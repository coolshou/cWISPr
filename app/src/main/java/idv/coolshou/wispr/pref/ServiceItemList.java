package idv.coolshou.wispr.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import idv.coolshou.wispr.MyActivity;
import idv.coolshou.wispr.WISPrUtility;
import idv.coolshou.wispr.priv.Constants;
import idv.coolshou.wispr.util.Base64Coder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/15/12
 * Time: 8:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceItemList {
    SharedPreferences prefs;
    
    public final static String KEY_SERVICE_LIST_COUNT   = "num_service";
    public final static String KEY_SERVICE_NAME         = "service";
    public final static String KEY_SERVICE_SSID         = "ssid";
    public final static String KEY_SERVICE_USER         = "user";
    public final static String KEY_SERVICE_PASS         = "pass";
    public final static String KEY_SERVICE_MATCH        = "match";
    public final static String KEY_SERVICE_CASE         = "case";
    public final static String KEY_SERVICE_ACTIVE       = "active";
    public final static String KEY_SERVICE_LOGOFF       = "logoff";

    ArrayList<ServiceItem> list;

    public ServiceItemList(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        list = new ArrayList<ServiceItem>();
    }
    
    public boolean isActive() {
        return prefs.getBoolean(KEY_SERVICE_ACTIVE, false);
    }
    
    public void setActive(boolean enabled) {
        prefs.edit().putBoolean(KEY_SERVICE_ACTIVE, enabled).commit();
    }

    static String encrypt(String s) {
        try {
            byte[] bytes = s.getBytes();
            for (int i=0; i < bytes.length; i++ ) {
                bytes[i] -= (Constants.key -i);
            }
            char[] r = Base64Coder.encode(bytes);
            return new String(r);
        }
        catch (Exception e) {
            return "";
        }
    }
    
    static String decrypt(String s) {
        try {
            byte[] bytes = Base64Coder.decode(s);
            for (int i=0; i < bytes.length; i++ ) {
                bytes[i] += (Constants.key -i);
            }
            return new String(bytes);
        }
        catch (Exception e) {
            return "";
        }
    }
    
    public void loadFromPref() {
        int count = prefs.getInt(KEY_SERVICE_LIST_COUNT, 0);

        list.clear();

        for (int i = 0; i < count; i++ ) {
            String service = prefs.getString(KEY_SERVICE_NAME+i, null);
            if (service == null || service.equals("")) continue;
            String ssid = prefs.getString(KEY_SERVICE_SSID+i, null);
            if (ssid == null || ssid.equals("")) continue;
            String user = prefs.getString(KEY_SERVICE_USER+i, "");
            String pass = prefs.getString(KEY_SERVICE_PASS+i, "");
            boolean match = prefs.getBoolean(KEY_SERVICE_MATCH+i, false);
            boolean cas = prefs.getBoolean(KEY_SERVICE_CASE+i, false);

            if (pass.length() > 0)
                pass = decrypt(pass);

            ServiceItem item = addItem(service, ssid, user, pass, match, cas);
            item.setIndex(i);
        }
    }

    public void saveToPref() {
        int count = list.size();

        SharedPreferences.Editor edit = prefs.edit();

        for (int i = 0; i < count; i++ ) {
            ServiceItem item = list.get(i);
            edit.putString(KEY_SERVICE_NAME+i, item.getServiceName());
            edit.putString(KEY_SERVICE_SSID+i, item.getSSID());
            edit.putString(KEY_SERVICE_USER+i, item.getUsername());
            edit.putString(KEY_SERVICE_PASS+i, encrypt(item.getPassword()));
            edit.putBoolean(KEY_SERVICE_MATCH+i, item.isFullyMatch());
            edit.putBoolean(KEY_SERVICE_CASE+i, item.isCaseSensitive());
        }
        edit.putInt(KEY_SERVICE_LIST_COUNT, count);

        edit.commit();
    }
    
    public ServiceItem addItem(String service, String ssid, String user, String pass, boolean match, boolean cas) {
        ServiceItem item = new ServiceItem(service, ssid, match, cas, user, pass);
        list.add(item);
        return item;
    }

    public boolean checkRange(int pos) {
        return (pos >= 0 && pos < list.size());
    }
    
    public void deleteItem(int pos) {
        if (!checkRange(pos))
            return;
        list.remove(pos);
    }
    
    public int size() {
        return list.size();
    }

    public ServiceItem getAt(int pos) {
        if (!checkRange(pos))
            return null;
        return list.get(pos);
    }
    
    public void setItem(int pos, ServiceItem item) {
        if (!checkRange(pos))
            return;
        list.set(pos, item);
    }
    
    public ArrayList<HashMap<String,String>> buildMapList() {

        ArrayList<HashMap<String,String>> maplist = new ArrayList<HashMap<String,String>>();

        int count = list.size();
        for (int i = 0; i < count; i++ ) {
            ServiceItem item = list.get(i);
            HashMap<String, String> hash = new HashMap<String, String>();
            
            hash.put("service", item.getServiceName());
            hash.put("ssid", item.getSSID());
            hash.put("user", item.getUsername());
            maplist.add(hash);
        }
        return maplist;
    }
    
    public ServiceItem isMatch(String ssid) {
        int count = list.size();
        for (int i=0; i < count; i++) {
            ServiceItem item = list.get(i);
            if (item != null) {
                if (item.isMatch(ssid)) {
                    return item;
                }
            }
        }
        return null;
    }

    public ServiceItem findWiflyService() {
        int count = list.size();
        for (int i=0; i < count; i++) {
            ServiceItem item = list.get(i);
            if (item != null) {
                if (item.isMatch(WISPrUtility.SSID_WIFLY)
                        || item.isMatch(WISPrUtility.SSID_FET_MOBILE)
                        || item.isMatch(WISPrUtility.SSID_CYBER_CITIZENS)
                        || item.isMatch(WISPrUtility.SSID_TWNGSM)
                        || item.isMatch(WISPrUtility.SSID_TWROAM)
                        || item.isMatch(WISPrUtility.SSID_KANSAI)
                        || (MyActivity.rd_debug && item.isMatch(WISPrUtility.SSID_TPE_FREE))
                        )
                {
                    return item;
                }
            }
        }
        return null;
    }

    public ServiceItem findTaiwanService() {
        int count = list.size();
        for (int i=0; i < count; i++) {
            ServiceItem item = list.get(i);
            if (item != null) {
                if (item.isMatch(WISPrUtility.SSID_ITAIWAN) ||
                    item.isMatch(WISPrUtility.SSID_NEW_TAIPEI) ||
                    item.isMatch(WISPrUtility.SSID_TPE_FREE)
                    )
                {
                    return item;
                }
            }
        }
        return null;
    }

}
