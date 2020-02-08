package com.padaliya.hardnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.padaliya.hardnotes.dataModel.Category;
import com.padaliya.hardnotes.dataModel.Note;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private SQLLiteDatabaseHelper sqlHelper ;

    private Context context; // context is the middle layer

    private SQLiteDatabase database ;

    public DatabaseManager(Context context)
    {
        this.context = context;
    } // constructor

    public DatabaseManager open() {
        sqlHelper = new SQLLiteDatabaseHelper(context);
        database = sqlHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        sqlHelper.close();
    }

    public void insertCategory(String name)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(sqlHelper.CATEGORY_NAME , name);
        database.insert(sqlHelper.CATEGORY_TABLE_NAME , null , contentValues);
    }

    public List<Category> getCategories()
    {
        List<Category> categories = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+sqlHelper.CATEGORY_TABLE_NAME, null);
        if(c.moveToFirst()) {
            do{
                Category category = new Category();

                category.CATEGORY_ID = c.getInt(0);
                category.CATEGORY_NAME = c.getString(1);

                categories.add(category);

            } while (c.moveToNext());
        }
        return categories;
    }

    public void insertNote( String title , String date , String  text , String photo , String audio , String location , int category_id )
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put(sqlHelper.TITLE , title);
        contentValues.put(sqlHelper.DATE , date);
        contentValues.put(sqlHelper.DESCRIPTION , text);
        contentValues.put(sqlHelper.PHOTO , photo);
        contentValues.put(sqlHelper.AUDIO , audio);
        contentValues.put(sqlHelper.LOCATION , location);
        contentValues.put(sqlHelper.NOTES_CATEGORY_ID , category_id);

        database.insert(sqlHelper.NOTES_TABLE_NAME , null , contentValues);

    }

    public List<Note> getNotes (int category_id)
    {
        List<Note> notes = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+sqlHelper.NOTES_TABLE_NAME+" WHERE "+sqlHelper.NOTES_CATEGORY_ID+" = '"+ category_id, null);

        if(c.moveToFirst()) {

            do {

                Note note = new Note();

                note.NOTE_ID = c.getInt(0);
                note.TITLE = c.getString(1);
                note.DATE = c.getString(2);
                note.DESCRIPTION = c.getString(3);
                note.PHOTO = c.getString(4);
                note.AUDIO = c.getString(5);
                note.LOCATION = c.getString(6);
                note.CATEGORY_ID = c.getInt(7);

                notes.add(note);

            } while (c.moveToNext());

        }

        return notes;

    }

    public List<Note> searchNotes ( int subject_id , int user_id , String search)
    {
        List<Note> notes = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+sqlHelper.NOTES_TABLE_NAME+" WHERE "+sqlHelper.NOTES_CATEGORY_ID+" = '"+ subject_id+"' and ( "+sqlHelper.TITLE+" LIKE '%"+search+"%' or "+sqlHelper.DESCRIPTION+" LIKE '%"+search+"%' )" , null);
        if(c.moveToFirst()) {

            do {
                Note note = new Note();
                note.NOTE_ID = c.getInt(0);
                note.TITLE = c.getString(1);
                note.DATE = c.getString(2);
                note.DESCRIPTION = c.getString(3);
                note.PHOTO = c.getString(4);
                note.AUDIO = c.getString(5);
                note.LOCATION = c.getString(6);
                note.CATEGORY_ID = c.getInt(7);
                notes.add(note);

            } while (c.moveToNext());
        }
        return notes;
    }


    public Note getSingleNote ( int note_id)
    {
        Cursor c = database.rawQuery("SELECT * FROM "+sqlHelper.NOTES_TABLE_NAME+" WHERE "+sqlHelper.NOTES_ID+" = '"+ note_id+"'" , null);

        if(c.moveToFirst()) {
            Note note = new Note();

            note.NOTE_ID = c.getInt(0);
            note.TITLE = c.getString(1);
            note.DATE = c.getString(2);
            note.DESCRIPTION = c.getString(3);
            note.PHOTO = c.getString(4);
            note.AUDIO = c.getString(5);
            note.LOCATION = c.getString(6);
            note.CATEGORY_ID = c.getInt(7);
            return note;
        }

        return null;

    }

    public void deleteNote (int note_id)
    {
        // TODO : implementation
    }

    public void deleteCategory (int category_id)
    {
        // TODO : implementation
    }
}
