package com.PollBuzz.pollbuzz.adapters;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.results.PercentageResult;
import com.PollBuzz.pollbuzz.results.ResultActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileFeedAdapter extends RecyclerView.Adapter<ProfileFeedAdapter.ProfileViewHolder> {

    private ArrayList<PollDetails> mPollDetails;
    private Context mContext;

    public ProfileFeedAdapter(Context mContext, ArrayList<PollDetails> mPollDetails) {
        this.mContext = mContext;
        this.mPollDetails = mPollDetails;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.card_layout, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        setData(holder, position);
        clickListener(holder, position);
    }

    private void clickListener(@NonNull ProfileViewHolder holder, int position) {
        holder.cardV.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PercentageResult.class);
            intent.putExtra("UID", mPollDetails.get(position).getUID());
            intent.putExtra("type", mPollDetails.get(position).getPoll_type());
            mContext.startActivity(intent);
        });
    }

    private void setData(@NonNull ProfileViewHolder holder, int position) {
        if (mPollDetails.get(position).getPoll_type() != null)
            holder.card_type.setText(mPollDetails.get(position).getPoll_type());
        if (mPollDetails.get(position).getQuestion() != null)
            holder.card_query.setText(mPollDetails.get(position).getQuestion().trim());
        if (mPollDetails.get(position).getAuthor() != null)
            holder.card_author.setText(mPollDetails.get(position).getAuthor());
        if (mPollDetails.get(position).getCreated_date() != null)
            holder.card_date.setText(mPollDetails.get(position).getCreated_date().trim());
    }

    @Override
    public int getItemCount() {
        return mPollDetails.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {

        private CardView cardV;
        private TextView card_title, card_type, card_query, card_author, card_date;

        ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            setGlobals(itemView);
        }

        private void setGlobals(@NonNull View itemView) {
            cardV = itemView.findViewById(R.id.cardV);
            card_type = itemView.findViewById(R.id.card_type);
            card_query = itemView.findViewById(R.id.card_query);
            card_author = itemView.findViewById(R.id.card_author);
            card_date = itemView.findViewById(R.id.card_date);
        }
    }
}