package idv.coolshou.wispr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/14/12
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusActivity extends Activity {

    WifiManager wifiMan;
    EditText    statusMAC, statusSSID, statusIP, statusMode;
    Button      buttonSwitchWifi, buttonWifiSetting, buttonWisprDisconnect, buttonWisprConnect;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        setContentView(R.layout.status);

        wifiMan = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        
        statusMAC = (EditText)findViewById(R.id.statusMAC);
        statusSSID = (EditText)findViewById(R.id.statusSSID);
        statusIP = (EditText)findViewById(R.id.statusIP);
        statusMode = (EditText)findViewById(R.id.statusMode);
        buttonSwitchWifi = (Button)findViewById(R.id.statusSwitchWifi);
        buttonWifiSetting = (Button)findViewById(R.id.statusWifiSetting);
        buttonWisprDisconnect = (Button)findViewById(R.id.statusWisprDisconnect);
        buttonWisprConnect = (Button)findViewById(R.id.statusWisprConnect);

        buttonSwitchWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHandleSwitchWifi();
            }
        });
        buttonWifiSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHandleWifiSetting();
            }
        });
        buttonWisprDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHandleWisprDisconnect();
            }
        });
        buttonWisprConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHandleWisprConnect();
            }
        });
    }

    private void updateWifiStatus() {
        if (wifiMan.isWifiEnabled()) {
            WifiInfo info = wifiMan.getConnectionInfo();
            if (info != null) {
                setStatusMAC(info.getMacAddress());
                setStatusSSID(info.getSSID());
                setStatusIP(info.getIpAddress());
            }
            buttonSwitchWifi.setText(R.string.strDisableWifi);
        }
        else {
            setStatusMAC("");
            setStatusSSID("");
            setStatusIP(0);
            buttonSwitchWifi.setText(R.string.strEnableWifi);
        }
        
        String status = getString(R.string.strWifiStatusUnknown);
        switch (wifiMan.getWifiState()) {
            case WifiManager.WIFI_STATE_DISABLED:
                status = getString(R.string.strWifiStatusDisabled);
                buttonSwitchWifi.setEnabled(true);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                status = getString(R.string.strWifiStatusDisabling);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                status = getString(R.string.strWifiStatusEnabled);
                buttonSwitchWifi.setEnabled(true);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                status = getString(R.string.strWifiStatusEnabled);
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
            default:
                break;
        }
        setStatusMode(status);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWifiStatus();
        registerIntentFilter();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWifiStatus();
        registerIntentFilter();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterIntentFilter();
    }

    private void unregisterIntentFilter() {
        unregisterReceiver(mWifiStateReceiver);
    }

    private void registerIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.registerReceiver(mWifiStateReceiver, intentFilter);
    }

    public class WifiStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateWifiStatus();
        }
    }

    WifiStateReceiver mWifiStateReceiver = new WifiStateReceiver();

    private void setStatusMAC(String s) {
        statusMAC.setText(s);
    }
    private void setStatusSSID(String s) {
        statusSSID.setText(s);
    }
    
    private void setStatusIP(int n) {
        if (n == 0) {
            statusIP.setText("");
            return;
        }
        String s =  Integer.toString((n & 0xff)) + '.' +
                Integer.toString((n & 0xff00) >> 8) + '.' +
                Integer.toString((n & 0xff0000) >> 16) + '.' +
                Integer.toString(((n & 0xff000000) >> 24) & 0xff);
        statusIP.setText(s);
    }

    private void setStatusMode(String s) {
        statusMode.setText(s);
    }

    private void onHandleWisprConnect() {
        if (wifiMan.isWifiEnabled()) {
            boolean result = WISPrUtility.tryConnect(this, true);
            if (!result) {
                Toast.makeText(this, R.string.strTryConnectNotFound, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, R.string.strTryConnectNow, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onHandleWisprDisconnect() {
        if (wifiMan.isWifiEnabled()) {
            WISPrUtility.tryDisconnect(this, statusSSID.getText().toString());
        }
    }

    private void onHandleSwitchWifi() {
        wifiMan.setWifiEnabled( !wifiMan.isWifiEnabled() );
        buttonSwitchWifi.setEnabled(false);
    }

    private void onHandleWifiSetting() {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }


}
