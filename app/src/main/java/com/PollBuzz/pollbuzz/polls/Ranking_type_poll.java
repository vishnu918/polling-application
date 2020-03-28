package com.PollBuzz.pollbuzz.polls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.PollList;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.helper;

public class Ranking_type_poll extends AppCompatActivity {
    Button add;
    LinearLayout group;
    String name;
    TextInputEditText title_ranking, question_ranking;
    MaterialButton post_ranking;
    int c;
    RadioButton b;
    TextView page_title;
    FirebaseAuth auth;
    ImageButton home,logout;
    FirebaseAuth.AuthStateListener listener;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Date date = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_type);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Ranking_type_poll.this, MainActivity.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
            }
        });
        page_title = view.findViewById(R.id.page_title);
        //page_title.setText("Ranking Type Poll");
        group = findViewById(R.id.options);
        add = findViewById(R.id.add);
        c = group.getChildCount();
        title_ranking = findViewById(R.id.title_ranking);
        question_ranking = findViewById(R.id.question_ranking);
        post_ranking = findViewById(R.id.post_ranking);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String formatteddate = dateFormat.format(date);
        if (group.getChildCount() == 0)
            group.setVisibility(View.INVISIBLE);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RadioButton button = new RadioButton(getApplicationContext());
                button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                String t = "Option" + (c + 1);
                //
                showDialog(Ranking_type_poll.this, button);

                button.setTag(t.toLowerCase());
                group.removeView(findViewById(R.id.option1));
                group.removeView(findViewById(R.id.option2));
                group.addView(button);
                group.setVisibility(View.VISIBLE);
                registerForContextMenu(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.showContextMenu();
                        button.setChecked(false);
                    }
                });


            }
        });
        auth = FirebaseAuth.getInstance();
        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    Intent i=new Intent(Ranking_type_poll.this, LoginSignupActivity.class);
                    startActivity(i);
                }

            }
        };

        post_ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title_ranking.getText().toString().isEmpty()){
                    title_ranking.setError("Please enter the question");
                    title_ranking.requestFocus();
                }
                else if(question_ranking.getText().toString().isEmpty())
                {
                    question_ranking.setError("Please enter the question");
                    question_ranking.requestFocus();
                }
                else
                {
                    if(auth.getCurrentUser() != null)
                    {
                        PollDetails polldetails = new PollDetails();
                        polldetails.setTitle(title_ranking.getText().toString().trim());
                        polldetails.setQuestion(question_ranking.getText().toString().trim());
                        polldetails.setCreated_date(formatteddate);
                        polldetails.setAuthor(helper.getusernamePref(getApplicationContext()));
                        Map<String,Integer> map = new HashMap<>();
                        for(int i=0;i<group.getChildCount();i++)
                        {
                            RadioButton v = (RadioButton)group.getChildAt(i);
                            map.put(v.getText().toString().trim(),0);
                        }
                        polldetails.setMap(map);
                        polldetails.setPoll_type("PRIORITY POLL");
                        CollectionReference docCreated = firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Created");
                        DocumentReference doc = firebaseFirestore.collection("Polls").document();
                        doc.set(polldetails)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Map<String,String> m=new HashMap<>();
                                        m.put("pollId",doc.getId());
                                        docCreated.document().set(m);
                                        Toast.makeText(Ranking_type_poll.this, "Your data added Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Ranking_type_poll.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Ranking_type_poll.this, "Unable to post.Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

            }
        });

    }
    public void showDialog(Activity activity, final RadioButton button){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.set_name_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        dialog.setCancelable(false);
        dialog.show();
        window.setAttributes(lp);



        final TextInputLayout text =  dialog.findViewById(R.id.name);


        Button dialogButton = (Button) dialog.findViewById(R.id.done);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=text.getEditText().getText().toString();
                if(name.isEmpty())
                {
                    text.setError("Please enter tit");
                    text.requestFocus();
                }
                else {
                    button.setText(name);
                    Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });



    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.radiobutton_menu, menu);
        b=(RadioButton)v;
        menu.setHeaderTitle("Select The Action");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        if(item.getItemId()==R.id.edit){
            //Toast.makeText(getApplicationContext(),"calling code",Toast.LENGTH_LONG).show();
            showDialog(Ranking_type_poll.this,b);
        }
        else if(item.getItemId()==R.id.delete){
            group.removeView(b);
            if(group.getChildCount()==0)
                group.setVisibility(View.INVISIBLE);
        }else{
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);

    }
}