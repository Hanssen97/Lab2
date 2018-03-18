package com.hanssen.lab2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class content extends AppCompatActivity {

    String title       = null;
    String description = null;
    String link        = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        setup();
    }

    private void setup() {
        getIntentData();
        setContent();

        addListenerOnBackButton();
        addListenerOnReadMoreButton();
    }


    private void getIntentData() {
        // Initialises global data with intent data.
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            title       = extras.getString("title");
            description = extras.getString("description");
            link        = extras.getString("link");
        }
    }


    private void setContent() {
        // Updates widget data.
        TextView titleView       = findViewById(R.id.title);
        TextView descriptionView = findViewById(R.id.description);

        titleView.setText(title);
        descriptionView.setText(description);
    }



    private void addListenerOnBackButton() {
        // Open activity (Main) when back button is clicked.
        Button button = findViewById(R.id.goBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(content.this, main.class));
            }
        });
    }
    private void addListenerOnReadMoreButton() {
        // Open activity (Main) when back button is clicked.
        TextView more = findViewById(R.id.link);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            }
        });
    }

}
