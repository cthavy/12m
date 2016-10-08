package io.github.a12m.one2many;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/*
Class to display profile page as well as present a logout button for user.
Logout will bring the app back to the login page.

Does not display friend and event count yet
 */
public class Profile extends AppCompatActivity implements View.OnClickListener {
    ImageView d_pic;
    TextView d_friends;
    TextView d_username;
    TextView d_email;
    TextView d_events;

    String username;
    String email;
    int ct_friends;
    int ct_events;

    Button editProfile;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        d_pic = (ImageView) findViewById(R.id.profile_image);
        d_friends = (TextView) findViewById(R.id.Text_friend);
        d_friends.setOnClickListener(this);
        d_username = (TextView) findViewById(R.id.Text_username);
        d_email = (TextView) findViewById(R.id.Text_email);
        d_events = (TextView) findViewById(R.id.Text_event);

        editProfile = (Button) findViewById(R.id.btn_edit);
        editProfile.setOnClickListener(this);

        logout = (Button) findViewById(R.id.btn_logout);
        logout.setOnClickListener(this);

        username = "@username";
        email = "email@email.com";
        ct_friends = 0;
        ct_events = 0;

        getPic();
        getFriends();
        getEvents();
    }

    //Loads data like profile picture and friend count from Parse
    public void getPic() {
        //Queries the user class and crabs current user as an object
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                //If user exists then grabs their avatar picture
                if (parseObject != null) {
                    ParseFile file = (ParseFile) parseObject.get("avatarPic");
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

                    //Displays username
                    username = (String) parseObject.get("username");
                    d_username.setText("@"+username);

                    //Displays email address
                    email = (String) parseObject.get("email");
                    d_email.setText(email);

                } else {
                    System.out.println("No data available");
                }
            }
        });
    }

    //Counts the number of events user has through two queries
    public void getEvents(){
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Event");
        query1.whereEqualTo("owner", ParseUser.getCurrentUser().getUsername());

        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("EventMembers");
        query2.whereEqualTo("memberUsername", ParseUser.getCurrentUser().getUsername());

        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null) {
                    ct_events = list.size();
                    query2.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (list != null){
                                ct_events += list.size();
                                d_events.setText(String.valueOf(ct_events) + " Events");
                            }
                        }
                    });
                }
            }
        });
    }

    //'or' queries to find friend count
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
                    ct_friends = list.size();
                    d_friends.setText(String.valueOf(ct_friends) + " Friends");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Text_friend:
                startActivity(new Intent(this, FriendsList.class));
                break;

            case R.id.btn_edit:
                startActivity(new Intent(this, EditProfile.class));
                finish();
                break;

            case R.id.btn_logout:
                ParseUser.logOut();
                Intent intent = new Intent(this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}
