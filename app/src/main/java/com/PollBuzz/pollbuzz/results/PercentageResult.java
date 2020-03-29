package com.PollBuzz.pollbuzz.results;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.bumptech.glide.Glide;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class PercentageResult extends AppCompatActivity {

    TextView question_percentage, date_percentage;
    ProgressBar progressBar;
    firebase fb;
    ImageButton home,logout;
    String uid,type;
    Map<String,Integer> map;
    LinearLayout linearLayout;
    Integer total;
    MaterialButton result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percentage_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        setGlobals(view);
        getIntentExtras(intent);
        setActionBarFunctionality();
        retrievedata(fb);

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PercentageResult.this, ResultActivity.class);
                intent.putExtra("UID", uid);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });


    }

    private void retrievedata(firebase fb) {

        fb.getPollsCollection().document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            PollDetails pollDetails = documentSnapshot.toObject(PollDetails.class);
                            question_percentage.setText(pollDetails.getQuestion());
                            date_percentage.setText(pollDetails.getCreated_date());
                            map = pollDetails.getMap();
                            total = pollDetails.getPollcount();
                            setProgressbar(map);
                        }
                        else
                            Log.d("hello","hii");
                    }
                });
    }

    private void setProgressbar(Map<String, Integer> map) {
        linearLayout.removeAllViews();
        if (type.equals("IMAGE POLL")) {
            for(Map.Entry<String,Integer> entry : map.entrySet()){
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                imageView.setLayoutParams(layoutParams);
                imageView.setBackgroundResource(R.drawable.circle);
                loadProfilePic(imageView,entry.getKey());
                linearLayout.addView(imageView);
                RelativeLayout relativeLayout1 = new RelativeLayout(getApplicationContext());
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(5, 10, 5, 10);
                ProgressBar progressBar = new ProgressBar(PercentageResult.this, null, android.R.attr.progressBarStyleHorizontal);
                progressBar.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                progressBar.setLayoutParams(layoutParams2);
                progressBar.setScaleY(5);
                relativeLayout1.addView(progressBar);
                TextView textView = new TextView(this);
                Integer per = (entry.getValue() / total) * 100;
                textView.setText(per+"%");
                progressBar.setProgress(per);
                textView.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                relativeLayout1.addView(textView);
                linearLayout.addView(relativeLayout1);

            }

        }
        else {

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
                ProgressBar progressBar = new ProgressBar(PercentageResult.this, null, android.R.attr.progressBarStyleHorizontal);
                progressBar.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 10, 30, 10);
                progressBar.setLayoutParams(layoutParams);
                progressBar.setScaleY(5);
                relativeLayout.addView(progressBar);
                TextView textView = new TextView(this);
                Log.d("option", entry.getKey());
                if (total != 0) {
                    Integer per = (entry.getValue() / total) * 100;
                    textView.setText(entry.getKey() + " - " + per + "%");
                    progressBar.setProgress(per);
                    textView.setLayoutParams(layoutParams);
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    relativeLayout.addView(textView);
                    linearLayout.addView(relativeLayout);
                }
            }
        }
    }

    private void loadProfilePic(ImageView view, String url) {
        if (url != null) {
            Glide.with(this)
                    .load(url)
                    .into(view);
        } else {
            view.setImageResource(R.drawable.place_holder);
        }
    }


    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(PercentageResult.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut();
            Intent i = new Intent(PercentageResult.this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }

    private void getIntentExtras(Intent intent) {
     uid = intent.getExtras().getString("UID");
     type = intent.getExtras().getString("type");
    }

    private void setGlobals(View view) {
        linearLayout = findViewById(R.id.percentage);
        fb = new firebase();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        progressBar = findViewById(R.id.progressbar);
        question_percentage = findViewById(R.id.question_percentage);
        date_percentage = findViewById(R.id.date_percentage);
        result = findViewById(R.id.result);
        map = new HashMap<>();
    }

}
