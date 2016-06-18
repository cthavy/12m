package io.github.a12m.one2many;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Aqeel on 4/22/16.
 */
public class one2many extends android.app.Application {

    @Override
    public void onCreate(){
        super.onCreate();

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

        ParseValues a = new ParseValues();

        Parse.initialize(new Parse.Configuration.Builder(this.getApplicationContext())
                .applicationId(a.getAppId())
                .clientKey(a.getClientKey())
                .server(a.getServer())
                .build()
        );
    }
}
