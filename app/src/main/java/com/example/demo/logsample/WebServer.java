package com.example.demo.logsample;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.ZoneId;
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
        long now = System.currentTimeMillis();
        long startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;

        if(query != null) {
            WebServer.LOG.info(method + " '" + uri + "' " + query);
        } else {
            WebServer.LOG.info(method + " '" + uri + "' ");
        }
        if(uri.compareTo("/logs") == 0) {
            //JsonObject usages = UsageLogger.retrieve(startOfToday,now);
            //return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", new Gson().toJson(usages));
        }

        return NanoHTTPD.newChunkedResponse(Response.Status.NOT_FOUND, "text/plain",null);
    }
}