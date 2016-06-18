package io.github.a12m.one2many;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register_new_user(View view){
        Intent intent = new Intent(getBaseContext(), lobby.class);
        startActivity(intent);
    }
}
