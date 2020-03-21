package com.example.pollbuzz;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Signup_Activity extends AppCompatActivity {
    Button login,signup;
    Fragment login_frag,signup_frag;

    FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);
        login=findViewById(R.id.login);
        signup=findViewById(R.id.signup);
        getSupportActionBar().hide();
        login_frag=Login_Fragment.newInstance("Login");
        signup_frag=Signup_Fragment.newInstance("Signup ");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        login.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        login.setTextColor(getResources().getColor(R.color.white));
        ft.replace(R.id.placeholder,login_frag,"Login Fragment");
        ft.addToBackStack("Login Fragment");
        ft.commit();
        signup.setBackgroundColor(getResources().getColor(R.color.white));
        signup.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        signup.setAlpha(0.5f);
        signup.setElevation(-0.5f);
        auth=FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.placeholder,login_frag,"Login Fragment");
                ft.addToBackStack("Login Fragment");
                ft.commit();
                login.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                login.setTextColor(getResources().getColor(R.color.white));
                signup.setBackgroundColor(getResources().getColor(R.color.white));
                signup.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                login.setAlpha(1.0f);
                signup.setAlpha(0.5f);
                signup.setElevation(-0.5f);


            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.placeholder,signup_frag,"Signup Fragment");
                ft.addToBackStack("Signup Fragment");
                ft.commit();
                signup.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                signup.setTextColor(getResources().getColor(R.color.white));
                login.setBackgroundColor(getResources().getColor(R.color.white));
                login.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                signup.setAlpha(1.0f);
                login.setAlpha(0.5f);
                login.setElevation(-0.5f);


            }
        });



    }
}
