package io.github.a12m.one2many;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SavePhotos extends AppCompatActivity {

    ImageView picturePreview;
    Button selectEvent;
    Button buttonSavePicture;

    String eventNames[];
    List<ParseObject> list1;
    List<ParseObject> list2;

    //Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_photos);

        picturePreview = (ImageView) findViewById(R.id.imageViewSavePicture);
        buttonSavePicture = (Button) findViewById(R.id.button_save_to_database);
        selectEvent = (Button) findViewById(R.id.button_select_event);

        new GetUserEvents().execute();

        Bundle extra = getIntent().getExtras();

        final Uri imageUri = (Uri) extra.get("imageUri");

        Bitmap thumbnail = null;
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int nh = (int) ( thumbnail.getHeight() * (1024.0 / thumbnail.getWidth()) );
        final Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 1024, nh, true);
        picturePreview.setImageBitmap(scaled);

        selectEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = onCreateDialogSingleChoice(eventNames);
                dialog.show();
            }
        });


        // Capture button clicks
        buttonSavePicture.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Convert it to byte
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                scaled.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();

                // Create the ParseFile
                ParseFile file = new ParseFile("androidbegin.png", image);
                // Upload the image into Parse Cloud

                file.saveInBackground();
                // Create a New Class called "ImageUpload" in Parse
                ParseObject imgupload = new ParseObject("Picture");

                // Create a column named "ImageName" and set the string
                imgupload.put("eventId", "Testing");

                // Insert isVideo, pic, and takenBy to each of the columns
                imgupload.put("isVideo", false);
                imgupload.put("pic", file);
                imgupload.put("takenBy", ParseUser.getCurrentUser().getUsername());

                // Create the class and the columns
                imgupload.saveInBackground();

                // Show a simple toast message to show the photo has been uploaded
                Toast.makeText(getApplicationContext(), "The image has been uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Dialog onCreateDialogSingleChoice(final String[] myEvents) {

        //Initialize the Alert Dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(SavePhotos.this);
        //Source of the data in the DIalog

        Toast.makeText(this, "Length: " + myEvents.length, Toast.LENGTH_LONG).show();

        final int[] whichSelected = new int[1];

        // Set the dialog title
        builder.setTitle("Select Event")
        // Specify the list array, the items to be selected by default (null for none),
        // and the listener through which to receive callbacks when items are selected
            .setSingleChoiceItems(myEvents, 1, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    whichSelected[0] = which;

                }
            })

            // Set the action buttons
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK, so save the result somewhere
                    // or return them to the component that opened the dialog
                    selectEvent.setText(myEvents[whichSelected[0]]);
                    Toast.makeText(getApplicationContext(), "Event Name: " + myEvents[whichSelected[0]], Toast.LENGTH_SHORT).show();
                    buttonSavePicture.setEnabled(true);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

        return builder.create();
    }

    private class GetUserEvents extends AsyncTask<Void, Void, Void> {
        String[] itemName;
        List<ParseObject> ob;
        List<ParseObject> ob2;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Getting all the events this user owns
            ParseQuery<ParseObject> queryEventOwn = new ParseQuery<>("Event");
            queryEventOwn.whereEqualTo("owner", ParseUser.getCurrentUser().getUsername());
            try {
                ob = queryEventOwn.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Getting all the events this user is a part of
            ParseQuery<ParseObject> queryEventPartOf = new ParseQuery<>("EventMembers");
            queryEventPartOf.whereEqualTo("memberUsername", ParseUser.getCurrentUser().getUsername());
            try {
                ob2 = queryEventPartOf.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Setting the size of the list to the total size of owned events and
            //events a part of
            itemName = new String[ob.size() + ob2.size()];
            int i = 0;

            //Adding owned events to list
            for (ParseObject event : ob) {
                itemName[i] = (String) event.get("name");
                i++;
            }

            //Adding events the user is a part of to the list
            for (ParseObject event : ob2) {
                String eventId = (String) event.get("eventId");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                try {
                    itemName[i] = query.get(eventId).getString("name");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                i++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void param) {
            //setting the event names to the adapter
            eventNames = new String[itemName.length];
            eventNames = itemName.clone();
            list1 = ob;
            list2 = ob2;
            selectEvent.setEnabled(true);
            selectEvent.setText("Select Event");
        }
    }

}
