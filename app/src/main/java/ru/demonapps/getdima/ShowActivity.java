package ru.demonapps.getdima;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ShowActivity extends AppCompatActivity {
    private static final String TAG = "MyApps";
    private TextView showDate, showTitle, showZaeb, showAutor, showIspolneno;
    private String NEWS_KEY = "Task";
    Button onClickIspolneno;
    private DatabaseReference mDataBase;
    final String FILENAME = "uid_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);
        init();
        getIntentMain();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (Objects.equals(NEWS_KEY, "Executed")) {
                onClickIspolneno.setVisibility(View.GONE);
            }
            readFile();
        }
    }

    private void init() {
        showDate = findViewById(R.id.showDate);
        showTitle = findViewById(R.id.showTitle);
        showZaeb = findViewById(R.id.showZaeb);
        showAutor = findViewById(R.id.showAutor);
        showIspolneno = findViewById(R.id.showIspolneno);
        onClickIspolneno = findViewById(R.id.onClickIspolneno);
        mDataBase = FirebaseDatabase.getInstance().getReference(NEWS_KEY);

    }

    private void getIntentMain() {
        Intent i = getIntent();
        if (i != null) {
            NEWS_KEY = i.getStringExtra(Constant.TASK_BAZA);
            showDate.setText(i.getStringExtra(Constant.TASK_DATE));
            showTitle.setText(i.getStringExtra(Constant.TASK_TITLE));
            showZaeb.setText(i.getStringExtra(Constant.TASK_ZAEB));
            showAutor.setText(i.getStringExtra(Constant.TASK_AUTOR));
            showIspolneno.setText(i.getStringExtra(Constant.TASK_ISPOLNENO));
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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query newsQuery = ref.child(NEWS_KEY).orderByChild("title").equalTo(title_delet);

            newsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot newsSnapshot : dataSnapshot.getChildren()) {
                        newsSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Toast.makeText(this, "Задание удалено...", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(ShowActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickIspolneno(View view) {
        //Удаление из базы заданий по дате постановки задачи
        String task_delet = (String) showDate.getText();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query newsQuery = ref.child(NEWS_KEY).orderByChild("date").equalTo(task_delet);

        newsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot newsSnapshot : dataSnapshot.getChildren()) {
                    newsSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Запись в архив
        NEWS_KEY = "Executed";
        mDataBase = FirebaseDatabase.getInstance().getReference(NEWS_KEY);
        String id = mDataBase.getKey();
        String date = showDate.getText().toString();
        String title = showTitle.getText().toString();
        String zaeb = showZaeb.getText().toString();
        String autor = showAutor.getText().toString();
        Date dateNow = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatForDateNow = new SimpleDateFormat("E, dd.MM.yyyy, HH:mm");
        String ispolneno = "Исполнено ("+formatForDateNow.format(dateNow)+")";
        Zadacha newZadacha = new Zadacha(id, date, title, zaeb, autor, ispolneno);
        mDataBase.push().setValue(newZadacha);
        Intent i = new Intent(ShowActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void readFile() {
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            String uid = "";
            // читаем содержимое
            while ((uid = br.readLine()) != null) {
                Log.d(TAG, uid);
                if (!uid.equals("Дмитрий Л.")) {
                    onClickIspolneno.setVisibility(View.GONE);
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


