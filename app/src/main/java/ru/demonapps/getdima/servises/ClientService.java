/*
 * *
 *  * Created by DemonApps on 24.04.21 0:37
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 23.03.21 21:17
 *
 */

package ru.demonapps.getdima.servises;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import ru.demonapps.getdima.Constant;
import ru.demonapps.getdima.MainArhiv;
import ru.demonapps.getdima.R;
import ru.demonapps.getdima.Zadacha;

public class ClientService extends Service {
    final String FILENAME = "lasttaskexecut";
    private static final String TAG = "MyApps";
    private static String lastTask;
    Timer mTimer;
    MyTimerTask mMyTimerTask;
    DatabaseReference mDataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mDataBase = FirebaseDatabase.getInstance().getReference("Task");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Запущен ClientService");

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 60000, 300000);

        return MyService.START_STICKY;
    }


    public ClientService() {
        Log.d(TAG, "Работает ClientService");
    }

    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            //Проверка подключения
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                    Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED) {
                //Если есть интернет
                // Здесь трудоемкие задачи переносятся в дочерний поток.
                Log.i(TAG, "фон");
                LastTaskClass lastTaskClass = new LastTaskClass();
                Thread thread = new Thread(lastTaskClass);
                thread.start();
            } else {
                //Если нет  интернета
                Log.i(TAG, "Нет сети");
            }

        }
    }

    class LastTaskClass implements Runnable {
        private String oldTask;

        @Override
        public void run() {

            // открываем поток для чтения
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(
                        openFileInput(FILENAME)));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }

            // читаем содержимое
            while (true) {
                String oldTask1 = "";
                try {
                    if (br != null) {
                        if ((oldTask1 = br.readLine()) == null) break;
                    }
                    else{
                        writeFile("Пусть будет не пусто...");
                        Log.d(TAG,  "файла нет записали null");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Log.d(TAG, oldTask1 + "(прочитано)");
                oldTask = oldTask1;
            }


            DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
            Query lastQuery = mDataBase.child("Executed").orderByKey().limitToLast(1);
            lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Zadacha objTask = data.getValue(Zadacha.class);
                        assert objTask != null;
                        Log.i(TAG, data.getKey() + " = " + objTask.title);
                        lastTask = objTask.title;
                        Log.i(TAG, "lastTask - в onDataChange " + lastTask);
                        //Отправка сообщения...
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Log.i(TAG, "lastTask  перед сообщением-" + lastTask);
                            if (lastTask != null) {
                                if (Objects.equals(oldTask, lastTask)) {
                                    Log.i(TAG, "Нет Новостей");
                                } else {
                                    Log.i(TAG, "oldNews - " + oldTask);
                                    Log.i(TAG, "Сообщение отправка");
                                    // Create an explicit intent for an Activity in your app
                                    Intent intent = new Intent(getApplicationContext(), MainArhiv.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                                    Log.d(TAG, lastTask + "(сообщение)");
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), Constant.CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_stat_name)
                                            .setTicker("Задание")
                                            .setContentTitle("Задание выполнено")
                                            .setContentText(lastTask)
                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                    .bigText(lastTask))
                                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                                    R.drawable.zastavka))
                                            .setPriority(NotificationCompat.PRIORITY_MAX)
                                            // Set the intent that will fire when the user taps the notification
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true);
                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                    // notificationId is a unique int for each notification that you must define
                                    int notificationId = 4454;
                                    notificationManager.notify(notificationId, builder.build());
                                    writeFile(lastTask);
                                }
                            }
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Handle possible errors.
                }
            });
        }

        public void writeFile(String lastTask) {
            try {
                // отрываем поток для записи
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                        openFileOutput(FILENAME, MODE_PRIVATE)));
                // пишем данные
                bw.write(lastTask);
                // закрываем поток
                bw.close();
                Log.d(TAG, "Файл записан");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}