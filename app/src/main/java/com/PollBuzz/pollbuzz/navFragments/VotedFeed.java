package com.PollBuzz.pollbuzz.navFragments;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.PollBuzz.pollbuzz.Poll_list;
import com.PollBuzz.pollbuzz.Polldetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.adapters.ProfileFeedAdapter;
import com.PollBuzz.pollbuzz.adapters.VotedFeedAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VotedFeed extends Fragment {
    RecyclerView votedRV;
    VotedFeedAdapter mAdapter;
    ArrayList<Polldetails> mArrayList;
    private LinearLayoutManager layoutManager;
    FloatingActionButton fab;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference userColRef, pollsColRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    public VotedFeed() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_voted_feed, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mArrayList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userColRef = firebaseFirestore.collection("Users").document(mUser.getUid()).collection("Voted");
        pollsColRef = firebaseFirestore.collection("Polls");
        fab=view.findViewById(R.id.fab);
        votedRV = view.findViewById(R.id.votedrecyclerview);
        votedRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new VotedFeedAdapter(getContext(),mArrayList);

        votedRV.setLayoutManager(layoutManager);
        votedRV.setAdapter(mAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getContext(), Poll_list.class);
                startActivity(i);
            }
        });
        userColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot dS : task.getResult()) {
                        if (dS.get("pollId") != null)
                            pollsColRef.document(dS.get("pollId").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    Log.d("arraySize",task.getResult().getId());
                                    DocumentSnapshot dS1 = task.getResult();
                                    if (dS1.exists()) {
                                        Log.d("arraySize","Exists");
                                        Polldetails polldetails = dS1.toObject(Polldetails.class);
                                        mArrayList.add(polldetails);
                                        Log.d("arraySize",String.valueOf(mArrayList.size()));
                                        mAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}