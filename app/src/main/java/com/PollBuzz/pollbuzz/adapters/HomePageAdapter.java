package com.PollBuzz.pollbuzz;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.HomeViewHolder> {

    private ArrayList<Polldetails> mPolldetails;
    Context mContext;

    public HomePageAdapter(Context mContext, ArrayList<Polldetails> mPolldetails) {
        this.mContext = mContext;
        this.mPolldetails = mPolldetails;
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
        if(mPolldetails.get(position).getTitle()!=null)
        holder.card_title.setText(mPolldetails.get(position).getTitle().trim());
        if(mPolldetails.get(position).getPoll_type()!=null)
            holder.card_type.setText(mPolldetails.get(position).getPoll_type());
        if(mPolldetails.get(position).getQuestion()!=null)
            holder.card_query.setText(mPolldetails.get(position).getQuestion().trim());
        holder.card_author.setText("hello");
        if(mPolldetails.get(position).getCreated_date()!=null)
        holder.card_date.setText(mPolldetails.get(position).getCreated_date().trim());
    }

    @Override
    public int getItemCount() {
        return mPolldetails.size();
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