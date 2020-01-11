package com.example.demo.logsample;

import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ScreenCapturePermission extends AppCompatActivity {
    private static final int CAPTURE_REQUEST = 1;
    private MediaProjectionManager mediaProjectionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),CAPTURE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAPTURE_REQUEST && resultCode == RESULT_OK){
            ScreenCapture.setMediaProjection(mediaProjectionManager.getMediaProjection(resultCode,data));
        }
        finish();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
