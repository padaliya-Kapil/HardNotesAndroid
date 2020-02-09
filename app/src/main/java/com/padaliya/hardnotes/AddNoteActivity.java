package com.padaliya.hardnotes;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

   String  location =  "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

    }

    public void finish(View view)
    {

        finish();
    }

    public void add_audio(View view)
    {

    }

    public void add_image(View view)
    {

    }

    public void add_note(View view)
    {

        EditText title_et = findViewById(R.id.title_id);

        EditText text_et = findViewById(R.id.text_id);

        String title = title_et.getText().toString().trim();

        String text = text_et.getText().toString().trim();

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date()) ;

        int category_id = getIntent().getIntExtra("category_id" , 0);

        String photoPath = "";

        String audioPath = "";


        if(title.equalsIgnoreCase("") && text.equalsIgnoreCase(""))
        {
            Toast.makeText(AddNoteActivity.this , "Either title or text is neccessary !" , Toast.LENGTH_SHORT).show();

        }

        else {




            DatabaseManager db = new DatabaseManager(AddNoteActivity.this);

            db.open();

            db.insertNote(title , date , text , photoPath , audioPath ,location , category_id );

            db.close();

            Toast.makeText(AddNoteActivity.this , "Note added successfully" , Toast.LENGTH_SHORT).show();

            finish();

        }

    }

}
