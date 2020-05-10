package com.example.laundrosense;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.renderscript.Sampler;
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

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron.*;
import weka.classifiers.functions.*;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.*;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH;


public class MainActivity extends AppCompatActivity {


    private ValueActivity valueActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        valueActivity = new ValueActivity();

        doSomethingRepeatedly();
    }

    private void doSomethingRepeatedly() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            public void run() {
                //valueActivity = new ValueActivity();
                // we need those for creating new instances later


            }
        }, 0, 1000);
    }
    
    }

