package com.example.laundrosense;
import com.example.laundrosense.ValueActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class main extends AppCompatActivity {

    int id = 000;
    private TextView stage_name;
    private TextView progressValue;
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

    private final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
    private ValueActivity valueActivity = new ValueActivity();

    public static void main(String[] args) {
        new main().main();
    }

    public void main() {

        // we need those for creating new instances later
        final Attribute ax = new Attribute("acclX");
        final Attribute ay = new Attribute("acclY");
        final Attribute az = new Attribute("acclZ");
        final Attribute gy = new Attribute("gyroY");
        final Attribute gx = new Attribute("gyroZ");
        final Attribute gz = new Attribute("gyroX");
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
        ArrayList<Attribute> attributeList = new ArrayList<Attribute>(2) {
            {
                add(ax);
                add(ay);
                add(az);
                add(gy);
                add(gx);
                add(gz);
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
                setValue(ax, valueActivity.particleValues.get(0));
                setValue(ay, valueActivity.particleValues.get(1));
                setValue(az, valueActivity.particleValues.get(2));
                setValue(gx, valueActivity.particleValues.get(3));
                setValue(gy, valueActivity.particleValues.get(4));
                setValue(gz, valueActivity.particleValues.get(5));
            }
        };
        // instance to use in prediction
        DenseInstance newInstance = newInstanceStage;

        // reference to dataset
        newInstance.setDataset(dataUnpredicted);

        // import ready trained model
        Classifier cls = null;
        try {
            cls = (Classifier) weka.core.SerializationHelper
                    .read("/home/pirius/iris_model_logistic_allfeatures.model");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cls == null)
            return;

        // predict new sample
        try {
            double result = cls.classifyInstance(newInstance);

            System.out.println("Index of predicted class label: " + result + ", which corresponds to class: " + classes.get(new Double(result).intValue()));
            String predictedClass = classes.get(new Double(result).intValue());
                if (predictedClass == "Sensing") {
                    stage_name.setText("Sensing");
                    if (sensingCount == 0) {
                        sendNotification("Sensing stage");
                        progressBar.setMax(5);
                        progressBar.setProgress(1);
                    }
                    sensingCount ++;
                } else if (predictedClass == "Wash") {
                    stage_name.setText("Wash");
                    if (washCount == 0) {
                        sendNotification("In Washing Stage");
                        progressBar.setProgress(2);
                    }
                    washCount ++;

                } else if (predictedClass == "Rinse") {
                    stage_name.setText("Rinse");
                    if (rinseCount == 0) {
                        sendNotification("Rinsing Now");
                        progressBar.setProgress(3);
                    }
                    washCount ++;
                } else if (predictedClass == "Spin") {
                    stage_name.setText("Spin");
                    if (spinCount == 0 ) {
                        sendNotification("In Spin Stage");
                        progressBar.setProgress(4);
                    }
                    spinCount ++;
                } else if (predictedClass == "Done") {
                    stage_name.setText("Done");
                    if (doneCount == 0) {
                        sendNotification("Washing machine done!");
                        progressBar.setProgress(5);
                    }
                    doneCount ++;
                } else if (predictedClass == "Dry") {
                    stage_name.setText("Dry");
                    if (dryingCount == 0) {
                        sendNotification("Drying");
                        progressBar.setMax(2);
                        progressBar.setProgress((1));
                    }
                    dryingCount ++;
                } else if (predictedClass == "Off") {
                    stage_name.setText("Off");
                    if (offCount == 0) {
                        sendNotification("Dryer is finished!");
                        progressBar.setProgress((2));
                    }
                    doneCount ++;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(String content) {

        //Get an instance of NotificationManager//


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("LaundroSense")
                        .setContentText(content);

        // Gets an instance of the NotificationManager service/
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
