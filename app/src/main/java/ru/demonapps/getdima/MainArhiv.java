/*
 * *
 *  * Created by DemonApps on 01.07.2022, 23:15
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 01.07.2022, 23:02
 *
 */

package ru.demonapps.getdima;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private static final String TAG = "MyApps";
    private ListView listIspolneno;
    private List<Zadacha> listTemp;
    ArrayList<Zadacha> taskExecuted = new ArrayList<>();
    MyTaskAdapter myTaskAdapter;
    SQLiteDatabase db;
    String TASK_TABLE = "executed";
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
        //Открываем базу данных
        db = getBaseContext().openOrCreateDatabase("tasks.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TASK_TABLE + " (date TEXT, title TEXT, zaeb TEXT, ispolneno TEXT, UNIQUE(title))");
    }

    private void getDataFromDB() {
        if (taskExecuted.size() > 0) taskExecuted.clear();
        if (listTemp.size() > 0) listTemp.clear();
        Cursor query = db.rawQuery("SELECT * FROM executed;", null);
        while (query.moveToNext()) {
            String date = query.getString(0);
            String title = query.getString(1);
            String zaeb = query.getString(2);
            String ispolneno = query.getString(3);
            Zadacha zadacha = new Zadacha(date, title, zaeb, ispolneno);
            taskExecuted.add(zadacha);
            listTemp.add(zadacha);
        }
        listIspolneno.setAdapter(myTaskAdapter);
        query.close();
        db.close();
    }

    private void setOnClickItem() {
        listIspolneno.setOnItemClickListener((parent, view, position, id) -> {
            Zadacha zadacha = listTemp.get(position);
            Intent i = new Intent(MainArhiv.this, ShowActivity.class);
            i.putExtra(Constant.TASK_DATE, zadacha.date);
            i.putExtra(Constant.TASK_TITLE, zadacha.title);
            i.putExtra(Constant.TASK_ZAEB, zadacha.zaeb);
            i.putExtra(Constant.TASK_ISPOLNENO, zadacha.ispolneno);
            i.putExtra(Constant.TASK_TABLE, TASK_TABLE);
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
