package com.App.SolarPing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class generatingDatabase {
    private static final String DATABASE_NAME = "citiesDataSet.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase DB;
    generatingDatabase.Database DBHelper;
    private final Context dbCtx;

    private static class Database extends SQLiteOpenHelper {

        public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(generatingAmountDatabase.CreateDB._Create0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS "+ generatingAmountDatabase.CreateDB.tablename);
            onCreate(db);
        }
    }

    public generatingDatabase(Context context){
        this.dbCtx = context;
    }

    public void open() throws SQLException {
        DBHelper = new generatingDatabase.Database(dbCtx,DATABASE_NAME,null,DATABASE_VERSION);
        DB = DBHelper.getWritableDatabase();
    }

    public void create(){
        DBHelper.onCreate(DB);
    }

    public void close(){
        DB.close();
    }

    public void insertColumn(String region,String value){
        ContentValues values = new ContentValues();
        values.put(generatingAmountDatabase.CreateDB.region,region);
        values.put(generatingAmountDatabase.CreateDB.amount,value);
        DB.insert(generatingAmountDatabase.CreateDB.tablename, null, values);
    }


    public Cursor selectColumns(){
        return DB.query(generatingAmountDatabase.CreateDB.tablename, null, null, null, null, null, null);
    }

    public Cursor selectWhatIWant(String region){
        String sql = String.format("SELECT * FROM %s WHERE %s = '%s'",generatingAmountDatabase.CreateDB.tablename,generatingAmountDatabase.CreateDB.region,region);
        return DB.rawQuery(sql,null);
    }

    public void UpdateColumn(String region,String values){
        String sql = String.format("UPDATE %s SET %s = '%s',%s = '%s' WHERE %s = '%s'",generatingAmountDatabase.CreateDB.tablename,generatingAmountDatabase.CreateDB.region,region,generatingAmountDatabase.CreateDB.amount,values,generatingAmountDatabase.CreateDB.region,region);
        DB.execSQL(sql);
    }



    public void deleteAll(){
        DBHelper.onUpgrade(DB,1,2);
    }



}
