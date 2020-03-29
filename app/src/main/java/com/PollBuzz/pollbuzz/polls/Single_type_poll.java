package com.PollBuzz.pollbuzz.polls;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firestore.v1.DocumentTransform;
import com.google.firestore.v1.DocumentTransform.FieldTransform.ServerValue;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.responses.Single_type_response;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import Utils.helper;
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
    firebase fb;
    ImageButton home,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGlobals();
        setActionBarFunctionality();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        final String formattedDate = df.format(date);
        setListeners(formattedDate);

    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Single_type_poll.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut();
            Intent i = new Intent(Single_type_poll.this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }
    private void setListeners(String formattedDate) {
        add.setOnClickListener(v -> {
            RadioButton button = new RadioButton(getApplicationContext());
            button.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
            String t = "Option" + (c + 1);
            showDialog(Single_type_poll.this, button, 0);
            button.setTag(t.toLowerCase());
            group.removeView(findViewById(R.id.option1));
            group.removeView(findViewById(R.id.option2));
            group.addView(button);
            group.setVisibility(View.VISIBLE);
            registerForContextMenu(button);
        });
        group.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) (group, checkedId) -> {
            RadioButton button = (RadioButton) findViewById(checkedId);
            button.setChecked(false);
            button.showContextMenu();
        });
        button.setOnClickListener(view -> {
            if (title.getText().toString().isEmpty()) {
                title.setError("Please enter the title");
                title.requestFocus();
            } else if (question.getText().toString().isEmpty()) {
                question.setError("Please enter the question");
                question.requestFocus();
            } else if (group.getChildCount() == 0) {
                Toast.makeText(Single_type_poll.this, "U shld have atleast two options", Toast.LENGTH_SHORT).show();
            } else {
                addToDatabase(formattedDate);
            }
        });
    }

    private void addToDatabase(String formattedDate) {
        if (fb.getUser() != null) {
            PollDetails polldetails = new PollDetails();
            polldetails.setTitle(title.getText().toString());
            polldetails.setQuestion(question.getText().toString());
            polldetails.setAuthor(helper.getusernamePref(getApplicationContext()));
            polldetails.setAuthorUID(fb.getUserId());
            polldetails.setTimestamp(Timestamp.now().getSeconds());
            Map<String, Integer> map = new HashMap<>();
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton v = (RadioButton) group.getChildAt(i);
                map.put(v.getText().toString(), 0);
            }
            polldetails.setMap(map);
            polldetails.setPoll_type("SINGLE ANSWER POLL");
            polldetails.setCreated_date(formattedDate);
            CollectionReference docCreated = fb.getUserDocument().collection("Created");
            DocumentReference doc = fb.getPollsCollection().document();
            doc.set(polldetails)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Map<String, Object> m = new HashMap<>();
                            m.put("pollId", doc.getId());
                            m.put("timestamp",Timestamp.now().getSeconds());
                            docCreated.document().set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Single_type_poll.this, "Your data added successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Single_type_poll.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else
                                        Toast.makeText(Single_type_poll.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(Single_type_poll.this, "Unable to post.Please try again", Toast.LENGTH_SHORT).show());
        }
    }

    private void setGlobals() {
        setContentView(R.layout.activity_single_type_poll);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        fb = new firebase();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        group = findViewById(R.id.options);
        add = findViewById(R.id.add);
        c = group.getChildCount();
        title = findViewById(R.id.title1);
        button = findViewById(R.id.post);
        question = findViewById(R.id.question);
        if(group.getChildCount()==0)
            group.setVisibility(View.INVISIBLE);
    }


    public void showDialog(Activity activity, final RadioButton button,int flag){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.set_name_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextInputLayout text =  dialog.findViewById(R.id.name);
        if(flag == 1 && b.getText() != null){
            text.getEditText().setText(b.getText().toString().trim());
        }

        dialog.setCancelable(false);
        dialog.show();
        window.setAttributes(lp);






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
            showDialog(Single_type_poll.this,b,1);
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