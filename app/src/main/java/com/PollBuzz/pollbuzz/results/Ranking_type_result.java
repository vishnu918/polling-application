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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.TreeMap;

import Utils.firebase;

public class Ranking_type_result extends AppCompatActivity {

    MaterialTextView title_ranking_result, query_ranking_result;
    LinearLayout group;
    firebase fb;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener listener;
    String key,uid;
    ImageButton home, logout;
    Typeface typeface;
    Dialog dialog;
    Map<String, Object> response;
    TreeMap<String, Object> options;
    Integer integer;

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
        options=new TreeMap<>();
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
                Intent i = new Intent(Ranking_type_result.this, MainActivity.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
            }
        });
        auth = FirebaseAuth.getInstance();
        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    Intent i=new Intent(Ranking_type_result.this, LoginSignupActivity.class);
                    startActivity(i);
                }

            }
        };

        title_ranking_result = findViewById(R.id.title_ranking_result);
        query_ranking_result = findViewById(R.id.query_ranking_result);
        group = findViewById(R.id.options_ranking_result);

        fb = new firebase();

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Ranking_type_result.this);
        showDialog();
        fb.getPollsCollection().document(key).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot data = task.getResult();
                            if (data.exists()) {
                                group.removeAllViews();

                                PollDetails polldetails = data.toObject(PollDetails.class);
                                title_ranking_result.setText(polldetails.getTitle());
                                title_ranking_result.setPaintFlags(title_ranking_result.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                query_ranking_result.setText(polldetails.getQuestion());
                                    fb.getPollsCollection().document(key).collection("Response").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                if (documentSnapshot != null) {
                                                    response = documentSnapshot.getData();

                                                    setAccordingToPriority();


                                                }
                                            }

                                        }
                                    });
                             /*   if(integer == 1){
                                    fb.getPollsCollection().document(key).collection("Response")
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if(querySnapshot != null)
                                                {
                                                    for(DocumentSnapshot documentSnapshot : querySnapshot)
                                                    {
                                                        response = documentSnapshot.getData();
                                                        setAccordingToPriority();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }*/


                            }
                        }

                    }
                });
    }

    private void setAccordingToPriority() {
        options.putAll(response);
        for(Map.Entry<String,Object> entry : options.entrySet())
        {

            TextView v=new TextView(getApplicationContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(8, 20, 5, 20);
            v.setLayoutParams(layoutParams);
            v.setTypeface(typeface);
            v.setText(entry.getKey());
            v.setTextSize(20.0f);
            v.setTextColor(getResources().getColor(R.color.black));
            group.addView(v);
            String s=(Integer.parseInt(entry.getKey().substring(6))+1)+". "+entry.getValue().toString();
            v.setText(s);


        }
        dialog.dismiss();

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