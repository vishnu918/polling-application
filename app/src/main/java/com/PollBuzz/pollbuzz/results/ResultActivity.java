package com.PollBuzz.pollbuzz.results;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.VoteDetails;
import com.PollBuzz.pollbuzz.adapters.VoterPageAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Utils.firebase;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ResultActivity extends AppCompatActivity {

    ShimmerRecyclerView voteRV;
    VoterPageAdapter mPageAdapter;
    List<VoteDetails> mVoteDetailsList;
    FirebaseAuth mAuth;
    ImageButton home,logout;
    TextView page_title;
    String UID,type;
    firebase fb = new firebase();
    FirebaseAuth.AuthStateListener listener;
    private LayoutAnimationController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();

        setGlobals(view);
        Intent intent = getIntent();
        getIntentExtras(intent);
        setActionBarFunctionality();
        setAuthStateListener();
        retriveData(fb);


    }

    private void setAuthStateListener() {

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent i = new Intent(ResultActivity.this, LoginSignupActivity.class);
                    startActivity(i);
                }

            }
        };
    }

    private void retriveData(firebase fb) {
        voteRV = findViewById(R.id.voterListRV);

        voteRV.showShimmerAdapter();
        mVoteDetailsList = new ArrayList<>();
        mPageAdapter = new VoterPageAdapter(getApplicationContext(), mVoteDetailsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        voteRV.setLayoutManager(linearLayoutManager);
        if (UID != null) {
            fb.getPollsCollection().document(UID).collection("Response").get().addOnCompleteListener(task -> {
                voteRV.setAdapter(mPageAdapter);
                voteRV.hideShimmerAdapter();
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot dS : querySnapshot) {
                            fb.getUsersCollection().document(dS.getId()).get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null) {
                                            DocumentSnapshot documentSnapshot = task1.getResult();
                                            Object author = documentSnapshot.get("username");
                                            Object imageUrl = documentSnapshot.get("pic");
                                            Log.d("type", author.toString());
                                            VoteDetails voteDetails;
                                            if (imageUrl != null)
                                                voteDetails = new VoteDetails(UID, type, author.toString(), dS.getId(), imageUrl.toString());
                                            else
                                                voteDetails = new VoteDetails(UID, type, author.toString(), dS.getId(), null);
                                            Log.d("TypeOf", voteDetails.getOption());
                                            mVoteDetailsList.add(voteDetails);
                                            voteRV.setLayoutAnimation(controller);
                                            mPageAdapter.notifyDataSetChanged();
                                            voteRV.scheduleLayoutAnimation();
                                            Log.d("count", Integer.toString(mPageAdapter.getItemCount()));
                                        }
                                    });
                        }
                    }
                } else {
                    Toast.makeText(ResultActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getIntentExtras(Intent parent) {
         UID = parent.getStringExtra("UID");
         type = parent.getStringExtra("type");
    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResultActivity.this, MainActivity.class);
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

    private void setGlobals(View view) {
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        page_title=view.findViewById(R.id.page_title);
        mAuth = FirebaseAuth.getInstance();
        page_title.setText("Results");
        controller =
                AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.animation_down_to_up);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener);

    }
}
