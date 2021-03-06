package com.PollBuzz.pollbuzz.results;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import Utils.firebase;

public class Image_type_result extends AppCompatActivity {

    MaterialTextView query;
    LinearLayout group;
    firebase fb = new firebase();
    RadioButton b1, b2;
    ImageView image1, image2;
    String key, uid;
    Typeface typeface;
    Dialog dialog;
    ImageButton logout, home;
    Map<String, Object> response;
    Map<String, Integer> options;
    Integer integer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_type_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();


        setGlobals(view);
        Intent intent = getIntent();
        getIntentExtras(intent);
        setActionBarFunctionality();
        showDialog();
        retriveData(fb);

    }

    private void getIntentExtras(Intent intent) {

        key = intent.getExtras().getString("UID");
        integer = intent.getExtras().getInt("flag");

        if (integer == 1) {
            uid = intent.getExtras().getString("UIDUser");
        }
        if (integer == 0) {
            uid = fb.getUserId();
        }

    }

    private void retriveData(firebase fb) {

        fb.getPollsCollection().document(key).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                PollDetails pollDetails = documentSnapshot.toObject(PollDetails.class);
                                query.setText(pollDetails.getQuestion());
                                options = pollDetails.getMap();
                                int i = 0;
                                for (Map.Entry<String, Integer> entry : options.entrySet()) {
                                    if (i == 0) {
                                        loadProfilePic(image1, entry.getKey());
                                    }
                                    if (i == 1) {
                                        loadProfilePic(image2, entry.getKey());
                                    }
                                    i++;
                                }

                                fb.getPollsCollection().document(key).collection("Response").document(uid).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    response = documentSnapshot.getData();
                                                    if (response.containsValue("Option 2")) {
                                                        b2.setChecked(true);
                                                    } else
                                                        b1.setChecked(true);

                                                }


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Image_type_result.this, "Failed to access your response", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                        dialog.dismiss();
                    }
                });
        b1.setEnabled(false);
        b2.setEnabled(false);
    }

    private void setGlobals(View view) {

        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);

        group = findViewById(R.id.options);
        response = new HashMap<>();
        options = new HashMap<>();
        query = findViewById(R.id.query_imageresult);
        group = findViewById(R.id.options);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        b1 = findViewById(R.id.option1);
        b2 = findViewById(R.id.option2);


        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Image_type_result.this);
    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Image_type_result.this, MainActivity.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fb.signOut();
            }
        });
    }

    private void loadProfilePic(ImageView view, String url) {
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (url != null) {
            Glide.with(this)
                    .load(url)
                    .into(view);
        } else {
            view.setImageResource(R.drawable.place_holder);
        }
    }

    private void showDialog() {
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
}
