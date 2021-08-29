package dev.gigafyde.apollo.utils;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.server.RemoteRef;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

public final class Haste {
    private static final OkHttpClient client = new OkHttpClient();

    public static String paste(String input) {
        try {
            RequestBody body = RequestBody.create(input, MediaType.parse("text/plain; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("https://hastebin.com/documents")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            @SuppressWarnings("ConstantConditions") JSONObject json = new JSONObject(response.body().string());
            return "https://hastebin.com/" + json.getString("key");
        } catch (IOException | NullPointerException | JSONException e) {
            LoggerFactory.getLogger(Haste.class).error("Failed to generate paste", e);
            return null;
        }
    }

    public static String paste(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        String stack = sw.toString();
        String message = t.getMessage();
        StringBuilder output = new StringBuilder("======================= COMMAND FAILED =======================\n\n");
        if (message != null) output.append("---Reason:\n").append(message).append("\n\n");
        Thread c = Thread.currentThread();
        output.append("---Thread:\n")
                .append("--Name: ").append(c.getName())
                .append("\n--Id: ").append(c.getId())
                .append("\n--State: ").append(c.getState());
        output.append("\n\n---Stack Trace:\n").append(stack);
        return paste(output.toString());
    }
}
