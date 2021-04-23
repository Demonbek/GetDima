/*
 * *
 *  * Created by DemonApps on 24.04.21 0:37
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 24.04.21 0:37
 *
 */

package ru.demonapps.getdima;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTask extends AppCompatActivity {
    private static final String TAG = "MyApp" ;
    private EditText editTitle;
    private EditText editZaeb;
    private DatabaseReference mDataBase;
    final String FILENAME = "uid_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        init();

    }

    private void init() {
        editTitle = findViewById(R.id.editTitle);
        editZaeb = findViewById(R.id.editZaeb);
        String NEWS_KEY = "Task";
        mDataBase = FirebaseDatabase.getInstance().getReference(NEWS_KEY);
    }

    public void onClickSave(View view) {
        String id = mDataBase.getKey();
        Date dateNow = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatForDateNow = new SimpleDateFormat("E, dd.MM.yyyy, HH:mm");
        String date = formatForDateNow.format(dateNow);
        String title = editTitle.getText().toString();
        String zaeb = editZaeb.getText().toString();
        String autor = readFile();
        String ispolneno = "В работе...";
        Zadacha newZadacha = new Zadacha(id, date, title, zaeb, autor, ispolneno);

        if ( (!TextUtils.isEmpty(title)) && (!TextUtils.isEmpty(zaeb)) && (!TextUtils.isEmpty(autor))) {
            mDataBase.push().setValue(newZadacha);
            editTitle.setText(null);
            editZaeb.setText(null);
            Toast.makeText(this, "Задача добавлена... ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Не поля заполнены...", Toast.LENGTH_LONG).show();
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

    String readFile() {
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(TAG, str+" Прочитано из файла");
                return str;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}