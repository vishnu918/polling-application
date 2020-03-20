package com.example.pollbuzz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser()==null)
        {
            Intent i=new Intent(MainActivity.this,Login_Signup_Activity.class);
            startActivity(i);
        }
    }
}
