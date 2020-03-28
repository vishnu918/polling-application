package com.PollBuzz.pollbuzz.navFragments;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.PollList;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.adapters.VotedFeedAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.ArrayList;

import Utils.firebase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class VotedFeed extends Fragment {
    private ShimmerRecyclerView votedRV;
    private VotedFeedAdapter mAdapter;
    private ArrayList<PollDetails> mArrayList;
    private LinearLayoutManager layoutManager;
    private FloatingActionButton fab;
    private CollectionReference userVotedRef;
    private firebase fb;
    private LayoutAnimationController controller;

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
        setListeners();
        getData();
    }

    private void setListeners() {
        fab.setOnClickListener(view1 -> {
            Intent i = new Intent(getContext(), PollList.class);
            startActivity(i);
        });
    }

    private void getData() {
        userVotedRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                votedRV.hideShimmerAdapter();
                for (QueryDocumentSnapshot dS : task.getResult()) {
                    if (dS.exists()) {
                        fb.getPollsCollection().document(dS.getId())
                                .get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful() && task1.getResult() != null) {
                                addToRecyclerView(task1.getResult());
                            }
                        });
                    }
                }
            }
        });
    }

    private void addToRecyclerView(DocumentSnapshot dS1) {
        PollDetails polldetails = dS1.toObject(PollDetails.class);
        polldetails.setUID(dS1.getId());
        mArrayList.add(polldetails);
        votedRV.setLayoutAnimation(controller);
        mAdapter.notifyDataSetChanged();
        votedRV.scheduleLayoutAnimation();
    }

    private void setGlobals(@NonNull View view) {
        fab = view.findViewById(R.id.fab);
        votedRV = view.findViewById(R.id.votedrecyclerview);
        votedRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        votedRV.setLayoutManager(layoutManager);
        mArrayList = new ArrayList<>();
        mAdapter = new VotedFeedAdapter(getContext(), mArrayList);
        votedRV.setAdapter(mAdapter);
        votedRV.showShimmerAdapter();
        fb = new firebase();
        userVotedRef = fb.getUserDocument().collection("Voted");
        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.animation_down_to_up);
    }
}