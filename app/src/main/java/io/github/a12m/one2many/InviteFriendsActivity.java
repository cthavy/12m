package io.github.a12m.one2many;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class InviteFriendsActivity extends AppCompatActivity {

    ListView inviteFriendsLV;

    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

    }

    private class getFriends extends AsyncTask<Void, Void, Void>{
        String[] friends;
        List<ParseObject> results;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
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

            try {
                results = mainQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            friends = new String[results.size()];
            //Iterates through the list and adds all friends to the array list
            for (int i = 0; i < results.size(); i++){
                if (!results.get(i).get("fromUser").equals(ParseUser.getCurrentUser().getUsername())){
                    friends[i] = ((String) results.get(i).get("fromUser"));
                } else {
                    friends[i] = ((String) results.get(i).get("toUser"));
                }
            }

//            Toast.makeText(getApplicationContext(), "Total: " + friends.length + " results: " + results.size(), Toast.LENGTH_SHORT).show();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            pb.setVisibility(View.INVISIBLE);
            inviteFriendsLV.setVisibility(View.VISIBLE);

            ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.friends_list, R.id.username,friends);
            inviteFriendsLV.setAdapter(arrayAdapter);
        }
    }
}
