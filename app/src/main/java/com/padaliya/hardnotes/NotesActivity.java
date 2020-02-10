package com.padaliya.hardnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.padaliya.hardnotes.dataModel.Note;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BlockedNumberContract.BlockedNumbers.COLUMN_ID;


public class NotesActivity extends AppCompatActivity {

    private RecyclerView note_list;
    private List<Note> note_data ;
    private Adapter adapter ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("Debug Notes Ac" ,"onCreate" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        note_data = new ArrayList<>();
        note_list = findViewById(R.id.notes_list);
        note_list.setLayoutManager(new LinearLayoutManager(NotesActivity.this , RecyclerView.VERTICAL , false));
}
public void add_note(View view)
{

    Log.d("Debug Notes Ac" ,"addNotes" );

    Intent i = new Intent(NotesActivity.this , AddNoteActivity.class);
    i.putExtra("category_id" , getIntent().getIntExtra("category_id" , 0));
    startActivity(i);

}

public void search(View view)
{
    Log.d("Debug Notes Ac" ,"search ");
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
            Log.d("Debug Notes Ac" ,"ViewHolder constructor ");
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
            Log.d("Debug Notes Ac" ,"ViewHolder onCreateViewHolder");
            return new ViewHolder(LayoutInflater.from(NotesActivity.this).inflate(R.layout.note_cell , parent , false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            Log.d("Debug Notes Ac" ,"ViewHolder onBindViewHolder");

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
            Log.d("Debug Notes Ac" ,"getItemCOunt");
            return note_data.size();
        }
    }

    // https://www.youtube.com/watch?v=M1XEqqo6Ktg
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback  = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT)
    {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getLayoutPosition() ;
            Note note = note_data.get(position) ;

            Log.d("Debug Notes Ac" , "position" +position + "");



            deleteNote(note) ;


//

        }
    };


    public void deleteNote(final Note note )
    {
        final Boolean[] value = {false};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete note ? ")
                    .setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Log.d("Debug Notes Ac" , "Number of notes" +note_data.size() + "");

                            int note_Id = note.NOTE_ID ;
                            int cat_Id = note.CATEGORY_ID ;

                            DatabaseManager db = new DatabaseManager(NotesActivity.this);
                            db.open();

                            db.deleteNote(note_Id,cat_Id);
                            db.close();


                            Log.d("Debug Notes Ac" ,"remove 1");
                            Log.d("Debug Notes Ac" , "Number of notes" +note_data.size() + "");
                            note_data.remove(note);
                            adapter.notifyDataSetChanged();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            loadData();

                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.setTitle("Are you sure?");
            alertDialog.show();
    }

    public void finish(View view)
    {

        Log.d("Debug Notes Ac" ,"finish");
        finish();
    }

    @Override
    protected void onResume() {
        Log.d("Debug Notes Ac" ,"onResume");
        super.onResume();

        DatabaseManager db = new DatabaseManager(NotesActivity.this);
        db.open();

        // get user id

        int category_id = getIntent().getIntExtra("category_id" , 0);
        note_data = db.getNotes(category_id );
        db.close();


        adapter = new Adapter();
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(note_list);
        note_list.setAdapter(adapter);

    }

    public void loadData()
    {

        DatabaseManager db = new DatabaseManager(NotesActivity.this);
        db.open();

        // get user id

        int category_id = getIntent().getIntExtra("category_id" , 0);
        note_data = db.getNotes(category_id );
        db.close();


        adapter = new Adapter();
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(note_list);
        note_list.setAdapter(adapter);


    }
}
