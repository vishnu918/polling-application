package com.PollBuzz.pollbuzz.adapters;

import com.PollBuzz.pollbuzz.Polldetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.results.ResultActivity;
import com.PollBuzz.pollbuzz.results.Single_type_result;

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

    private ArrayList<Polldetails> mPolldetails;
    Context mContext;

    public ProfileFeedAdapter(Context mContext, ArrayList<Polldetails> mPolldetails) {
        this.mContext = mContext;
        this.mPolldetails = mPolldetails;
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
        if(mPolldetails.get(position).getTitle()!=null)
            holder.card_title.setText(mPolldetails.get(position).getTitle().trim());
        if(mPolldetails.get(position).getPoll_type()!=null)
            holder.card_type.setText(mPolldetails.get(position).getPoll_type());
        if(mPolldetails.get(position).getQuestion()!=null)
            holder.card_query.setText(mPolldetails.get(position).getQuestion().trim());
        holder.card_author.setText("hello");
        if(mPolldetails.get(position).getCreated_date()!=null)
            holder.card_date.setText(mPolldetails.get(position).getCreated_date().trim());

        holder.cardV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ResultActivity.class);
                intent.putExtra("UID", mPolldetails.get(position).getUID());
                intent.putExtra("type",mPolldetails.get(position).getPoll_type());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPolldetails.size();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        private CardView cardV;
        public TextView card_title, card_type, card_query, card_author, card_date;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            cardV = itemView.findViewById(R.id.cardV);
            card_title = itemView.findViewById(R.id.card_title);
            card_type = itemView.findViewById(R.id.card_type);
            card_query = itemView.findViewById(R.id.card_query);
            card_author = itemView.findViewById(R.id.card_author);
            card_date = itemView.findViewById(R.id.card_date);
        }
    }
}