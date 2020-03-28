package com.PollBuzz.pollbuzz.polls;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import Utils.helper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Descriptive_type_poll extends AppCompatActivity {
    MaterialButton post_descriptive;
    TextInputLayout title, query;
    TextInputEditText title_descriptive,question_descriptive;
    firebase fb;
    Date date = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_type);
        setGlobals();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String formatteddate = dateFormat.format(date);
        setListeners(formatteddate);
    }

    private void setListeners(String formatteddate) {
        post_descriptive.setOnClickListener(view -> {
            if (title_descriptive.getText().toString().isEmpty()) {
                title_descriptive.setError("Please enter the title");
                title_descriptive.requestFocus();
            } else if (question_descriptive.getText().toString().isEmpty()) {
                question_descriptive.setError("Please enter the question");
                question_descriptive.requestFocus();
            } else {
                addToDatabase(formatteddate);
            }
        });
    }

    private void addToDatabase(String formatteddate) {
        if (fb.getUser() != null) {
            PollDetails polldetails = new PollDetails();
            polldetails.setTitle(title_descriptive.getText().toString().trim());
            polldetails.setQuestion(question_descriptive.getText().toString().trim());
            polldetails.setCreated_date(formatteddate);
            polldetails.setPoll_type("DESCRIPTIVE POLL");
            polldetails.setAuthor(helper.getusernamePref(getApplicationContext()));
            CollectionReference docCreated = fb.getUserDocument().collection("Created");
            DocumentReference doc = fb.getPollsCollection().document();
            doc.set(polldetails)
                    .addOnSuccessListener(aVoid -> {
                        Map<String, String> m = new HashMap<>();
                        m.put("pollId", doc.getId());
                        docCreated.document().set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Descriptive_type_poll.this, "Your data added successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Descriptive_type_poll.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Descriptive_type_poll.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    })
                    .addOnFailureListener(e -> Toast.makeText(Descriptive_type_poll.this, "Unable to post.Please try again", Toast.LENGTH_SHORT).show());
        }
    }

    private void setGlobals() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        fb = new firebase();
        post_descriptive = findViewById(R.id.post_descriptive);
        title_descriptive = findViewById(R.id.title_descriptive);
        question_descriptive = findViewById(R.id.question_descriptive);
    }
}