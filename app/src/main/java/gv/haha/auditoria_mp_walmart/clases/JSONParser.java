package gv.haha.auditoria_mp_walmart.clases;

/**
 * Created by HAHA on 4/10/2016.
 */

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.List;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    static JSONArray jarr = null;

    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params) {

        DefaultHttpClient httpClient = null;

        // Making HTTP request
        try {

            // Setup a custom SSL Factory object which simply ignore the certificates validation and accept all type of self signed certificates
            SSLSocketFactory sslFactory = new SimpleSSLSocketFactory(null);
            sslFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            // Enable HTTP parameters
            HttpParams params1 = new BasicHttpParams();
            HttpProtocolParams.setVersion(params1, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params1, HTTP.UTF_8);

            // Register the HTTP and HTTPS Protocols. For HTTPS, register our custom SSL Factory object.
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sslFactory, 443));

            // Create a new connection manager using the newly created registry and then create a new HTTP client using this connection manager
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params1, registry);
            httpClient = new DefaultHttpClient(ccm, params1);

            // check for request method
            if (method == "POST") {
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient1 = new DefaultHttpClient(ccm,params1);
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                try {
                    HttpResponse httpResponse = httpClient1.execute(httpPost); // envia

                    HttpEntity httpEntity = httpResponse.getEntity();       // recupera
                    is = httpEntity.getContent();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                }

            } else if (method == "GET") {
                // request method is GET
                try {
                    DefaultHttpClient httpClient2 = new DefaultHttpClient(ccm,params1);
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;

                    HttpGet httpGet = new HttpGet(url);

                    HttpResponse httpResponse = httpClient2.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }catch (UnknownHostException uhe){
                    uhe.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
            jarr = new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data [" + e.toString() + "] " + json);

            try {
                if (json.length()>0)
                jObj = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));
            } catch (Exception e0) {

                Log.e("JSON Parser0", "Error parsing data0 [" + e0.getMessage() + "] " + json);
                Log.e("JSON Parser0", "Error parsing data0 " + e0.toString());
                e0.printStackTrace();
                try {
                    if (json.length() > 0)
                        jObj = new JSONObject(json.substring(1));
                } catch (Exception e1) {

                    Log.e("JSON Parser1", "Error parsing data1 [" + e1.getMessage() + "] " + json);
                    Log.e("JSON Parser1", "Error parsing data1 " + e1.toString());
                    e1.printStackTrace();
                    try {
                        jObj = new JSONObject(json.substring(2));
                    } catch (Exception e2) {

                        Log.e("JSON Parser2", "Error parsing data2 [" + e2.getMessage() + "] " + json);
                        Log.e("JSON Parser2", "Error parsing data2 " + e2.toString());
                        e2.printStackTrace();
                        try {
                            jObj = new JSONObject(json.substring(3));
                        } catch (Exception e3) {

                            Log.e("JSON Parser3", "Error parsing data3 [" + e3.getMessage() + "] " + json);
                            Log.e("JSON Parser3", "Error parsing data3 " + e3.toString());
                            e3.printStackTrace();
                        }
                    }
                }
            }

        }

        // return JSON String
        return jObj;
        //       return new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));

    }
}