package gv.haha.auditoria_mp_walmart;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static gv.haha.auditoria_mp_walmart.clases.Variables.URL_WS_HOST;

public class DescargarActualizacionWebView extends AppCompatActivity {

    WebView webView;
    WebSettings webSettings;
    String filename, urlapk;
    boolean descargando = false;

    @Override
    public void onBackPressed() {
        if (!descargando) {

        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descargar_actualizacion);

        webView = (WebView) findViewById(R.id.wV_descargar);
        webSettings = webView.getSettings();
        webSettings.setAppCacheEnabled(true);
        //webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
        CookieManager.getInstance().setAcceptCookie(true);
        //final SharedPreferences profile = getSharedPreferences("userdata", 0);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        //if SDK version is greater of 19 then activate hardware acceleration otherwise activate software acceleration
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            urlapk = bundle.getString("URL");
        } else {
            urlapk = URL_WS_HOST + "ad/audit_mp_walmart.apk";
        }

        webView.loadUrl(urlapk);
        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                try {
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(url));
                    /* Let's have some Cookies !!!*/
                    //------------------------COOKIE!!------------------------
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie", cookies);
                    //------------------------COOKIE!!------------------------
                    //Yummy !!
                    request.setMimeType (mimetype);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file...");
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!

                    filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
                    File direct = new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORY_DOWNLOADS, filename);
                    if (direct.exists()) {
                        //Toast.makeText(getApplicationContext(),"ya existe. bien", Toast.LENGTH_LONG).show();
                        direct.delete();
                    }

                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, filename);
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    //webView.loadUrl("file:///android_asset/pag_sin_con/index.html");
                    Toast.makeText(getApplicationContext(), "Descargando... Por favor esperar. ", //To notify the Client that the file is being downloaded
                            Toast.LENGTH_LONG).show();
                    registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                } catch (SecurityException e) {
                    Toast.makeText(getApplicationContext(), "Please grant the storage permission !", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            // your code
            descargando = true;
            Toast.makeText(getApplicationContext(), "Ya se ha descargado "+filename, //To notify the Client that the file is being downloaded
            Toast.LENGTH_LONG).show();
            Instalar();
        }
    };

    public void Instalar() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORY_DOWNLOADS, filename)), "application/vnd.android.package-archive");
        startActivity(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity();
        } else finish();
    }
}
