package com.example.laundrosense;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
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
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class ValueActivity extends AppCompatActivity {

    // private static final String ARG_VALUE = "ARG_VALUE";
    public int particleax = 0;
    public int particleay = 0;
    public int particleaz = 0;
    public int particlegx = 0;
    public int particlegy = 0;
    public int particlegz = 0;

    private ProgressBar progressBar;
    private TextView stage_name;
    private TextView progressValue;

    int id = 000;
    int timeRemaining;
    private int sensingCount = 0;
    private int washCount = 0;
    private int rinseCount = 0;
    private int spinCount = 0;
    private int doneCount = 0;
    private int dryingCount = 0;
    private int offCount = 0;
    // baselines all in seconds
    private int senseBaseline = 484;
    private int washBaseline = 1939;
    private int rinseBaseline = 609;
    private int spinBaseline = 659;

    // edit to connect to different devices
    private static final String ARG_DEVICEID = "e00fce6883a68891f704eabb";
    private static final String USR_NAME = "ntweir@andrew.cmu.edu";
    private static final String PASSWORD = "YAL2qFOJpKZKxn4V38#J&fi!5%29SaR6cVFN0^5Lb*8tb84cXn*Xi#e^Ebsshxgg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ParticleCloudSDK.init(this); //init cloud

        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressValue = findViewById(R.id.estimated_time);
        stage_name = findViewById((R.id.stage_value));
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
                            particleax = device.getIntVariable("ax"); //change

                            particleay = device.getIntVariable("ay");

                            particleaz = device.getIntVariable("az");
                            particlegx = device.getIntVariable("gx");

                            particlegy = device.getIntVariable("gy");

                            particlegz = device.getIntVariable("gz");


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
                        final Attribute ax = new Attribute("acclX");
                        final Attribute ax2 = new Attribute("acclX2");
                        final Attribute ax3 = new Attribute("acclX3");
                        final Attribute ay = new Attribute("acclY");
                        final Attribute ay2 = new Attribute("acclY2");
                        final Attribute ay3 = new Attribute("acclY3");
                        final Attribute az = new Attribute("acclZ");
                        final Attribute az2 = new Attribute("acclZ2");
                        final Attribute az3 = new Attribute("acclZ3");
                        final Attribute gy = new Attribute("gyroY");
                        final Attribute gy2 = new Attribute("gyroY2");
                        final Attribute gy3 = new Attribute("gyroY3");
                        final Attribute gx = new Attribute("gyroZ");
                        final Attribute gx2 = new Attribute("gyroZ2");
                        final Attribute gx3 = new Attribute("gyroZ3");
                        final Attribute gz = new Attribute("gyroX");
                        final Attribute gz2 = new Attribute("gyroX2");
                        final Attribute gz3 = new Attribute("gyroX3");
                        final List<String> classes = new ArrayList<String>() {
                            {
                                add("sense");
                                add("wash");
                                add("rinse");
                                add("spin");
                                add("done");
                                add("drying");
                                add("off");
                            }
                        };

                        // Instances(...) requires ArrayList<> instead of List<>...
                        ArrayList<Attribute> attributeList = new ArrayList<Attribute>(1) {
                            {
                                add(ax);
                                add(ax2);
                                add(ax3);
                                add(ay);
                                add(ay2);
                                add(ay3);
                                add(az);
                                add(az2);
                                add(az3);
                                add(gy);
                                add(gy2);
                                add(gy3);
                                add(gx);
                                add(gx2);
                                add(gx3);
                                add(gz);
                                add(gz2);
                                add(gz3);
                                Attribute attributeClass = new Attribute("@@class@@", classes);
                                add(attributeClass);
                            }
                        };
                        // unpredicted data sets (reference to sample structure for new instances)
                        Instances dataUnpredicted = new Instances("TestInstances",
                                attributeList, 1);
                        // last feature is target variable
                        dataUnpredicted.setClassIndex(dataUnpredicted.numAttributes() - 1);

                        // create new instance: this one should fall into the setosa domain
                        DenseInstance newInstanceStage = new DenseInstance(dataUnpredicted.numAttributes()) {
                            {
                                setValue(ax, particleax);
                                setValue(ax2, Math.pow(particleax,2));
                                setValue(ax3, Math.pow(particleax,3));
                                setValue(ay, particleay);
                                setValue(ay2, Math.pow(particleay,2));
                                setValue(ay3, Math.pow(particleay, 3));
                                setValue(az, particleaz);
                                setValue(az2, Math.pow(particleaz,2));
                                setValue(az3, Math.pow(particleaz, 3));
                                setValue(gx, particlegx);
                                setValue(gx2, Math.pow(particlegx, 2));
                                setValue(gx3, Math.pow(particlegx, 3));
                                setValue(gy, particlegy);
                                setValue(gy2, Math.pow(particlegy,2));
                                setValue(gy3, Math.pow(particlegy,3));
                                setValue(gz, particlegz);
                                setValue(gz2, Math.pow(particlegz, 2));
                                setValue(gz3, Math.pow(particlegz, 3));
                            }
                        };

                        DenseInstance newInstance = newInstanceStage;

                        // reference to dataset
                        newInstance.setDataset(dataUnpredicted);

                        RandomForest classifier = null;

                        AssetManager assetManager = getAssets();

                        try {
                            classifier = (RandomForest) weka.core.SerializationHelper.read(assetManager.open("Wash-Cycle-Model.model"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            // Weka "catch'em all!"
                            e.printStackTrace();
                        }


                        if (classifier == null)
                            return;

                        // predict new sample
                        try {
                            double result = classifier.classifyInstance(newInstance);

                            System.out.println("Index of predicted class label: " + result + ", which corresponds to class: " + classes.get(new Double(result).intValue()));
                            String predictedClass = classes.get(new Double(result).intValue());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (predictedClass == "sensing") {
                                        stage_name.setText("Sensing");
                                        if (sensingCount == 0) {
                                            sendNotification("Sensing stage");
                                            progressBar.setMax(senseBaseline);


                                        }
                                        progressBar.setProgress(sensingCount);
                                        sensingCount ++;
                                    } else if (predictedClass == "wash") {
                                        stage_name.setText("Wash");
                                        if (washCount == 0) {
                                            sendNotification("In Washing Stage");
                                            progressBar.setProgress(2);
                                        }
                                        if (washCount == 60) {
                                            washBaseline = washBaseline * sensingCount / senseBaseline;
                                            rinseBaseline = rinseBaseline * sensingCount / senseBaseline;
                                            spinBaseline = spinBaseline * sensingCount / senseBaseline;
                                        }
                                        washCount ++;

                                    } else if (predictedClass == "rinse") {
                                        stage_name.setText("Rinse");
                                        if (rinseCount == 0) {
                                            sendNotification("Rinsing Now");
                                            progressBar.setProgress(3);
                                        }
                                        if (rinseCount == 60) {
                                            rinseBaseline = rinseBaseline * washCount / washBaseline;
                                            spinBaseline = spinBaseline * washCount / washBaseline;
                                        }

                                        rinseCount ++;
                                    } else if (predictedClass == "spin") {
                                        stage_name.setText("Spin");
                                        if (spinCount == 0 ) {
                                            sendNotification("In Spin Stage");
                                            progressBar.setProgress(4);
                                        }
                                        if (spinCount == 60) {
                                            spinBaseline = spinBaseline * rinseCount / rinseBaseline;
                                        }
                                        spinCount ++;
                                    } else if (predictedClass == "done") {
                                        stage_name.setText("Done");
                                        if (doneCount == 0) {
                                            sendNotification("Washing machine done!");
                                            progressBar.setProgress(5);
                                        }
                                        doneCount ++;
                                    } else if (predictedClass == "dry") {
                                        stage_name.setText("Dry");
                                        if (dryingCount == 0) {
                                            sendNotification("Drying");
                                            progressBar.setMax(2);
                                            progressBar.setProgress((1));
                                        }
                                        dryingCount ++;
                                    } else if (predictedClass == "off") {
                                        stage_name.setText("Off");
                                        if (offCount == 0) {
                                            sendNotification("Dryer is finished!");
                                            progressBar.setProgress((2));
                                        }
                                        doneCount ++;
                                    }
                                    // in minutes
                                    timeRemaining = ((senseBaseline + washBaseline + rinseBaseline + spinBaseline)
                                            - (sensingCount + washCount + rinseCount + spinCount)) / 60;
                                    progressValue.setText(timeRemaining+" minutes");
                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ParticleCloudException e) {
                        e.printStackTrace();
                    }
                });

            }
        }, 0, 1000);
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
