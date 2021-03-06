/*
 * *
 *  * Created by DemonApps on 24.04.21 0:37
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 24.04.21 0:37
 *
 */

package ru.demonapps.getdima;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainArhiv extends AppCompatActivity {
    //private static final String TAG = "MyApps";
    private ListView listIspolneno;
    private List<Zadacha> listTemp;
    private DatabaseReference mDataBase;
    ArrayList<Zadacha> taskExecuted = new ArrayList<>();
    MyTaskAdapter myTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arhiv);

        init();
        getDataFromDB();
        setOnClickItem();
    }

    private void init() {
        listIspolneno = findViewById(R.id.listIspolneno);
        listTemp = new ArrayList<>();
        myTaskAdapter = new MyTaskAdapter(this, taskExecuted);
        String NEWS_KEY = "Executed";
        mDataBase = FirebaseDatabase.getInstance().getReference(NEWS_KEY);
    }

    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (taskExecuted.size() > 0) taskExecuted.clear();
                if (listTemp.size() > 0) listTemp.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Zadacha zadacha = ds.getValue(Zadacha.class);
                    assert zadacha != null;
                    taskExecuted.add(0, zadacha);
                    listTemp.add(0, zadacha);
                }
                listIspolneno.setAdapter(myTaskAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDataBase.addValueEventListener(vListener);
    }

    private void setOnClickItem() {
        listIspolneno.setOnItemClickListener((parent, view, position, id) -> {
            Zadacha zadacha = listTemp.get(position);
            Intent i = new Intent(MainArhiv.this, ShowActivity.class);
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

        getMenuInflater().inflate(R.menu.arhiv_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.vrabote_menu) {
            Intent intent1 = new Intent(MainArhiv.this, MainActivity.class);
            startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
