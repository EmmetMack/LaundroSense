package com.example.laundrosense;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.particle.android.sdk.utils.Toaster;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;


public class MainActivity extends AppCompatActivity {
    private main main;
    private ProgressBar progressBar;
    private TextView stage_name;
    private TextView progressValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = new main();
        doSomethingRepeatedly();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressValue = findViewById(R.id.estimated_time);
        stage_name = findViewById((R.id.current_stage));
    }

    private void doSomethingRepeatedly() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            public void run() {
                main.main(progressValue, progressBar, stage_name);
            }
        }, 0, 1000);
    }

        public void sendNotification(View view) {

            //Get an instance of NotificationManager//

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("My notification")
                            .setContentText("Hello World!");


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

            mNotificationManager.notify(001, mBuilder.build());
        }
    }

