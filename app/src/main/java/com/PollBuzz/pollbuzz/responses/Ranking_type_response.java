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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    LinearLayout group;
    CollectionReference ref;
    Map<String,Integer> options;
    Map<String,String> response;
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
        key= "8fVyr6eHM3KVrcERKelr";
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
                    Intent i=new Intent(Ranking_type_response.this, LoginSignupActivity.class);
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
                        PollDetails polldetails=data.toObject(PollDetails.class);
                        title_ranking.setText(polldetails.getTitle());
                        title_ranking.setPaintFlags(title_ranking.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                        query_ranking.setText(polldetails.getQuestion());
                        options=polldetails.getMap();
                        int i=0;
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
                            int finalI = i;
                            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    if(b)
                                    {
                                        response.put("option"+ finalI,button.getText().toString());
                                    }
                                }
                            });
                              i++;
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

                    /*  for(int i=0;i<group.getChildCount();i++)
                      {
                          RadioButton bt = (RadioButton)group.getChildAt(i);
                          if(bt.isChecked())
                          {
                              response.put("option"+i,bt.getText().toString());
                          }

                      }*/
                     ref = firebaseFirestore.collection("Polls").document(key).collection("Response");
                    RadioButton button=findViewById(b_id);


                    ref.document(auth.getCurrentUser().getUid()).set(response).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_LONG).show();
                            firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Voted").document(key).set(response);
                            Intent i=new Intent(Ranking_type_response.this,MainActivity.class);
                            startActivity(i);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Ranking_type_response.this, "Failde", Toast.LENGTH_SHORT).show();
                                }
                            });

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
