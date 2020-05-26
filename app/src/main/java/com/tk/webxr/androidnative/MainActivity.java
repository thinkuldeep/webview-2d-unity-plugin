package com.tk.webxr.androidnative;

import android.os.Bundle;
import android.util.Log;

import com.tk.webxr.webview2d.IResponseCallBack;
import com.tk.webxr.webview2d.WebViewActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebViewActivity.launchActivity(this, "", new CommandHandler());
    }

    class CommandHandler implements IResponseCallBack {

        @Override
        public void OnSuccess(String command) {
            Log.d("CommandHandler", command);
        }

        @Override
        public void OnFailure(String result) {
            Log.e("CommandHandler", result);
        }
    }
}
