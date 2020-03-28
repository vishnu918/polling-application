package com.PollBuzz.pollbuzz.responses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;

import java.util.HashMap;
import java.util.Map;

import Utils.firebase;

public class Multiple_type_response extends AppCompatActivity {
    TextView title, query;
    LinearLayout group;
    Map<String,Integer> options;
    String key;
    Typeface typeface;
    Dialog dialog;
    FirebaseAuth auth;
    ImageButton home,logout;
    FirebaseAuth.AuthStateListener listener;
    Button submit;
    firebase fb = new firebase();
    int c;
    Map<String,String> response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        getIntentExtras(intent);
        setGlobals(view);
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

    private void getIntentExtras(Intent intent) {
        key = intent.getExtras().getString("UID");

    }

    private void submitResponse() {

        fb.getPollsCollection().document(key).collection("Response")
        .document(auth.getCurrentUser().getUid()).set(response);
        Map<String,String> mapi = new HashMap<>();
        mapi.put("pollId",auth.getCurrentUser().getUid());
        fb.getUsersCollection().document(auth.getCurrentUser().getUid()).collection("Voted").document(key).set(mapi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Multiple_type_response.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(Multiple_type_response.this,MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Multiple_type_response.this, "Unable to submit.\nPlease try again", Toast.LENGTH_SHORT).show();
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
                                                                                         options=polldetails.getMap();
                                                                                         group.removeAllViews();
                                                                                         response.clear();

                                                                                         int i=0;
                                                                                         for(Map.Entry<String,Integer> entry : options.entrySet())
                                                                                         {
                                                                                             CheckBox button=new CheckBox(getApplicationContext());
                                                                                             LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                                                                             layoutParams.setMargins(5,20,5,20);
                                                                                             button.setLayoutParams(layoutParams);
                                                                                             button.setTypeface(typeface);

                                                                                             button.setText(entry.getKey());
                                                                                             button.setTextSize(20.0f);
                                                                                             group.addView(button);
                                                                                             int finalI = i;
                                                                                             button.setOnClickListener(new View.OnClickListener() {
                                                                                                 @Override
                                                                                                 public void onClick(View v) {
                                                                                                     CheckBox b=(CheckBox) v;
                                                                                                     if(b.isChecked())
                                                                                                         response.put("option"+ finalI,b.getText().toString());
                                                                                                     else
                                                                                                         response.values().remove(b.getText().toString());


                                                                                                 }
                                                                                             });
                                                                                             i++;
                                                                                         }
                                                                                     }
                                                                                 }

                                                                             }
                                                                         }

        );


    }

    private void setAuthStateListener() {
        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    Intent i=new Intent(Multiple_type_response.this, LoginSignupActivity.class);
                    startActivity(i);
                }

            }
        };

    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Multiple_type_response.this, MainActivity.class);
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

    private void setGlobals(View view) {
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        c=0;
        title=findViewById(R.id.title);
        submit=findViewById(R.id.submit);
        query=findViewById(R.id.query);
        group=findViewById(R.id.options);
        options=new HashMap<>();
        response=new HashMap<>();
        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        dialog=new Dialog(Multiple_type_response.this);
        auth = FirebaseAuth.getInstance();



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
