package io.github.a12m.one2many;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

        search_username = "";
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

    //Searches for selected user and opens up their profile page
    public void Search(View view){
        search_username = search_text.getText().toString();
        Intent intent = new Intent(this, SearchedUser.class);
        intent.putExtra("searchedName", search_username);
        startActivity(intent);
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
