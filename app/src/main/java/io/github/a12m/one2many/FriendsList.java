package io.github.a12m.one2many;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/*
Class that just shows all of the user's friends

Accept and ignore buttons don't work. I don't know how to make it invisible.
 */
public class FriendsList extends AppCompatActivity {
    ListView friendsList;
    ArrayList<String> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        friendsList = (ListView) findViewById(R.id.friend_list);

        friends = new ArrayList<>();
        getFriends();
    }

    //Retrieves friend data from user and fills up the list
    public void getFriends(){
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

        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null){
                    //Iterates through the list and adds all friends to the array list
                    for (int i = 0; i < list.size(); i++){
                        if (!list.get(i).get("fromUser").equals(ParseUser.getCurrentUser().getUsername())){
                            friends.add((String) list.get(i).get("fromUser"));
                        } else {
                            friends.add((String) list.get(i).get("toUser"));
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.friends_list, R.id.username, friends);
                    friendsList.setAdapter(arrayAdapter);
                }
            }
        });
    }

    //Goes to the user's profile page when their username is clicked
    public void goToUser(View view) {
        final int position = friendsList.getPositionForView((View) view.getParent());
        final String person = friends.get(position);

        Intent intent = new Intent(this, SearchedUser.class);
        intent.putExtra("searchedName", person);
        startActivity(intent);
    }
}
