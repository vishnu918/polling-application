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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.PollBuzz.pollbuzz.LogIn_SignUp.Login_Signup_Activity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.Polldetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Ranking_type_response extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    MaterialButton submit;
    MaterialTextView title_ranking , query_ranking;
    RadioGroup group;
    CollectionReference ref;
    Map<String,Integer> options,response;
    String key;
    Typeface typeface;
    Dialog dialog;
    ImageButton logout;
    FirebaseAuth.AuthStateListener listener;
    int c;
    int b_id;
    String resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        logout = findViewById(R.id.logout);
        group=findViewById(R.id.options);
        options=new HashMap<>();
        response=new HashMap<>();
        key= "XPkv84bvWMRXX4mEM97j";
        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        dialog=new Dialog(Ranking_type_response.this);
        showDialog();
        auth = FirebaseAuth.getInstance();

        title_ranking = findViewById(R.id.title);
        query_ranking = findViewById(R.id.query);
        submit = findViewById(R.id.submit);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    auth.signOut();
            }
        });

        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    Intent i=new Intent(Ranking_type_response.this, Login_Signup_Activity.class);
                    startActivity(i);
                }

            }
        };

        firebaseFirestore.collection("Polls").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot data = task.getResult();
                    if(data.exists())
                    {   group.removeAllViews();
                        dialog.dismiss();
                        Polldetails polldetails=data.toObject(Polldetails.class);
                        title_ranking.setText(polldetails.getTitle());
                        title_ranking.setPaintFlags(title_ranking.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                        query_ranking.setText(polldetails.getQuestion());
                        options=polldetails.getMap();

                        for(Map.Entry<String,Integer> entry : options.entrySet())
                        {
                            RadioButton button=new RadioButton(getApplicationContext());
                            RadioGroup.LayoutParams layoutParams=new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,RadioGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(5,20,5,20);
                            button.setLayoutParams(layoutParams);
                            button.setTypeface(typeface);
                            /*button.setId(c+1);*/
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
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(auth.getCurrentUser() != null)
                {
                     ref = firebaseFirestore.collection("Polls").document(key).collection("Response");
                    RadioButton button=findViewById(b_id);
                    Toast.makeText(getApplicationContext(),resp+" Opted",Toast.LENGTH_LONG).show();
                    options.clear();
                    options.put(resp,0);

                    ref.document(auth.getCurrentUser().getUid()).set(options);

                    firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Voted").document(key).set(options);
                    Intent i=new Intent(Ranking_type_response.this,MainActivity.class);
                    startActivity(i);
                }
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
