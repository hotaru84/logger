package com.example.demo.logsample;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.LruCache;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.function.BiConsumer;

import static android.content.Context.SENSOR_SERVICE;

public class StepLogger implements SensorEventListener {
    private static final int STEP_DETECT_DURATION_SEC = 10;
    private static final long MIN_STEP_COUNT_TO_LOG = 3;
    private static final int MAX_CACHE_LOGSIZE = 1024 * 1024;
    private SensorManager sensorManager;
    private Handler handler;
    private long stepStartTimeMs = 0;
    private long stepCount = 0;
    private static LruCache<Long, JsonObject> sLruStepLogCache;

    public StepLogger(Context context){
        handler = new Handler();
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sLruStepLogCache = new LruCache<Long, JsonObject>(MAX_CACHE_LOGSIZE){
            @Override
            protected int sizeOf(Long key, JsonObject value) {
                return value.getAsByte();
            }
        };
    }

    public void start() {
        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void stop(){
        sensorManager.unregisterListener(this);
    }
    public static JsonArray retrieve() {
        JsonArray jsonArray = new JsonArray();
        Map<Long, JsonObject> logs = sLruStepLogCache.snapshot();
        logs.forEach((time, log) -> jsonArray.add(log));
        return jsonArray;
    }
    public void clearAll() {sLruStepLogCache.evictAll();}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepCount++;
            if(stepCount > MIN_STEP_COUNT_TO_LOG) stepStartTimeMs = System.currentTimeMillis();
            handler.removeCallbacks(refreshLog);

            handler.postDelayed(refreshLog, STEP_DETECT_DURATION_SEC * 1000);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }
    private final Runnable refreshLog = () -> {
        JsonObject object = new JsonObject();
        object.addProperty("duration", System.currentTimeMillis() - stepStartTimeMs);
        object.addProperty("step",stepCount);
        sLruStepLogCache.put(stepStartTimeMs,object);
        stepCount = 0;
        stepStartTimeMs = 0;
    };
}