package io.github.a12m.one2many;

//import android.support.v7.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/*
Main lobby page. User should see this when first logging in or opening
the app while account is cached in local storage.

This page should allow the user to view all projects w/ their respective links,
search for other users, view their friends list, and start a new event.

*Subject to change
*Needs update on navbar
 */
//Deleted extension to AppCompatActivity. Not sure how this affects anything though.
public class Lobby extends ListActivity implements View.OnClickListener {

    ImageButton newEvent;

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

        newEvent = (ImageButton) findViewById(R.id.btn_newEvent);
        newEvent.setOnClickListener(this);
    }

    //Will bring user to their basic profile
    public void goToProfile(View view) {
        Intent intent = new Intent(getBaseContext(), Profile.class);
        startActivity(intent);
    }

    //Will be a settings drop down but temporarily will be edit profile
    public void editAccount(View view) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_newEvent:
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.create_event_dialog);
                dialog.setTitle("Create a New Event");

                // set the custom dialog components - text, image and button
                EditText eventName = (EditText) dialog.findViewById(R.id.editTextNewEventName);

                ImageView image = (ImageView) dialog.findViewById(R.id.imageViewNewEventProfilePic);
                image.setImageResource(R.drawable.lobby_example);

                Button createButton = (Button) dialog.findViewById(R.id.buttonCreateEventAccept);
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


        }
    }
}
