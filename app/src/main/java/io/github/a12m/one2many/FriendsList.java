package io.github.a12m.one2many;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class FriendsList extends AppCompatActivity {
    ListView friendsList;
    ArrayList<String> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        friendsList = (ListView) findViewById(R.id.friend_list);

        friends = new ArrayList<>();
        friends.add("cool guy");
        friends.add("cool girl");

        final ArrayAdapter arrayAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.friends_list, R.id.username, friends);
        friendsList.setAdapter(arrayAdapter);
    }

    public void AddRequestee(View view){

    }
    public void IgnoreRequestee(View view){

    }
}
