package com.padaliya.hardnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.padaliya.hardnotes.dataModel.Category;
import com.padaliya.hardnotes.dataModel.Image;
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

    public void insertNote( int note_id ,String title , String date , String  text , String audio , String location , int category_id )
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put(sqlHelper.NOTES_ID, note_id) ;
        contentValues.put(sqlHelper.TITLE , title);
        contentValues.put(sqlHelper.DATE , date);
        contentValues.put(sqlHelper.DESCRIPTION , text);
        contentValues.put(sqlHelper.AUDIO , audio);
        contentValues.put(sqlHelper.LOCATION , location);
        contentValues.put(sqlHelper.NOTES_CATEGORY_ID , category_id);

        database.insert(sqlHelper.NOTES_TABLE_NAME , null , contentValues);

    }

    public List<Note> getNotes (int category_id)
    {
        List<Note> notes = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT * FROM "+sqlHelper.NOTES_TABLE_NAME+" WHERE "+sqlHelper.NOTES_CATEGORY_ID+" = "+ category_id, null);
        if(c.moveToFirst()) {

            do {

                Note note = new Note();
                note.NOTE_ID = c.getInt(0);
                note.TITLE = c.getString(1);
                note.DATE = c.getString(2);
                note.DESCRIPTION = c.getString(3);
                note.AUDIO = c.getString(4);
                note.LOCATION = c.getString(5);
                note.CATEGORY_ID = c.getInt(6);

                notes.add(note);
            } while (c.moveToNext());
        }
        return notes;
    }

    public List<Note> searchNotes ( int subject_id , String search)
    {
        List<Note> notes = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+sqlHelper.NOTES_TABLE_NAME+" WHERE "+sqlHelper.NOTES_CATEGORY_ID+" = "+ subject_id+" and ( "+sqlHelper.TITLE+" LIKE '%"+search+"%' or "+sqlHelper.DESCRIPTION+" LIKE '%"+search+"%' )" , null);
        if(c.moveToFirst()) {

            do {
                Note note = new Note();
                note.NOTE_ID = c.getInt(0);
                note.TITLE = c.getString(1);
                note.DATE = c.getString(2);
                note.DESCRIPTION = c.getString(3);
                note.AUDIO = c.getString(4);
                note.LOCATION = c.getString(5);
                note.CATEGORY_ID = c.getInt(6);
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
            note.AUDIO = c.getString(4);
            note.LOCATION = c.getString(5);
            note.CATEGORY_ID = c.getInt(6);
            return note;
        }

        return null;

    }

    public void deleteNote (int note_id , int category_id)
    {
        database.delete(sqlHelper.NOTES_TABLE_IMAGES , "NOTES_ID = ?" +" and CATEGORY_ID = ?",
                new String[] {
                        String.valueOf(note_id),
                        String.valueOf(category_id)
                });
        database.delete(sqlHelper.NOTES_TABLE_NAME , "NOTES_ID = ?" +" and CATEGORY_ID = ?",
                new String[] {
                String.valueOf(note_id),
                String.valueOf(category_id)
                             });

    }

    public void deleteCategory (int category_id)
    {
        database.delete(sqlHelper.NOTES_TABLE_IMAGES , " CATEGORY_ID = ?",
                new String[] {
                        String.valueOf(category_id)
                });
        database.delete(sqlHelper.NOTES_TABLE_NAME , " CATEGORY_ID = ?",
                new String[] {
                        String.valueOf(category_id)
                });
        database.delete(sqlHelper.CATEGORY_TABLE_NAME , " CATEGORY_ID = ?",
                new String[] {
                        String.valueOf(category_id)
                });

    }


    public void updateNote( int note_id  , String title , String date , String  text ,  String audio , String location , int category_id )
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put(sqlHelper.NOTES_ID, note_id) ;
        contentValues.put(sqlHelper.TITLE , title);
        contentValues.put(sqlHelper.DATE , date);
        contentValues.put(sqlHelper.DESCRIPTION , text);

        contentValues.put(sqlHelper.AUDIO , audio);
        contentValues.put(sqlHelper.LOCATION , location);
        contentValues.put(sqlHelper.NOTES_CATEGORY_ID , category_id);

        database.update(sqlHelper.NOTES_TABLE_NAME ,contentValues , "NOTES_ID = ?", new String[] {
                String.valueOf(note_id)
        }) ;

    }

    public void insertImage( String image ,int notes_id , int category_id )
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put(sqlHelper.IMAGE , image);
        contentValues.put(sqlHelper.NOTES_ID , notes_id);
        contentValues.put(sqlHelper.NOTES_CATEGORY_ID , category_id);

        database.insert(sqlHelper.NOTES_TABLE_IMAGES , null , contentValues);

    }

    public List<Image> getImages (int note_id , int category_id)
    {
        List<Image> images = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM "+sqlHelper.NOTES_TABLE_IMAGES+" WHERE "+sqlHelper.NOTES_CATEGORY_ID+" = "+ category_id + " and " +sqlHelper.NOTES_IMAGE_ID + "=" + note_id, null);

        if(c.moveToFirst()) {

            do {

                Image image = new Image();
                image.IMAGE = c.getString(0);

                images.add(image);

            } while (c.moveToNext());

        }

        return images;

    }

    public List<Note> getSortedNotes (int category_id , String sortOrder )
    {
        List<Note> notes = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT * FROM "+sqlHelper.NOTES_TABLE_NAME+" WHERE "+sqlHelper.NOTES_CATEGORY_ID+" = "+ category_id + " order by " + sortOrder, null);
        if(c.moveToFirst()) {

            do {

                Note note = new Note();
                note.NOTE_ID = c.getInt(0);
                note.TITLE = c.getString(1);
                note.DATE = c.getString(2);
                note.DESCRIPTION = c.getString(3);
                note.AUDIO = c.getString(4);
                note.LOCATION = c.getString(5);
                note.CATEGORY_ID = c.getInt(6);

                notes.add(note);
            } while (c.moveToNext());
        }
        return notes;
    }
}
