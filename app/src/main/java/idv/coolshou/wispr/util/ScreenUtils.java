package idv.coolshou.wispr.util;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 3/18/12
 * Time: 11:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScreenUtils {
    static int width;
    static int height;
    
    static public void setWidth(int w) {
        width = w;
    }
    static public void setHeight(int h) {
        height = h;
    }
    
    static public int getWidth() { return width; }
    static public int getHeight() { return height; }

    public static void initScreenUtil(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
    }
}
