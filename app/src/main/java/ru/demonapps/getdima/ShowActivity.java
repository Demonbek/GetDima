package ru.demonapps.getdima;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class ShowActivity extends AppCompatActivity {
    private TextView showDate, showTitle, showZaeb, showAutor, showIspolneno;
    private String NEWS_KEY = "Task";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);
        init();
        getIntentMain();
    }

    private void init() {
        showDate = findViewById(R.id.showDate);
        showTitle = findViewById(R.id.showTitle);
        showZaeb = findViewById(R.id.showZaeb);
        showAutor = findViewById(R.id.showAutor);
        showIspolneno = findViewById(R.id.showIspolneno);

    }

    private void getIntentMain() {
        Intent i = getIntent();
        if (i != null) {
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
    }
}

