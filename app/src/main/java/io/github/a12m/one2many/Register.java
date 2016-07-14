package io.github.a12m.one2many;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/*
Register new users page. Will gather user input and create a new user stored in Parse.
 */
public class Register extends AppCompatActivity {
    EditText newUser;
    EditText newPass;
    EditText confirmPass;
    EditText newEmail;
    String username;
    String password;
    String cPass;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Gathers values from user input text fields
        newUser = (EditText) findViewById(R.id.new_user);
        newPass = (EditText) findViewById(R.id.new_pass);
        confirmPass = (EditText) findViewById(R.id.confirm_pass);
        newEmail = (EditText) findViewById(R.id.new_email);
    }

    //Register button method
    public void register_new_user(View view){
        //String values of password and confirm password
        password = newPass.getText().toString();
        cPass = confirmPass.getText().toString();

        //Checks to see if the pass and confirmed pass are the same
        if (!password.equals(cPass)){
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            //String values for username and email
            username = newUser.getText().toString();
            email = newEmail.getText().toString();

            //Makes a new Parse user with user input parameters
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            //Parse method for signing up the new user. Automatically checks if username and email are unique
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Intent intent = new Intent(getBaseContext(), Lobby.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Username or email already in use", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
