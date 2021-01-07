package com.App.SolarPing;

import android.provider.BaseColumns;

public class citiesNameDatabaseStructure {
    public static class CreateDB implements BaseColumns {
        final static String tablename = "citytable";
        final static String province = "province";
        final static String city = "city";
        final static String anotherInfo = "anotherInfo";
        final static String _Create0 = "CREATE TABLE IF NOT EXISTS "+tablename
                +"(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +province + " TEXT NOT NULL, "
                +city + " TEXT NOT NULL, "
                +anotherInfo + " TEXT NOT NULL );";
    }
}
