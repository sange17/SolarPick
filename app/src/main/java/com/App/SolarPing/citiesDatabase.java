package com.App.SolarPing;

//citiesDatabase

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class citiesDatabase {
    private static final String DATABASE_NAME = "citiesDataSet.db";
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
            db.execSQL(citiesNameDatabaseStructure.CreateDB._Create0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS "+ citiesNameDatabaseStructure.CreateDB.tablename);
            onCreate(db);
        }
    }

    public citiesDatabase(Context context){
        this.dbCtx = context;
    }

    public citiesDatabase open() throws SQLException{
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

    public long insertColumn(String province, String city,String anotherInfo){
        ContentValues values = new ContentValues();
        values.put(citiesNameDatabaseStructure.CreateDB.province,province);
        values.put(citiesNameDatabaseStructure.CreateDB.city,city);
        values.put(citiesNameDatabaseStructure.CreateDB.anotherInfo,anotherInfo);

        return DB.insert(citiesNameDatabaseStructure.CreateDB.tablename, null,values);
    }

    public Cursor selectColumns(){
        return DB.query(citiesNameDatabaseStructure.CreateDB.tablename,null,null,null,null,null,null);
    }


    public Cursor selectWhatIWant(String province,String city){
        String sql = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = '%s'",citiesNameDatabaseStructure.CreateDB.tablename,citiesNameDatabaseStructure.CreateDB
                .province,province,citiesNameDatabaseStructure.CreateDB.city,city);
        return DB.rawQuery(sql,null);
    }

    public void deleteAll(){
        DBHelper.onUpgrade(DB,1,2);
    }

    public void UpdateAll(String province,String city,String anotherInfo){
        String a = String.format("UPDATE %s SET %s = '%s' , %s = '%s',%s = '%s' WHERE %s = '%s' AND %s = '%s' ;",
                citiesNameDatabaseStructure.CreateDB.tablename,citiesNameDatabaseStructure.CreateDB.province,province,citiesNameDatabaseStructure.CreateDB.city,city,citiesNameDatabaseStructure.CreateDB.anotherInfo,anotherInfo,citiesNameDatabaseStructure.CreateDB.province,province,citiesNameDatabaseStructure.CreateDB.city,city);
        DB.execSQL(a);
    }




}

