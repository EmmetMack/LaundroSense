package com.example.laundrosense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class ValueActivity extends AppCompatActivity {

    // private static final String ARG_VALUE = "ARG_VALUE";
    private static final String ARG_DEVICEID = "e00fce68ae329b6376267a66"; //change for specific device
    private TextView stage_name;
    private TextView progressValue;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ParticleCloudSDK.init(this); //init cloud

        setContentView(R.layout.activity_main);
        stage_name = findViewById(R.id.stage_value);
        progressValue = findViewById(R.id.estimated_time);

        doSomethingRepeatedly();

    }

    private void doSomethingRepeatedly() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            public void run() {
                Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

                    @Override
                    public Object callApi(@NonNull ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                        ParticleCloudSDK.getCloud().logIn("emack@andrew.cmu.edu", "Emack101!"); //change for specific device
                        ParticleDevice device = ParticleCloud.getDevice(ARG_DEVICEID);

                        String variable;


                        try {
                            variable = device.getStringVariable("output"); //change
                            Log.d("TAG", "variable: " + variable);

                        } catch (ParticleDevice.VariableDoesNotExistException e) {
                            Toaster.l(ValueActivity.this, e.getMessage());
                            variable = "Can't Get Device Info";
                        }
                        return variable;
                    }

                    @Override
                    public void onSuccess(@NonNull Object i) { // this goes on the main thread
                    //include logic of getting variables in here and counting/checking on them


                            stage_name.setText(i.toString());

                    }

                    @Override
                    public void onFailure(@NonNull ParticleCloudException e) {
                        e.printStackTrace();
                    }
                });

            }
        }, 0, 500);
    }
}
