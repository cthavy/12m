package io.github.a12m.one2many;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity implements View.OnClickListener{

    CollapsingToolbarLayout collapsingToolbarLayout;

    Toolbar toolbar;

    TextView ownerName;
    TextView numberOfMembers;

    String eventName;

    Button btnEdit;
    Button btnMembers;
    Button btnFinalize;

    GridView eventPhotos;

    boolean isNameChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent i = getIntent();

        eventName = i.getStringExtra("EventName");

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarEvent);
        collapsingToolbarLayout.setTitle(eventName);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));

        numberOfMembers = (TextView) findViewById(R.id.textViewEventNumberOfMembers);
        ownerName = (TextView) findViewById(R.id.textViewEventOwnerName);

        //If current user is owner
        if(i.getBooleanExtra("IsOwner", false)){
            findViewById(R.id.relativeLayoutEventOwner).setVisibility(View.VISIBLE);
            String owner = "Owner: " + i.getStringExtra("OwnerName");
            ownerName.setText(owner);
            new GetNumberOfMembersAndName(i.getStringExtra("EventId"), true).execute();
        } else {
            new GetNumberOfMembersAndName(i.getStringExtra("EventId"), false).execute();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbarEvent);
        setSupportActionBar(toolbar);
        //Sets the back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnEdit = (Button) findViewById(R.id.buttonEventOwnerEdit);
        btnEdit.setOnClickListener(this);

        btnMembers = (Button) findViewById(R.id.buttonEventOwnerAddMembers);
        btnMembers.setOnClickListener(this);

        btnFinalize = (Button) findViewById(R.id.buttonEventOwnerFinalize);
        btnFinalize.setOnClickListener(this);

        eventPhotos = (GridView) findViewById(R.id.gridViewEventPictures);


        //new GetEventPictures(i.getStringExtra("EventId")).execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Restarts lobby if the event name changed
        if(isNameChanged) {
            Intent intent = new Intent(EventActivity.this, Lobby.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }

        finish();
    }

    //Get number of members in event
    private class GetNumberOfMembersAndName extends AsyncTask<Void, Void, Void>{
        String eventId;
        boolean isOwner;
        List<ParseObject> ob;
        List<ParseObject> ob2;

        public GetNumberOfMembersAndName(String eventId, boolean isOwner){
            this.eventId = eventId;
            this.isOwner = isOwner;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("EventMembers");

            query.whereContains("eventId", eventId);
            try {
                ob = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(!isOwner){
                ParseQuery<ParseObject> query1 = new ParseQuery<>("Event");
                query1.whereContains("objectId", eventId);

                try {
                    ob2 = query1.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String members = "Number of Members: " + String.valueOf(ob.size() + 1);

            if(!isOwner){
                String owner = "Owner: " + ob2.get(0).getString("owner");
                ownerName.setText(owner);
            }

            numberOfMembers.setText(members);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.buttonEventOwnerEdit:
                //Creates the edit dialog
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.edit_event_dialog);
                dialog.show();

                final EditText eName = (EditText) dialog.findViewById(R.id.eventName);
                eName.setText(eventName);

                //Cancel button to exit dialog
                final Button cancelButton = (Button) dialog.findViewById(R.id.buttonCancel);
                cancelButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        dialog.dismiss();
                    }
                });

                //Save button to save edits
                final Button saveButton = (Button) dialog.findViewById(R.id.buttonSave);
                saveButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        final String temp = eName.getText().toString();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                        query.whereEqualTo("name", eventName);
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                parseObject.put("name", temp);
                                parseObject.saveInBackground();
                                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                                //Refreshes toolbar name
                                collapsingToolbarLayout.setTitle(temp);
                                isNameChanged = true;
                            }
                        });
                        dialog.dismiss();
                    }
                });

                break;
            case R.id.buttonEventOwnerAddMembers:
                break;
            case R.id.buttonEventOwnerFinalize:
                break;
        }
    }

    //When the back button on the toolbar is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    //This class gets and displays all the pics in the event
    private class GetEventPictures extends AsyncTask<Void, Void, Void>{
        String eventId;
        List<ParseObject> ob;
        ArrayList<Bitmap> bitmaps;
        GridViewAdapter adapter;

        public GetEventPictures(String eventId){
            this.eventId = eventId;
            bitmaps = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParseQuery<ParseObject> query = new ParseQuery<>("Picture");

            query.whereContains("eventId", eventId);
            try{
                ob = query.find();
            } catch (ParseException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(ob.size() != 0){

                for (ParseObject pictureItem : ob) {
                    //adapter.add((String) pictureItem.get("name"));
                    //adapter.add((ParseFile) pictureItem.get("picture"));    // can't not be applied
                    if(!pictureItem.getBoolean("isVideo")){
                        ParseFile image = (ParseFile) pictureItem.get("pic");
                        byte[] file = new byte[0];
                        try {
                            file = image.getData();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Bitmap imageBitmap = BitmapFactory.decodeByteArray(file, 0, file.length);

                        bitmaps.add(imageBitmap);
                    }
                }

                adapter = new GridViewAdapter(EventActivity.this, R.layout.gridview_item, bitmaps);
                eventPhotos.setAdapter(adapter);
            }
        }
    }
}
