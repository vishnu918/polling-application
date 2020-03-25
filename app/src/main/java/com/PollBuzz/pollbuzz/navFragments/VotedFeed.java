package com.PollBuzz.pollbuzz.navFragments;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.PollBuzz.pollbuzz.PollList;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.adapters.VotedFeedAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import Utils.firebase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VotedFeed extends Fragment {
    private RecyclerView votedRV;
    private VotedFeedAdapter mAdapter;
    private ArrayList<PollDetails> mArrayList;
    private LinearLayoutManager layoutManager;
    private FloatingActionButton fab;
    private CollectionReference userVotedRef;
    private firebase fb;

    public VotedFeed() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_voted_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setGlobals(view);
        fab.setOnClickListener(view1 -> {
            Intent i = new Intent(getContext(), PollList.class);
            startActivity(i);
        });
        userVotedRef.get().addOnCompleteListener(task -> getVotedPolls(task));
    }

    private void getVotedPolls(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful() && task.getResult() != null) {
            for (QueryDocumentSnapshot dS : task.getResult()) {
                if (dS.get("pollId") != null)
                    fb.getPollsCollection().document(dS.get("pollId").toString()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful() && task1.getResult() != null) {
                            DocumentSnapshot dS1 = task1.getResult();
                            if (dS1.exists())
                                addToRecyclerView(dS1);
                        } else {
                            Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }
    }

    private void addToRecyclerView(DocumentSnapshot dS1) {
        PollDetails polldetails = dS1.toObject(PollDetails.class);
        mArrayList.add(polldetails);
        mAdapter.notifyDataSetChanged();
    }

    private void setGlobals(@NonNull View view) {
        fab = view.findViewById(R.id.fab);
        votedRV = view.findViewById(R.id.votedrecyclerview);
        votedRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        votedRV.setLayoutManager(layoutManager);
        mAdapter = new VotedFeedAdapter(getContext(), mArrayList);
        votedRV.setAdapter(mAdapter);
        mArrayList = new ArrayList<>();
        fb = new firebase();
        userVotedRef = fb.getUserDocument().collection("Voted");
    }
}