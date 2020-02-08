package com.padaliya.hardnotes;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLLiteDatabaseHelper extends SQLiteOpenHelper {


    static final String DATABASE_NAME = "MY_NOTES.DB";

    static final int DB_VERSION = 1;

    // category table name

    public static final String CATEGORY_TABLE_NAME = "CATEGORIES";

    // category table columns

    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";

    // create category table query

    private static final String CREATE_CATEGORY_TABLE = "create table "+
            CATEGORY_TABLE_NAME +"("+
            CATEGORY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            CATEGORY_NAME +" TEXT NOT NULL );";


    //notes table name

    public static final String NOTES_TABLE_NAME = "NOTES";

    // notes table columns

    public static final String NOTES_ID = "notes_id";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String DESCRIPTION = "description";
    public static final String PHOTO = "photo";
    public static final String AUDIO = "audio";
    public static final String LOCATION = "location";
    public static final String NOTES_CATEGORY_ID = "category_id";



    //create notes table query

    private static final String CREATE_NOTES_TABLE = "create table "+
            NOTES_TABLE_NAME +" ("+
            NOTES_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            TITLE +" TEXT NOT NULL, "+
            DATE +" TEXT NOT NULL, "+
            DESCRIPTION +" TEXT, "+
            PHOTO +" TEXT, "+
            AUDIO +" TEXT, "+
            LOCATION +" TEXT, "+
            NOTES_CATEGORY_ID +" INTEGER NOT NULL); ";

    public SQLLiteDatabaseHelper( Context context ) {

        super(context, DATABASE_NAME , null , DB_VERSION);
    }


    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_NOTES_TABLE);

    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int i, int i1){

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);

        onCreate(sqLiteDatabase);

    }


}
