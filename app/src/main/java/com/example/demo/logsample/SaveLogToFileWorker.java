/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo.logsample;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.JsonObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Saves the image to a permanent file
 */
public class SaveLogToFileWorker extends Worker {

    /**
     * Creates an instance of the {@link Worker}.
     *
     * @param appContext   the application {@link Context}
     * @param workerParams the set of {@link WorkerParameters}
     */
    public SaveLogToFileWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams
    ) {
        super(appContext, workerParams);
    }

    private static final String TAG = SaveLogToFileWorker.class.getSimpleName();

    private static final String TITLE = "Blurred Image";
    private static final SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault());
    private boolean cleanup(){
        try {
            File outputDirectory = new File(getApplicationContext().getFilesDir(),
                    Constants.OUTPUT_PATH);
            if (outputDirectory.exists()) {
                File[] entries = outputDirectory.listFiles();
                if (entries != null && entries.length > 0) {
                    for (File entry : entries) {
                        String name = entry.getName();
                        if (!TextUtils.isEmpty(name) && name.endsWith(".png")) {
                            boolean deleted = entry.delete();
                            Log.i(TAG, String.format("Deleted %s - %s", name, deleted));
                        }
                    }
                }
            }
            return true;
        } catch (Exception exception) {
            Log.e(TAG, "Error cleaning up", exception);
            return false;
        }
    }
    @NonNull
    @Override
    public Worker.Result doWork() {
        Context applicationContext = getApplicationContext();

        //WorkerUtils.makeStatusNotification("clean up logs...", applicationContext);
        //cleanup();

        WorkerUtils.makeStatusNotification("Saving logs", applicationContext);
        WorkerUtils.sleep();
        Data data = getInputData();
        long now = data.getLong(Constants.KEY_LOG_TIMESTAMP,0);
        long prev = data.getLong(Constants.KEY_LOG_PREV_TIMESTAMP,0);
        JsonObject usages = UsageLogger.retrieve(prev,now);

        //WorkerUtils.writeJsonToFile(getApplicationContext(),"utils.json",)

        return Result.success();
    }
}
