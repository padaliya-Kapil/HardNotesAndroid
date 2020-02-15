package com.padaliya.hardnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.padaliya.hardnotes.dataModel.Image;
import com.padaliya.hardnotes.dataModel.Note;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class ViewNotes extends AppCompatActivity {

    private static final int SELECT_IMAGE_CODE = 101;

    private Uri cameraPhotoURI = null;
    private List<Uri> mUris = new ArrayList<>();
    private Uri audioUri = null;

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
        Log.d("View ", "getNote") ;

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

        title_et.setText(note.TITLE);
        text_et.setText(note.DESCRIPTION);

        List<Image> images = db.getImages(note.NOTE_ID,note.CATEGORY_ID) ;
        LinearLayout linearLayout1 =  findViewById(R.id.viewImages);

        Log.d("View ", "" + images.size()) ;

        for(int i = 0 ; i < images.size() ; i ++)
        {
            Log.d("View ", "" + images.get(i).IMAGE) ;

            if(!images.get(i).IMAGE.equalsIgnoreCase(""))
            {
                Log.d("View ", ""+i) ;
                ImageView image = new ImageView(ViewNotes.this);
                image.setImageURI(Uri.parse(images.get(i).IMAGE));
                linearLayout1.addView(image);

                TextView tv = new TextView(this);
                tv.setHeight(20);
                linearLayout1.addView(tv);

            mUris.add(Uri.parse(images.get(i).IMAGE));


            }
        }


//        if(!note.AUDIO.equalsIgnoreCase(""))
//        {
//            ImageView playImage = findViewById(R.id.play_audio);
//
//            playImage.setVisibility(View.VISIBLE);
//
//            final MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(note.AUDIO));
//
//            playImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//
//                }
//            });
//        }


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

        String audioPath = "";                      // TODO



        if(title.equalsIgnoreCase("") && text.equalsIgnoreCase(""))
        {
            Toast.makeText(ViewNotes.this , "Either title or text is neccessary !" , Toast.LENGTH_SHORT).show();
        }
        else {
            db.updateNote( note_id ,title , date , text , audioPath ,location , category_id );
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String z = image.getAbsolutePath();
        return image;
    }


    public void add_image(View view)
    {
        Intent photoIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        photoIntent.setType("image/*");


        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            cameraPhotoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoURI);

            mUris.add(cameraPhotoURI) ;
        }


        Intent selectOption = Intent.createChooser(photoIntent, "Select Image");
        selectOption.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        startActivityForResult(selectOption, SELECT_IMAGE_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK) {

            Log.d("Add note"  , "123");

            if (data != null) {
                Log.d("Add note"  , "126" + data.toString());

                if (cameraPhotoURI != null) {

                    Log.d("Add note"  , "131");

                    LinearLayout linearLayout1 =  findViewById(R.id.viewImages);

                    ImageView image = new ImageView(this);

                    image.setImageURI(cameraPhotoURI);
                    linearLayout1.addView(image);
                    TextView tv = new TextView(this);
                    tv.setHeight(20);
                    linearLayout1.addView(tv);


                }
            } else {

                System.out.println("camera photo uri : " + cameraPhotoURI);
                Log.d("Add note"  , "aaya kya??");

                LinearLayout linearLayout1 =  findViewById(R.id.viewImages);

                for(int i = 0 ; i < mUris.size() ; i ++)
                {
                    ImageView image = new ImageView(this);
                    image.setImageURI(mUris.get(0));
                    linearLayout1.addView(image);
                    TextView tv = new TextView(this);
                    tv.setHeight(20);
                    linearLayout1.addView(tv);

                }


            }


            DatabaseManager db = new DatabaseManager(this);
            db.open();
            db.insertImage( cameraPhotoURI.toString() , note.NOTE_ID, note.CATEGORY_ID );
            db.close();


        }
    }
}
