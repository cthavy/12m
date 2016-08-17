package io.github.a12m.one2many;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    ImageView d_pic;

    String searchedName;
    String emailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_user);

        d_username = (TextView) findViewById(R.id.Text_username);
        d_email = (TextView) findViewById(R.id.Text_email);
        d_pic = (ImageView) findViewById(R.id.profile_image);

        Intent intent = getIntent();
        searchedName = intent.getStringExtra("searchedName");
        emailString = "email@email.com";

        getData(searchedName);
    }

    //Loads user searched data from Parse
    public void getData(String username){
        //Queries Parse users and finds which username matches the searched field
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (list != null){
                    ParseObject parseObject = list.get(0);

                    //Gets and converts profile pic to be displayed
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
                    //Displays the rest of the user's fields
                    d_username.setText("@"+searchedName);

                    emailString = (String) parseObject.get("email");
                    d_email.setText(emailString);
                } else {
                    System.out.println("No data");
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
