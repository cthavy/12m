package io.github.a12m.one2many;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/*
Password resetting page that will request the user's email address
associated with their account and then send a link to reset the password.
 */
public class PasswordReset extends AppCompatActivity {
    EditText inputEmail;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        //User input value of email
        inputEmail = (EditText) findViewById(R.id.sendEmail);
    }

    //Button method for sending forgotten password link
    public void forgotPass(View view){
        //String value of email
        email = inputEmail.getText().toString();

        //Parse method to send password reset link to user's email address
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    finish();
                    Toast.makeText(getApplicationContext(), "Password reset link sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to send link", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
