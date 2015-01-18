package idv.coolshou.wispr;

//import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
//import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
//import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import idv.coolshou.wispr.R;
//import com.RombieSoft.whisper.util.AdCheck;
//import com.google.ads.AdRequest;
//import com.google.ads.AdSize;
//import com.google.ads.AdView;
//import com.google.android.apps.analytics.GoogleAnalyticsTracker;

//import idv.coolshou.wispr.pref.ServiceItemList;
import idv.coolshou.wispr.priv.Constants;
import idv.coolshou.wispr.service.WISPrLoginService;
import idv.coolshou.wispr.util.ScreenUtils;

import java.util.UUID;

public class MyActivity extends TabActivity
{
    static public final String AppName ="cWISPr";
    private TabHost tabHost;
    //private AdView adView = null;
    //public GoogleAnalyticsTracker tracker = null;
    public static boolean rd_debug = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initDeviceDebug();

        //if (AdCheck.isAdsDisabled())
        //    finish();
        
        setContentView(R.layout.main);

        tabHost = getTabHost();
        initTabs();

        if (rd_debug == false) {
            //initTracker();
            //initAdView();
        }

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
//                doTrack("tabview", s);
            }
        });
        
        ScreenUtils.initScreenUtil(this);
    }

    private static boolean is_debug() {
        return rd_debug;
    }

    private void initDeviceDebug() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        
        if(deviceId.equals(Constants.deviceId)) {
            rd_debug = true;
        }
    }

    @Override
    public void onDestroy() {
        /*if (tracker != null) {
            tracker.dispatch();
            tracker.stopSession();
            tracker = null;
        }*/

        //if (adView != null)
        //    adView.destroy();

        super.onDestroy();
    }
/*
    private void initTracker() {
        tracker = GoogleAnalyticsTracker.getInstance();
        tracker.startNewSession(Constants.trackId, 20, this);
    }
    */
/*
    private void initAdView() {
        adView = new AdView(this, AdSize.BANNER, Constants.admobId);
        LinearLayout layout = (LinearLayout)findViewById(R.id.adview);
        layout.addView(adView);
        AdRequest request = new AdRequest();
        request.addTestDevice(AdRequest.TEST_EMULATOR);
        request.addTestDevice(Constants.admobTestDeviceId);
        adView.loadAd(request);
    }
*/    
    private void initTabs() {
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, StatusActivity.class);
        spec = tabHost.newTabSpec("Status").setIndicator(getString(R.string.tabStatus),
                getResources().getDrawable(android.R.drawable.ic_menu_agenda)
                ).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, SettingActivity.class);
        spec = tabHost.newTabSpec("Setting").setIndicator(getString(R.string.tabSetting),
                getResources().getDrawable(android.R.drawable.ic_menu_manage)
                ).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, AboutActivity.class);
        spec = tabHost.newTabSpec("About").setIndicator(getString(R.string.tabAbout),
                getResources().getDrawable(android.R.drawable.ic_menu_info_details)
                ).setContent(intent);
        tabHost.addTab(spec);
        
        /*
        intent = new Intent().setClass(this, HelpActivity.class);
        spec = tabHost.newTabSpec("Help").setIndicator(getString(R.string.tabHelp),
                getResources().getDrawable(android.R.drawable.ic_menu_help)
        ).setContent(intent);
        tabHost.addTab(spec);
        */
//        doTrack("tabview", "Status");

        tabHost.setCurrentTab(0);
    }

/*    public void doTrack(String action, String str) {
        if (tracker != null) {
            tracker.trackEvent(AppName, action, str, 0);
            tracker.dispatch();
        }
    }
*/
    private final static int MENU_ID_LOG = Menu.FIRST+1;
    private final static int MENU_ID_DEBUG = MENU_ID_LOG + 1;
    private final static int MENU_ID_WEB = MENU_ID_DEBUG + 1;
    private final static int MENU_ID_MARKET = MENU_ID_WEB + 1;
    private final static int MENU_ID_DBGXML = MENU_ID_MARKET + 1;
    private final static int MENU_ID_TEST_TPE = MENU_ID_DBGXML + 1;
    private final static int MENU_ID_APTILO_SESSION = MENU_ID_TEST_TPE + 1;
    private final static int MENU_ID_APTILO_LOGOFF = MENU_ID_APTILO_SESSION + 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item;
        item = menu.add(0, MENU_ID_WEB, 0, R.string.menuOptionWeb);
        item.setIcon(android.R.drawable.ic_menu_compass);
        item = menu.add(0, MENU_ID_MARKET, 0, R.string.menuOptionMarket);
        item.setIcon(android.R.drawable.ic_menu_directions);
        item = menu.add(0, MENU_ID_LOG, 0, R.string.menuOptionViewLog);
        item.setIcon(android.R.drawable.ic_menu_agenda);
        item = menu.add(0, MENU_ID_DEBUG, 0, R.string.menuOptionDebug);
        item.setIcon(android.R.drawable.ic_menu_view);
        if (rd_debug) {
            item = menu.add(0, MENU_ID_DBGXML, 0, "Debug Xml");
            item.setIcon(android.R.drawable.ic_menu_view);
            item = menu.add(0, MENU_ID_TEST_TPE, 0, "TPE-Free rule");
            item.setIcon(android.R.drawable.ic_menu_add);
            item = menu.add(0, MENU_ID_APTILO_SESSION, 0, "Aptilo Session");
            item.setIcon(android.R.drawable.ic_menu_view);
            item = menu.add(0, MENU_ID_APTILO_LOGOFF, 0, "Aptilo Logoff");
            item.setIcon(android.R.drawable.ic_menu_view);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch  (item.getItemId()) {
            case MENU_ID_WEB:
                //doTrack("OptMenu", "web");
                String url = "http://rombiesoft.blogspot.com/p/1st-whisper.html";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            case MENU_ID_MARKET:
                //doTrack("OptMenu", "market");
                Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
                break;
            case MENU_ID_LOG:
                //doTrack("OptMenu", "log");
                i = new Intent(this, AppLogViewActivity.class);
                startActivity(i);
                break;
            case MENU_ID_DEBUG:
                //doTrack("OptMenu", "debug");
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                boolean enabled = !pref.getBoolean("debug", false);
                pref.edit().putBoolean("debug", enabled).commit();
                Intent logIntent = new Intent(this, WISPrLoginService.class);
                logIntent.setAction("DEBUG");
                logIntent.putExtra("debug", enabled);
                startService(logIntent);

                Toast.makeText(this, "Detail DEBUG Log " + (enabled ? "enabled" : "disabled"), Toast.LENGTH_LONG).show();
                break;
            case MENU_ID_DBGXML:
                //doTrack("OptMenu", "dbgxml");
                startActivity(new Intent(this, DebugActivity.class));
                break;
            case MENU_ID_TEST_TPE:
                break;
            case MENU_ID_APTILO_LOGOFF:
                i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://apc.aptilo.com/sci/logoff"));
                startActivity(i);
                break;
            case MENU_ID_APTILO_SESSION:
                i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://apc.aptilo.com/pas/QWFNH/showsession.htm?key="));
                startActivity(i);
                break;
            default:
                break;
        }
        return true;
    }
    
    static int mRuleTpe = 0;
    
    static public int getRuleTpe() {
        return mRuleTpe;
    }
    
}
