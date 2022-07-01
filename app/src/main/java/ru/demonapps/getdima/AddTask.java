/*
 * *
 *  * Created by DemonApps on 01.07.2022, 23:15
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 01.07.2022, 22:58
 *
 */

package ru.demonapps.getdima;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTask extends AppCompatActivity {
    private static final String TAG = "MyApp" ;
    private EditText editTitle;
    private EditText editZaeb;
    private static final String TASK_TABLE = "task";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        init();

    }

    private void init() {
        editTitle = findViewById(R.id.editTitle);
        editZaeb = findViewById(R.id.editZaeb);
    }

    public void onClickSave(View view) {
        //Открываем базу данных
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("tasks.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TASK_TABLE + " (data TEXT, title TEXT, zaeb TEXT, ispolneno TEXT, UNIQUE(title))");
        Date dateNow = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatForDateNow = new SimpleDateFormat("E, dd.MM.yyyy, HH:mm");
        String date = formatForDateNow.format(dateNow);
        String title = editTitle.getText().toString();
        String zaeb = editZaeb.getText().toString();
        String ispolneno = "В работе...";

        if ( (!TextUtils.isEmpty(title)) && (!TextUtils.isEmpty(zaeb))) {
            db.execSQL("INSERT OR IGNORE INTO " + TASK_TABLE + " VALUES ('" + date + "', '" + title + "', '" + zaeb + "', '" + ispolneno + "');");
            editTitle.setText(null);
            editZaeb.setText(null);
            Toast.makeText(this, "Задача добавлена... ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Не все поля заполнены...", Toast.LENGTH_LONG).show();
        }
        Intent i = new Intent(AddTask.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.addtask_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clear_menu) {
            editTitle.setText(null);
            editZaeb.setText(null);
            Toast.makeText(this, "Поля очищены...", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}