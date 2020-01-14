package com.example.demo.logsample;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class LoggerService extends Service {
    private static final String TRACKER_SERVICE_NOTIFICATION_CH = "LoggerService";
    private static final int STOP_REQUEST = 1;
    public static final String ACTION_STOP_SERVICE = ".LoggerService.StopAction";
    public static final String ACTION_START_SERVICE = ".LoggerService.StartAction";

    public static boolean isRunning = false;
    public static MediaProjection mediaProjection;
    private WebServer webServer;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private ImageFileObserver imageFileObserver;
    private BatteryLogger batteryLogger;
    private StepLogger stepLogger;
    private UsageLogger usageLogger;

    @Override
    public void onCreate() {
        super.onCreate();
        webServer = new WebServer();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        List<File> files = new ArrayList<>();
        files.add(getExternalFilesDir(Environment.DIRECTORY_DCIM));
        files.add(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        imageFileObserver = new ImageFileObserver(files);
        batteryLogger = new BatteryLogger(this);
        stepLogger = new StepLogger(getApplicationContext());
        usageLogger = new UsageLogger(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            stop();
            stopSelf();
        } else if(ACTION_START_SERVICE.equals(intent.getAction())){
            start();
        }
        return START_STICKY;
    }

    private void start() {
        try {
            webServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRunning = true;
        initNotification();
        startForeground(1, notificationBuilder.build());

        imageFileObserver.startWatching();

        batteryLogger.start();
        stepLogger.start();
        usageLogger.start();

        //startActivity(new Intent(this, ScreenCapturePermission.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Toast.makeText(getApplicationContext(), "Start !", Toast.LENGTH_SHORT).show();
    }
    private void stop() {
        stepLogger.stop();
        batteryLogger.stop();
        usageLogger.stop();
        imageFileObserver.stopWatching();
        notificationManager.cancelAll();
        webServer.stop();
        isRunning = false;
        mediaProjection = null;
        Toast.makeText(getApplicationContext(),"Stop !",Toast.LENGTH_SHORT).show();
    }

    private void initNotification() {
        if (notificationManager.getNotificationChannel(TRACKER_SERVICE_NOTIFICATION_CH) == null) {
            NotificationChannel ch = new NotificationChannel(TRACKER_SERVICE_NOTIFICATION_CH,
                    "Logger",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(ch);
        }

        Intent stopSelf = new Intent(this, LoggerService.class);
        stopSelf.setAction(ACTION_STOP_SERVICE);
        PendingIntent startActivityIntent = PendingIntent.getActivity(
                getApplicationContext(),
                MainActivity.SHOW_REQUEST,
                new Intent(this, MainActivity.class), FLAG_UPDATE_CURRENT);
        PendingIntent stopServiceIntent = PendingIntent.getForegroundService(
                getApplicationContext(),
                STOP_REQUEST,
                stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), TRACKER_SERVICE_NOTIFICATION_CH)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(Utils.getIPAddress() + ":" + webServer.getListeningPort())
                .setSmallIcon(R.drawable.ic_timeline_black_24dp)
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_list_black_24dp,
                        getString(R.string.notification_show_btn),
                        startActivityIntent))
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_baseline_pause_24px,
                        getString(R.string.notification_stop_btn),
                        stopServiceIntent));
    }
    private void initPeriodicWork() {

    }
    private class ImageFileObserver extends FileObserver{
        public ImageFileObserver(@NonNull List<File> files) {
            super(files);
        }
        @Override
        public void onEvent(int i, String s) {
            if(i == FileObserver.CREATE) {
                File file = new File(s);
                String ext = file.toString().substring(file.toString().lastIndexOf('.'));
                if(ext.compareTo(".webp") != -1 || ext.compareTo(".jpg") != -1) {
                    Utils.writeEventLog("Image save", file.getAbsolutePath());
                }
            }
        }
    };
}
