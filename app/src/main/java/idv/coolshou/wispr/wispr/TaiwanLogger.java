package idv.coolshou.wispr.wispr;

//import java.util.HashMap;
//import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/18/12
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaiwanLogger extends WISPrLogger {
    @Override
    public String getBlockedUrl() {
        return "http://www.gov.tw";
    }

    @Override
    public boolean isConnected(String html) {
        return html.contains("<title>Mobile</title>") &&
                html.contains("main.php?width=");
    }

    /*
    @Override
    public Map<String, String> getPostParameters(String user, String password) {
        Map<String, String> postParams = new HashMap<String, String>();
        int i = user.indexOf('@');
        if (i > 0) {
            if (is_iTaiwanAccount(user))
                postParams.put("ssid","iTaiwan");
            else if (is_NewTaipeiAccount(user))
                postParams.put("ssid","NEWTAIPEI");
            postParams.put("account", user.substring(0, i));
        }
        else {
            postParams.put("account", user);
            postParams.put("ssid", "TPE");
        }
        postParams.put("pass", password);

        return postParams;
    }
    */
}
