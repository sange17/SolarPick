package com.App.SolarPing;

import android.provider.BaseColumns;

public class GraphDatabaseStructure {
    public static final class CreateDB implements BaseColumns{
        public static final String TABLE_NAME = "twoGraphsTmp";
        public static final String CITY_NAME = "cityName";
        public static final String YEARS_OF_SOLAR_POWER = "yearsOfSolarPower";
        public static final String NUMBERS_OF_SOLAR_POWER = "numberOfSolarPower";
        public static final String YEARS_OF_AVALANCHE = "yearsOfAvalanche";
        public static final String VALUES_OF_AVALANCHE = "ValuesOfAvalanche";

        public static final String _CREATE0 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                +"("
                +_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +CITY_NAME +" TEXT NOT NULL, "
                +YEARS_OF_SOLAR_POWER + " TEXT NOT NULL, "
                +NUMBERS_OF_SOLAR_POWER + " TEXT NOT NULL, "
                +YEARS_OF_AVALANCHE + " TEXT NOT NULL, "
                +VALUES_OF_AVALANCHE + " TEXT NOT NULL );";
    }
}
