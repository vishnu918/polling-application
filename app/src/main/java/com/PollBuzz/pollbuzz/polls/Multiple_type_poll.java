package com.PollBuzz.pollbuzz.polls;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.kinda.alert.KAlertDialog;

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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import Utils.helper;
import cn.pedant.SweetAlert.SweetAlertDialog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Multiple_type_poll extends AppCompatActivity {
    Button add;
    MaterialButton post_multi;
    TextInputEditText title_multi,question_multi;
    LinearLayout group;
    String name;
    int c;
    RadioButton b;
    Date date = Calendar.getInstance().getTime();
    firebase fb;
    ImageButton home, logout;
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
            Intent i = new Intent(Multiple_type_poll.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut();
            Intent i = new Intent(Multiple_type_poll.this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }

    private void setListeners(String formatteddate) {
        add.setOnClickListener(v -> {
            final RadioButton button = new RadioButton(getApplicationContext());
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            String t = "Option" + (c + 1);
            showDialog(Multiple_type_poll.this, button, 0);
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

        post_multi.setOnClickListener(view -> {
            if (title_multi.getText().toString().isEmpty()) {
                title_multi.setError("Please enter the title");
                title_multi.requestFocus();
            } else if (question_multi.getText().toString().isEmpty()) {
                question_multi.setError("Please enter the question");
                question_multi.requestFocus();
            } else if (group.getChildCount() == 0) {
                Toast.makeText(Multiple_type_poll.this, "Please enter atleast two options", Toast.LENGTH_SHORT).show();
            } else {
                addToDatabase(formatteddate);
            }
        });
    }
    private void showDialog() {
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Uploading your poll");
        dialog.setCancelable(false);
        dialog.show();
    }


    private void addToDatabase(String formatteddate) {
        showDialog();
        post_multi.setEnabled(false);
        if (fb.getUser() != null) {
            PollDetails polldetails = new PollDetails();
            polldetails.setTitle(title_multi.getText().toString().trim());
            polldetails.setQuestion(question_multi.getText().toString().trim());
            polldetails.setCreated_date(formatteddate);
            polldetails.setPoll_type("MULTI ANSWER POLL");
            polldetails.setAuthor(helper.getusernamePref(getApplicationContext()));
            polldetails.setAuthorUID(fb.getUserId());
            Map<String, Integer> map = new HashMap<>();
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton v = (RadioButton) group.getChildAt(i);
                map.put(v.getText().toString().trim(), 0);
            }
            polldetails.setMap(map);
            CollectionReference docCreated = fb.getUserDocument().collection("Created");
            DocumentReference doc = fb.getPollsCollection().document();
            doc.set(polldetails)
                    .addOnSuccessListener(aVoid -> {
                        dialog.dismissWithAnimation();
                        Map<String, String> m = new HashMap<>();
                        m.put("pollId", doc.getId());
                        docCreated.document().set(m);
                        Toast.makeText(Multiple_type_poll.this, "Your data added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Multiple_type_poll.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Multiple_type_poll.this, "Unable to post.Please try again", Toast.LENGTH_SHORT).show();
                        dialog.dismissWithAnimation();
                        post_multi.setEnabled(true);
                    });

        }
    }

    private void setGlobals() {
        setContentView(R.layout.activity_multiple_type_poll);
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
        post_multi = findViewById(R.id.post_multi);
        title_multi = findViewById(R.id.title_multi);
        question_multi = findViewById(R.id.question_multi);
        dialog=new KAlertDialog(Multiple_type_poll.this,SweetAlertDialog.PROGRESS_TYPE);

        if (group.getChildCount() == 0)
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
        dialogButton.setOnClickListener(v -> {
            name = text.getEditText().getText().toString();
            if (name.isEmpty()) {
                text.setError("Please enter tit");
                text.requestFocus();
            } else {
                button.setText(name);
                Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
                dialog.dismiss();
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
            showDialog(Multiple_type_poll.this,b,1);
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