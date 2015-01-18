package idv.coolshou.wispr.wispr;

//import java.util.HashMap;
//import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 3/18/12
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class TPEFreeLogger extends HTTPLogger {
    protected Map<String, String> getPostParameters(String user, String password)
    {
        Map<String, String> map = super.getPostParameters(user,password);
        
        map.put("username", user);
        map.put("password", password);
        return map;
    }

    @Override
    protected String getLogOffUrl() {
        return "https://apc.aptilo.com/sci/logoff";
    }


}
