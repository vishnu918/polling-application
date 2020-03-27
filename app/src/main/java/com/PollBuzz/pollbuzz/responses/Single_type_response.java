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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;

import java.util.HashMap;
import java.util.Map;

public class Single_type_response extends AppCompatActivity {
    TextView title, query;
    RadioGroup group;
    FirebaseFirestore db;
    CollectionReference ref;
    Map<String,Integer> options;
    String key;
    Typeface typeface;
     Dialog dialog;
    FirebaseAuth auth;
    ImageButton home,logout;
    FirebaseAuth.AuthStateListener listener;
    Button submit;
    int c;
    Map<String,String> response;
    int b_id;
    String resp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        c=0;
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Single_type_response.this, MainActivity.class);
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
        group=findViewById(R.id.options);
        db=FirebaseFirestore.getInstance();
       options=new HashMap<>();
       response=new HashMap<>();
        Intent intent = getIntent();
        key = intent.getExtras().getString("UID");
       typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
       dialog=new Dialog(Single_type_response.this);
        showDialog();
        auth = FirebaseAuth.getInstance();
        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    Intent i=new Intent(Single_type_response.this, LoginSignupActivity.class);
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
                   {   group.removeAllViews();
                   dialog.dismiss();
                       PollDetails polldetails=data.toObject(PollDetails.class);
                       title.setText(polldetails.getTitle());
                       title.setPaintFlags(title.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                       query.setText(polldetails.getQuestion());
                       options=polldetails.getMap();

                       for(Map.Entry<String,Integer> entry : options.entrySet())
                       {
                           RadioButton button=new RadioButton(getApplicationContext());
                           RadioGroup.LayoutParams layoutParams=new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,RadioGroup.LayoutParams.WRAP_CONTENT);
                           layoutParams.setMargins(5,20,5,20);
                           button.setLayoutParams(layoutParams);
                           button.setTypeface(typeface);
                           button.setText(entry.getKey());
                           button.setTextSize(20.0f);
                           group.addView(button);
                           button.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   RadioButton b=(RadioButton)v;
                                   if(b.isChecked())
                                       resp=b.getText().toString();


                               }
                           });
                       }
                   }
                   }

               }
           }

       );

        ref=db.collection("Polls").document(key).collection("Response");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton button=findViewById(b_id);
                Toast.makeText(getApplicationContext(),resp+" Opted",Toast.LENGTH_LONG).show();
                response.put("option",resp);

                ref.document(auth.getCurrentUser().getUid()).set(response);

                Map<String,String> mapi = new HashMap<>();
                mapi.put("pollId",auth.getCurrentUser().getUid().toString());
                db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Voted").document(key).set(mapi)
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(Single_type_response.this, "Added", Toast.LENGTH_SHORT).show();
                               Intent i=new Intent(Single_type_response.this,MainActivity.class);
                               i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(i);
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(Single_type_response.this, "Failed", Toast.LENGTH_SHORT).show();
                           }
                       });



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
