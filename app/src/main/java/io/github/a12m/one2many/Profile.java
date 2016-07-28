package io.github.a12m.one2many;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class Profile extends AppCompatActivity {
    ImageView d_pic;
    TextView d_friends;
    TextView d_username;
    String username;
    int ct_friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        d_pic = (ImageView) findViewById(R.id.profile_image);
        d_friends = (TextView) findViewById(R.id.count_friend);
        d_username = (TextView) findViewById(R.id.Text_username);

        ct_friends = 0;

        getData();
    }

    //Loads data like profile picture and friend count from Parse
    public void getData() {
        //Queries the user class and crabs current user as an object
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
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

                    username = (String) parseObject.get("username");
                    d_username.setText("@"+username);

                    //friend's list undefined as of right now

//                    JSONArray arr = (JSONArray) parseObject.get("friendsList");
//                    ct_friends = arr.length();
//                    d_friends.setText(ct_friends);
                } else {
                    System.out.println("No picture available");
                }
            }
        });
    }

    //Self explanatory
    public void logOut(View view) {
        ParseUser.logOut();
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
