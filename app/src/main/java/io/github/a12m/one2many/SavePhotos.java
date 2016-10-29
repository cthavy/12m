package io.github.a12m.one2many;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class SavePhotos extends AppCompatActivity {

    ImageView picturePreview;
    Button selectEvent;
    Button buttonSavePicture;

    String eventNames[];
    List<ParseObject> list1;
    List<ParseObject> list2;

    String eventId;

    final int[] whichSelected = new int[1];

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

        if(extra.getBoolean("isVideo")){
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(imageUri.getPath(),
                    MediaStore.Images.Thumbnails.MINI_KIND);

            int nh = (int) (thumbnail.getHeight() * (1024.0 / thumbnail.getWidth()));
            final Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 1024, nh, true);
            picturePreview.setImageBitmap(scaled);

            final File file = new File(imageUri.getPath());

            picturePreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "video/*");
                    startActivity(intent);
                }
            });

            selectEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = onCreateDialogSingleChoice(eventNames);
                    dialog.show();
                }
            });

            buttonSavePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSavePicture.setEnabled(false);
                    selectEvent.setEnabled(false);

                    try {
                        byte[] data = convert(imageUri.getPath());

                        // Create the ParseFile
                        ParseFile file = new ParseFile("androidbegin.mp4", data);
                        // Upload the image into Parse Cloud

                        try {
                            file.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // Create a New Class called "ImageUpload" in Parse
                        ParseObject imgupload = new ParseObject("Picture");

                        // Create a column named "ImageName" and set the string
                        imgupload.put("eventId", eventId);

                        // Insert isVideo, pic, and takenBy to each of the columns
                        imgupload.put("isVideo", true);
                        imgupload.put("pic", file);
                        imgupload.put("takenBy", ParseUser.getCurrentUser().getUsername());

                        // Create the class and the columns
                        imgupload.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null) {
                                    // Show a simple toast message to show the photo has been uploaded
                                    Toast.makeText(getApplicationContext(), "The video has been uploaded",
                                            Toast.LENGTH_SHORT).show();

                                    Intent i = new Intent(SavePhotos.this, EventActivity.class);
                                    i.putExtra("EventName", selectEvent.getText().toString());
                                    i.putExtra("EventId", eventId);
                                    if (whichSelected[0] > list1.size() - 1) {
                                        i.putExtra("IsOwner", false);
                                    } else {
                                        i.putExtra("IsOwner", true);
                                        i.putExtra("OwnerName", ParseUser.getCurrentUser().getUsername());
                                    }
                                    finish();
                                    startActivity(i);


                                } else {
                                    buttonSavePicture.setEnabled(true);
                                    selectEvent.setEnabled(true);

                                    Toast.makeText(getApplicationContext(), "Unable to upload, please try again",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            Bitmap thumbnail = null;
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int nh = (int) (thumbnail.getHeight() * (1024.0 / thumbnail.getWidth()));
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
                    buttonSavePicture.setEnabled(false);
                    selectEvent.setEnabled(false);

                    // Convert it to byte
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    scaled.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();

                    // Create the ParseFile
                    ParseFile file = new ParseFile("androidbegin.png", image);
                    // Upload the image into Parse Cloud

                    try {
                        file.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // Create a New Class called "ImageUpload" in Parse
                    ParseObject imgupload = new ParseObject("Picture");

                    // Create a column named "ImageName" and set the string
                    imgupload.put("eventId", eventId);

                    // Insert isVideo, pic, and takenBy to each of the columns
                    imgupload.put("isVideo", false);
                    imgupload.put("pic", file);
                    imgupload.put("takenBy", ParseUser.getCurrentUser().getUsername());

                    // Create the class and the columns
                    imgupload.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {
                                // Show a simple toast message to show the photo has been uploaded
                                Toast.makeText(getApplicationContext(), "The image has been uploaded",
                                        Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(SavePhotos.this, EventActivity.class);
                                i.putExtra("EventName", selectEvent.getText().toString());
                                i.putExtra("EventId", eventId);
                                if (whichSelected[0] > list1.size() - 1) {
                                    i.putExtra("IsOwner", false);
                                } else {
                                    i.putExtra("IsOwner", true);
                                    i.putExtra("OwnerName", ParseUser.getCurrentUser().getUsername());
                                }
                                finish();
                                startActivity(i);


                            } else {
                                buttonSavePicture.setEnabled(true);
                                selectEvent.setEnabled(true);

                                Toast.makeText(getApplicationContext(), "Unable to upload, please try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SavePhotos.this, CustomCamera.class));
        super.onBackPressed();
    }

    public byte[] convert(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        for (int readNum; (readNum = fis.read(b)) != -1;) {
            bos.write(b, 0, readNum);
        }

        byte[] bytes = bos.toByteArray();

        return bytes;
    }

    public Dialog onCreateDialogSingleChoice(final String[] myEvents) {

        //Initialize the Alert Dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(SavePhotos.this);
        //Source of the data in the Dialog

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

                // 0 1 2 3 4 5 size: 6
                // 6 7 8 9 size: 4

            // Set the action buttons
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK, so save the result somewhere
                    // or return them to the component that opened the dialog
                    selectEvent.setText(myEvents[whichSelected[0]]);
                    //eventId = myEvents[whichSelected[0]];
                    if(whichSelected[0] > list1.size() - 1){
                        eventId = (String) list2.get(whichSelected[0] - list1.size()).get("eventId");
                    } else{
                        eventId = list1.get(whichSelected[0]).getObjectId();
                    }

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



//            //Adding events the user is a part of to the list
            for (ParseObject event : ob2) {
                String eventId = (String) event.get("eventId");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                try {
                    Log.i("SET: ", eventId);
                    itemName[i] = query.get(eventId).getString("name");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                i++;
            }

            //Adding events the user is a part of to the list
//            for (ParseObject event : ob2) {
//                String eventId = (String) event.get("eventId");
//                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
//                Log.i("SET: ", eventId);
//                itemName[i] = eventId;
//                i++;
//            }

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
