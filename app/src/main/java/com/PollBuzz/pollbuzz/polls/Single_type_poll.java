package com.PollBuzz.pollbuzz.polls;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.PollBuzz.pollbuzz.Polldetails;
import com.PollBuzz.pollbuzz.R;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.Polldetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Single_type_poll extends AppCompatActivity {
    Button add;
    RadioGroup group;
    String name;
    int c;
    RadioButton b;
    TextInputEditText title,question;
    MaterialButton button;
    Date date = Calendar.getInstance().getTime();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore fb = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_type_poll);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        group=findViewById(R.id.options);
        add=findViewById(R.id.add);
        c=group.getChildCount();
        title = findViewById(R.id.title1);
        button = findViewById(R.id.post);
        question = findViewById(R.id.question);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        final String formattedDate = df.format(date);

        if(group.getChildCount()==0)
            group.setVisibility(View.INVISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton button=new RadioButton(getApplicationContext());
                button.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,RadioGroup.LayoutParams.WRAP_CONTENT));
                String t="Option"+(c+1);
                //
                showDialog(Single_type_poll.this, button);

                button.setTag(t.toLowerCase());

                //group.removeAllViews();
                group.removeView(findViewById(R.id.option1));
                group.removeView(findViewById(R.id.option2));

                //button.setText(name);
                group.addView(button);
                group.setVisibility(View.VISIBLE);
                registerForContextMenu(button);


            }
        });
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button=(RadioButton)findViewById(checkedId);
                button.setChecked(false);
                button.showContextMenu();



            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.getText().toString().isEmpty())
                {
                    title.setError("Please enter the title");
                    title.requestFocus();
                }
                else if(question.getText().toString().isEmpty())
                {
                    question.setError("Please enter the question");
                    question.requestFocus();
                }
                else if(group.getChildCount()==0) {
                    Toast.makeText(Single_type_poll.this, "U shld have atleast two options", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(auth.getCurrentUser()!=null) {
                        Polldetails polldetails = new Polldetails();
                        polldetails.setTitle(title.getText().toString());
                        polldetails.setQuestion(question.getText().toString());
                        Map<String,Integer> map = new HashMap<>();
                        for(int i=0; i<group.getChildCount();i++)
                        {
                            RadioButton v = (RadioButton)group.getChildAt(i);
                            map.put(v.getText().toString(),0);
                        }
                        polldetails.setMap(map);
                        polldetails.setPoll_type("SINGLE ANSWER POLL");
                        polldetails.setCreated_date(formattedDate);
                        CollectionReference docCreated = fb.collection("Users").document(auth.getCurrentUser().getUid()).collection("Created");
                        DocumentReference doc = fb.collection("Polls").document();
                        doc.set(polldetails)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Map<String,String> m=new HashMap<>();
                                        m.put("pollId",doc.getId());
                                        docCreated.document().set(m);
                                        Toast.makeText(Single_type_poll.this, "Added to database", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Single_type_poll.this, "Unable to add retry", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        String name_doc = doc.getId();
                        Map<String,Integer> mapi = new HashMap<>();
                        mapi.put(name_doc,0);
                        fb.collection("Users").document(auth.getCurrentUser().getUid()).collection("Created").document().set(mapi)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Single_type_poll.this, "document added to users", Toast.LENGTH_SHORT).show();
                                        Intent i=new Intent(Single_type_poll.this, MainActivity.class);
                                        startActivity(i);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Single_type_poll.this, "Failed to add document", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Please Enter the option name", Toast.LENGTH_SHORT).show();
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
            showDialog(Single_type_poll.this,b);
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
}