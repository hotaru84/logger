package com.example.demo.logsample;

import android.graphics.Bitmap;

import androidx.collection.LruCache;

import java.io.ByteArrayOutputStream;

public class ImageCache {
    private static final int CACHE_SIZE_BASE = 100;
    private static final int CACHE_SIZE = CACHE_SIZE_BASE * 1024 * 1024;
    private static LruCache<String, byte[]> sLruCache;
    static {
        sLruCache = new LruCache<String, byte[]>(CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, byte[] value) {
                return value.length;
            }
        };
    }

    private ImageCache() {
    }

    public static byte[] get(String key) {
        synchronized (sLruCache) {
            return sLruCache.get(key);
        }
    }

    public static void put(String key, Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap.compress(Bitmap.CompressFormat.WEBP, 20, stream)) {
            synchronized (sLruCache) {
                sLruCache.put(key, stream.toByteArray());
            }
        }
    }
    public static int getCount() {
        return sLruCache.snapshot().size();
    }
    public static void remove(String key) {
        synchronized (sLruCache) {
            sLruCache.remove(key);
        }
    }
}
