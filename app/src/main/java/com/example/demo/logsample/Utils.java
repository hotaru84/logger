package com.example.demo.logsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public final class Utils {
    private static boolean useIPv4 = true;
    public static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
    public static boolean saveBmp(Context context, String type, String fname, Bitmap bmp) {
        if(isExternalStorageWritable()) {
            File file = new File(context.getExternalFilesDir(type), fname);
            try (FileOutputStream fileOutputStream =
                         new FileOutputStream(file, true)
            ) {
                long s = System.currentTimeMillis();
                bmp.compress(Bitmap.CompressFormat.WEBP,20,fileOutputStream);
                Log.d("@@",file.length() + "bytes, " + (System.currentTimeMillis() - s) + "ms");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
        return false;
    }
    public static void saveJsonFile(Context context, String type, String fname, String str) {
        if(isExternalStorageWritable()) {
            String text;
            File file = new File(context.getExternalFilesDir(type), fname);
            try (FileOutputStream fileOutputStream =
                         new FileOutputStream(file, false);
                 OutputStreamWriter outputStreamWriter =
                         new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                 BufferedWriter bw =
                         new BufferedWriter(outputStreamWriter);
            ) {

                bw.write(str);
                bw.flush();
                text = "saved";
            } catch (Exception e) {
                text = "error: FileOutputStream";
                e.printStackTrace();
            }
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,"Can not write external storage",Toast.LENGTH_SHORT).show();
        }
    }
    static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }
    public static void writeEventLog(String type, String l){
        Log.d("@@","write type:" + type + ", log:" + l);
    }
}
