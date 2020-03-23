package com.PollBuzz.pollbuzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.File;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_type_poll);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        l1=findViewById(R.id.l1);
        group=findViewById(R.id.options);
        add=findViewById(R.id.add);
        l1=findViewById(R.id.l1);
        l2=findViewById(R.id.l2);
        b1=findViewById(R.id.option1);
        b2=findViewById(R.id.option2);
        view1=findViewById(R.id.image1);
        view2=findViewById(R.id.image2);
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        c=group.getChildCount();
        registerForContextMenu(b1);
        registerForContextMenu(b1);

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
        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    Intent i=new Intent(Image_type_poll.this, Login_Signup_Activity.class);
                    startActivity(i);
                }

            }
        };




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
            }
            if(requestCode==2) {
                view2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                Picasso.get().load(uri).fit().placeholder(R.drawable.place_holder).into(view2);

            }
        }

        }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);

    }





        }


