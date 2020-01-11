package com.example.demo.logsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.util.DisplayMetrics;

public class ScreenCapture implements ImageReader.OnImageAvailableListener {
    private static final String SCREENCAP_NAME = "Screen Capture";
    private DisplayMetrics metrics;
    private static MediaProjection mediaProjection;
    private ImageReader imageReader;

    public interface OnCaptureListener {
        void onCaptured(Bitmap bitmap);
    }

    private OnCaptureListener captureListener;
    private VirtualDisplay virtualDisplay;

    public ScreenCapture(Context context){
        metrics = context.getResources().getDisplayMetrics();
    }
    public static void setMediaProjection(MediaProjection mp){
        mediaProjection = mp;
    }
    public void capture(OnCaptureListener listener) {
        if(mediaProjection != null) {
            stop();
            this.captureListener = listener;
            virtualDisplay = createVirtualDisplay();
        }
    }
    private void stop(){
        if(virtualDisplay != null) virtualDisplay.release();
        if(imageReader != null) imageReader.close();
        captureListener = null;
    }
    @Override
    public void onImageAvailable(ImageReader imageReader) {
        if(captureListener != null) {
            captureListener.onCaptured(captureImage(imageReader));
            stop();
        }
    }
    private Bitmap captureImage(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        Image.Plane plane = image.getPlanes()[0];
        Bitmap bitmap = Bitmap.createBitmap(plane.getRowStride()/plane.getPixelStride(),
                image.getHeight(),Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(plane.getBuffer());
        image.close();
        return  bitmap;
    }
    private VirtualDisplay createVirtualDisplay() {
        imageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, PixelFormat.RGBA_8888, 1);
        imageReader.setOnImageAvailableListener(this, null);
        return mediaProjection.createVirtualDisplay(SCREENCAP_NAME,
                metrics.widthPixels, metrics.heightPixels, metrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, null);
    }

}
