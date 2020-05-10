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
    // edit to connect to different devices
    private static final String ARG_DEVICEID = "e00fce6883a68891f704eabb";
    private static final String USR_NAME = "ntweir@andrew.cmu.edu";
    private static final String PASSWORD = "YAL2qFOJpKZKxn4V38#J&fi!5%29SaR6cVFN0^5Lb*8tb84cXn*Xi#e^Ebsshxgg";
    public int ax = 0;
    public int ay = 0;
    public int az = 0;
    public int gx = 0;
    public int gy = 0;
    public int gz = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ParticleCloudSDK.init(this); //init cloud

        setContentView(R.layout.activity_main);
        doSomethingRepeatedly();

    }

    private void doSomethingRepeatedly() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            public void run() {
                Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
                    String variable;
                    @Override
                    public Object callApi(@NonNull ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                        ParticleCloudSDK.getCloud().logIn(USR_NAME, PASSWORD);
                        ParticleDevice device = ParticleCloud.getDevice(ARG_DEVICEID);

                        try {
                            ax = device.getIntVariable("ax"); //change

                            ay = device.getIntVariable("ay");

                            az = device.getIntVariable("az");
                            gx = device.getIntVariable("gx");

                            gy = device.getIntVariable("gy");

                            gz = device.getIntVariable("gz");


                            Log.d("TAG", "Successfully pulled values");

                        } catch (ParticleDevice.VariableDoesNotExistException e) {
                            Toaster.l(ValueActivity.this, e.getMessage());
                            variable = "Can't Get Device Info";
                        }
                        return variable;
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
        }, 0, 1000);
    }
}
