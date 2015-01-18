package idv.coolshou.wispr.util;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/16/12
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.IOException;
import java.net.SocketException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtils {
    private static final int DEFAULT_MAX_RETRIES = 3;

    private static String TAG = HttpUtils.class.getName();

    private static final String UTF8 = "UTF-8";

    private static HttpParams defaultHttpParams = new BasicHttpParams();

    static {
        defaultHttpParams.setParameter(CoreProtocolPNames.USER_AGENT, "FONAccess; whisper; (Linux; U; Android)");
    }

    public static String getUrl(String url) throws IOException {
        return getUrl(url, DEFAULT_MAX_RETRIES);
    }

    public static String getUrl(String url, int maxRetries) throws IOException {
        String result = null;
        int retries = 0;
        DefaultHttpClient httpclient = getNewHttpClient();
        httpclient.setCookieStore(null);
        HttpGet httpget = new HttpGet(url);
        while (retries <= maxRetries && result == null) {
            try {
                retries++;
                HttpEntity entity = httpclient.execute(httpget).getEntity();

                if (entity != null) {
                    result = EntityUtils.toString(entity).trim();
                }
            } catch (SocketException se) {
                if (retries > maxRetries) {
                    throw se;
                } else {
                    Log.v(TAG, "SocketException, retrying " + retries);
                }
            }
        }

        return result;
    }

    public static String getUrlByPost(String url, Map<String, String> params, int maxRetries) throws IOException {
        return getUrlByPost(url, params, null, maxRetries);
    }

    public static String getUrlByPost(String url, Map<String, String> params) throws IOException {
        return getUrlByPost(url, params, DEFAULT_MAX_RETRIES);
    }

    public static DefaultHttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.USER_AGENT, "FONAccess; whisper; (Linux; U; Android)");

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static String getUrlByPost(String url, Map<String, String> params, Map<String, String> headers,
                                      int maxRetries) throws IOException {
        String result = null;
        int retries = 0;

        DefaultHttpClient httpclient = getNewHttpClient();
        httpclient.setCookieStore(null);

        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        if (params != null) {
            Set<Entry<String, String>> paramsSet = params.entrySet();
            for (Entry<String, String> entry : paramsSet) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formParams, UTF8);
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(postEntity);

        if (headers != null) {
            Set<Entry<String, String>> headersSet = headers.entrySet();
            for (Entry<String, String> entry : headersSet) {
                httppost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        while (retries < maxRetries && result == null) {
            try {
                retries++;
                HttpEntity responseEntity = httpclient.execute(httppost).getEntity();
                if (responseEntity != null) {
                    result = EntityUtils.toString(responseEntity).trim();
                }
            } catch (SocketException se) {
                if (retries > maxRetries) {
                    throw se;
                } else {
                    Log.v(TAG, "SocketException, retrying " + retries, se);
                }
            }
        }

        return result;
    }

    public static String getHttpMoved(String html) {
        String s = html.toLowerCase();
        if (s.indexOf("HTTP/1.1 301") != -1 || s.indexOf("HTTP/1.0 301") != -1) {
            int start = s.indexOf("Location: ");
            if (start > -1) {
                start += 10;
                
                int end = s.indexOf("\n", start);
                if (end > -1) {
                    String meta = html.substring(start, end);
                    return new String(meta);
                }
            }
        }
        return null;
    }
    
    public static String getMetaRefresh(String html) {
        String meta = null;
        int start = html.toLowerCase().indexOf("<meta http-equiv=\"refresh\" content=\"");
        if (start > -1) {
            start += 36;

            int end = html.indexOf('"', start);
            if (end > -1) {
                meta = html.substring(start, end);
                start = meta.toLowerCase().indexOf("url=");
                if (start > -1) {
                    start += 4;
                    meta = new String(meta.substring(start));
                }
            }
        }

        start = html.toLowerCase().indexOf("refresh: 0; url=");
        if (start > -1) {
            start += 16;

            int end = html.indexOf("\n", start);
            if (end > -1) {
                meta = html.substring(start, end);
                start = meta.toLowerCase().indexOf("url=");
                if (start > -1) {
                    start += 4;
                    meta = new String(meta.substring(start));
                }
            }
        }
        return meta;
    }

}
