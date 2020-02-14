package com.padaliya.hardnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.padaliya.hardnotes.dataModel.Note;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class ViewNotes extends AppCompatActivity {

    Note note = null ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        getNote();
    }




    public void finish(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to update note ? ")
                .setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        update_note();
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Update?");
        alertDialog.show();

    }

    public void getNote()
    {

        int note_id = getIntent().getIntExtra("note_id" , 0);

        DatabaseManager db = new DatabaseManager(ViewNotes.this);

        db.open();

        note = db.getSingleNote(note_id);

        EditText title_et = findViewById(R.id.title_id);

        EditText text_et = findViewById(R.id.text_id);

        TextView date = findViewById(R.id.date);

        final TextView location = findViewById(R.id.location);

        date.setText("Created on : "+note.DATE);

        if(!note.LOCATION.trim().equalsIgnoreCase(""))
        {

            updateLocation.run();
            
            location.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String url = "http://maps.google.com/maps?daddr=" + note.LOCATION;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
        }

        ImageView photo = findViewById(R.id.selected_image);

        title_et.setText(note.TITLE);

        text_et.setText(note.DESCRIPTION);

        if(!note.PHOTO.equalsIgnoreCase("")) {
            photo.setImageURI(Uri.parse(note.PHOTO));
        }

        if(!note.AUDIO.equalsIgnoreCase(""))
        {
            ImageView playImage = findViewById(R.id.play_audio);

            playImage.setVisibility(View.VISIBLE);

            final MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(note.AUDIO));

            playImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                }
            });
        }


    }


    public void update_note()
    {
        int note_id = getIntent().getIntExtra("note_id" , 0);

        DatabaseManager db = new DatabaseManager(ViewNotes.this);
        db.open();

        final Note oldNote = db.getSingleNote(note_id);

        EditText title_et = findViewById(R.id.title_id); // updates title
        EditText text_et = findViewById(R.id.text_id); // updates text

        String title = title_et.getText().toString().trim();
        String text = text_et.getText().toString().trim();

        String location = oldNote.LOCATION ;

        String date = oldNote.DATE;                 // date will not be updated
        int category_id = oldNote.CATEGORY_ID;      // category will not be updated as of now

        String photoPath = "";                      // TODO
        String audioPath = "";                      // TODO



        if(title.equalsIgnoreCase("") && text.equalsIgnoreCase(""))
        {
            Toast.makeText(ViewNotes.this , "Either title or text is neccessary !" , Toast.LENGTH_SHORT).show();
        }
        else {
            db.updateNote( note_id ,title , date , text , photoPath , audioPath ,location , category_id );
            Toast.makeText(ViewNotes.this , "Note updated successfully" , Toast.LENGTH_SHORT).show();
            finish();

        }
        db.close();
    }

    Runnable updateLocation = new Runnable() {
        @Override
        public void run() {

            final TextView location = findViewById(R.id.location);

            Log.d("View Notes",note.LOCATION) ;
            String[] latLong = note.LOCATION.split(",");
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = gcd.getFromLocation(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1]), 1);

                if (addresses.size() > 0) {
                    location.setText(addresses.get(0).getAdminArea() );
                }
                else {
                    location.setText("Location on map");
                }
            } catch (IOException e) {

                Log.d("View Notes","IOEXception try stacktrace") ;
                location.setText("Location on map");

            } catch (Exception error)
            {

                location.setText("Location on map");
                Log.d("View Notes","Something else went wrong") ;

            }
}
    };
}
