package idv.coolshou.wispr.wispr;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/16/12
 * Time: 7:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoggerResult {
    private String result;
    private String msg;
    private String logOffUrl;

    public LoggerResult(String result, String logOffUrl) {
        this.result = result;
        this.logOffUrl = logOffUrl;
        this.msg = "";
    }

    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String s) {
        msg = s;
    }
            
    public String getResult() {
        return result;
    }

    public String getLogOffUrl() {
        return logOffUrl;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{Result: " + result + ", LogOff:" + logOffUrl + "}";
    }
}
