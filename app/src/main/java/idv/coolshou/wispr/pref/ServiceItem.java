package idv.coolshou.wispr.pref;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/14/12
 * Time: 11:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceItem implements Comparable<ServiceItem> {
    private String  serviceName;
    private String  SSID;
    private boolean fullyMatch;
    private boolean caseSensitive;
    private String  username;
    private String  password;
    private int     index;
    
    public ServiceItem(String service, String SSID,
                       boolean fullyMatch, boolean caseSensitive,
                       String username, String password)
    {
        this.serviceName = service.trim();
        this.SSID = SSID.trim();
        this.fullyMatch = fullyMatch;
        this.caseSensitive = caseSensitive;
        this.username = username.trim();
        this.password = password.trim();
    }
    
    public int getIndex() { return index; }
    public void setIndex(int idx) { index = idx; }
    public final String getServiceName() {return serviceName; }
    public final String getSSID() { return SSID; }
    public final String getUsername() { return username; }
    public final String getPassword() { return password; }
    public boolean isFullyMatch() { return fullyMatch; }
    public boolean isCaseSensitive() { return caseSensitive; }

    public boolean isMatch(String SSID) {
        if (SSID == null)
            return false;
        if (fullyMatch) {
            if (caseSensitive) {
                return this.SSID.equals(SSID);
            }
            else {
                return this.SSID.equalsIgnoreCase(SSID);
            }
        }
        else {
            if (caseSensitive) {
                return SSID.contains(this.SSID);
            }
            else {
                return SSID.toUpperCase().contains(this.SSID.toUpperCase());
            }
        }
    }
    
    public int compareTo(ServiceItem rhs) {
        int n = serviceName.compareTo(rhs.serviceName);
        if (n == 0) return 0;
        return username.compareTo(rhs.username);
    }


}
