/*
 * *
 *  * Created by DemonApps on 01.07.2022, 23:15
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 01.07.2022, 23:02
 *
 */

package ru.demonapps.getdima;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowActivity extends AppCompatActivity {
    private static final String TAG = "MyApps";
    private TextView showDate, showTitle, showZaeb, showIspolneno;
    private String TASK_TABLE;
    Button onClickIspolneno;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);
        init();
        getIntentMain();
        db = getBaseContext().openOrCreateDatabase("tasks.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TASK_TABLE + " (data TEXT, title TEXT, zaeb TEXT, ispolneno TEXT, UNIQUE(title))");
        if (TASK_TABLE.equals("executed")) onClickIspolneno.setVisibility(View.GONE);
    }

    private void init() {
        showDate = findViewById(R.id.showDate);
        showTitle = findViewById(R.id.showTitle);
        showZaeb = findViewById(R.id.showZaeb);
        showIspolneno = findViewById(R.id.showIspolneno);
        onClickIspolneno = findViewById(R.id.onClickIspolneno);
    }

    private void getIntentMain() {
        Intent i = getIntent();
        if (i != null) {
            showDate.setText(i.getStringExtra(Constant.TASK_DATE));
            showTitle.setText(i.getStringExtra(Constant.TASK_TITLE));
            showZaeb.setText(i.getStringExtra(Constant.TASK_ZAEB));
            showIspolneno.setText(i.getStringExtra(Constant.TASK_ISPOLNENO));
            TASK_TABLE = i.getStringExtra(Constant.TASK_TABLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.show_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delet_menu) {
            String title_delet = (String) showTitle.getText();
            db.delete(TASK_TABLE, "title=?", new String[]{title_delet});
            db.close();
            Toast.makeText(this, "Задание удалено...", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(ShowActivity.this, MainActivity.class);
            startActivity(intent1);

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickIspolneno(View view) {
        //Удаление из базы заданий по дате Title задачи
        String task_delet = (String) showTitle.getText();
        db.delete(TASK_TABLE, "title=?", new String[]{task_delet});
        //Запись в архив
        TASK_TABLE = "executed";
        String date = showDate.getText().toString();
        String title = showTitle.getText().toString();
        String zaeb = showZaeb.getText().toString();
        Date dateNow = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatForDateNow = new SimpleDateFormat("E, dd.MM.yyyy, HH:mm");
        String ispolneno = "Исполнено (" + formatForDateNow.format(dateNow) + ")";
        db.execSQL("INSERT OR IGNORE INTO " + TASK_TABLE + " VALUES ('" + date + "', '" + title + "', '" + zaeb + "', '" + ispolneno + "');");
        db.close();
        Intent i = new Intent(ShowActivity.this, MainActivity.class);
        startActivity(i);
    }

}


