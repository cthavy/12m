package io.github.a12m.one2many;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

/*
Login page with links to registration page and forgot password page.
 */
public class Login extends AppCompatActivity {
    EditText input_username;
    EditText input_pw;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Calls onCreateParse method
        //onCreateParse();

        //Gathers username and password from edit fields
        input_username = (EditText) findViewById(R.id.editTextLogin);
        input_pw = (EditText) findViewById(R.id.editTextPW);
    }

    //Login and goes to Lobby
    public void loginNow(View view){
        //Grab username and password values for string comparison
        username = input_username.getText().toString();
        password = input_pw.getText().toString();

        //Login attempt by checking username and password with Parse server
        ParseUser.logInInBackground(username, password,
                new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null){
                            Intent intent = new Intent(getBaseContext(), Lobby.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "User or password invalid", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Initializes Parse server with app key and client key
    public void onCreateParse(){
        Parse.initialize(this, "5g44gXLHE9HIOzzAr7VWo0em9SWll2EvyKyPuoac",
                "359AULLuseeJcNDGsHSKZ6KniwZOHu8gG5AnpFoj");
    }

    //Forgot password link, goes to forgot pass page
    public void forgotPass(View view){
        Intent intent = new Intent(getBaseContext(), PasswordReset.class);
        startActivity(intent);
    }

    //Register link, goes to Register page
    public void register(View view){
        Intent intent = new Intent(getBaseContext(), Register.class);
        startActivity(intent);
    }
}
