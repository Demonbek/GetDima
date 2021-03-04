package ru.demonapps.getdima;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTask extends AppCompatActivity {
    private EditText editTitle;
    private EditText editZaeb;
    private EditText editAutor;
    private DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        init();

    }

    private void init() {
        editTitle = findViewById(R.id.editTitle);
        editZaeb = findViewById(R.id.editZaeb);
        editAutor = findViewById(R.id.editAutor);
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
        String autor = editAutor.getText().toString();
        String ispolneno = "В работе...";
        Zadacha newZadacha = new Zadacha(id, date, title, zaeb, autor, ispolneno);

        if ( (!TextUtils.isEmpty(title)) && (!TextUtils.isEmpty(zaeb)) && (!TextUtils.isEmpty(autor))) {
            mDataBase.push().setValue(newZadacha);
            editTitle.setText(null);
            editZaeb.setText(null);
            editAutor.setText(null);
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
            editAutor.setText(null);
            Toast.makeText(this, "Поля очищены...", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}