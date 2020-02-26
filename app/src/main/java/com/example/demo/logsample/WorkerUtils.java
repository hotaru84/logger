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

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

final class WorkerUtils {
    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private static final String TAG = WorkerUtils.class.getSimpleName();

    static boolean saveJsonToFile(Context context, String fname, JsonElement element) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fname);
        try (FileOutputStream fileOutputStream =
                     new FileOutputStream(file, file.exists());
             OutputStreamWriter outputStreamWriter =
                     new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             BufferedWriter bw =
                     new BufferedWriter(outputStreamWriter);
        ) {

            bw.write(new Gson().toJson(element));
            bw.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    static JsonElement getJsonFileList(Context context){
        JsonObject object = new JsonObject();
        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath());
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})_(\\d).json");
        for(File f : dir.listFiles()){
            Matcher matcher = pattern.matcher(f.getName());
            if(matcher.find()) {
                object.addProperty(matcher.group(1),matcher.group(2));
            }
        }
        return object;
    }
    static void postJsonToUrl(JsonElement json, String url) {
        RequestBody body = RequestBody.create(new Gson().toJson(json), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private WorkerUtils() {
    }
}