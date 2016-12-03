package com.example.lgx.pypi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Jiwon on 2016-12-02.
 */
public class SettingsActivity extends AppCompatActivity {

    CommunicationManager communicationManager = new CommunicationManager();
    Button connectButton;
    Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        communicationManager.bindService( this );

        connectButton = (Button)findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicationManager.connect();
            }
        });

        testButton = (Button)findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicationManager.send("test", getApplicationContext() );
            }
        });
    }

}
