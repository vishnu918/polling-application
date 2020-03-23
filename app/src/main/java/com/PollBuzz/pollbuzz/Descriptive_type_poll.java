package com.PollBuzz.pollbuzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
                        firebaseFirestore.collection("Polls").document().set(polldetails)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Descriptive_type_poll.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Descriptive_type_poll.this, "Unable to add try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });




    }
}
