package io.github.a12m.one2many;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/*
Class that displays all of the requests to the user.
Broken up into two sections, friends and events, the user will be able
to see which user sent a request and what even they're being invited to.

They can choose to accept, ignore, or not respond in which case ignore
will remove the request from ever coming up again.
 */
public class Notifications extends AppCompatActivity {
    ListView eventslist;
    ListView friendslist;

    ArrayList<String> requestee;
    ArrayList<String> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        friendslist = (ListView) findViewById(R.id.FR_list);
        eventslist = (ListView) findViewById(R.id.EV_list);

        requestee = new ArrayList<>();
        events = new ArrayList<>();

        getFriendRequests();

        events.add("nikko's cool event");
        events.add("tung's swag event");
        events.add("mohammed's lit event");

        final ArrayAdapter arrayAdapter1 = new ArrayAdapter(getBaseContext(), R.layout.friends_list, R.id.username, events);
        eventslist.setAdapter(arrayAdapter1);
    }

    //Queries to find all unanswered friend requests that are not ignored
    public void getFriendRequests(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequest");
        query.whereEqualTo("toUser", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("accepted", false);
        query.whereEqualTo("ignored", false);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null){
                    for (int i = 0; i < list.size(); i++){
                        requestee.add((String) list.get(i).get("fromUser"));
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.friends_list, R.id.username, requestee);
                    friendslist.setAdapter(arrayAdapter);
                }
            }
        });
    }

    //Goes to the user's profile page when their username is clicked
    public void goToUser(View view){
        final int position = friendslist.getPositionForView((View) view.getParent());
        final String person = requestee.get(position);

        Intent intent = new Intent(this, SearchedUser.class);
        intent.putExtra("searchedName", person);
        startActivity(intent);
    }

    //Adds friend from their position on the list when the accept button is clicked
    public void AddRequestee(View view){
        final int position = friendslist.getPositionForView((View) view.getParent());
        final String userToAdd = requestee.get(position);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequest");
        query.whereEqualTo("toUser", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("fromUser", userToAdd);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null){
                    parseObject.put("accepted", true);
                    parseObject.saveInBackground();

                    Toast.makeText(getApplicationContext(), userToAdd+" added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Removes this person and updates the list in real time after being added
        requestee.remove(position);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.friends_list, R.id.username, requestee);
        friendslist.setAdapter(arrayAdapter);
    }

    //Ignores the person requesting when button is clicked
    public void IgnoreRequestee(View view){
        final int position = friendslist.getPositionForView((View) view.getParent());
        final String userToRemove = requestee.get(position);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequest");
        query.whereEqualTo("toUser", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("fromUser", userToRemove);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null){
                    parseObject.put("ignored", true);
                    parseObject.saveInBackground();

                    Toast.makeText(getApplicationContext(), userToRemove+" ignored", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Removes this person and updates the list in real time after being added
        requestee.remove(position);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.friends_list, R.id.username, requestee);
        friendslist.setAdapter(arrayAdapter);
    }
}

