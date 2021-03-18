package ru.demonapps.getdima;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Objects;

import ru.demonapps.getdima.servises.ClientService;
import ru.demonapps.getdima.servises.MyService;

public class Login extends AppCompatActivity {
    private static final String TAG = "MyApps";
    private EditText edLogin, edPassword;
    private FirebaseAuth mAuth;
    final String FILENAME = "uid_user";
    public static String uid;
    private DatabaseReference mDataBase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser = mAuth.getCurrentUser();

        if (cUser != null) {
            Toast.makeText(this, "Привет", Toast.LENGTH_LONG).show();
            uid = mAuth.getUid();
            writeFile();
            Log.d(TAG, " Записан при входе");
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "User null", Toast.LENGTH_LONG).show();
        }
    }

    private void init() {
        edLogin = findViewById(R.id.edLogin);
        edPassword = findViewById(R.id.edPassword);
        mAuth = FirebaseAuth.getInstance();
        String NEWS_KEY = "Uid";
        mDataBase = FirebaseDatabase.getInstance().getReference(NEWS_KEY);
    }

    public void OnClickSignUp(View view) {
        if (!TextUtils.isEmpty(edLogin.getText().toString()) && !TextUtils.isEmpty(edPassword.getText().toString())) {
            mAuth.createUserWithEmailAndPassword(edLogin.getText().toString(), edPassword.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Пользователь зарегистрировался", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка регистрации", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Заполните все поля...", Toast.LENGTH_LONG).show();
        }
    }

    public void OnClickSignIn(View view) {
        if (!TextUtils.isEmpty(edLogin.getText().toString()) && !TextUtils.isEmpty(edPassword.getText().toString())) {
            mAuth.signInWithEmailAndPassword(edLogin.getText().toString(), edPassword.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Вход выполнен", Toast.LENGTH_LONG).show();
                    writeFile();
                    Log.d(TAG, uid + " Записан по логину");
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка входа", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void writeFile() {
        String uid = mAuth.getUid();
        assert uid != null;
        mDataBase.child(uid).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                String autor = String.valueOf(Objects.requireNonNull(task.getResult()).getValue());

                //Запускаем сервисы
                if (autor.equals("Дмитрий Л.")){
                    startService(new Intent(this, MyService.class));
                }
                else {
                   startService(new Intent(this, ClientService.class));
                }
                // отрываем поток для записи
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new OutputStreamWriter(
                            openFileOutput(FILENAME, MODE_PRIVATE)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // пишем данные
                try {
                    assert bw != null;
                    bw.write(autor);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // закрываем поток
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, (autor + "Файл записан"));
                Log.d(TAG, autor +" autor");
            }
        });


    }


}
