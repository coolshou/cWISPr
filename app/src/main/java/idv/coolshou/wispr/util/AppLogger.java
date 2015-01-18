package idv.coolshou.wispr.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/16/12
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppLogger {
    private BufferedWriter os;
    public static final String LOG_FILE = "activity.log";

    public AppLogger(Context context) {

        try {
            FileOutputStream fos = context.openFileOutput(LOG_FILE, Context.MODE_APPEND);
            os = new BufferedWriter(new OutputStreamWriter(fos));
        }
        catch (Exception e) {

        }
    }

    private DateFormat df = DateFormat.getInstance();
    public void log(int type, String msg) {
        if (os != null) {
            try {
                final String t;
                switch(type) {
                    case Log.INFO:  t = "<I>"; break;
                    case Log.ERROR: t = "<E>"; break;
                    case Log.WARN:  t = "<W>"; break;
                    case Log.VERBOSE: t = "<V>"; break;
                    case Log.DEBUG:
                    default:
                        t = "<D>"; break;
                }
                Date now = new Date();
                final String s = df.format(now) + t + msg + "\n";
                os.write(s);
                os.flush();
            }
            catch(Exception e) {
            }
        }
        Log.println(type, "Whisper", msg);
    }
}
