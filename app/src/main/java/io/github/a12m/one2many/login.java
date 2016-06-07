package io.github.a12m.one2many;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    //Login and goes to lobby
    public void login(View view){
        Intent intent = new Intent(getBaseContext(), lobby.class);
        startActivity(intent);
    }

    //Forgot pass link, goes to lobby temporarily
    public void forgotPass(View view){
        Intent intent = new Intent(getBaseContext(), lobby.class);
        startActivity(intent);
    }

    //Register link, goes to lobby temporarily
    public void register(View view){
        Intent intent = new Intent(getBaseContext(), lobby.class);
        startActivity(intent);
    }
}
