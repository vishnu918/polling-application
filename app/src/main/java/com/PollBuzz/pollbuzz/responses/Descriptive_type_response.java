package com.PollBuzz.pollbuzz.responses;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.PollBuzz.pollbuzz.LogIn_SignUp.Login_Signup_Activity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.Polldetails;
import com.PollBuzz.pollbuzz.R;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class Descriptive_type_response extends AppCompatActivity {
    Button submit;
    TextView title,query;
    Map<String,Integer> response;
    Typeface typeface;
    Dialog dialog;
    FirebaseAuth auth;
    ImageButton home,logout;
    FirebaseAuth.AuthStateListener listener;
    FirebaseFirestore db;
    CollectionReference ref;
    TextInputLayout answer;
    String key;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        key="80E2L7Whekw7AAtFu6yA";
        response=new HashMap<>();
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Descriptive_type_response.this, MainActivity.class);
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
        submit=findViewById(R.id.submit);
        query=findViewById(R.id.query);
        answer=findViewById(R.id.answer);
        db=FirebaseFirestore.getInstance();
        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        dialog=new Dialog(Descriptive_type_response.this);
        showDialog();
        auth = FirebaseAuth.getInstance();
        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    Intent i=new Intent(Descriptive_type_response.this, Login_Signup_Activity.class);
                    startActivity(i);
                }

            }
        };

        db.collection("Polls").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot data = task.getResult();
                    if(data.exists())
                    {
                        dialog.dismiss();
                        Polldetails polldetails=data.toObject(Polldetails.class);
                        title.setText(polldetails.getTitle());
                        title.setPaintFlags(title.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                        query.setText(polldetails.getQuestion());
                    }
                }
            }
        });
        ref=db.collection("Polls").document(key).collection("Response");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> m = new HashMap<>();
                 response.put(answer.getEditText().getText().toString(),0);

                System.out.println(response);

                ref.document(auth.getCurrentUser().getUid()).set(response);
                db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Voted").document(key).set(response);
                Intent i=new Intent(Descriptive_type_response.this,MainActivity.class);
                startActivity(i);


            }
        });


    }
    private void showDialog()
    {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        dialog.setCancelable(false);
        dialog.show();
        window.setAttributes(lp);
    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);

    }
}
