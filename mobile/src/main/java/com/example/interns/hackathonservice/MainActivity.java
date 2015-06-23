package com.example.interns.hackathonservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(MainActivity.this, BackendService.class);
        Intent intent = new Intent(MainActivity.this, BackendService.class);
        startService(intent);

        /*ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate (new Runnable() {
            public void run() {
                //startService(intent);
                NotificationSender();

            }
        }, 0, 20, TimeUnit.SECONDS);*/
    }

    public void NotificationSender(){
        System.out.println("asdf" + SystemClock.currentThreadTimeMillis());
        NotificationBuilder n = new NotificationBuilder(this, new Intent(MainActivity.this, NotificationBuilder.class));
        n.NewNotification("asdf");
    }


}
