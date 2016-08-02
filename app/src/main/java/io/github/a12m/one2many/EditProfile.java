package io.github.a12m.one2many;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

/*
This class is for the user to update their info like username or email address.
 */
public class EditProfile extends AppCompatActivity {
    private EditText newUser;
    private EditText newEmail;
    private ImageButton newPic;

    private String c_user;
    private String c_email;
    private Bitmap c_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_dialog);

        newUser = (EditText) findViewById(R.id.edit_username);
        newEmail = (EditText) findViewById(R.id.edit_email);
        newPic = (ImageButton) findViewById(R.id.edit_img);

        //Queries to load user's fields
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                c_user = (String) parseObject.get("username");
                c_email = (String) parseObject.get("email");

                newUser.setText(c_user);
                newEmail.setText(c_email);

                //Grabs the profile picture and converts into bmp to view
                ParseFile file = (ParseFile) parseObject.get("avatarPic");
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null){
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            newPic.setImageBitmap(bmp);
                            c_img = bmp;
                        }
                    }
                });
            }
        });
    }

    //Lets user change profile pic to view before actually saving
    public void ChangePic(View view){
        Intent photoGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoGalleryIntent.setType("image/*");
        startActivityForResult(photoGalleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                Uri selectedImg = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImg, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                c_img = BitmapFactory.decodeFile(picturePath);
                newPic.setImageBitmap(c_img);
            }
        }
    }

    //Cancel the edit and go back to profile
    public void Cancel(View view){
        finish();
    }

    //Saves whatever is in the edit fields and updates the user's info on the backend
    public void Save(View view){
        c_user = newUser.getText().toString();
        c_email = newEmail.getText().toString();

        UpdateInfo();

        finish();
    }

    //Self explanatory
    public void UpdateInfo(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                //Compress user selected image and creates byte[] type for ParseFile
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                c_img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();

                //Updates each field into parse
                ParseFile file = new ParseFile("updatedPic.png", image);
                parseObject.put("avatarPic", file);
                parseObject.put("email", c_email);
                parseObject.put("username", c_user);
                parseObject.saveInBackground();
            }
        });
    }
}
