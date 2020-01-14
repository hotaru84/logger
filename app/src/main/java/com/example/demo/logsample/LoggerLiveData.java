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

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class LoggerLiveData {

    private WorkManager mWorkManager;
    private Uri mImageUri;
    private Uri mOutputUri;
    private LiveData<List<WorkInfo>> mSavedWorkInfo;

    public LoggerLiveData(Context context) {
        mWorkManager = WorkManager.getInstance(context);
        mSavedWorkInfo = mWorkManager.getWorkInfosByTagLiveData(Constants.TAG_OUTPUT);
    }

    void applyLogger() {
        // Create charging constraint
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Add WorkRequest to save the image to the filesystem
        PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(SaveLogToFileWorker.class,1, TimeUnit.HOURS);
        //builder.setInputData(createInputData(System.currentTimeMillis()))
        PeriodicWorkRequest save = builder
                .setConstraints(constraints)
                .addTag(Constants.TAG_OUTPUT)
                .build();

        // Actually start the work
        mWorkManager.enqueueUniquePeriodicWork(Constants.PERIODIC_LOG_OUTPUT_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP,save);

    }
    /**
     * Cancel work using the work's unique name
     */
    void cancelWork() {
        mWorkManager.cancelUniqueWork(Constants.PERIODIC_LOG_OUTPUT_WORK_NAME);
    }


    private Uri uriOrNull(String uriString) {
        if (!TextUtils.isEmpty(uriString)) {
            return Uri.parse(uriString);
        }
        return null;
    }

    LiveData<List<WorkInfo>> getOutputWorkInfo() { return mSavedWorkInfo; }
}