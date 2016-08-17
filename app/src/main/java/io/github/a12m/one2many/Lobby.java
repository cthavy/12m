package io.github.a12m.one2many;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.widget.EditText;

/*
Main lobby page. User should see this when first logging in or opening
the app while account is cached in local storage.

This page should allow the user to view all projects w/ their respective links,
search for other users, view their friends list, and start a new event.
 */
public class Lobby extends ListActivity {

    //Test list to populate the Lobby
    String[] itemName = {
            "The making of sr project",
            "Yosemite",
            "Nikko's house"
    };

    String search_username;
    EditText search_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        search_username = "";
        search_text = (EditText) findViewById(R.id.searchField);

        //Populates the list with array itemName using lobby_list.xml as a base.
        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.lobby_list,
                R.id.Itemname,itemName));
    }

    public void Search(View view){
        search_username = search_text.getText().toString();
        Intent intent = new Intent(this, SearchedUser.class);
        intent.putExtra("searchedName", search_username);
        startActivity(intent);
    }

    //Will bring user to their basic profile
    public void goToProfile(View view) {
        Intent intent = new Intent(getBaseContext(), Profile.class);
        startActivity(intent);
    }

    //Brings user to their notifications
    public void goToNotifications(View view){
        Intent intent = new Intent(getBaseContext(), Notifications.class);
        startActivity(intent);
    }

    //Prevents user from going back to login page while logged in (prevents a crash)
    public void onBackPressed() {

    }
}
