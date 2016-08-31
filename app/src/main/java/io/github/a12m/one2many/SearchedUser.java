package io.github.a12m.one2many;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/*
Profile class for a searched user.
Shows basic info and allows the current user
to request to add them to their friends list.

Does not display current friends and event count yet
 */
public class SearchedUser extends AppCompatActivity {
    TextView d_username;
    TextView d_email;
    TextView d_friend_count;
    ImageView d_pic;
    Button d_add;

    String searchedName;
    String emailString;
    int friendSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_user);

        d_username = (TextView) findViewById(R.id.Text_username);
        d_email = (TextView) findViewById(R.id.Text_email);
        d_friend_count = (TextView) findViewById(R.id.count_friend);
        d_pic = (ImageView) findViewById(R.id.profile_image);
        d_add = (Button) findViewById(R.id.btn_add);

        Intent intent = getIntent();
        searchedName = intent.getStringExtra("searchedName");
        emailString = "email@email.com";

        //Prevents user from searching themselves
        if (searchedName.equals(ParseUser.getCurrentUser().getUsername())){
            finish();
            Toast.makeText(getApplicationContext(), "No reason to search yourself :)", Toast.LENGTH_SHORT).show();
        }

        getData(searchedName);
        ifFriends(searchedName);
        getFriends(searchedName);
    }

    //Loads user searched data from Parse
    public void getData(String username){
        //Queries Parse users and finds which username matches the searched field
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null){
                    //Gets and converts profile pic to be displayed
                    ParseFile file = (ParseFile) parseUser.get("avatarPic");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            //If avatar pic exists, then decode it into a bitmap to load
                            if (e == null){
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                d_pic.setImageBitmap(bmp);
                            } else {
                                System.out.println("error loading picture");
                            }
                        }
                    });
                    //Displays the rest of the user's fields
                    d_username.setText("@"+searchedName);

                    emailString = (String) parseUser.get("email");
                    d_email.setText(emailString);
                } else {
                    finish();
                    Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Counts searched user's friends and displays info
    public void getFriends(String username){
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("FriendRequest");
        query1.whereEqualTo("toUser", username);
        query1.whereEqualTo("accepted", true);

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("FriendRequest");
        query2.whereEqualTo("fromUser", username);
        query2.whereEqualTo("accepted", true);

        //Joint 'or' query for finding all friends that the user has accepted or was accepted by
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null){
                    friendSize = list.size();
                    d_friend_count.setText(String.valueOf(friendSize));
                }
            }
        });
    }

    //Checks to see if the current user is already friends with searched user. Disables add button if yes
    public void ifFriends(String username){
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("FriendRequest");
        query1.whereEqualTo("fromUser", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("toUser", username);
        query1.whereEqualTo("accepted", true);

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("FriendRequest");
        query2.whereEqualTo("toUser", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("fromUser", username);
        query2.whereEqualTo("accepted", true);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

        mainQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null){
                    d_add.setVisibility(View.GONE);
                }
            }
        });
    }

    /*
    Finds if the user has already sent a friend request or not.
    If not then it will create a new object and make a request
    to the other user.
     */
    public void RequestFriend(View view){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequest");
        query.whereEqualTo("fromUser", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("toUser", searchedName);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject == null){
                    ParseObject request = new ParseObject("FriendRequest");
                    request.put("fromUser", ParseUser.getCurrentUser().getUsername());
                    request.put("toUser", searchedName);
                    request.put("accepted", false);
                    request.put("ignored", false);
                    request.saveInBackground();

                    Toast.makeText(getApplicationContext(), "Request sent", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getApplicationContext(), "Pending reply", Toast.LENGTH_LONG).show();
            }
        });
    }
}
