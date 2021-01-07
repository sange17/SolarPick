package com.App.SolarPing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class GraphDatabase {
    private static final String DATABASE_NAME = "GDataset.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase DB;
    Database DBHelper;
    private final Context dbCtx;

    private static class Database extends SQLiteOpenHelper{

        public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(GraphDatabaseStructure.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS "+ GraphDatabaseStructure.CreateDB.TABLE_NAME);
            onCreate(db);
        }
    }

    public GraphDatabase(Context context){
        this.dbCtx = context;
    }

    public GraphDatabase open() throws SQLException{
        DBHelper = new Database(dbCtx,DATABASE_NAME,null,DATABASE_VERSION);
        DB = DBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        DBHelper.onCreate(DB);
    }

    public void close(){
        DB.close();
    }

    public void insertColumn(String CITY_NAME, String YEARS_OF_SOLAR_POWER, String NUMBERS_OF_SOLAR_POWER, String YEARS_OF_AVALANCHE, String VALUES_OF_AVALANCHE){
        ContentValues values = new ContentValues();
        values.put(GraphDatabaseStructure.CreateDB.CITY_NAME,CITY_NAME);
        values.put(GraphDatabaseStructure.CreateDB.YEARS_OF_SOLAR_POWER,YEARS_OF_SOLAR_POWER);
        values.put(GraphDatabaseStructure.CreateDB.NUMBERS_OF_SOLAR_POWER,NUMBERS_OF_SOLAR_POWER);
        values.put(GraphDatabaseStructure.CreateDB.YEARS_OF_AVALANCHE,YEARS_OF_AVALANCHE);
        values.put(GraphDatabaseStructure.CreateDB.VALUES_OF_AVALANCHE,VALUES_OF_AVALANCHE);
        DB.insert(GraphDatabaseStructure.CreateDB.TABLE_NAME, null, values);
    }

    public Cursor selectColumns(){
        return DB.query(GraphDatabaseStructure.CreateDB.TABLE_NAME,null,null,null,null,null,GraphDatabaseStructure.CreateDB.YEARS_OF_SOLAR_POWER);
    }

    public Cursor selectedWhatIWant(String province_city){
        String sql = String.format("SELECT * FROM %s WHERE %s = '%s'",GraphDatabaseStructure.CreateDB.TABLE_NAME,GraphDatabaseStructure.CreateDB.CITY_NAME,province_city);
        return DB.rawQuery(sql,null);
    }

    public void deleteAll(){
        DBHelper.onUpgrade(DB,1,2);
    }

    public void UpdateAll(String CITY_NAME, String YEARS_OF_SOLAR_POWER, String NUMBERS_OF_SOLAR_POWER, String YEARS_OF_AVALANCHE, String VALUES_OF_AVALANCHE){
        String sql = String.format("UPDATE %s SET %s = '%s' , %s = '%s' WHERE %s = '%s' AND %s = '%s' AND %s = '%s';",
                GraphDatabaseStructure.CreateDB.TABLE_NAME,
                GraphDatabaseStructure.CreateDB.NUMBERS_OF_SOLAR_POWER,NUMBERS_OF_SOLAR_POWER,GraphDatabaseStructure.CreateDB.VALUES_OF_AVALANCHE,VALUES_OF_AVALANCHE,
                GraphDatabaseStructure.CreateDB.YEARS_OF_SOLAR_POWER,YEARS_OF_SOLAR_POWER,GraphDatabaseStructure.CreateDB.YEARS_OF_AVALANCHE,YEARS_OF_AVALANCHE,GraphDatabaseStructure.CreateDB.CITY_NAME,CITY_NAME);
        DB.execSQL(sql);
    }




}
