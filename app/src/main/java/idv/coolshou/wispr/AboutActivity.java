package idv.coolshou.wispr;

import idv.coolshou.wispr.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/14/12
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class AboutActivity extends Activity {
    final public static String PACKAGE_NAME = "idv.coolshou.whisper";
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        setContentView(R.layout.about);

        TextView version = (TextView)findViewById(R.id.textAboutVersion);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(PACKAGE_NAME, 0);
            version.setText("1st Whisper Version " + info.versionName);
        }
        catch (Exception e) {

        }
    }
}
