package com.PollBuzz.pollbuzz.polls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.PollBuzz.pollbuzz.Polldetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.PollBuzz.pollbuzz.LogIn_SignUp.Login_Signup_Activity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Image_type_poll extends AppCompatActivity {
    Button add;
    LinearLayout l1,l2,group;
    int c;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener listener;
    ImageButton home,logout;
    Uri uri;
    ImageView view1,view2;
    RadioButton b1,b2;
    MaterialButton post_image;
    TextInputEditText title_image , question_image;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Date date = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_type_poll);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        l1 = findViewById(R.id.l1);
        group = findViewById(R.id.options);
        add = findViewById(R.id.add);
        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        b1 = findViewById(R.id.option1);
        b2 = findViewById(R.id.option2);
        view1 = findViewById(R.id.image1);
        view2 = findViewById(R.id.image2);
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        post_image = findViewById(R.id.post_imagetype);
        title_image = findViewById(R.id.title_imagetype);
        question_image = findViewById(R.id.question_imagetype);
        c = group.getChildCount();
        registerForContextMenu(b1);
        registerForContextMenu(b1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String formatteddate = dateFormat.format(date);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Image_type_poll.this, MainActivity.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.showContextMenu();
                b1.setChecked(false);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.showContextMenu();
                b2.setChecked(false);
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
            }
        });
        auth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent i = new Intent(Image_type_poll.this, Login_Signup_Activity.class);
                    startActivity(i);
                }

            }
        };
        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title_image.getText().toString().isEmpty()) {
                    title_image.setError("Please enter the title");
                    title_image.requestFocus();
                } else if (question_image.getText().toString().isEmpty()) {
                    question_image.setError("Please enter the question");
                    question_image.requestFocus();
                } else {
                    if (auth.getCurrentUser() != null) {
                        Polldetails polldetails = new Polldetails();
                        polldetails.setTitle(title_image.getText().toString().trim());
                        polldetails.setQuestion(question_image.getText().toString().trim());
                        polldetails.setCreated_date(formatteddate);
                        polldetails.setPoll_type("IMAGE POLL");
                        Map<String,Integer> map = new HashMap<>();
                        map.put(view1.getTag().toString(),0);
                        map.put(view2.getTag().toString(),0);
                        polldetails.setMap(map);
                        CollectionReference docCreated = firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Created");
                        DocumentReference doc = firebaseFirestore.collection("Polls").document();
                        doc.set(polldetails)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Map<String, String> m = new HashMap<>();
                                        m.put("pollId", doc.getId());
                                        docCreated.document().set(m);
                                        Toast.makeText(Image_type_poll.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Image_type_poll.this, "Unable to add try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle=data.getExtras();
        if(resultCode==RESULT_OK)
        {  uri=data.getData();
            if(requestCode==1) {
                view1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                Picasso.get().load(uri).placeholder(R.drawable.place_holder).into(view1);
                view1.setTag(uri);
            }
            if(requestCode==2) {
                view2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                Picasso.get().load(uri).fit().placeholder(R.drawable.place_holder).into(view2);
                view2.setTag(uri);
            }
        }

        }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);

    }





        }


