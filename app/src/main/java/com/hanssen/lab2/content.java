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

        getIntentData();
        setContent();

        addListenerOnBackButton();
        addListenerOnReadMoreButton();
    }


    private void getIntentData() {
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            title       = extras.getString("title");
            description = extras.getString("description");
            link        = extras.getString("link");
        }
    }


    private void setContent() {
        TextView titleView       = findViewById(R.id.title);
        TextView descriptionView = findViewById(R.id.description);

        titleView.setText(title);
        descriptionView.setText(description);
    }



    private void addListenerOnBackButton() {
        Button button = findViewById(R.id.goBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(content.this, main.class));
            }
        });
    }
    private void addListenerOnReadMoreButton() {
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
