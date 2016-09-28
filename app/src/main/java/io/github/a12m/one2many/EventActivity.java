package io.github.a12m.one2many;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;

    Toolbar toolbar;

    TextView ownerName;
    TextView numberOfMembers;

    GridView eventPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent i = getIntent();

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarEvent);
        collapsingToolbarLayout.setTitle(i.getStringExtra("EventName"));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));

        numberOfMembers = (TextView) findViewById(R.id.textViewEventNumberOfMembers);
        ownerName = (TextView) findViewById(R.id.textViewEventOwnerName);

        //If current user is owner
        if(i.getBooleanExtra("IsOwner", false)){
            findViewById(R.id.relativeLayoutEventOwner).setVisibility(View.VISIBLE);
            String owner = "Owner: " + i.getStringExtra("OwnerName");
            ownerName.setText(owner);
            new GetNumberOfMembers(i.getStringExtra("EventId"), true).execute();
        } else {
            new GetNumberOfMembers(i.getStringExtra("EventId"), false).execute();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbarEvent);
        setSupportActionBar(toolbar);
        //Sets the back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventPhotos = (GridView) findViewById(R.id.gridViewEventPictures);


        //new GetEventPictures(i.getStringExtra("EventId")).execute();

    }

    //Get number of members in event
    private class GetNumberOfMembers extends AsyncTask<Void, Void, Void>{
        String eventId;
        boolean isOwner;
        List<ParseObject> ob;
        List<ParseObject> ob2;

        public GetNumberOfMembers(String eventId, boolean isOwner){
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
