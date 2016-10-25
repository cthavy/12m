package io.github.a12m.one2many;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/*
Main lobby page. User should see this when first logging in or opening
the app while account is cached in local storage.

This page should allow the user to view all projects w/ their respective links,
search for other users, view their friends list, and start a new event.
 */

public class Lobby extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {


    private static final int REQUEST_CAMERA = 0;

    ImageButton newEvent;
    ImageButton profileButton;
    ImageButton notifsButton;
    ImageButton btn_camera;

    Button searchButton;

    ListView eventsListView;



    ProgressBar mProgressBar;
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    Uri imageUri;

    //Test list to populate the Lobby

    ArrayList<String> searchableUsers;
    ArrayList<String> selectedFriends = new ArrayList<>();

    String search_username;
    private AutoCompleteTextView search_text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        eventsListView = (ListView) findViewById(R.id.eventsList);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarLobby);

        search_username = "";
        search_text = (AutoCompleteTextView) findViewById(R.id.searchField);
        searchableUsers = new ArrayList<>();

        getListofUsers();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, searchableUsers);

        search_text.setAdapter(adapter);
        search_text.setThreshold(1);

        btn_camera = (ImageButton) findViewById(R.id.btn_camera);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        newEvent = (ImageButton) findViewById(R.id.btn_newEvent);
        newEvent.setOnClickListener(this);

        profileButton = (ImageButton) findViewById(R.id.btn_profile);
        profileButton.setOnClickListener(this);

        notifsButton = (ImageButton) findViewById(R.id.btn_notifs);
        notifsButton.setOnClickListener(this);

        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        new GetUserEvents().execute();
    }

    /*
    This method queries the list of all users in the database
    so that it could fill in for the auto complete search bar
     */
    public void getListofUsers(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null){
                    for (int i = 0; i < list.size(); i++){
                        searchableUsers.add((String) list.get(i).get("username"));
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_newEvent:
                //Opens the dialog to create new event and handles it
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.create_event_dialog);
                dialog.show();

                // set the custom dialog components - text, image and button
                final EditText eventName = (EditText) dialog.findViewById(R.id.editTextNewEventName);

                ImageView image = (ImageView) dialog.findViewById(R.id.imageViewNewEventProfilePic);
                image.setImageResource(R.drawable.lobby_example);

                final Button cancelButton = (Button) dialog.findViewById(R.id.buttonCreateEventCanel);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final Button chooseFriends = (Button) dialog.findViewById(R.id.buttonNewEventChooseFriends);
                chooseFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //This dialog shows up when a user wants to select their friends to invite to event
                        final Dialog dialogInviteFriends = new Dialog(v.getContext());
                        dialogInviteFriends.setContentView(R.layout.activity_invite_friends);
                        dialogInviteFriends.show();

                        final ListView inviteFriendsLV = (ListView) dialogInviteFriends.findViewById(R.id.listViewInviteFriends);

                        //ProgressBar pb = (ProgressBar) dialogInviteFriends.findViewById(R.id.progressBarInviteFriends);

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

                        String[] friends = new String[results.size()];
                        //Iterates through the list and adds all friends to the array
                        for (int i = 0; i < results.size(); i++){
                            if (!results.get(i).get("fromUser").equals(ParseUser.getCurrentUser().getUsername())){
                                friends[i] = ((String) results.get(i).get("fromUser"));
                            } else {
                                friends[i] = ((String) results.get(i).get("toUser"));
                            }
                        }

                        //pb.setVisibility(View.INVISIBLE);
                        inviteFriendsLV.setVisibility(View.VISIBLE);
                        inviteFriendsLV.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                        inviteFriendsLV.setItemsCanFocus(true);

                        final ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,friends);
                        inviteFriendsLV.setAdapter(arrayAdapter);

                        //When the user clicks the button to add these friends
                        Button selectedFriends = (Button) dialogInviteFriends.findViewById(R.id.buttonChosenFriends);
                        selectedFriends.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SparseBooleanArray checked = inviteFriendsLV.getCheckedItemPositions();
                                ArrayList<String> selectedItems = new ArrayList<>();
                                //Getting the names of the user's friends
                                for (int i = 0; i < checked.size(); i++) {
                                    // Item position in adapter
                                    int position = checked.keyAt(i);
                                    // Add sport if it is checked i.e.) == TRUE!
                                    if (checked.valueAt(i))
                                        selectedItems.add((String) arrayAdapter.getItem(position));
                                }

                                //Showing the user how many friends they selected
                                chooseFriends.setText("Choose Friends - " + selectedItems.size());

                                //Saving it in a variable so we can access it when creating the event
                                saveToSelectedFriends(selectedItems);

                                dialogInviteFriends.dismiss();
                            }
                        });

                        //Cancel button to exit dialog
                        Button cancelInvites = (Button) dialogInviteFriends.findViewById(R.id.buttonInviteCancel);
                        cancelInvites.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                dialogInviteFriends.dismiss();
                            }
                        });
                    }
                });

                final Button createButton = (Button) dialog.findViewById(R.id.buttonCreateEventAccept);
                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View vCreate) {
                        String eventNameString = eventName.getText().toString();
                        createButton.setEnabled(false);
                        eventName.setEnabled(false);
                        cancelButton.setEnabled(false);

                        //Making sure they didn't enter a blank name
                        if (!eventNameString.replace(" ", "").equals("")) {

                            final ParseObject parseEvent = new ParseObject("Event");
                            parseEvent.put("owner", ParseUser.getCurrentUser().getUsername());
                            parseEvent.put("name", eventNameString);
                            parseEvent.put("active", true);
                            parseEvent.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ArrayList<String> friendNames = getSelectedFriends();

                                        //Once the event is saved, send a request to all the friends
                                        if(friendNames.size() != 0){
                                            for(String friendName : friendNames){
                                                ParseObject req = new ParseObject("EventRequest");
                                                req.put("fromUser", ParseUser.getCurrentUser().getUsername());
                                                req.put("toUser", friendName);
                                                req.put("forEventId", parseEvent.getObjectId());
                                                req.put("accepted", false);
                                                req.put("ignored", false);
                                                try {
                                                    req.save();
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        }

                                        dialog.dismiss();

                                        //Refresh the events list once event saved and requests are sent
                                        new GetUserEvents().execute();
                                    } else {
                                        Toast.makeText(vCreate.getContext(), "Something went wrong",
                                                Toast.LENGTH_SHORT).show();
                                        createButton.setEnabled(true);
                                        eventName.setEnabled(true);
                                        cancelButton.setEnabled(true);
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(vCreate.getContext(), "Please enter a name for the event",
                                    Toast.LENGTH_SHORT).show();
                            eventName.setEnabled(true);
                            createButton.setEnabled(true);
                            cancelButton.setEnabled(true);
                        }
                    }
                });

                break;

            case R.id.btn_profile:
                Intent intent = new Intent(getBaseContext(), Profile.class);
                startActivity(intent);
                break;

            case R.id.btn_notifs:
                Intent i = new Intent(getBaseContext(), Notifications.class);
                startActivity(i);
                break;

            case R.id.searchButton:
                //Searches for selected user and opens up their profile page
                search_username = search_text.getText().toString();
                Intent intent2 = new Intent(this, SearchedUser.class);
                intent2.putExtra("searchedName", search_username);
                startActivity(intent2);
                break;
        }
    }


    private void saveToSelectedFriends(ArrayList<String> fds){
        selectedFriends = fds;
    }

    public ArrayList<String> getSelectedFriends(){
        return selectedFriends;
    }


    private class GetUserEvents extends AsyncTask<Void, Void, Void> {
        String[] itemName;
        List<ParseObject> ob;
        List<ParseObject> ob2;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            eventsListView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
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
            eventsListView.setAdapter(new EventsListAdapter(getApplicationContext(), itemName));
            eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Check which list this position is coming from 
                    // If the position is greater than the size of the first(owned events) list 
                    // then look in the second list
                    if(position > ob.size() - 1){
                        // String eventName = itemName[position];
                        // String eventId = (String) ob2.get(position - ob.size()).get("eventId");

                        Intent i = new Intent(Lobby.this, EventActivity.class);
                        i.putExtra("EventName", itemName[position]);
                        i.putExtra("EventId", (String) ob2.get(position - ob.size()).get("eventId"));
                        i.putExtra("IsOwner", false);

                        startActivity(i);
                    } else {
                        // This means that the event is in the first list
                        // String eventName = itemName[position];
                        // String eventId = ob.get(position).getObjectId();

                        Intent i = new Intent(Lobby.this, EventActivity.class);
                        i.putExtra("EventName", itemName[position]);
                        i.putExtra("EventId", ob.get(position).getObjectId());
                        i.putExtra("IsOwner", true);
                        i.putExtra("OwnerName", ParseUser.getCurrentUser().getUsername());

                        startActivity(i);
                    }
                }
            });

            eventsListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Method to launch camera after permission accepted from user
     */
    void takePicture() {


        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 0);
    }

    /**
     * Method to launch camera after permission accepted from user
     */
    void takeVideo() {
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        startActivityForResult(intent, 0);
    }

    /**
     * Method to request permission for camera
     */
    private void requestCameraPermission() {


        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i("Lobby", "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length > 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                // Camera permission has been granted, preview can be displayed

                //takePicture();
                startActivity(new Intent(Lobby.this, CustomCamera.class));

            } else {
                //Permission not granted
                Toast.makeText(Lobby.this,"You need to grant camera permission to use camera",Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * Method to check permission
     */
    void checkCameraPermission() {
        boolean isGranted;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.

            requestCameraPermission();


        } else {

            startActivity(new Intent(Lobby.this, CustomCamera.class));
//            takePicture();
//            takeVideo();

        }
    }

}
