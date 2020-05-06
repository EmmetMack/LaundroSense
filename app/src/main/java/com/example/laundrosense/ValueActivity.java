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
    private int count = 0;
    int id = 000;
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
                        if (i.toString() == "Sensing") {
                            stage_name.setText("Sensing");
                            sendNotification("In Sensing stage" );
                            progressBar.setMax(5);
                            progressBar.setProgress(1);
                        } else if (i.toString() == "Wash") {
                            stage_name.setText("Wash");
                            sendNotification("In Washing Stage" );
                            progressBar.setProgress(2);
                        } else if (i.toString() == "Rinse") {
                            stage_name.setText("Rinse");
                            sendNotification("In Rinse stage" );
                            progressBar.setProgress(3);
                        } else if (i.toString() == "Spin") {
                            stage_name.setText("Spin");
                            sendNotification("In Spin stage" );
                            progressBar.setProgress(4);
                        } else if (i.toString() == "Done") {
                            stage_name.setText("Done");
                            sendNotification("Washing machine done" );
                            progressBar.setProgress(5);
                        } else if (i.toString() == "Dry") {
                            stage_name.setText("Dry");
                            sendNotification("Drying" );
                            progressBar.setMax(2);
                            progressBar.setProgress((1));
                        } else if (i.toString() == "Off") {
                            stage_name.setText("Off");
                            sendNotification("Dryer finished ");
                            progressBar.setProgress((2));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ParticleCloudException e) {
                        e.printStackTrace();
                    }
                });

            }
        }, 0, 500);
    }

    public void sendNotification(String content) {

        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("LaundroSense")
                        .setContentText(content);


        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//

//            NotificationManager.notify();

        id++;
        mNotificationManager.notify(id, mBuilder.build());
    }
}
