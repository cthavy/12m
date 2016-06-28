package io.github.a12m.one2many;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.app.ListActivity;

/*
Main lobby page. User should see this when first logging in or opening
the app while account is cached in local storage.

This page should allow the user to view all projects w/ their respective links,
search for other users, view their friends list, and start a new event.

*Subject to change
*Needs update on navbar
 */
//Deleted extension to AppCompatActivity. Not sure how this affects anything though.
public class Lobby extends ListActivity {

    //Test list to populate the Lobby
    String[] itemName = {
            "The making of sr project",
            "Yosemite",
            "Nikko's house"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        //Populates the list with array itemName using lobby_list.xml as a base.
        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.lobby_list,
                R.id.Itemname,itemName));
    }
}
