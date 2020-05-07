package com.example.laundrosense;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

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
    public int ax = 0;
    public int ay = 0;
    public int az = 0;
    public int gx = 0;
    public int gy = 0;
    public int gz = 0;
    List<Integer> particleValues = new ArrayList<>();

    private final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

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

                        int particleax;
                        int particleay;
                        int particleaz;
                        int particlegx;
                        int particlegy;
                        int particlegz;
                        String variable;
                        particleValues.clear();

                        try {
                            particleax = device.getIntVariable("ax"); //change
                            particleValues.add(particleax);
                            particleay = device.getIntVariable("ay");
                            particleValues.add(particleay);//change
                            particleaz = device.getIntVariable("az");
                            particleValues.add(particleaz);//change
                            particlegx = device.getIntVariable("gx");
                            particleValues.add(particlegx);//change
                            particlegy = device.getIntVariable("gy");
                            particleValues.add(particlegy);
                            particlegz = device.getIntVariable("gz");
                            particleValues.add(particlegz);


                            Log.d("TAG", "Successfully pulled values and added to array");

                        } catch (ParticleDevice.VariableDoesNotExistException e) {
                            Toaster.l(ValueActivity.this, e.getMessage());
                            variable = "Can't Get Device Info";
                        }
                        return particleValues;
                    }

                    @Override
                    public void onSuccess(@NonNull Object i) { // this goes on the main thread
                    //include logic of getting variables in here and counting/checking on them

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
