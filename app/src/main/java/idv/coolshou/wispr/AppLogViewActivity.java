package idv.coolshou.wispr;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import idv.coolshou.wispr.R;

import idv.coolshou.wispr.util.AppLogger;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/16/12
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppLogViewActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logview);

        Button buttonReset = (Button)findViewById(R.id.textLogReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResetLog();
            }
        });

        Button buttonEmail = (Button)findViewById(R.id.textLogEmailButton);
        buttonEmail.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                final String[] recipients = new String[]{"morven@livemail.tw", "",};
                final String msg =
                        "Description:\n(Comments/Crash/Issues/Bugs)\n\n" +
                                "Report detail:\n" + readMobileSpec() + "\n================\n" +
                                mLog;
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "1st Whisper Activity Report");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
                AppLogViewActivity.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                finish();
            }
        });
    }

    private void onResetLog() {
        try {
            AppLogViewActivity.this.openFileOutput(AppLogger.LOG_FILE, MODE_PRIVATE);
            TextView textView = (TextView)findViewById(R.id.textLogView);
            textView.setText("");
        }
        catch (Exception e) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        readLog();
    }

    @Override
    public void onResume() {
        super.onResume();
        readLog();
    }

    String mLog = "";

    private String readMobileSpec() {
        String version = "";
        try {
            PackageInfo info = getPackageManager().getPackageInfo(AboutActivity.PACKAGE_NAME, 0);
            version = info.versionName;
        }

        catch (Exception e) {
        }

        return "Android: " + Build.VERSION.CODENAME + " " + Build.VERSION.RELEASE + "\n"
                + "Model: " + Build.MANUFACTURER + " " + Build.MODEL + "\n"
                + "AppVer: " + version;
    }

    private void readLog() {
        StringWriter s = new StringWriter();
        PrintWriter writer = new PrintWriter(s);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    openFileInput(AppLogger.LOG_FILE)));

            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
            }
            reader.close();
            writer.flush();
        }
        catch (FileNotFoundException e) {
            writer.print("Log file not found");
        }
        catch (Exception e) {
            e.printStackTrace(writer);
        }

        mLog = s.toString();
        writer.close();

        TextView textView = (TextView)findViewById(R.id.textLogView);
        //textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setText(mLog);
        //textView.scrollBy(0,10000);
    }

}
