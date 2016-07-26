package io.github.a12m.one2many;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.parse.ParseFile;
import com.parse.ParseUser;


public class Profile extends AppCompatActivity {
    ImageView profilePic;
    ParseFile fileObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = (ImageView) findViewById(R.id.profile_image);
    }

    //Self explanatory
    public void logOut(View view) {
        ParseUser.logOut();
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
