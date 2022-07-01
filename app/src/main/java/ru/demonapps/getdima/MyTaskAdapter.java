/*
 * *
 *  * Created by DemonApps on 01.07.2022, 23:15
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 01.07.2022, 21:28
 *
 */

package ru.demonapps.getdima;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;


public class MyTaskAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Zadacha> objects;

    MyTaskAdapter(Context context, ArrayList<Zadacha> task) {
        ctx = context;
        objects = task;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.task_item, parent, false);
        }

        Zadacha n = getZadacha(position);

        // заполняем View в пункте списка данными из задачи: дата, наименование, автор
        ((TextView) view.findViewById(R.id.taskData)).setText(n.date);
        ((TextView) view.findViewById(R.id.taskTitle)).setText(n.title);

        return view;
    }

    // Задание по позиции
    Zadacha getZadacha(int position) {
        return ((Zadacha) getItem(position));
    }
}
