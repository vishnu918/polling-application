package com.PollBuzz.pollbuzz.responses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.bumptech.glide.Glide;
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

public class Image_type_responses extends AppCompatActivity {

    TextView title , query;
    ImageView image1 ,image2;
    RadioGroup group;
    RadioButton b1,b2;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;
    MaterialButton submit;
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
        setContentView(R.layout.activity_image_type_responses);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        logout = findViewById(R.id.logout);
        group=findViewById(R.id.options);
        options=new HashMap<>();
        response=new HashMap<>();
        Intent intent = getIntent();
        key = intent.getExtras().getString("UID");
        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        dialog=new Dialog(Image_type_responses.this);
        showDialog();
        auth = FirebaseAuth.getInstance();

        title = findViewById(R.id.title);
        query = findViewById(R.id.query);
        submit = findViewById(R.id.submit);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);

        b1 = findViewById(R.id.option1);
        b2 = findViewById(R.id.option2);

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
                    Intent i=new Intent(Image_type_responses.this, LoginSignupActivity.class);
                    startActivity(i);
                }

            }
        };
        b1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    b2.setChecked(false);
                }
            }
        });
        b2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    b1.setChecked(false);
                }
            }
        });


        firebaseFirestore.collection("Polls").document(key).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot snapshot = task.getResult();
                            if(snapshot.exists())
                            {
                                dialog.dismiss();
                                PollDetails polldetails = snapshot.toObject(PollDetails.class);
                                title.setText(polldetails.getTitle().trim());
                                query.setText(polldetails.getQuestion().trim());
                                options =polldetails.getMap();
                                int i=0;
                                for(Map.Entry<String,Integer> entry : options.entrySet())
                                {
                                    if(i==0)
                                    {
                                        loadProfilePic(image1,entry.getKey());
                                    }
                                    if(i==1)
                                    {
                                        loadProfilePic(image2,entry.getKey());
                                    }
                                    i++;
                                }
                            }
                        }
                    }
                });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(b1.isChecked())
                {
                    response.put("option",b1.getText().toString().trim());
                }
                if(b2.isChecked())
                {
                    response.put("option",b2.getText().toString().trim());
                }
                if(auth.getCurrentUser() != null)
                {
                    collectionReference = firebaseFirestore.collection("Polls").document(key).collection("Response");
                    collectionReference.document(auth.getCurrentUser().getUid()).set(response).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Map<String,String> mapi = new HashMap<>();
                            mapi.put("pollId",auth.getCurrentUser().getUid());
                            Toast.makeText(Image_type_responses.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                            firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Voted").document(key).set(mapi);
                            Intent i=new Intent(Image_type_responses.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Image_type_responses.this, "Unable to submit .Please try again ", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
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
