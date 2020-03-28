package com.PollBuzz.pollbuzz.responses;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import Utils.firebase;

public class Descriptive_type_response extends AppCompatActivity {
    Button submit;
    TextView title,query;
    Map<String,String> response;
    Typeface typeface;
    Dialog dialog;
    FirebaseAuth auth;
    ImageButton home,logout;
    FirebaseAuth.AuthStateListener listener;
    firebase fb = new firebase();
    TextInputLayout answer;
    String key;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        Intent intent = getIntent();
        getIntentExtras(intent);
        View view =getSupportActionBar().getCustomView();
        setGlobals( view);
        setActionBarFunctionality();
        showDialog();
        setAuthStateListener();
        retrieveData();





        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResponse();

            }
        });


    }

    private void submitResponse() {
        response.put("option",answer.getEditText().getText().toString());
        System.out.println(response);
        fb.getPollsCollection().document(key).collection("Response")
        .document(auth.getCurrentUser().getUid()).set(response);
        Map<String,String> mapi = new HashMap<>();
        mapi.put("pollId",auth.getCurrentUser().getUid());
        fb.getUsersCollection().document(auth.getCurrentUser().getUid()).collection("Voted").document(key).set(mapi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Descriptive_type_response.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(Descriptive_type_response.this,MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Descriptive_type_response.this, "Unable to submit.\nPlease try again ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void retrieveData() {
        fb.getPollsCollection().document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot data = task.getResult();
                    if(data.exists())
                    {
                        dialog.dismiss();
                        PollDetails polldetails=data.toObject(PollDetails.class);
                        title.setText(polldetails.getTitle());
                        title.setPaintFlags(title.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                        query.setText(polldetails.getQuestion());
                    }
                }
            }
        });

    }

    private void setAuthStateListener() {
        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    Intent i=new Intent(Descriptive_type_response.this, LoginSignupActivity.class);
                    startActivity(i);
                }

            }
        };

    }

    private void setActionBarFunctionality() {
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


    }

    private void getIntentExtras(Intent intent) {
        key = intent.getExtras().getString("UID");
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
    private void setGlobals(View view)
    {
        title=findViewById(R.id.title);
        submit=findViewById(R.id.submit);
        query=findViewById(R.id.query);
        answer=findViewById(R.id.answer);
        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        dialog=new Dialog(Descriptive_type_response.this);
        auth = FirebaseAuth.getInstance();
        response=new HashMap<>();
        logout=view.findViewById(R.id.logout);
        home=view.findViewById(R.id.home);
    }
}
