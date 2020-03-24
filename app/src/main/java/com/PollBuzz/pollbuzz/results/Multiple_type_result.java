package com.PollBuzz.pollbuzz.results;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.PollBuzz.pollbuzz.LogIn_SignUp.Login_Signup_Activity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Multiple_type_result extends AppCompatActivity {
    TextView title, query;
    LinearLayout group;
    FirebaseFirestore db;
    CollectionReference ref;
    Map<String,Integer> options;
    String key,uid;
    Typeface typeface;
    Dialog dialog;
    FirebaseAuth auth;
    ImageButton home,logout;
    FirebaseAuth.AuthStateListener listener;
    Button submit;
    Map<String,Integer> response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_type_result);
        setContentView(R.layout.activity_single_type_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Multiple_type_result.this, MainActivity.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
            }
        });
        title=findViewById(R.id.title);
        query=findViewById(R.id.query);
        group=findViewById(R.id.options);
        db=FirebaseFirestore.getInstance();
        options=new HashMap<>();
        response=new HashMap<>();
        key= "XPkv84bvWMRXX4mEM97j";
        uid="3fpFZ9pGKASP570h8BVBFn5UBDH2";

        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        auth = FirebaseAuth.getInstance();
        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    Intent i=new Intent(Multiple_type_result.this, Login_Signup_Activity.class);
                    startActivity(i);
                }

            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);

    }
}
