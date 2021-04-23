/*
 * *
 *  * Created by DemonApps on 24.04.21 0:37
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 24.04.21 0:37
 *
 */

package ru.demonapps.getdima;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = "MyApps";
    private ListView listVrabote;
    private ImageView imageView;
    private List<Zadacha> listTemp;
    private DatabaseReference mDataBase;
    ArrayList<Zadacha> task = new ArrayList<>();
    MyTaskAdapter myTaskAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Intent intent = new Intent(MainActivity.this, AddTask.class);
            startActivity(intent);
        });

        init();
        getDataFromDB();
        setOnClickItem();
        createNotificationChannel();
    }

    private void init() {
        listVrabote = findViewById(R.id.listVrabote);
        imageView = findViewById(R.id.imageView);
        listTemp = new ArrayList<>();
        myTaskAdapter = new MyTaskAdapter(this, task);
        String NEWS_KEY = "Task";
        mDataBase = FirebaseDatabase.getInstance().getReference(NEWS_KEY);
    }

    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (task.size() > 0) task.clear();
                if (listTemp.size() > 0) listTemp.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Zadacha zadacha = ds.getValue(Zadacha.class);
                    assert zadacha != null;
                    task.add(zadacha);
                    listTemp.add(zadacha);
                }
                if (task.size() == 0) imageView.setVisibility(View.VISIBLE);
                else imageView.setVisibility(View.GONE);
                listVrabote.setAdapter(myTaskAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDataBase.addValueEventListener(vListener);
    }

    private void setOnClickItem() {
        listVrabote.setOnItemClickListener((parent, view, position, id) -> {
            Zadacha zadacha = listTemp.get(position);
            Intent i = new Intent(MainActivity.this, ShowActivity.class);
            i.putExtra(Constant.TASK_BAZA, zadacha.id);
            i.putExtra(Constant.TASK_DATE, zadacha.date);
            i.putExtra(Constant.TASK_TITLE, zadacha.title);
            i.putExtra(Constant.TASK_ZAEB, zadacha.zaeb);
            i.putExtra(Constant.TASK_AUTOR, zadacha.autor);
            i.putExtra(Constant.TASK_ISPOLNENO, zadacha.ispolneno);
            startActivity(i);
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.arhiv_menu) {
            Intent intent1 = new Intent(MainActivity.this, MainArhiv.class);
            startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Constant.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
