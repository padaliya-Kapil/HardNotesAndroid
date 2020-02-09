package com.padaliya.hardnotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.padaliya.hardnotes.dataModel.Note;

import java.util.ArrayList;
import java.util.List;


public class NotesActivity extends AppCompatActivity {

    private RecyclerView note_list;

    private List<Note> note_data ;

    private Adapter adapter ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        note_data = new ArrayList<>();
        note_list = findViewById(R.id.notes_list);
        note_list.setLayoutManager(new LinearLayoutManager(NotesActivity.this , RecyclerView.VERTICAL , false));
}


public void add_note(View view)
{

    Intent i = new Intent(NotesActivity.this , AddNoteActivity.class);
    i.putExtra("category_id" , getIntent().getIntExtra("category_id" , 0));
    startActivity(i);

}

    public void search(View view)
    {
        EditText search_et = findViewById(R.id.search_et);

        String search = search_et.getText().toString().trim();

        if(!search.equalsIgnoreCase(""))
        {
            DatabaseManager db = new DatabaseManager(NotesActivity.this);

            db.open();


            int category_id = getIntent().getIntExtra("category_id" , 0);

            note_data = db.searchNotes(category_id , search);

            db.close();

            adapter = new Adapter();

            note_list.setAdapter(adapter);

        }

        else {


            DatabaseManager db = new DatabaseManager(NotesActivity.this);

            db.open();

            int category_id = getIntent().getIntExtra("category_id" , 0);

            note_data = db.getNotes(category_id );

            db.close();

            adapter = new Adapter();

            note_list.setAdapter(adapter);

        }




    }
    private class ViewHolder extends RecyclerView.ViewHolder{

        TextView title , text , date ;

        LinearLayout note_layout ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);
            date = itemView.findViewById(R.id.date);
            note_layout = itemView.findViewById(R.id.note_layout);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder>
    {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(NotesActivity.this).inflate(R.layout.note_cell , parent , false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            final Note note = note_data.get(position);

            holder.title.setText(note.TITLE);

            holder.text.setText(note.DESCRIPTION);

            holder.date.setText(note.DATE);

            holder.note_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(NotesActivity.this , ViewNotes.class);

                    i.putExtra("note_id" , note.NOTE_ID);

                    startActivity(i);
                }
            });


        }

        @Override
        public int getItemCount() {
            return note_data.size();
        }
    }

    public void finish(View view)
    {

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseManager db = new DatabaseManager(NotesActivity.this);

        db.open();

        // get user id

        int category_id = getIntent().getIntExtra("category_id" , 0);
        note_data = db.getNotes(category_id );
        db.close();

        adapter = new Adapter();

        note_list.setAdapter(adapter);
    }
}
