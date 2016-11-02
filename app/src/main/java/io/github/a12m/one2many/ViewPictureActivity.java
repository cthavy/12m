package io.github.a12m.one2many;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ViewPictureActivity extends AppCompatActivity implements View.OnClickListener {

    Button removePic;

    ImageView picture;

    ProgressBar progressBar;

    String objId;

    // TODO: Handle crash when image not found on server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        Intent i = getIntent();
        try {
            objId = i.getExtras().getString("objId");
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Unable to load picture at this time",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        removePic = (Button) findViewById(R.id.button_remove_picture);
        removePic.setOnClickListener(this);
        picture = (ImageView) findViewById(R.id.image_view_picture);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_picture);

        new getPicture(objId).execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_remove_picture:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                deletePicture();
                                dialog.dismiss();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPictureActivity.this);
                builder.setMessage("Are you sure you want to delete?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
        }
    }

    public void deletePicture(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Picture");
        query.whereEqualTo("objectId", objId);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                try {
                    parseObject.delete();
                    parseObject.saveInBackground();
                    Toast.makeText(getApplicationContext(), "Picture Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    public class getPicture extends AsyncTask<Void, Void, Void>{
        String objectId;
        ParseObject parseObject;


        public getPicture(String objId){
            objectId = objId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParseQuery query = new ParseQuery("Picture");
            query.whereEqualTo("objectId", objectId);

            try {
                parseObject = query.getFirst();
            } catch (ParseException e) {
                Toast.makeText(getApplicationContext(), "Unable to load picture at this time",
                        Toast.LENGTH_LONG).show();
                finish();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // If current user is the one who took the picture, they will be able to delete it
            // TODO: Give the ability to delete the pic to the event owner
            if(parseObject.get("takenBy").equals(ParseUser.getCurrentUser().getUsername())){
                removePic.setEnabled(true);
                removePic.setVisibility(View.VISIBLE);
            }

            String picUrl = parseObject.getParseFile("pic").getUrl();

            // Loading into the image view with Picasso
            Picasso.with(ViewPictureActivity.this)
                    .load(picUrl)
                    .into(picture, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(getApplicationContext(), "Unable to load picture at this time", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
        }
    }
}
