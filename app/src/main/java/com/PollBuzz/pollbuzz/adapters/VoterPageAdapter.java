package com.PollBuzz.pollbuzz.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.PollBuzz.pollbuzz.Polldetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.VoteDetails;

public class VoterPageAdapter extends RecyclerView.Adapter<VoterPageAdapter.VoterViewHolder> {

    private List<VoteDetails> mVotedetails;
    Context mContext;

    public VoterPageAdapter(Context mContext, List<VoteDetails> mVoteDetails) {
        this.mContext = mContext;
        this.mVotedetails = mVoteDetails;
    }

    @NonNull
    @Override
    public VoterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.voterlistitem, parent, false);
        return new VoterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoterViewHolder holder, int position) {
        if(mVotedetails.get(position).getUsername()!=null)
            holder.voterUsername.setText(mVotedetails.get(position).getUsername().trim());
        if(mVotedetails.get(position).getOption()!=null)
            holder.vote.setText(mVotedetails.get(position).getOption());
    }

    @Override
    public int getItemCount() {
        return mVotedetails.size();
    }

    public static class VoterViewHolder extends RecyclerView.ViewHolder {

        public TextView voterUsername,vote;

        public VoterViewHolder(@NonNull View itemView) {
            super(itemView);
            voterUsername = itemView.findViewById(R.id.voterUsername);
            vote = itemView.findViewById(R.id.vote);
        }
    }
}