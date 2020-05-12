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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class ValueActivity extends AppCompatActivity {

    // private static final String ARG_VALUE = "ARG_VALUE";
    private int particleax;
    private int particleay;
    private int particleaz;
    private int particlegx;
    private int particlegy;
    private int particlegz;

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

    private DescriptiveStatistics accelX = new DescriptiveStatistics(10);
    private DescriptiveStatistics accelY = new DescriptiveStatistics(10);
    private DescriptiveStatistics accelZ = new DescriptiveStatistics(10);
    private DescriptiveStatistics gyroX = new DescriptiveStatistics(10);
    private DescriptiveStatistics gyroY = new DescriptiveStatistics(10);
    private DescriptiveStatistics gyroZ = new DescriptiveStatistics(10);

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
                            Log.d("DATA", "Beginning value pull.");

                            particleax = device.getIntVariable("ax"); //change
                            accelX.addValue(particleax);
                            Log.d("DATA", "ax: " + particleax);
                            particleay = device.getIntVariable("ay");
                            accelY.addValue(particleay);
                            Log.d("DATA", "ay: " + particleay);
                            particleaz = device.getIntVariable("az");
                            accelZ.addValue(particleaz);
                            Log.d("DATA", "az: " + particleaz);
                            particlegx = device.getIntVariable("gx");
                            gyroX.addValue(particlegx);
                            Log.d("DATA", "gx: " + particlegx);
                            particlegy = device.getIntVariable("gy");
                            gyroY.addValue(particlegy);
                            Log.d("DATA", "gy: " + particlegy);
                            particlegz = device.getIntVariable("gz");
                            gyroZ.addValue(particlegz);
                            Log.d("DATA", "gz: " + particlegz);

                            Log.d("DATA", "Successfully pulled values.");

                        } catch (ParticleDevice.VariableDoesNotExistException e) {
                            Toaster.l(ValueActivity.this, e.getMessage());
                            variable = "Can't Get Device Info";
                        }
                        return variable;
                    }

                    @Override
                    public void onSuccess(@NonNull Object i) { // this goes on the main thread
                    //include logic of getting variables in here and counting/checking on them

                        final Attribute meanax = new Attribute("meanax");
                        final Attribute maxax = new Attribute("maxax");
                        final Attribute minax = new Attribute("minax");
                        final Attribute sdax = new Attribute("sdax");
                        final Attribute kurtax = new Attribute("kurtax");
                        final Attribute ax = new Attribute("ax");


                        final Attribute meanay = new Attribute("meanay");
                        final Attribute maxay = new Attribute("maxay");
                        final Attribute minay = new Attribute("minay");
                        final Attribute sday = new Attribute("sday");
                        final Attribute kurtay = new Attribute("kurtay");
                        final Attribute ay = new Attribute("ay");


                        final Attribute meanaz = new Attribute("meanaz");
                        final Attribute maxaz = new Attribute("maxaz");
                        final Attribute minaz = new Attribute("minaz");
                        final Attribute sdaz = new Attribute("sdaz");
                        final Attribute kurtaz = new Attribute("kurtaz");
                        final Attribute az = new Attribute("az");


                        final Attribute meangy = new Attribute("meangy");
                        final Attribute maxgy = new Attribute("maxgy");
                        final Attribute mingy = new Attribute("mingy");
                        final Attribute sdgy = new Attribute("sdgy");
                        final Attribute kurtgy = new Attribute("kurtgy");
                        final Attribute gy = new Attribute("gy");

                        final Attribute meangx = new Attribute("meangx");
                        final Attribute maxgx = new Attribute("maxgx");
                        final Attribute mingx = new Attribute("mingx");
                        final Attribute sdgx = new Attribute("sdgx");
                        final Attribute kurtgx = new Attribute("kurtgx");
                        final Attribute gx = new Attribute("gx");

                        final Attribute meangz = new Attribute("meangz");
                        final Attribute maxgz = new Attribute("maxgz");
                        final Attribute mingz = new Attribute("mingz");
                        final Attribute sdgz = new Attribute("sdgz");
                        final Attribute kurtgz = new Attribute("kurtgz");
                        final Attribute gz = new Attribute("gz");

                        final List<String> classes = new ArrayList<String>() {
                            {
                                add("pre");
                                add("sense");
                                add("wash");
                                add("rinse");
                                add("spin");
                                add("done");
                            }
                        };

                        // Instances(...) requires ArrayList<> instead of List<>...
                        ArrayList<Attribute> attributeList = new ArrayList<Attribute>(1) {
                            {
                                add(ax);
                                add(minax);
                                add(maxax);
                                add(kurtax);
                                add(sdax);
                                add(meanax);

                                add(ay);
                                add(minay);
                                add(maxay);
                                add(kurtay);
                                add(sday);
                                add(meanay);

                                add(az);
                                add(minaz);
                                add(maxaz);
                                add(kurtaz);
                                add(sdaz);
                                add(meanaz);

                                add(gx);
                                add(mingx);
                                add(maxgx);
                                add(kurtgx);
                                add(sdgx);
                                add(meangx);

                                add(gy);
                                add(mingy);
                                add(maxgy);
                                add(kurtgy);
                                add(sdgy);
                                add(meangy);

                                add(gz);
                                add(mingz);
                                add(maxgz);
                                add(kurtgz);
                                add(sdgz);
                                add(meangz);

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
                        DenseInstance newInstanceStage = null;
                        if (particleax != 0 && particleay !=0 && particleaz != 0 && particlegx != 0 && particlegy != 0 && particlegz != 0) {
                             newInstanceStage = new DenseInstance(dataUnpredicted.numAttributes()) {
                                {
                                    setValue(ax, particleax);
                                    setValue(minax, accelX.getMin());
                                    setValue(maxax, accelX.getMax());
                                    setValue(meanax, accelX.getMean());
                                    setValue(sdax, accelX.getStandardDeviation());
                                    setValue(kurtax, accelX.getKurtosis());

                                    setValue(ay, particleay);
                                    setValue(minay, accelY.getMin());
                                    setValue(maxay, accelY.getMax());
                                    setValue(meanay, accelY.getMean());
                                    setValue(sday, accelY.getStandardDeviation());
                                    setValue(kurtay, accelY.getKurtosis());

                                    setValue(az, particleaz);

                                    setValue(minaz, accelZ.getMin());
                                    setValue(maxaz, accelZ.getMax());
                                    setValue(meanaz, accelZ.getMean());
                                    setValue(sdaz, accelZ.getStandardDeviation());
                                    setValue(kurtaz, accelZ.getKurtosis());

                                    setValue(gx, particlegx);
                                    setValue(mingx, gyroX.getMin());
                                    setValue(maxgx, gyroX.getMax());
                                    setValue(meangx, gyroX.getMean());
                                    setValue(sdgx, gyroX.getStandardDeviation());
                                    setValue(kurtgx, gyroX.getKurtosis());

                                    setValue(gy, particlegy);
                                    setValue(mingy, gyroY.getMin());
                                    setValue(maxgy, gyroY.getMax());
                                    setValue(meangy, gyroY.getMean());
                                    setValue(sdgy, gyroY.getStandardDeviation());
                                    setValue(kurtgy, gyroY.getKurtosis());

                                    setValue(gz, particlegz);
                                    setValue(mingz, gyroZ.getMin());
                                    setValue(maxgz, gyroZ.getMax());
                                    setValue(meangz, gyroZ.getMean());
                                    setValue(sdgz, gyroZ.getStandardDeviation());
                                    setValue(kurtgz, gyroZ.getKurtosis());

                                }
                            };
                        }

                        if (newInstanceStage == null) {
                            return;
                        }
                        DenseInstance newInstance = newInstanceStage;

                        // reference to dataset
                        newInstance.setDataset(dataUnpredicted);

                        Classifier classifier = null;

                        AssetManager assetManager = getAssets();

                        try {
                            classifier = (Classifier) weka.core.SerializationHelper.read(assetManager.open("laundrosense.model"));
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

                            String predictedClass = classes.get(new Double(result).intValue());

                            Log.d("CLASS", "Index: " + result + ", Class: " + predictedClass);


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
                                    } else if (predictedClass == "pre") {
                                        return;
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
