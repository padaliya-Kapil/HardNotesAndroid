package com.padaliya.hardnotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
    }

    public void finish(View view)
    {
        finish();
    }

    public void add_category(View view)
    {

        EditText category_et = findViewById(R.id.category_name);

        String category_name = category_et.getText().toString().trim();

        if( category_name.equalsIgnoreCase(""))
        {

            Toast.makeText(AddCategory.this , "Please enter category name" , Toast.LENGTH_SHORT).show();


        }

        else {

            DatabaseManager db = new DatabaseManager(AddCategory.this);
            db.open();
            db.insertCategory(category_name);
            db.close();
            Toast.makeText(AddCategory.this , "Category added successfully" , Toast.LENGTH_SHORT).show();
            finish();


        }

    }


}
