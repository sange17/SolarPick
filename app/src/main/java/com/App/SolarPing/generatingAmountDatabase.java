package com.App.SolarPing;

import android.provider.BaseColumns;

public class generatingAmountDatabase {
    public static class CreateDB implements BaseColumns {
        final static String tablename = "generatingTable";
        final static String region = "region";
        final static String amount = "amountOfGenerating";
        final static String _Create0 = "CREATE TABLE IF NOT EXISTS "+tablename
                +"(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +region + " TEXT NOT NULL, "
                +amount + " TEXT NOT NULL );";
    }
}
