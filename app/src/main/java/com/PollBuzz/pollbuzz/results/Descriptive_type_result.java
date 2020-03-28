package com.PollBuzz.pollbuzz.results;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Descriptive_type_result extends AppCompatActivity {
    Button submit;
    TextView title, query, answer;
    Map<String, Object> response;
    Typeface typeface;
    Dialog dialog;
    FirebaseAuth auth;
    ImageButton home, logout;
    FirebaseAuth.AuthStateListener listener;
    FirebaseFirestore db;
    CollectionReference ref;
    String key, uid;
    Integer integer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_type_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        key = intent.getExtras().getString("UID");
        integer = intent.getExtras().getInt("flag");

        if(integer == 1)
        {
            uid = intent.getExtras().getString("UIDUser");
        }
        if(integer == 0)
        {
            uid = auth.getCurrentUser().getUid();
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Descriptive_type_result.this, MainActivity.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
            }
        });
        title = findViewById(R.id.title);
        query = findViewById(R.id.query);
        answer = findViewById(R.id.answer);
        db = FirebaseFirestore.getInstance();
        response = new HashMap<>();

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Descriptive_type_result.this);
        showDialog();

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent i = new Intent(Descriptive_type_result.this, LoginSignupActivity.class);
                    startActivity(i);
                }

            }
        };
        db.collection("Polls").document(key)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                             @Override
                                                                             public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                                                                                 if (task.isSuccessful()) {

                                                                                     DocumentSnapshot data = task.getResult();
                                                                                     if (data.exists()) {
                                                                                         dialog.dismiss();
                                                                                         PollDetails polldetails = data.toObject(PollDetails.class);
                                                                                         title.setText(polldetails.getTitle());
                                                                                         title.setPaintFlags(title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                                                                         query.setText(polldetails.getQuestion());
                                                                                             db.collection("Polls").document(key).collection("Response").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                 @Override
                                                                                                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                     if (task.isSuccessful()) {
                                                                                                         DocumentSnapshot data = task.getResult();
                                                                                                         if (data.exists()) {
                                                                                                             response = data.getData();
                                                                                                             setAnswer();
                                                                                                         }
                                                                                                     }

                                                                                                 }
                                                                                             });

                                                                                     /*    if (integer == 1) {
                                                                                             db.collection("Polls").document(key).collection("Response")
                                                                                                     .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                 @Override
                                                                                                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                     if (task.isSuccessful()) {
                                                                                                         QuerySnapshot querySnapshot = task.getResult();
                                                                                                         if (querySnapshot != null) {
                                                                                                             for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                                                                                                 response = documentSnapshot.getData();
                                                                                                                 setAnswer();
                                                                                                             }
                                                                                                         }
                                                                                                     }
                                                                                                 }
                                                                                             });
                                                                                         }*/

                                                                                     }
                                                                                 }

                                                                             }
                                                                         }
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);

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

    private void setAnswer() {
        String key = "";
        for (Map.Entry<String, Object> entry : response.entrySet()) {
            key = key + entry.getValue();
        }
        answer.setText(key);
        dialog.dismiss();
    }
}
