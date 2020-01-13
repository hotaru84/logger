package com.example.demo.logsample;
import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.util.ServerRunner;

public class WebServer extends NanoHTTPD {
    private static final Logger LOG = Logger.getLogger(WebServer.class.getName());
    public static void main(String[] args) {
        ServerRunner.run(WebServer.class);
    }
    public WebServer() {
        super(8080);
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        String query = session.getQueryParameterString();
        long end = System.currentTimeMillis();
        UnixCalendar calendar = new UnixCalendar(end);
        calendar.addDays(-1);
        long begin = calendar.getTimeInMillis();

        if(query != null) {
            WebServer.LOG.info(method + " '" + uri + "' " + query);
        } else {
            WebServer.LOG.info(method + " '" + uri + "' ");
        }
        if(uri.compareTo("/usages") == 0) {
            JsonArray usages = UsageLogger.retrieve(begin,end);
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", new Gson().toJson(usages));
        }
        if(uri.compareTo("/steps") == 0){
            JsonArray steps = StepLogger.retrieve(begin,end);
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", new Gson().toJson(steps));
        }
        if(uri.compareTo("/battery") == 0){
            JsonArray battery = BatteryLogger.retrieve(begin,end);
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", new Gson().toJson(battery));
        }

        return NanoHTTPD.newChunkedResponse(Response.Status.NOT_FOUND, "text/plain",null);
    }
}