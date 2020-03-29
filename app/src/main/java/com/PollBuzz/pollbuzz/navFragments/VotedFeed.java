package com.PollBuzz.pollbuzz.navFragments;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
                for (QueryDocumentSnapshot dS : task.getResult()) {
                    if (dS.exists()) {
                        long timestamp= (long) dS.get("timestamp");
                        fb.getPollsCollection().document(dS.getId())
                                .get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful() && task1.getResult() != null) {
                                addToRecyclerView(task1.getResult(),timestamp);
                            }
                        });
                    }
                }
            }else{
                votedRV.hideShimmerAdapter();
                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToRecyclerView(DocumentSnapshot dS1,long timestamp) {
        Log.d("TimeStamp", String.valueOf(timestamp));
        PollDetails polldetails = dS1.toObject(PollDetails.class);
        polldetails.setUID(dS1.getId());
        polldetails.setTimestamp(timestamp);
        mArrayList.add(polldetails);
        Collections.sort(mArrayList, new Comparator<PollDetails>() {
            @Override
            public int compare(PollDetails pollDetails, PollDetails t1) {
                return Long.compare(t1.getTimestamp(), pollDetails.getTimestamp());
            }
        });
        votedRV.setLayoutAnimation(controller);
        mAdapter.notifyDataSetChanged();
        votedRV.hideShimmerAdapter();
        votedRV.scheduleLayoutAnimation();
    }

    private void setGlobals(@NonNull View view) {
        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.animation_down_to_up);
        fab = view.findViewById(R.id.fab);
        votedRV = view.findViewById(R.id.votedrecyclerview);
        votedRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        votedRV.setLayoutManager(layoutManager);
        mArrayList = new ArrayList<>();
        mAdapter = new VotedFeedAdapter(getContext(), mArrayList);
        votedRV.setAdapter(mAdapter);
        votedRV.setLayoutAnimation(controller);
        votedRV.showShimmerAdapter();
        fb = new firebase();
        userVotedRef = fb.getUserDocument().collection("Voted");
    }
}