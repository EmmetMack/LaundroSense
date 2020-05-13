package com.example.laundrosense;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.utils.Async;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class ValueActivity extends AppCompatActivity {
    String stage;
    boolean dryPath = false;

    private ProgressBar progressBar;
    private TextView stage_name;
    private TextView progressValue;

    int timeRemaining, baselineSum, activeSum;
    private int senseCount = 0;
    private int washCount = 0;
    private int rinseCount = 0;
    private int spinCount = 0;
    private int doneCount = 0;
    private int dryCount = 0;
    private int offCount = 0;
    // baselines all in seconds
    private int senseBaseline = 484;
    private int washBaseline = 1939;
    private int rinseBaseline = 609;
    private int spinBaseline = 659;
    private int dryBaseline = 4500;

    // edit to connect to different devices
    private static final String ARG_DEVICEID = "e00fce6883a68891f704eabb";
    private static final String USR_NAME = "ntweir@andrew.cmu.edu";
    private static final String PASSWORD = "YAL2qFOJpKZKxn4V38#J&fi!5%29SaR6cVFN0^5Lb*8tb84cXn*Xi#e^Ebsshxgg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressValue = findViewById(R.id.estimated_time);
        stage_name = findViewById((R.id.stage_value));

        ParticleCloudSDK.init(this); //init cloud
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, String>() {
            @Override
            public String callApi(ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                try {
                    ParticleCloudSDK.getCloud().logIn(USR_NAME, PASSWORD);
                    long subID = ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents(
                            null,
                            new ParticleEventHandler() {
                                public void onEvent(String eventName, ParticleEvent event) {
                                    stage = event.dataPayload;
                                    Log.i("EVENT", "Event payload received: " + stage);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // in minutes
                                            baselineSum = (dryPath) ? dryBaseline : senseBaseline + washBaseline + rinseBaseline + spinBaseline;
                                            activeSum = (dryPath) ? dryCount : senseCount + washCount + rinseCount + spinCount;
                                            timeRemaining = (baselineSum - activeSum) / 60;
                                            progressBar.setMax(baselineSum);
                                            progressBar.setProgress(activeSum);
                                            progressValue.setText(timeRemaining+" minutes");

                                            switch (stage) {
                                                case "sense":
                                                    stage_name.setText("Sensing");
                                                    if (senseCount == 0) {
                                                        sendNotification("Sensing stage");
                                                    }
                                                    senseCount++;
                                                    senseBaseline = (senseCount > senseBaseline) ? senseCount : senseBaseline;
                                                    break;
                                                case "wash":
                                                    stage_name.setText("Wash");
                                                    if (washCount == 0) {
                                                        sendNotification("In Washing Stage");
                                                        washBaseline = washBaseline * senseCount / senseBaseline;
                                                        rinseBaseline = rinseBaseline * senseCount / senseBaseline;
                                                        spinBaseline = spinBaseline * senseCount / senseBaseline;
                                                        senseBaseline = senseCount;
                                                    }
                                                    washCount++;
                                                    washBaseline = (washCount > washBaseline) ? washCount : washBaseline;
                                                    break;
                                                case "rinse":
                                                    stage_name.setText("Rinse");
                                                    if (rinseCount == 0) {
                                                        sendNotification("Rinsing Now");
                                                        rinseBaseline = rinseBaseline * washCount / washBaseline;
                                                        spinBaseline = spinBaseline * washCount / washBaseline;
                                                        washBaseline = washCount;
                                                    }
                                                    rinseCount++;
                                                    rinseBaseline = (rinseCount > rinseBaseline) ? rinseCount : rinseBaseline;
                                                    break;
                                                case "spin":
                                                    stage_name.setText("Spin");
                                                    if (spinCount == 0 ) {
                                                        sendNotification("In Spin Stage");
                                                        spinBaseline = spinBaseline * rinseCount / rinseBaseline;
                                                        rinseBaseline = rinseCount;
                                                    }
                                                    spinCount++;
                                                    spinBaseline = (spinCount > spinBaseline) ? spinCount : spinBaseline;
                                                    break;
                                                case "done":
                                                    stage_name.setText("Done");
                                                    if (doneCount == 0) {
                                                        sendNotification("Washing machine done!");
                                                        spinBaseline = spinCount;
                                                    }
                                                    doneCount++;
                                                    break;
                                                case "dry":
                                                    stage_name.setText("Dry");
                                                    if (dryCount == 0) {
                                                        dryPath = true;
                                                        sendNotification("Drying");
                                                    }
                                                    dryCount++;
                                                    dryBaseline = (dryCount > dryBaseline) ? dryCount : dryBaseline;
                                                    break;
                                                case "off":
                                                    stage_name.setText("Off");
                                                    if (offCount == 0) {
                                                        sendNotification("Dryer is finished!");
                                                        dryBaseline = dryCount;
                                                    }
                                                    doneCount++;
                                                    break;
                                            }
                                        }
                                    });
                                }

                                public void onEventError(Exception e) {
                                    Log.e("EVENT", "Event error: ", e);
                                }
                            });
                    ParticleCloudSDK.getCloud().logOut();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onSuccess(String value) {
                Log.i("ASYNC", "Success: " + value);
            }

            @Override
            public void onFailure(ParticleCloudException e) {
                Log.e("ASYNC", "Failure: ", e);
            }
        });
    }

    public void sendNotification(String content) {

        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("LaundroSense Notification")
                        .setContentText(content)
                        .setPriority(PRIORITY_HIGH);


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
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mNotificationManager.notify(001, mBuilder.build());

    }
}
