package com.hanssen.lab2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.EditText;

public class preferences extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        getUserPreferences();
        addListenerOnBackButton();
    }


    private void setupNumberPicker(NumberPicker limiter, int minValue, int maxValue, int value) {
        limiter.setMinValue(minValue);
        limiter.setMaxValue(maxValue);
        limiter.setValue(value);

        limiter.setWrapSelectorWheel(false);
    }
    private void setupEditText(EditText field, String value) {
        field.setText(value);
    }


    private void addListenerOnBackButton() {
        Button button = findViewById(R.id.goBack);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                savePreferences();
                startActivity(new Intent(preferences.this, main.class));
            }
        });
    }


    private void savePreferences() {
        EditText     rssURL  = findViewById(R.id.rssURL);
        NumberPicker items   = findViewById(R.id.limiterItems);
        NumberPicker refresh = findViewById(R.id.limiterRefresh);


        SharedPreferences sharedPref = getSharedPreferences("preferences",0);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("rssURL", rssURL.getText().toString());
        prefEditor.putInt("items", items.getValue());
        prefEditor.putInt("refresh", refresh.getValue());
        prefEditor.apply();
    }


    private void getUserPreferences() {
        SharedPreferences sharedPref = getSharedPreferences("preferences", MODE_PRIVATE);
        String rssURL  = sharedPref.getString("rssURL", "");
        int items   = sharedPref.getInt("items", 0);
        int refresh = sharedPref.getInt("refresh", 0);


        setupEditText((EditText)findViewById(R.id.rssURL), rssURL);
        setupNumberPicker((NumberPicker)findViewById(R.id.limiterItems),   1, 20, items);
        setupNumberPicker((NumberPicker)findViewById(R.id.limiterRefresh), 1, 10, refresh);
    }
}
