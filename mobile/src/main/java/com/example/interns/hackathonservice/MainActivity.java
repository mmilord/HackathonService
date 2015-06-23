package com.example.interns.hackathonservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;


public class MainActivity extends Activity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(MainActivity.this, BackendService.class);
        //Intent intent = new Intent(MainActivity.this, BackendService.class);
        //startService(intent);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate (new Runnable() {
            public void run() {
                //startService(intent);
                hello();
            }
        }, 0, 20, TimeUnit.SECONDS);
    }

    public void hello(){
        System.out.println("asdf" + SystemClock.currentThreadTimeMillis());

    }


}
