package com.padaliya.hardnotes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class AddNoteActivity extends AppCompatActivity {

   String  userLocation =  "";

    private static final int SELECT_IMAGE_CODE = 101;

    private static final int RECORD_AUDIO_CODE = 105;

    private Uri cameraPhotoURI = null;
    private List<Uri> mUris = new ArrayList<>();
    private Uri audioUri = null;

    private FusedLocationProviderClient fusedLocationClient;


    // https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Logic to handle location object

                            System.out.println("Location is : "+location.getLatitude());

                            userLocation = location.getLatitude()+","+location.getLongitude();
                        }
                    }
                });
    }

    public void finish(View view)
    {

        finish();
    }

    public void add_audio(View view)
    {

        Intent pickIntent = new Intent();
        pickIntent.setType("audio/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent recordIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        recordIntent.setType("audio/*") ;

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, pickIntent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Record");
        Intent[] intentarray= {recordIntent};
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS,intentarray);
        startActivityForResult(chooser , RECORD_AUDIO_CODE);
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

                  LinearLayout linearLayout1 =  findViewById(R.id.add_view_images);

                  ImageView image = new ImageView(AddNoteActivity.this);

                  image.setImageURI(cameraPhotoURI);
                  linearLayout1.addView(image);
                    TextView tv = new TextView(this);
                    tv.setHeight(20);
                    linearLayout1.addView(tv);


                }
            } else {



                System.out.println("camera photo uri : " + cameraPhotoURI);
                Log.d("Add note"  , "aaya kya??");

                LinearLayout linearLayout1 =  findViewById(R.id.add_view_images);

                for(int i = 0 ; i < mUris.size() ; i ++)
                {
                    ImageView image = new ImageView(AddNoteActivity.this);
                    image.setImageURI(mUris.get(0));
                    linearLayout1.addView(image);
                    TextView tv = new TextView(this);
                    tv.setHeight(20);
                    linearLayout1.addView(tv);

                }


            }

        }

        if(requestCode == RECORD_AUDIO_CODE && resultCode == RESULT_OK)
        {
            audioUri = data.getData();

            System.out.println("audio uri : " + audioUri);

            TextView audio_selected = findViewById(R.id.audio_selected_text);

            audio_selected.setVisibility(View.VISIBLE);

        }
    }



    public void add_note(View view)
    {
        Random random = new Random() ;
        int id = random.nextInt(9999999);

        EditText title_et = findViewById(R.id.title_id);
        EditText text_et = findViewById(R.id.text_id);
        String title = title_et.getText().toString().trim();
        String text = text_et.getText().toString().trim();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date()) ;
        int category_id = getIntent().getIntExtra("category_id" , 0);
        String audioPath = "";

        DatabaseManager db = new DatabaseManager(AddNoteActivity.this);
        db.open();



        if(title.equalsIgnoreCase("") && text.equalsIgnoreCase(""))
        {
            Toast.makeText(AddNoteActivity.this , "Either title or text is neccessary !" , Toast.LENGTH_SHORT).show();
        }

        else {
            for(int i = 0 ; i < mUris.size() ; i ++) {

                if (mUris.get(i) != null) {
                    db.insertImage( mUris.get(i).toString() , id, category_id );
                }
            }

            db.insertNote(id ,title , date , text ,  audioPath ,userLocation , category_id ) ;
            Toast.makeText(AddNoteActivity.this , "Note added successfully" , Toast.LENGTH_SHORT).show();
        }

        db.close();
        finish();

    }

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
}
