package com.tk.webxr.webview2d;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URLEncoder;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = WebViewActivity.class.getName();
    public static final String INTENT_RESPONSE_CALLBACK = "resultReceiver";
    public static final int INTENT_RESULT_CODE = 1;
    public static final String INTENT_RESULT_PARAM_NAME = "command";
    public static final String INTENT_INPUT_URL = "url";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final String baseURL = getIntent().getStringExtra(INTENT_INPUT_URL);

        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                Log.d(TAG, "shouldOverrideUrlLoading" + url);
                String command =  uri.getQueryParameter("command");
                if (command !=null) {
                    ResultReceiver resultReceiver = getIntent().getParcelableExtra(INTENT_RESPONSE_CALLBACK);
                    if(resultReceiver!=null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("command", command);
                        resultReceiver.send(INTENT_RESULT_CODE, bundle);
                        Log.d(TAG, "sent response: -> " + command);
                    }
                    view.loadUrl(baseURL + "?result=" +  URLEncoder.encode("Command " + command + " executed"));
                    return true;
                }
                view.loadUrl(url);
                return true; // then it is not handled by default action
            }
        });
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        setContentView(webView);

        this.webView.clearCache(true);
        WebStorage.getInstance().deleteAllData();
        // Clear all the cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();
        webView.loadUrl(baseURL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void launchActivity(Activity activity, String url, IResponseCallBack callBack){
      Intent intent = new Intent(activity, WebViewActivity.class);
      intent.putExtra(WebViewActivity.INTENT_INPUT_URL, url);
      intent.putExtra(WebViewActivity.INTENT_RESPONSE_CALLBACK, new CustomReceiver(callBack));
      activity.startActivity(intent);
    }


  private static class CustomReceiver extends ResultReceiver{
    private  IResponseCallBack callBack;
    CustomReceiver(IResponseCallBack responseCallBack){
      super(null);
      callBack = responseCallBack;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
      if(WebViewActivity.INTENT_RESULT_CODE == resultCode){
        String command = resultData.getString(WebViewActivity.INTENT_RESULT_PARAM_NAME);
        callBack.OnSuccess(command);
      } else {
        callBack.OnFailure("Not a valid code");
      }
    }
  }
}
