package com.PollBuzz.pollbuzz.polls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.Polldetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.helper;

public class Descriptive_type_poll extends AppCompatActivity {
    MaterialButton post_descriptive;
    TextInputLayout title, query;
    TextInputEditText title_descriptive,question_descriptive;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    Date date = Calendar.getInstance().getTime();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_type);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        post_descriptive=findViewById(R.id.post_descriptive);
        title_descriptive=findViewById(R.id.title_descriptive);
        question_descriptive=findViewById(R.id.question_descriptive);
        auth = FirebaseAuth.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String formatteddate = dateFormat.format(date);

        post_descriptive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title_descriptive.getText().toString().isEmpty())
                {
                    title_descriptive.setError("Please enter the title");
                    title_descriptive.requestFocus();
                }
                else if(question_descriptive.getText().toString().isEmpty())
                {
                    question_descriptive.setError("Please enter the question");
                    question_descriptive.requestFocus();
                }
                else {
                    if(auth.getCurrentUser() != null){
                        Polldetails polldetails = new Polldetails();
                        polldetails.setTitle(title_descriptive.getText().toString().trim());
                        polldetails.setQuestion(question_descriptive.getText().toString().trim());
                        polldetails.setCreated_date(formatteddate);
                        polldetails.setPoll_type("DESCRIPTIVE POLL");
                        polldetails.setAuthor(helper.getusernamePref(getApplicationContext()));
                        CollectionReference docCreated = firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Created");
                        DocumentReference doc = firebaseFirestore.collection("Polls").document();
                        doc.set(polldetails)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Map<String,String> m=new HashMap<>();
                                        m.put("pollId",doc.getId());
                                        docCreated.document().set(m);
                                        Toast.makeText(Descriptive_type_poll.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Descriptive_type_poll.this, "Unable to add try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        String name_doc = doc.getId();
                        Map<String,Integer> mapi = new HashMap<>();
                        mapi.put(name_doc,0);
                        firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Created").document().set(mapi)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Descriptive_type_poll.this, "document added to users", Toast.LENGTH_SHORT).show();
                                        Intent i=new Intent(Descriptive_type_poll.this, MainActivity.class);
                                        startActivity(i);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Descriptive_type_poll.this, "Failed to add document", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });




    }
}