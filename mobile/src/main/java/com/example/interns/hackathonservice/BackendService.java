package com.example.interns.hackathonservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackendService extends Service {

    private static final String TAG = "BackendService";

    private boolean isRunning  = false;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {


                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.
                /*for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }

                    if(isRunning){
                        Log.i(TAG, "Service running");
                    }
                }*/

                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

                scheduler.scheduleAtFixedRate (new Runnable() {
                    public void run() {
                        //startService(intent);
                        NotificationSender();

                    }
                }, 0, 20, TimeUnit.SECONDS);

                System.out.println("hi" + SystemClock.currentThreadTimeMillis());

                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }

    public void NotificationSender(){
        System.out.println("asdf" + SystemClock.currentThreadTimeMillis());
        NotificationBuilder n = new NotificationBuilder(this, new Intent(BackendService.this, NotificationBuilder.class));
        n.NewNotification("asdf");
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }
}
