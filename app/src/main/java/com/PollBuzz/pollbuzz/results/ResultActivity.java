package com.PollBuzz.pollbuzz.results;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.VoteDetails;
import com.PollBuzz.pollbuzz.adapters.VoterPageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Utils.firebase;

public class ResultActivity extends AppCompatActivity {

    RecyclerView voteRV;
    VoterPageAdapter mPageAdapter;
    List<VoteDetails> mVoteDetailsList;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String type;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference pollsColRef, userColRef;
    firebase fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent parent = getIntent();
        String UID = parent.getStringExtra("UID");
        String type = parent.getStringExtra("type");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        fb=new firebase();

//        if (UID != null) {
//            pollsColRef = firebaseFirestore.collection("Polls")
//                    .document(UID)
//                    .collection("Response");
//            firebaseFirestore.collection("Polls").document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if(task.isSuccessful() && task.getResult()!=null)
//                    {
//                        DocumentSnapshot documentSnapshot = task.getResult();
//                        PollDetails pollDetails = documentSnapshot.toObject(PollDetails.class);
//                        type = pollDetails.getPoll_type();
//
//                    }
//                }
//            });
//        }
        userColRef = firebaseFirestore.collection("Users");
        voteRV = findViewById(R.id.voterListRV);
        mVoteDetailsList = new ArrayList<>();
        mPageAdapter = new VoterPageAdapter(getApplicationContext(), mVoteDetailsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        voteRV.setLayoutManager(linearLayoutManager);
        voteRV.setAdapter(mPageAdapter);
        if (UID != null) {
            fb.getPollsCollection().document(UID).collection("Response").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot dS : querySnapshot) {
                            userColRef.document(dS.getId()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                            if (task1.isSuccessful() && task1.getResult() != null) {
                                                DocumentSnapshot documentSnapshot = task1.getResult();
                                                Object author = documentSnapshot.get("username");
                                                Log.d("type", author.toString());
                                                VoteDetails voteDetails = new VoteDetails(UID, type, author.toString());
                                                Log.d("TypeOf", voteDetails.getOption());
                                                mVoteDetailsList.add(voteDetails);
                                                mPageAdapter.notifyDataSetChanged();
                                                Log.d("count", Integer.toString(mPageAdapter.getItemCount()));
                                            }
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
}
