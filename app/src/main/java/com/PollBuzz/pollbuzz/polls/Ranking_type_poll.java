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

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.kinda.alert.KAlertDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import Utils.helper;
import cn.pedant.SweetAlert.SweetAlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Ranking_type_poll extends AppCompatActivity {
    Button add;
    LinearLayout group;
    String name;
    TextInputEditText question_ranking;
    MaterialButton post_ranking;
    int c;
    RadioButton b;
    TextView page_title;
    ImageButton home,logout;
    Date date = Calendar.getInstance().getTime();
    firebase fb;
    KAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGlobals();
        setActionBarFunctionality();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String formatteddate = dateFormat.format(date);
        setListeners(formatteddate);
    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Ranking_type_poll.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut();
            Intent i = new Intent(Ranking_type_poll.this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }
    private void setListeners(String formatteddate) {
        add.setOnClickListener(v -> {
            closeKeyboard();
            final RadioButton button = new RadioButton(getApplicationContext());
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            String t = "Option" + (c + 1);
            showDialog(Ranking_type_poll.this, button, 0);
            button.setTag(t.toLowerCase());
            group.removeView(findViewById(R.id.option1));
            group.removeView(findViewById(R.id.option2));
            group.addView(button);
            group.setVisibility(View.VISIBLE);
            registerForContextMenu(button);
            button.setOnClickListener(v1 -> {
                v1.showContextMenu();
                button.setChecked(false);
            });


        });
        post_ranking.setOnClickListener(view -> {
            closeKeyboard();
           if (question_ranking.getText().toString().isEmpty()) {
                question_ranking.setError("Please enter the question");
                question_ranking.requestFocus();
            } else {
                addToDatabase(formatteddate);
            }

        });
    }

    private void addToDatabase(String formatteddate) {
        showDialog();
        post_ranking.setEnabled(false);
        if (fb.getUser() != null) {
            PollDetails polldetails = new PollDetails();
            polldetails.setQuestion(question_ranking.getText().toString().trim());
            polldetails.setCreated_date(formatteddate);
            polldetails.setAuthor(helper.getusernamePref(getApplicationContext()));
            polldetails.setAuthorUID(fb.getUserId());
            polldetails.setTimestamp(Timestamp.now().getSeconds());
            Map<String, Integer> map = new HashMap<>();
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton v = (RadioButton) group.getChildAt(i);
                map.put(v.getText().toString().trim(), 0);
            }
            polldetails.setMap(map);
            polldetails.setPoll_type("PRIORITY POLL");
            CollectionReference docCreated = fb.getUserDocument().collection("Created");
            DocumentReference doc = fb.getPollsCollection().document();
            doc.set(polldetails)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            dialog.dismissWithAnimation();
                            Map<String, Object> m = new HashMap<>();
                            m.put("pollId", doc.getId());
                            m.put("timestamp",Timestamp.now().getSeconds());
                            docCreated.document().set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Ranking_type_poll.this, "Your data added Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Ranking_type_poll.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(Ranking_type_poll.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.dismissWithAnimation();
                                        post_ranking.setEnabled(true);
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Ranking_type_poll.this, "Unable to post.Please try again", Toast.LENGTH_SHORT).show();
                        post_ranking.setEnabled(true);
                        dialog.dismissWithAnimation();
                    });
        }
    }

    private void setGlobals() {
        setContentView(R.layout.activity_ranking_type);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        fb = new firebase();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        page_title = view.findViewById(R.id.page_title);
        group = findViewById(R.id.options);
        add = findViewById(R.id.add);
        c = group.getChildCount();
        question_ranking = findViewById(R.id.question_ranking);
        post_ranking = findViewById(R.id.post_ranking);
        dialog=new KAlertDialog(Ranking_type_poll.this,SweetAlertDialog.PROGRESS_TYPE);


        if (group.getChildCount() == 0)
            group.setVisibility(View.INVISIBLE);
    }

    private void showDialog() {
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Uploading your poll");
        dialog.setCancelable(false);
        dialog.show();
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
            showDialog(Ranking_type_poll.this,b,1);
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
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}