package io.github.a12m.one2many;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity implements View.OnClickListener{

    CollapsingToolbarLayout collapsingToolbarLayout;

    Toolbar toolbar;

    ImageButton coverPic;

    TextView ownerName;
    TextView numberOfMembers;

    String eventName;
    int numberOfMembersCount;

    List<ParseObject> selectedMembers;

    Button btnEdit;
    Button btnMembers;
    Button btnFinalize;

    GridView eventPhotos;

    boolean isNameChanged = false;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent i = getIntent();

        eventName = i.getStringExtra("EventName");

        //crashes app
        coverPic = (ImageButton) findViewById(R.id.imageViewEventPage);
        GetCoverPic();

        selectedMembers = new ArrayList<>();

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

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_event_page);

        new GetEventPictures(i.getStringExtra("EventId")).execute();

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

    //Gets the cover photo and sets it
    public void GetCoverPic() {
        ParseQuery<ParseObject> queryCover = new ParseQuery<>("Event");
        queryCover.getInBackground(getIntent().getStringExtra("EventId"), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null){
                    ParseFile file = (ParseFile) parseObject.get("eventImg");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            if (e == null){
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                coverPic.setImageBitmap(bmp);
                            } else {
                                System.out.println("error loading picture");
                            }
                        }
                    });
                }
            }
        });
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

            numberOfMembersCount = ob.size() + 1;
            String members = "Number of Members: " + String.valueOf(numberOfMembersCount);

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
                final Dialog dialogRename = new Dialog(v.getContext());
                dialogRename.setContentView(R.layout.edit_event_dialog);
                dialogRename.show();

                final EditText eName = (EditText) dialogRename.findViewById(R.id.eventName);
                eName.setText(eventName);

                final CheckBox deleteCheck = (CheckBox) dialogRename.findViewById(R.id.ChboxDelete);

                //Cancel button to exit dialog
                final Button cancelButton = (Button) dialogRename.findViewById(R.id.buttonCancel);
                cancelButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        dialogRename.dismiss();
                    }
                });

                //Save button to save edits
                final Button saveButton = (Button) dialogRename.findViewById(R.id.buttonSave);
                saveButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //Checks to see if user wants to delete event
                        if (deleteCheck.isChecked()){
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            query.whereEqualTo("objectId", getIntent().getStringExtra("EventId"));
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (e == null){
                                        parseObject.deleteInBackground();
                                        finish();
                                        startActivity(new Intent(EventActivity.this, Lobby.class));
                                        Toast.makeText(getApplicationContext(), "Event destroyed", Toast.LENGTH_LONG).show();
                                    } else
                                        Toast.makeText(getApplicationContext(), "Error in deleting event", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
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
                            dialogRename.dismiss();
                        }

                    }
                });
                break;

            case R.id.buttonEventOwnerAddMembers:
                //Creates dialog for editing the event members
                final Dialog dialogEditMembers = new Dialog(v.getContext());
                dialogEditMembers.setContentView(R.layout.activity_invite_friends);
                dialogEditMembers.show();

                final ListView inviteFriendsList = (ListView) dialogEditMembers.findViewById(R.id.listViewInviteFriends);

                //Getting all the names of the user's friends
                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("FriendRequest");
                query1.whereEqualTo("toUser", ParseUser.getCurrentUser().getUsername());
                query1.whereEqualTo("accepted", true);

                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("FriendRequest");
                query2.whereEqualTo("fromUser", ParseUser.getCurrentUser().getUsername());
                query2.whereEqualTo("accepted", true);

                //Joint 'or' query for finding all friends that the user has accepted or was accepted by
                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                queries.add(query1);
                queries.add(query2);

                ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

                List<ParseObject> results = null;

                try {
                    results = mainQuery.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                final String[] friends = new String[results.size()];
                //Iterates through the list and adds all friends to the array
                for (int i = 0; i < results.size(); i++){
                    if (!results.get(i).get("fromUser").equals(ParseUser.getCurrentUser().getUsername())){
                        friends[i] = ((String) results.get(i).get("fromUser"));
                    } else {
                        friends[i] = ((String) results.get(i).get("toUser"));
                    }
                }

                inviteFriendsList.setVisibility(View.VISIBLE);
                inviteFriendsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                inviteFriendsList.setItemsCanFocus(true);

                final ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,friends);
                inviteFriendsList.setAdapter(arrayAdapter);

                //This block of code checks friends that are already members of the event
                if (friends.length != 0){
                    List<ParseObject> results2 = null;
                    ParseQuery<ParseObject> membersQuery = ParseQuery.getQuery("EventMembers");
                    membersQuery.whereEqualTo("eventId", getIntent().getStringExtra("EventId"));
                    try{
                        results2 = membersQuery.find();
                        setMembers(results2);
                    } catch (ParseException e){
                        e.printStackTrace();
                    }
                    for (int i = 0; i < results2.size(); i++){
                        for (int j = 0; j < friends.length; j++){
                            if (results2.get(i).get("memberUsername").equals(friends[j])){
                                inviteFriendsList.setItemChecked(j, true);
                                break;
                            }
                        }
                    }
                }

                //When the user clicks the button to add these friends
                Button selectedFriends = (Button) dialogEditMembers.findViewById(R.id.buttonChosenFriends);
                selectedFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Remove members if they were unchecked
                        for (ParseObject member : getMembers()){
                            for (int j = 0; j < friends.length; j++){
                                if (member.get("memberUsername").equals(friends[j]) && !inviteFriendsList.isItemChecked(j)){
                                    ParseQuery<ParseObject> queryRemove = ParseQuery.getQuery("EventMembers");
                                    queryRemove.whereEqualTo("eventId", getIntent().getStringExtra("EventId"));
                                    queryRemove.whereEqualTo("memberUsername", member.get("memberUsername"));
                                    queryRemove.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject parseObject, ParseException e) {
                                            parseObject.deleteInBackground();
                                            //numberOfMembersCount--;
                                        }
                                    });
                                }
                            }
                        }

                        //Creates new arraylist for all the checked people
                        SparseBooleanArray checked = inviteFriendsList.getCheckedItemPositions();
                        ArrayList<String> selectedItems = new ArrayList<>();
                        //Getting the names of the user's friends
                        for (int i = 0; i < checked.size(); i++) {
                            // Item position in adapter
                            int position = checked.keyAt(i);
                            // Add sport if it is checked i.e.) == TRUE!
                            if (checked.valueAt(i))
                                selectedItems.add((String) arrayAdapter.getItem(position));
                        }

                        //Sends invites if there are any checked friends
                        if (selectedItems.size() != 0){
                            for (String list : selectedItems) {
                                Boolean check = false;
                                //Checks to see if the checked list already has members in event
                                for (ParseObject isMember : getMembers()){
                                    if (isMember.get("memberUsername").equals(list)){
                                        check = true;
                                    }
                                }
                                //Invites friends that are already not a member of the event
                                if (!check){
                                    ParseObject req = new ParseObject("EventRequest");
                                    req.put("fromUser", ParseUser.getCurrentUser().getUsername());
                                    req.put("toUser", list);
                                    req.put("forEventId", getIntent().getStringExtra("EventId"));
                                    req.put("accepted", false);
                                    req.put("ignored", false);
                                    try {
                                        req.save();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        }
                        //Not working
                        //numberOfMembers.setText("Number of Members: " + String.valueOf(numberOfMembersCount));
                        dialogEditMembers.dismiss();
                    }
                });

                //Cancel button to exit dialog
                Button cancelInvites = (Button) dialogEditMembers.findViewById(R.id.buttonInviteCancel);
                cancelInvites.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        dialogEditMembers.dismiss();
                    }
                });
                break;

            case R.id.buttonEventOwnerFinalize:
                break;
        }
    }

    //Setter for members of current event
    private void setMembers(List<ParseObject> listOfMembers){
        selectedMembers = listOfMembers;
    }

    //Getter for members of current event
    public List<ParseObject> getMembers() {
        return selectedMembers;
    }

    //When the back button on the toolbar is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void convertBytesToFile(byte[] bytearray) {
        try {

            File outputFile = File.createTempFile("file", "mp4", getCacheDir());
            outputFile.deleteOnExit();
            FileOutputStream fileoutputstream = new FileOutputStream("tempMp4");
            fileoutputstream.write(bytearray);
            fileoutputstream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //This class gets and displays all the pics in the event
    private class GetEventPictures extends AsyncTask<Void, Void, Void>{
        String eventId;
        List<ParseObject> ob;
//        ArrayList<Bitmap> bitmaps;
        GridViewAdapter adapter;
        ArrayList<ParseObject> links = new ArrayList<>();

        public GetEventPictures(String eventId){
            this.eventId = eventId;
//            bitmaps = new ArrayList<>();
            links = new ArrayList<>();
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
                for(ParseObject pictureItem: ob){
                    links.add(pictureItem);
                }
            }

            progressBar.setVisibility(View.INVISIBLE);

            adapter = new GridViewAdapter(EventActivity.this, R.layout.gridview_item, links);
            eventPhotos.setAdapter(adapter);

            //Allows for gridview scrolling within scrollview
            eventPhotos.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            eventPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final ParseObject obj = ob.get(position);
                    if(obj.getBoolean("isVideo")){


                        // TODO: Video deletes but need to refresh activity
                        if(obj.getString("takenBy").equals(ParseUser.getCurrentUser().getUsername())){
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //View button clicked
                                            ParseFile vid = (ParseFile) obj.get("pic");
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.parse(vid.getUrl()), "video/*");
                                            startActivity(intent);
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //Delete button clicked
                                            try {
                                                obj.delete();
                                                obj.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if(e == null){
                                                            Toast.makeText(getApplicationContext(), "Video Deleted", Toast.LENGTH_SHORT)
                                                                    .show();
                                                            Intent intent = getIntent();
                                                            finish();
                                                            startActivity(intent);
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                });
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                            builder.setMessage("Do you want to view or delete this?").setPositiveButton("View", dialogClickListener)
                                    .setNegativeButton("Delete", dialogClickListener).show();
                        } else {
                            ParseFile vid = (ParseFile) obj.get("pic");
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(vid.getUrl()), "video/*");
                            startActivity(intent);
                        }


                    } else {
                        Intent i = new Intent(EventActivity.this, ViewPictureActivity.class);
                        i.putExtra("objId", ob.get(position).getObjectId());

                        startActivity(i);
                    }
                }
            });
        }
    }


// Keeping these in case needed for the future
//    public void addImageWithPicasso(final ArrayList<Bitmap> theList, String imageUrl, final  String failedVideoOrPic){
//        Picasso.with(this)
//                .load(imageUrl)
//                .resize(140, 208)
//                .into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        theList.add(bitmap);
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//                        if(failedVideoOrPic.equals("Video")){
//                            theList.add(
//                                    (
//                                            (BitmapDrawable) getResources().getDrawable(R.drawable.video_preview)
//                                    ).getBitmap()
//                            );
//                        } else {
//                            theList.add(
//                                    (
//                                            (BitmapDrawable) getResources().getDrawable(R.drawable.error)
//                                    ).getBitmap()
//                            );
//                        }
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                });
//    }
//
//    public void addImageWithPicasso(final ArrayList<Bitmap> theList, ParseFile image, String imageUrl, final  String failedVideoOrPic){
//        try {
//            Picasso.with(this)
//                    .load(image.getFile())
//                    .resize(140, 208)
//                    .into(new Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                            theList.add(bitmap);
//                        }
//
//                        @Override
//                        public void onBitmapFailed(Drawable errorDrawable) {
//                            if(failedVideoOrPic.equals("Video")){
//                                theList.add(
//                                        (
//                                                (BitmapDrawable) getResources().getDrawable(R.drawable.video_preview)
//                                        ).getBitmap()
//                                );
//                            } else {
//                                theList.add(
//                                        (
//                                                (BitmapDrawable) getResources().getDrawable(R.drawable.error)
//                                        ).getBitmap()
//                                );
//                            }
//                        }
//
//                        @Override
//                        public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                        }
//                    });
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
}