package io.github.a12m.one2many;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/*
Main lobby page. User should see this when first logging in or opening
the app while account is cached in local storage.

This page should allow the user to view all projects w/ their respective links,
search for other users, view their friends list, and start a new event.

*Subject to change
*Needs update on navbar
 */

public class Lobby extends AppCompatActivity implements View.OnClickListener {

    ImageButton newEvent;
    ImageButton profileButton;

    ListView eventsListView;

    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        eventsListView = (ListView) findViewById(R.id.eventsList);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarLobby);

        //Populates the list with array itemName using lobby_list.xml as a base.

        newEvent = (ImageButton) findViewById(R.id.btn_newEvent);
        newEvent.setOnClickListener(this);

        profileButton = (ImageButton) findViewById(R.id.btn_profile);
        profileButton.setOnClickListener(this);

        new GetUserEvents().execute();
    }

    //Will be a settings drop down but temporarily will be edit profile
    //!!! MAY BE REDUNDANT !!!
//    public void editAccount(View view) {
//
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_newEvent:
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.create_event_dialog);
                dialog.setTitle("Create a New Event");

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

                Button cancelButton = (Button) dialog.findViewById(R.id.buttonCreateEventCanel);
                // if button is clicked, close the custom dialog
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;

            case R.id.btn_profile:
                Intent intent = new Intent(getBaseContext(), Profile.class);
                startActivity(intent);
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
            queryEventOwn.whereContains("owner", ParseUser.getCurrentUser().getUsername());
            try {
                ob = queryEventOwn.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Getting all the events this user is a part of
            ParseQuery<ParseObject> queryEventPartOf = new ParseQuery<>("EventMembers");
            queryEventPartOf.whereContains("memberUsername", ParseUser.getCurrentUser().getUsername());
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
