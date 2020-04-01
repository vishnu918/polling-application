package com.PollBuzz.pollbuzz.navFragments;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.polls.Image_type_poll;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.PollList;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.adapters.HomePageAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import Utils.firebase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFeed extends Fragment {
    private ArrayList<PollDetails> arrayList;
    private ShimmerRecyclerView recyclerView;
    private com.PollBuzz.pollbuzz.adapters.HomePageAdapter adapter;
    private LinearLayoutManager layoutManager;
    private firebase fb;
    private LayoutAnimationController controller;
    MaterialTextView viewed;
    private TextInputEditText search_type;
    private ImageButton search,back,back2,check;
    private LinearLayout search_layout,date_layout;
    private Button search_button;
    private  String name;
    TextView starting,ending;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private DocumentSnapshot lastIndex;
    ProgressBar progressBar;
    Boolean flagFirst=true,flagFetch=true;
    public HomeFeed() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_home_feed, container, false);
        setGlobals(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("Size",String.valueOf(arrayList.size()) + " "+String.valueOf(layoutManager.findLastVisibleItemPosition()));
                if(!arrayList.isEmpty() && layoutManager.findLastVisibleItemPosition()==arrayList.size()-1 && flagFetch){
                    progressBar.setVisibility(View.VISIBLE);
                    flagFirst=false;
                    flagFetch=false;
                    getData(0,"",null,null);
                }
            }
        });
        getData(0,"",null,null);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showPopup(view);
            }
        });
/*        search_type.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                arrayList.clear();
                adapter.notifyDataSetChanged();
                if(!charSequence.toString().isEmpty()){
                    getData(1,charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });*/
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                     arrayList.clear();;
                     name = search_type.getText().toString();
                     if(!name.isEmpty()){
                         getData(1,name,null,null);
                     }
                     search_type.setText("");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList.clear();
                getData(0,"",null,null);
                YoYo.with(Techniques.DropOut).duration(1000).playOn(search_layout);
                search_layout.setVisibility(View.GONE);
            }
        });
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList.clear();
                getData(0,"",null,null);
                YoYo.with(Techniques.DropOut).duration(1000).playOn(search_layout);
                date_layout.setVisibility(View.GONE);
            }
        });
        starting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String date=day+"-"+(month+1)+"-"+year;
                                starting.setText(date);

                            }
                        }, 0, 0, 0);
                datePickerDialog.show();
            }
        });
        ending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String date=day+"-"+(month+1)+"-"+year;
                                ending.setText(date);

                            }
                        }, 0, 0, 0);
                datePickerDialog.show();
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if(starting.getText().toString().isEmpty() && ending.getText().toString().isEmpty())
                        Toast.makeText(getActivity(),"Please atleast one of the dates",Toast.LENGTH_LONG).show();
                    else {
                        if(!starting.getText().toString().isEmpty() && !ending.getText().toString().isEmpty())
                        {
                            Date start=dateFormat.parse(starting.getText().toString());
                            Date end=dateFormat.parse(ending.getText().toString());
                            if(start.compareTo(end)>0)
                                Toast.makeText(getActivity(),"Starting date can't be after the ending date",Toast.LENGTH_LONG).show();
                            else
                            {   arrayList.clear();
                                getData(2,"",dateFormat.parse(starting.getText().toString()),dateFormat.parse(ending.getText().toString()));
                            }
                        }
                        else
                        {   arrayList.clear();
                            if(starting.getText().toString().isEmpty())
                                getData(2,"",null,dateFormat.parse(ending.getText().toString()));
                            else
                                getData(2,"",dateFormat.parse(starting.getText().toString()),null);


                        }

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getData(int flag, String editable, Date start, Date end) {
        if (lastIndex == null) {

            fb.getPollsCollection().orderBy("timestamp", Query.Direction.DESCENDING).
                    limit(5).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (!task.getResult().isEmpty()) {
                        viewed.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        for (QueryDocumentSnapshot dS : task.getResult()) {
                            addToRecyclerView(dS, flag, editable, start, end);
                            lastIndex=dS;
                        }
                        if (flag == 2 && arrayList.size() == 0) {
                            viewed.setText("No active polls for you to vote in that span ");
                            viewed.setVisibility(View.VISIBLE);
                        } else
                            viewed.setText("You have voted all active polls");
                    } else {
                        flagFetch=false;
                        recyclerView.hideShimmerAdapter();
                        viewed.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.hideShimmerAdapter();
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            fb.getPollsCollection().orderBy("timestamp", Query.Direction.DESCENDING).
                    startAfter(lastIndex).limit(5).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot dS : task.getResult()) {
                            addToRecyclerView(dS, flag, editable, start, end);
                            lastIndex=dS;
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                    else{
                        progressBar.setVisibility(View.GONE);
                        flagFetch=false;
                        Toast.makeText(getContext(), "You have viewed all polls...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void addToRecyclerView(QueryDocumentSnapshot dS,int flagi,String editable,Date start, Date end) {
        PollDetails polldetails = dS.toObject(PollDetails.class);
        polldetails.setUID(dS.getId());
            fb.getPollsCollection().document(dS.getId()).collection("Response").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                        Boolean flag = Boolean.TRUE;
                        for (QueryDocumentSnapshot dS1 : task.getResult()) {
                            if (dS1.getId().equals(fb.getUserId())) {
                                flag = Boolean.FALSE;
                                if(flagFirst==null)
                                recyclerView.hideShimmerAdapter();
                                break;
                            }
                        }
                        Log.d("TimeStamp",dS.get("timestamp").toString());
                        if (flag) {
                            if(flagi == 1 && !editable.isEmpty()){
                                if(polldetails.getAuthor().toLowerCase().contains(editable.toLowerCase()) && !arrayList.contains(polldetails)) {
                                    Log.d("HomeFeed",String.valueOf(arrayList.size()) );
                                    arrayList.add(polldetails);
                                }
                            }
                            else if(flagi==2)
                            {
                                if(start!=null && end!=null)
                                {
                                    int d,d1;
                                    if(start.compareTo(end)==0)
                                    {
                                        if(polldetails.getCreated_date().compareTo(start)==0)
                                        {
                                            arrayList.add(polldetails);

                                        }

                                    }
                                    if(polldetails.getCreated_date().compareTo(start)>=0 && polldetails.getCreated_date().compareTo(end)<=0)
                                    {
                                        arrayList.add(polldetails);
                                    }
                                    d=polldetails.getCreated_date().compareTo(start);
                                    d1=polldetails.getCreated_date().compareTo(end);

                                    //Toast.makeText(getActivity(),String.valueOf(d)+" "+String.valueOf(d1),Toast.LENGTH_LONG).show();
                                }
                                if(start==null)
                                {
                                    if(polldetails.getCreated_date().compareTo(end)<=0)
                                        arrayList.add(polldetails);
                                    {

                                    }
                                }
                                if(end==null)
                                {
                                    if(polldetails.getCreated_date().compareTo(start)>=0)
                                        arrayList.add(polldetails);
                                    {

                                    }
                                }
                            }
                            else {
                                if(flagi == 0)
                                    arrayList.add(polldetails);
                            }
                            Collections.sort(arrayList, (pollDetails, t1) -> Long.compare(t1.getTimestamp(), pollDetails.getTimestamp()));
                            viewed.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            flagFetch=true;
                            if(flagFirst) {
                                recyclerView.hideShimmerAdapter();
                                recyclerView.scheduleLayoutAnimation();
                            }
                        }
                }
            });
    }

    private void setGlobals(@NonNull View view) {
        progressBar=view.findViewById(R.id.pBar);
        arrayList = new ArrayList<>();
        viewed=view.findViewById(R.id.viewed);
        search_layout = view.findViewById(R.id.type_layout);
        search_layout.setVisibility(View.GONE);
        search = view.findViewById(R.id.search);
        search_type = view.findViewById(R.id.search_type);
        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.animation_down_to_up);
        back = view.findViewById(R.id.back);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        search_button = view.findViewById(R.id.search_button);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        adapter = new HomePageAdapter(getContext(), arrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.showShimmerAdapter();
        YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(view.findViewById(R.id.text));
        fb = new firebase();
        starting=view.findViewById(R.id.starting_date);
        ending=view.findViewById(R.id.ending_date);
        back2=view.findViewById(R.id.back2);
        check=view.findViewById(R.id.check);
        date_layout=view.findViewById(R.id.date_layout);
        date_layout.setVisibility(View.GONE);
    }
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.filter, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.by_author:
                        search_layout.setVisibility(View.VISIBLE);
                        date_layout.setVisibility(View.GONE);
                        YoYo.with(Techniques.BounceInDown).delay(1000).playOn(search_layout);
                        return true;
                    case R.id.by_date:
                        date_layout.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.BounceInDown).delay(1000).playOn(search_layout);
                        search_layout.setVisibility(View.GONE);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();
    }



}