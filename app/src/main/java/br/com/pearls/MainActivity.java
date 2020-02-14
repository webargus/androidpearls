package br.com.pearls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * WebView widget to display pearls pages;
     * the XML parameter configChanges="orientation|screenSize"
     * was included in the manifest activity declaration tag;
     * this should be enough to keep web view from reloading
     * upon screen rotation events (portrait/landscape).
     */
    WebView webView;

    private static final String baseUrl = "http://acesystems.com.br/pearls";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.web_view);
        webView.clearCache(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new PearlsWebViewClient());

        /**
         *
         * Using WebChromeClient here serves two purposes:
         *
         * a)  onConsoleMessage override captures console.log messages
         *     sent over from the JS running in the pearls main page;
         *     we may want to get rid of it when we think we need no more debugging.
         *
         * b)  onProgressChanged override shows and updates
         *     progress dialog when downloading pearls index/logoff pages
         */
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.v("console log: ", "#" + consoleMessage.lineNumber() + ": " +
                        "id: " + consoleMessage.sourceId() + " - " +
                        consoleMessage.message());
                return true;
            }

        });

        performNoCacheDeepReload(true);
    }

    private class PearlsWebViewClient extends WebViewClient {

        // Never prompt user to open any site page with default device browser:
        // stick to android web view instead.
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.v("url", url);
            if(!isNetworkConnected()) {
                return;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.v("hide load gif here:", url);
        }

    }

    /**
     * Switch back and forth from disabled caching & no cache loading
     * to caching enabled & cache loading depending on the boolean argument passed.
     * Good for forcing a clean slate page refresh when changes made to the page JS source code.
     * This avoids exceptions due to device outdated cache or bugged JS running on fresh HTML.
     */
    private void performNoCacheDeepReload(boolean deep) {

        if(!isNetworkConnected()) {
            return;
        }

        webView.stopLoading();
        webView.getSettings().setAppCacheEnabled(!deep);
        webView.getSettings().setCacheMode(deep ? WebSettings.LOAD_NO_CACHE : WebSettings.LOAD_DEFAULT);

        webView.loadUrl(baseUrl);
    }

    private boolean isNetworkConnected() {

        // hide web view panel so as not to show chrome default unavailable pages.
        webView.setVisibility(View.INVISIBLE);

        // check connectivity, show web view panel if device connected to network
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if( (networkInfo != null) && networkInfo.isConnected()) {
            webView.setVisibility(View.VISIBLE);
            return true;
        } else {
            // check if user connected via cellular connection
            TelephonyManager telephonyManager =
                    (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if((telephonyManager != null) &&
                    telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED) {
                webView.setVisibility(View.VISIBLE);
                return true;
            }
        }
        // show toast with no connection message
        Toast.makeText(webView.getContext(),
                getString(R.string.net_connect_msg),
                Toast.LENGTH_LONG).show();
        return false;
    }

    /**
     *  Regular run-of-the-mill top tool bar menu overrides
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // force a clean page refresh from server if user chooses the 'Atualizar' menu item
            case R.id.action_reload:
                performNoCacheDeepReload(true);
                break;
            case R.id.action_search:
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                searchIntent.putExtra("key", "value"); //Optional parameters
                MainActivity.this.startActivity(searchIntent);
            case R.id.action_close:
                finish();   // this calls onPause(); (festgestellt!)
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Override both onResume and onPause methods to freeze JS timed thread processes
     * running in pearls main page while app not shown on the device screen.
     */
    @Override
    protected void onResume() {
        super.onResume();
        webView.resumeTimers();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        Log.v("onPause", "MainActivity");

        /*********************************************************************************
         *
         *  VERY IMPORTANT!!!
         *  Lesson learned, we got to dismiss ANY Dialog when we stop,
         *  or a leak exception is triggered
         *
         *********************************************************************************/
        webView.stopLoading();
        webView.onPause();
        webView.pauseTimers();
        super.onPause();
    }

}
