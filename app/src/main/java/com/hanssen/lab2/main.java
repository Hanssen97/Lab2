package com.hanssen.lab2;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.XML;

import android.util.Log;
import android.widget.ListView;
import java.util.ArrayList;


public class main extends AppCompatActivity {

    JSONArray   itemList  = null;
    int         itemLimit = 0;
    String      xml       = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();
    }

    private void setup() {
        // Checks if background service is running, start it if it don't.
        if (!isMyServiceRunning()){
            startService(new Intent(this, update.class));
        }

        getData();
        addListenerOnPreferencesButton();
    }


    private void getData() {
        /*
            The code below introduces a delay (500ms) before fetching the XML data provided
            by the background service (async).
         */
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getUserPreferences();

                            // Checks if there is data to parse
                            if (!xml.isEmpty()) {
                                itemList = getItemList(xmlToJSON(xml));
                                updateList();
                            }

                            // Add xml data to list.
                            addListenerOnItemList();
                        }
                    });
                }
            },
            500
        );
    }


    private void addListenerOnPreferencesButton() {
        // Starts preferences activity when button is pressed.
        Button button = findViewById(R.id.gotoPreferences);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(main.this, preferences.class));
            }
        });
    }


    private void getUserPreferences() {
        // Retrieves xml and item limit from cache.
        SharedPreferences sharedPref = getSharedPreferences("preferences", MODE_PRIVATE);

        xml       = sharedPref.getString("xml", "");
        itemLimit = sharedPref.getInt("items", -1);
    }


    private JSONObject xmlToJSON(String xml) {
        JSONObject object = null;

        // Parses and returns json from xml.
        try {
            object = XML.toJSONObject(xml);
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
        }

        return object;
    }


    private JSONArray getItemList(JSONObject obj) {
        JSONArray list = null;

        // Gets the items from a rss json object and returns an array with the items.
        try {
            list = obj.getJSONObject("rss").getJSONObject("channel").getJSONArray("item");
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
        }

        return list;
    }


    private void updateList() {
        ListView list = findViewById(R.id.list);

        ArrayList<String> stringArr = new ArrayList<>();

        // Updates the layout widget list based on the global list of items.
        try {
            for (int i = 0; i < itemLimit; i++) {
                String title = itemList.getJSONObject(i).getString("title");
                stringArr.add(title);
            }
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(main.this, android.R.layout.simple_list_item_1, stringArr);
        list.setAdapter(adapter);
    }

    private void addListenerOnItemList() {
        ListView list = findViewById(R.id.list);

        // Opens a content page with information from the list of items.
        // Shares title, description and link with content activity.
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(main.this, content.class);
                try {
                    intent.putExtra("title", itemList.getJSONObject(position).getString("title"));
                    intent.putExtra("description", itemList.getJSONObject(position).getString("description"));
                    intent.putExtra("link", itemList.getJSONObject(position).getString("link"));
                } catch (JSONException e) {
                    Log.e("JSON exception", e.getMessage());
                }

                startActivity(intent);
            }
        });
    }












    public void alert(String message) {
        // Alert modal with custom message and "ok" button.
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton( "Ok",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }



    private boolean isMyServiceRunning() {
        // Checks if the background service is running.
        ActivityManager manager = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (update.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
