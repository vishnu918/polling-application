package com.PollBuzz.pollbuzz.results;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    Map<String,Object> response;
    Integer integer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_type_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();

        Intent intent = getIntent();
        getIntentExtras(intent);


       setGlobals(view);
       setActionBarFunctionality();
       setAuthStateListener();
       showDialog();
        retriveData(db);




    }

    private void retriveData(FirebaseFirestore db) {

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
                                                                                             db.collection("Polls").document(key).collection("Response").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                 @Override
                                                                                                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                     if (task.isSuccessful()) {
                                                                                                         DocumentSnapshot data = task.getResult();
                                                                                                         if (data.exists()) {
                                                                                                             response = data.getData();
                                                                                                             setOptions();
                                                                                                         }
                                                                                                     }

                                                                                                 }
                                                                                             });
                                                                                   /*      if( integer == 1){

                                                                                             db.collection("Polls").document(key).collection("Response")
                                                                                                     .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                 @Override
                                                                                                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                     if(task.isSuccessful())
                                                                                                     {
                                                                                                         QuerySnapshot querySnapshot = task.getResult();
                                                                                                         if(querySnapshot != null)
                                                                                                         {
                                                                                                             for(DocumentSnapshot documentSnapshot : querySnapshot)
                                                                                                             {
                                                                                                                 response = documentSnapshot.getData();
                                                                                                                 setOptions();
                                                                                                             }
                                                                                                         }
                                                                                                     }
                                                                                                 }
                                                                                             });
                                                                                         }*/



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
                    Intent i=new Intent(Multiple_type_result.this, LoginSignupActivity.class);
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
    private void setOptions()
    {
        for(Map.Entry<String,Object> entry: response.entrySet())
        {
            String key=entry.getValue().toString();
            if(options.containsKey(key))
            {
                options.remove(key);
                options.put(entry.getValue().toString(),1);
            }
        }
        for(Map.Entry<String,Integer> entry : options.entrySet())
        {
            RadioButton button=new RadioButton(getApplicationContext());
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,20,5,20);
            button.setLayoutParams(layoutParams);
            button.setTypeface(typeface);
            button.setText(entry.getKey());
            button.setTextSize(20.0f);
            group.addView(button);
            if(entry.getValue()==1)
                button.setChecked(true);
            else
                button.setEnabled(false);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton b=(RadioButton)v;
                    if(entry.getValue()==1)
                    {
                        b.setChecked(true);
                        if(!b.isChecked())
                            b.setChecked(true);
                    }

                    else
                        b.setChecked(false);
                }
            });
        }
        dialog.dismiss();
    }
    private void setGlobals(View view)
    {
        title=findViewById(R.id.title);
        query=findViewById(R.id.query);
        group=findViewById(R.id.options);
        db=FirebaseFirestore.getInstance();
        options=new HashMap<>();
        response=new HashMap<>();

        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        dialog=new Dialog(Multiple_type_result.this);
        auth = FirebaseAuth.getInstance();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
    }
    private void setActionBarFunctionality()
    {
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
    }
    private void getIntentExtras(Intent intent)
    {
        key = intent.getExtras().getString("UID");
        integer = intent.getExtras().getInt("flag");

        if(integer == 1)
        {
            uid = intent.getExtras().getString("UIDUser");
        }
        if(integer == 0)
        {
            uid = auth.getCurrentUser().getUid();
        }

    }
}
