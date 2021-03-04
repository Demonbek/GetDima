package ru.demonapps.getdima;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText edLogin, edPassword;
    private FirebaseAuth mAuth;

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
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка входа", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
