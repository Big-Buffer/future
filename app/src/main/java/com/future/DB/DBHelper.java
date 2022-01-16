package com.future.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * @author ：shenmegui
 * @date ：Created in 2021/12/15 19:59
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context, "future.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table event('id' int primary key, 'endDate' varchar(255),'content' varchar(255))");
        db.execSQL("insert into event values(1, '2022-01-01 19:30', '洗衣服')");
        db.execSQL("insert into event values(2, '2022-01-11 08:30', '洗衣服、晒被子')");
        db.execSQL("insert into event values(3, '2022-01-21 10:30', '晒被子')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
