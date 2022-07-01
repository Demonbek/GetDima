/*
 * *
 *  * Created by DemonApps on 01.07.2022, 23:15
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 01.07.2022, 23:13
 *
 */

package ru.demonapps.getdima;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
    private static final String TASK_TABLE = "task";
    private static final String TAG = "MyApps";
    private ListView listVrabote;
    private ImageView imageView;
    private List<Zadacha> listTemp;
    ArrayList<Zadacha> task = new ArrayList<>();
    MyTaskAdapter myTaskAdapter;
    SQLiteDatabase db;
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
    }

    private void init() {
        listVrabote = findViewById(R.id.listVrabote);
        imageView = findViewById(R.id.imageView);
        listTemp = new ArrayList<>();
        myTaskAdapter = new MyTaskAdapter(this, task);
        //Открываем базу данных
        db = getBaseContext().openOrCreateDatabase("tasks.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TASK_TABLE + " (date TEXT, title TEXT, zaeb TEXT, ispolneno TEXT, UNIQUE(title))");
    }

    private void getDataFromDB() {
        if (task.size() > 0) task.clear();
        if (listTemp.size() > 0) listTemp.clear();
        Cursor query = db.rawQuery("SELECT * FROM task;", null);
        while (query.moveToNext()) {
            String date = query.getString(0);
            String title = query.getString(1);
            String zaeb = query.getString(2);
            String ispolneno = query.getString(3);
            Zadacha zadacha = new Zadacha(date, title, zaeb, ispolneno);
            task.add(zadacha);
            listTemp.add(zadacha);
        }
        if(listTemp.size()==0) imageView.setVisibility(View.VISIBLE);
        listVrabote.setAdapter(myTaskAdapter);
        query.close();
        db.close();
    }

    private void setOnClickItem() {
        listVrabote.setOnItemClickListener((parent, view, position, id) -> {
            Zadacha zadacha = listTemp.get(position);
            Intent i = new Intent(MainActivity.this, ShowActivity.class);
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
}
