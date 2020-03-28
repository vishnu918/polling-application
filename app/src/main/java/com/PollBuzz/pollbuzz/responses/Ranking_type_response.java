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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Ranking_type_response extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    MaterialButton submit;
    MaterialTextView title_ranking , query_ranking;
    LinearLayout group,sequence;
    CollectionReference ref;
    Map<String,Integer> options;
    Map<String,String> response;
    String key;
    Typeface typeface;
    Dialog dialog;
    ImageButton logout,home;
    FirebaseAuth.AuthStateListener listener;
    int c;
    ArrayList<String> resp=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        setGlobals(view);

        Intent intent = getIntent();
        key = intent.getExtras().getString("UID");

        showDialog();
        setActionBarFunctionality();
        setAuthStateListener();
        retrieveData();







        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sequence.getChildCount()!=c)
                {
                    Toast.makeText(getApplicationContext(),"Select all options",Toast.LENGTH_LONG).show();
                }
                else
                {
                    setResponse();
                }
            }
        });
    }

    private void retrieveData() {
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
                        c=options.size();
                        setOptions();

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
                    Intent i=new Intent(Ranking_type_response.this, LoginSignupActivity.class);
                    startActivity(i);
                }

            }
        };

    }

    private void setActionBarFunctionality() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Ranking_type_response.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void setGlobals(View view) {
        logout = view.findViewById(R.id.logout);
        home=view.findViewById(R.id.home);
        group=findViewById(R.id.options);
        sequence=findViewById(R.id.sequence);
        options=new HashMap<>();
        response=new HashMap<>();
        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        dialog=new Dialog(Ranking_type_response.this);
        auth = FirebaseAuth.getInstance();
        title_ranking = findViewById(R.id.title);
        query_ranking = findViewById(R.id.query);
        submit = findViewById(R.id.submit);

    }

    private void setOptions() {
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
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox b=(CheckBox) v;
                    if(((CheckBox) v).isChecked())
                        resp.add(((CheckBox) v).getText().toString());
                    else
                        resp.remove((((CheckBox) v).getText().toString()));
                    setSequenceArea();

                }
            });

        }

    }

    private void setResponse() {
        for(int i=0;i<c;i++)
        {
            response.put("option"+i,resp.get(i));

        }
        submitResponse();
        return;
    }

    private void submitResponse() {

        ref = firebaseFirestore.collection("Polls").document(key).collection("Response");
        ref.document(auth.getCurrentUser().getUid()).set(response).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Map<String,String> mapi = new HashMap<>();
                mapi.put("pollId",auth.getCurrentUser().getUid());
                Toast.makeText(getApplicationContext(),"Successfully submitted your response",Toast.LENGTH_LONG).show();
                firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Voted").document(key).set(mapi);
                Intent i=new Intent(Ranking_type_response.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Ranking_type_response.this, "Unable to submit your.Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setSequenceArea() {
        sequence.removeAllViews();

        for(int i=0;i<resp.size();i++)
        {
            TextView v=new TextView(getApplicationContext());
            RadioGroup.LayoutParams layoutParams=new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,RadioGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,20,5,20);
            v.setLayoutParams(layoutParams);
            v.setTypeface(typeface);
            v.setTextSize(20.0f);
            v.setText(Integer.toString(i+1)+". "+resp.get(i));
            v.setTextColor(getResources().getColor(R.color.black));
            sequence.addView(v);
            sequence.requestFocus();

        }

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
