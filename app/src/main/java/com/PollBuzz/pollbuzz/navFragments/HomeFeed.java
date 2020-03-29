package com.PollBuzz.pollbuzz.navFragments;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.PollList;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.adapters.HomePageAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.RecyclerView;

public class HomeFeed extends Fragment {
    private ArrayList<PollDetails> arrayList;
    private ShimmerRecyclerView recyclerView;
    private com.PollBuzz.pollbuzz.adapters.HomePageAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;
    private firebase fb;
    private LayoutAnimationController controller;

    public HomeFeed() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setGlobals(view);
        setListeners();
        getData();
    }

    private void getData() {
        fb.getPollsCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                if (!task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot dS : task.getResult()) {
                        addToRecyclerView(dS);
                    }
                } else {
                    recyclerView.hideShimmerAdapter();
                }
            } else {
                recyclerView.hideShimmerAdapter();
                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setListeners() {
        fab.setOnClickListener(view1 -> {
            Intent i = new Intent(getContext(), PollList.class);
            startActivity(i);
        });
    }

    private void addToRecyclerView(QueryDocumentSnapshot dS) {
        PollDetails polldetails = dS.toObject(PollDetails.class);
        polldetails.setUID(dS.getId());
            fb.getPollsCollection().document(dS.getId()).collection("Response").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                        Boolean flag = Boolean.TRUE;
                        for (QueryDocumentSnapshot dS1 : task.getResult()) {
                            if (dS1.getId().equals(fb.getUserId())) {
                                flag = Boolean.FALSE;
                                break;
                            }
                        }
                        if (flag) {
                            arrayList.add(polldetails);
                            Collections.sort(arrayList, new Comparator<PollDetails>() {
                                @Override
                                public int compare(PollDetails pollDetails, PollDetails t1) {
                                    return Long.compare(t1.getTimestamp(), pollDetails.getTimestamp());
                                }
                            });
                        }
                    adapter.notifyDataSetChanged();
                    recyclerView.hideShimmerAdapter();
                    recyclerView.scheduleLayoutAnimation();
                }
            });
    }

    private void setGlobals(@NonNull View view) {
        arrayList = new ArrayList<>();
        fab = view.findViewById(R.id.fab);
        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.animation_down_to_up);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new HomePageAdapter(getContext(), arrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.showShimmerAdapter();
        YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(view.findViewById(R.id.text));
        YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(fab);
        fb = new firebase();
    }


}