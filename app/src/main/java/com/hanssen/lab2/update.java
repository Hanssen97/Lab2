package com.hanssen.lab2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class update extends Service {

    private static final int    NEW_CONTENT_NOTIFICATION_ID         = 0;
    private static final String NEW_CONTENT_NOTIFICATION_CHANNEL_ID = "RSS Feeder";

    NotificationCompat.Builder nBuilder = null;
    NotificationManager        nManager = null;

    RequestQueue queue       = null;

    int     refreshFrequency = 0;
    String  rssURL           = "";
    String  update           = "";


    public void onCreate() {
        // Log.d("SERVICE", "Created");

        // Initialize new queue.
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        // Notification setup
        nBuilder = new NotificationCompat.Builder(this, NEW_CONTENT_NOTIFICATION_CHANNEL_ID);
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // For specific SDK versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NEW_CONTENT_NOTIFICATION_CHANNEL_ID, "RSS Feeder", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 100, 0});
            notificationChannel.enableVibration(true);
            nManager.createNotificationChannel(notificationChannel);
        }

        // Uses handler for cron job.
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable(){
            public void run() {
                // updates data in interval (refreshFrequency minutes).
                refresh();
                handler.postDelayed(this, 60000 * refreshFrequency);
            }
        }, 0);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        //Log.d("SERVICE", "Destroyed");
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent){
        return null;
    }


    private void getUserPreferences() {
        // Gets data from cache.
        SharedPreferences sharedPref = getSharedPreferences("preferences", MODE_PRIVATE);

        rssURL            = sharedPref.getString("rssURL", "");
        refreshFrequency  = sharedPref.getInt("refresh", -1);

    }


    private void getRSSData(String url) {
        // Retrieves RSS data from url.
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                update = res;

                // Log.d("SERVICE", "Updated content");

                // Checks if the new data is different from old cached data.
                if (dataDifference()) {
                    notifyNewContent();
                }

                // Saves new data to cache.
                savePreferences(update);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError err) {
                Log.d("pageError", err.getMessage());
            }
        });

        // Adds request to queue.
        queue.add(req);
    }


    private void savePreferences(String xml) {
        // Saves xml to cache.
        SharedPreferences sharedPref = getSharedPreferences("preferences",0);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("xml", xml);
        prefEditor.apply();
    }


    public void refresh() {
        getUserPreferences();
        getRSSData(rssURL);
    }


    private boolean dataDifference() {
        // Checks difference in cache xml and global xml.
        SharedPreferences sharedPref = getSharedPreferences("preferences", MODE_PRIVATE);
        String current = sharedPref.getString("xml", "").replaceAll("\\s+","");
        String compare = update.replaceAll("\\s+","");

        return !current.equals(compare);
    }


    private void notifyNewContent() {
        // Notifies the user with new content.
        nBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        nBuilder.setContentTitle("New Stories!");
        nBuilder.setContentText("RSS Feeder");
        nBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        nBuilder.setAutoCancel(true);
        nBuilder.setContentIntent(PendingIntent.getActivity(this, 0,
                new Intent(this, main.class), PendingIntent.FLAG_UPDATE_CURRENT));

        nManager.notify(NEW_CONTENT_NOTIFICATION_ID, nBuilder.build());
    }
}
