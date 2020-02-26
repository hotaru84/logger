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
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.JsonElement;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Saves the image to a permanent file
 */
public class SaveLogWorker extends Worker {
    private LogRepository logRepository;
    public SaveLogWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams
    ) {
        super(appContext, workerParams);
        logRepository = new LogRepository((Application) appContext);
    }
    public static final String TAG = SaveLogWorker.class.getSimpleName();
    @NonNull
    @Override
    public Result doWork() {
        String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("YY-MM-DD"));
        WorkerUtils.saveJsonToFile(
                getApplicationContext(),
                localDate + ".json",
                logRepository.getLogList());
        return Result.success();
    }
}
