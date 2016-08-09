package io.github.a12m.one2many;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendsList extends ListActivity {

    String[] friends = {
            "friend 1",
            "friend 2",
            "thing 1",
            "thing 2"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List<ParseUser> list, ParseException e) {
//                if (list != null) {
//                    for (int i = 0; i < list.size(); i++){
//
//                    }
//                }
//            }
//        });

        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.friends_list,
                R.id.username,friends));
    }
}
