package com.tk.webxr.androidnative;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.tk.webxr.webview2d.IResponseCallBack;
import com.tk.webxr.webview2d.WebViewActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String COMMAND_VOLUME_UP = "volume-up";
    private static final String COMMAND_VOLUME_DOWN = "volume-down";
    private static final String COMMAND_BRIGHT_UP = "bright-up";
    private static final String COMMAND_BRIGHT_DOWN = "bright-down";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebViewActivity.launchActivity(this, "https://thinkuldeep.github.io/webxr-native/", new CommandHandler());
    }

    class CommandHandler implements IResponseCallBack {
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        @Override
        public void OnSuccess(String command) {
            Log.d("CommandHandler", command);
            if(COMMAND_VOLUME_UP.equals(command)){
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
            } else if(COMMAND_VOLUME_DOWN.equals(command)){
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
            } else if(COMMAND_BRIGHT_UP.equals(command)){
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = lp.screenBrightness + 1;
                getWindow().setAttributes(lp);
            } else if(COMMAND_BRIGHT_DOWN.equals(command)){
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = lp.screenBrightness - 1;
                getWindow().setAttributes(lp);
            } else {
                Log.w("CommandHandler", "No matching handler found");
            }
        }

        @Override
        public void OnFailure(String result) {
            Log.e("CommandHandler", result);
        }
    }
}
