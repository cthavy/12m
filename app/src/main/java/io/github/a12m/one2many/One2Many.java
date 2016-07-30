package io.github.a12m.one2many;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Aqeel on 7/29/16.
 */

/**
 * This class initializes the whole application and routes parse
 * so you dont need to initialize parse in every class, just
 * this once
 */

public class One2Many extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this.getApplicationContext())
                .applicationId("5g44gXLHE9HIOzzAr7VWo0em9SWll2EvyKyPuoac")
                .clientKey("359AULLuseeJcNDGsHSKZ6KniwZOHu8gG5AnpFoj")
                .build()
        );
    }
}
