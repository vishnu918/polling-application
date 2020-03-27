package com.PollBuzz.pollbuzz.adapters;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.responses.Descriptive_type_response;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.PollBuzz.pollbuzz.results.Descriptive_type_result;
import com.PollBuzz.pollbuzz.results.Image_type_result;
import com.PollBuzz.pollbuzz.results.Multiple_type_result;
import com.PollBuzz.pollbuzz.results.Ranking_type_result;
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

public class VotedFeedAdapter extends RecyclerView.Adapter<VotedFeedAdapter.VotedViewHolder> {

    private ArrayList<PollDetails> mPollDetails;
    Context mContext;

    public VotedFeedAdapter(Context mContext, ArrayList<PollDetails> mPollDetails) {
        this.mContext = mContext;
        this.mPollDetails = mPollDetails;
    }

    @NonNull
    @Override
    public VotedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.card_layout, parent, false);
        return new VotedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VotedViewHolder holder, int position) {
        if(mPollDetails.get(position).getTitle()!=null)
            holder.card_title.setText(mPollDetails.get(position).getTitle().trim());
        if(mPollDetails.get(position).getPoll_type()!=null)
            holder.card_type.setText(mPollDetails.get(position).getPoll_type());
        if(mPollDetails.get(position).getQuestion()!=null)
            holder.card_query.setText(mPollDetails.get(position).getQuestion().trim());
        if(mPollDetails.get(position).getAuthor()!=null)
        holder.card_author.setText(mPollDetails.get(position).getAuthor().trim());
        if(mPollDetails.get(position).getCreated_date()!=null)
            holder.card_date.setText(mPollDetails.get(position).getCreated_date().trim());
        holder.cardView.setOnClickListener(view -> startactivity(mPollDetails.get(position).getUID().trim(),mPollDetails.get(position).getPoll_type().trim()));
    }

    private void startactivity(String uid,String polltype) {
        Intent intent;
        switch(polltype)
        {
            case "SINGLE ANSWER POLL":
                intent = new Intent(mContext, Single_type_result.class);
                intent.putExtra("UID",uid);
                intent.putExtra("flag",0);
                mContext.startActivity(intent);
                break;
            case "MULTI ANSWER POLL":
                intent = new Intent(mContext, Multiple_type_result.class);
                intent.putExtra("UID",uid);
                intent.putExtra("flag",0);
                mContext.startActivity(intent);
                break;
            case "DESCRIPTIVE POLL":
                intent = new Intent(mContext, Descriptive_type_result.class);
                intent.putExtra("UID",uid);
                intent.putExtra("flag",0);
                mContext.startActivity(intent);
                break;
            case "PRIORITY POLL":
                intent = new Intent(mContext, Ranking_type_result.class);
                intent.putExtra("UID",uid);
                intent.putExtra("flag",0);
                mContext.startActivity(intent);
                break;
            case "IMAGE POLL":
                intent = new Intent(mContext, Image_type_result.class);
                intent.putExtra("UID",uid);
                intent.putExtra("flag",0);
                mContext.startActivity(intent);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return mPollDetails.size();
    }

    public static class VotedViewHolder extends RecyclerView.ViewHolder {

        public TextView card_title, card_type, card_query, card_author, card_date;
        CardView cardView;

        public VotedViewHolder(@NonNull View itemView) {
            super(itemView);
            card_title = itemView.findViewById(R.id.card_title);
            card_type = itemView.findViewById(R.id.card_type);
            card_query = itemView.findViewById(R.id.card_query);
            card_author = itemView.findViewById(R.id.card_author);
            card_date = itemView.findViewById(R.id.card_date);
            cardView = itemView.findViewById(R.id.cardV);
        }
    }
}