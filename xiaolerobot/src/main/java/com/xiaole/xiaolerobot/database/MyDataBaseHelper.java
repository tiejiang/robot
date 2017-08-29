package com.xiaole.xiaolerobot.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 *
 *@author: tiejiang
 * data: 20170819
 * 之前写的数据库ｄｅｍｏ暂不使用
 */

public class MyDataBaseHelper extends SQLiteOpenHelper{

    private Context mContext;
    public static final String CLASS_10 = "create table class_10 ("
            + "id integer primary key autoincrement, "
            + "start_year integer, "
            + "college integer, "
            + "class integer, "
            + "student_id integer, "
            + "student_name text) ";

    public static final String CLASS_11 = "create table class_11 ("
            + "id integer primary key autoincrement, "
            + "start_year integer, "
            + "college integer, "
            + "class integer, "
            + "student_id integer, "
            + "student_name text) ";

    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CLASS_10);
        sqLiteDatabase.execSQL(CLASS_11);
        Toast.makeText(mContext, "database create succeed !", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}