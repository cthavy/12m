package io.github.a12m.one2many;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

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

        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.friends_list,
                R.id.username,friends));
    }
}
