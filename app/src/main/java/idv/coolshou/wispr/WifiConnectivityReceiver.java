package idv.coolshou.wispr;

import idv.coolshou.wispr.util.ScreenUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/14/12
 * Time: 9:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class WifiConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ScreenUtils.initScreenUtil(context);

        if (WISPrUtility.isConnectedIntent(context, intent)) {
            WISPrUtility.tryConnect(context, false);
        }
        else if (WISPrUtility.isDisconnectedIntent(intent)) {
            WISPrUtility.cleanNotification(context);
        }

    }
}
