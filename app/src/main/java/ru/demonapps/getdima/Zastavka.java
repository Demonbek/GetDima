/*
 * *
 *  * Created by DemonApps on 24.04.21 0:37
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 23.03.21 21:17
 *
 */

package ru.demonapps.getdima;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Zastavka extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zastavka);

        //Создаем новый поток:
        Thread splash_time = new Thread()
        {
            public void run()
            {
                try
                {
                    //Целое значение время отображения картинки:
                    int SplashTimer = 0;
                    //Запускаем цикл длиной в 3 секунды:
                    while(SplashTimer < 3000) {
                        sleep(100);
                        SplashTimer = SplashTimer +100;
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace(); }
                finally {
                    //Закрываем activity:
                    finish();
                    startActivity(new Intent(Zastavka.this, Login.class));
                }
            }
        };
        splash_time.start();

    }
}

