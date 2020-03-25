package com.PollBuzz.pollbuzz.adapters;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.HomeViewHolder> {

    private ArrayList<PollDetails> mPollDetails;
    Context mContext;

    public HomePageAdapter(Context mContext, ArrayList<PollDetails> mPollDetails) {
        this.mContext = mContext;
        this.mPollDetails = mPollDetails;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.card_layout, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        if (mPollDetails.get(position).getTitle() != null)
            holder.card_title.setText(mPollDetails.get(position).getTitle().trim());
        if (mPollDetails.get(position).getPoll_type() != null)
            holder.card_type.setText(mPollDetails.get(position).getPoll_type());
        if (mPollDetails.get(position).getQuestion() != null)
            holder.card_query.setText(mPollDetails.get(position).getQuestion().trim());
        if (mPollDetails.get(position).getAuthor() != null)
            holder.card_author.setText((mPollDetails.get(position).getAuthor().trim()));
        if (mPollDetails.get(position).getCreated_date() != null)
            holder.card_date.setText(mPollDetails.get(position).getCreated_date().trim());
    }

    @Override
    public int getItemCount() {
        return mPollDetails.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {

        public TextView card_title, card_type, card_query, card_author, card_date;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            card_title = itemView.findViewById(R.id.card_title);
            card_type = itemView.findViewById(R.id.card_type);
            card_query = itemView.findViewById(R.id.card_query);
            card_author = itemView.findViewById(R.id.card_author);
            card_date = itemView.findViewById(R.id.card_date);
        }
    }
}