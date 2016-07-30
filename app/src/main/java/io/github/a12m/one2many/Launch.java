package io.github.a12m.one2many;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.Parse;
import com.parse.ParseUser;

/*
This launching activity checks to see if the user is already logged in from a previous session.
If so then the lobby class will automatically launch.
Otherwise it will bring the user to the login page.
 */
public class Launch extends AppCompatActivity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Parse.initialize(this, "5g44gXLHE9HIOzzAr7VWo0em9SWll2EvyKyPuoac",
                "359AULLuseeJcNDGsHSKZ6KniwZOHu8gG5AnpFoj");

        if (ParseUser.getCurrentUser() == null){
            intent = new Intent(this, Login.class);
        } else {
            intent = new Intent(this, Lobby.class);
        }
        startActivity(intent);
        this.finish();
    }
}
