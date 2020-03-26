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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.api.Distribution;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import Utils.firebase;

public class Ranking_type_result extends AppCompatActivity {

    MaterialTextView title_ranking_result, query_ranking_result;
    LinearLayout group;
    firebase fb;
    String key;
    ImageButton home, logout;
    Typeface typeface;
    Dialog dialog;
    Map<String, Object> response;
    Map<String, Integer> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_type_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Ranking_type_result.this, MainActivity.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fb.getAuth().signOut();
            }
        });

        title_ranking_result = findViewById(R.id.title_ranking_result);
        query_ranking_result = findViewById(R.id.query_ranking_result);
        group = findViewById(R.id.options_ranking_result);

        fb = new firebase();
        key = "8fVyr6eHM3KVrcERKelr";

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Ranking_type_result.this);
        fb.getPollsCollection().document(key).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot data = task.getResult();
                            if (data.exists()) {
                                group.removeAllViews();
                                dialog.dismiss();
                                PollDetails polldetails = data.toObject(PollDetails.class);
                                title_ranking_result.setText(polldetails.getTitle());
                                title_ranking_result.setPaintFlags(title_ranking_result.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                query_ranking_result.setText(polldetails.getQuestion());
                                fb.getPollsCollection().document(key).collection("Response").document("cczqqHMLI4dJ48dpz1dgL2Ad3V12").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot != null) {
                                                response = documentSnapshot.getData();
                                                int i=0;
                                               for(Map.Entry<String,Object> entry : response.entrySet())
                                               {
                                                   RadioButton button = new RadioButton(getApplicationContext());
                                                   LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                   layoutParams.setMargins(5, 20, 5, 20);
                                                   button.setLayoutParams(layoutParams);
                                                   button.setTypeface(typeface);
                                                   button.setText(entry.getKey());
                                                   button.setTextSize(20.0f);
                                                   group.addView(button);
                                                   button.setText(entry.getValue().toString());
                                                   button.setChecked(true);

                                               }

                                            }
                                        }
                                        dialog.dismiss();
                                    }
                                });


                            }
                        }

                    }
                });
    }
}