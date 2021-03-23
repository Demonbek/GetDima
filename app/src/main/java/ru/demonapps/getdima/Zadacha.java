/*
 * *
 *  * Created by DemonApps on 23.03.21 21:17
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 03.03.21 19:12
 *
 */

package ru.demonapps.getdima;

public class Zadacha {
    public String id;
    public String date;
    public String title;
    public String zaeb;
    public String autor;
    public String ispolneno;

    public Zadacha() {
    }

    public Zadacha(String id, String date, String title, String zaeb, String autor, String ispolneno) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.zaeb = zaeb;
        this.autor = autor;
        this.ispolneno = ispolneno;
    }
}
