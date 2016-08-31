package io.github.a12m.one2many;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/*
Main lobby page. User should see this when first logging in or opening
the app while account is cached in local storage.

This page should allow the user to view all projects w/ their respective links,
search for other users, view their friends list, and start a new event.
 */
public class Lobby extends ListActivity {

public class Lobby extends AppCompatActivity implements View.OnClickListener {

    ImageButton newEvent;
    ImageButton profileButton;
    ImageButton notifsButton;

    Button searchButton;

    ListView eventsListView;

    ProgressBar mProgressBar;
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    Uri imageUri;
    Button takePictureButton;

    //Test list to populate the Lobby
    String[] itemName = {
            "The making of sr project",
            "Yosemite",
            "Nikko's house"
    };

    ArrayList<String> searchableUsers;

    String search_username;
    private AutoCompleteTextView search_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        eventsListView = (ListView) findViewById(R.id.eventsList);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarLobby);

        search_text = (AutoCompleteTextView) findViewById(R.id.searchField);
        searchableUsers = new ArrayList<>();

        getListofUsers();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, searchableUsers);

        search_text.setAdapter(adapter);
        search_text.setThreshold(1);

        //Populates the list with array itemName using lobby_list.xml as a base.
        this.setListAdapter(new ArrayAdapter<>(
                this, R.layout.lobby_list,
                R.id.Itemname,itemName));

        takePictureButton = (Button) findViewById(R.id.buttonTakePicture);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Photo");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO_CODE);

            }
        });

    }

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
                for (int i = 0; i < list.size(); i++){
                    searchableUsers.add((String) list.get(i).get("username"));
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

                final Button createButton = (Button) dialog.findViewById(R.id.buttonCreateEventAccept);
                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View vCreate) {
                        String eventNameString = eventName.getText().toString();
                        createButton.setEnabled(false);
                        eventName.setEnabled(false);
                        if (!eventNameString.replace(" ", "").equals("")) {
                            ParseObject parseEvent = new ParseObject("Event");
                            parseEvent.put("owner", ParseUser.getCurrentUser().getUsername());
                            parseEvent.put("name", eventNameString);
                            parseEvent.put("active", true);
                            parseEvent.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        dialog.dismiss();
                                        new GetUserEvents().execute();
                                    } else {
                                        Toast.makeText(vCreate.getContext(), "Something went wrong",
                                                Toast.LENGTH_SHORT).show();
                                        createButton.setEnabled(true);
                                        eventName.setEnabled(true);
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(vCreate.getContext(), "Please enter a name for the event",
                                    Toast.LENGTH_SHORT).show();
                            eventName.setEnabled(true);
                            createButton.setEnabled(true);
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

            //Adding events a part of to the list
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
            eventsListView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
