package io.github.a12m.one2many;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class EditProfileDialog extends AppCompatActivity {
    EditText newUser;
    EditText newEmail;

    String c_user;
    String c_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_dialog);

        newUser = (EditText) findViewById(R.id.edit_username);
        newEmail = (EditText) findViewById(R.id.edit_email);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                c_user = (String) parseObject.get("username");
                c_email = (String) parseObject.get("email");

                newUser.setText(c_user);
                newEmail.setText(c_email);
            }
        });
    }

    public void Cancel(View view){
        finish();
        startActivity(new Intent(this, Profile.class));
    }

    public void Save(View view){
        c_user = newUser.getText().toString();
        c_email = newEmail.getText().toString();

        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(c_user);
        user.setEmail(c_email);

        user.saveInBackground();
        finish();
        startActivity(new Intent(this, Profile.class));
    }
}
