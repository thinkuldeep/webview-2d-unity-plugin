package com.tk.webxr.androidnative;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import com.tk.webxr.webview2d.IResponseCallBack;
import com.tk.webxr.webview2d.WebViewActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String COMMAND_VOLUME_UP = "volume-up";
    private static final String COMMAND_VOLUME_DOWN = "volume-down";
    private static final String COMMAND_TORCH_ON = "torch-on";
    private static final String COMMAND_TORCH_OFF = "torch-off";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebViewActivity.launchActivity(this, "https://thinkuldeep.github.io/webxr-native/", new CommandHandler());
    }

    class CommandHandler implements IResponseCallBack {
        @Override
        public void OnSuccess(String command) {
            Log.d("CommandHandler", command);
            if(COMMAND_VOLUME_UP.equals(command)){
                volumeControl(AudioManager.ADJUST_RAISE);
            } else if(COMMAND_VOLUME_DOWN.equals(command)){
                volumeControl(AudioManager.ADJUST_LOWER);
            } else if(COMMAND_TORCH_ON.equals(command)){
                switchFlashLight(true);
            } else if(COMMAND_TORCH_OFF.equals(command)){
                switchFlashLight(false);
            } else {
                Log.w("CommandHandler", "No matching handler found");
            }
        }

        @Override
        public void OnFailure(String result) {
            Log.e("CommandHandler", result);
        }

        void volumeControl (int type){
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.adjustVolume(type, AudioManager.FLAG_PLAY_SOUND);
        }

         void switchFlashLight(boolean status) {
            try {
                boolean isFlashAvailable = getApplicationContext().getPackageManager()
                        .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                if (!isFlashAvailable) {
                    OnFailure("No flash available in the device");
                }
                CameraManager mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                String mCameraId = mCameraManager.getCameraIdList()[0];
                mCameraManager.setTorchMode(mCameraId, status);
            } catch (CameraAccessException e) {
                OnFailure(e.getMessage());
            }
        }
    }
}
