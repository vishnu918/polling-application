package com.PollBuzz.pollbuzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Single_type_response extends AppCompatActivity {
    TextView title, query;
    RadioGroup group;
    FirebaseFirestore db;
    Map<String,Integer> options;
    String key;
    Typeface typeface;
     Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        title=findViewById(R.id.title);
        query=findViewById(R.id.query);
        group=findViewById(R.id.options);
        db=FirebaseFirestore.getInstance();
       options=new HashMap<>();
       key= "HuIbrwZXR6piG0uSBWWc";
       typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
       dialog=new Dialog(Single_type_response.this);
        showDialog();
       db.collection("Polls").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {



               if (task.isSuccessful()) {

                   DocumentSnapshot data = task.getResult();
                   if(data.exists())
                   {   group.removeAllViews();
                   dialog.dismiss();
                       Polldetails polldetails=data.toObject(Polldetails.class);
                       title.setText(polldetails.getTitle());
                       query.setText(polldetails.getQuestion());
                       options=polldetails.getMap();
                       int c=options.size();
                       for(Map.Entry<String,Integer> entry : options.entrySet())
                       {
                           RadioButton button=new RadioButton(getApplicationContext());
                           RadioGroup.LayoutParams layoutParams=new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,RadioGroup.LayoutParams.WRAP_CONTENT);
                           layoutParams.setMargins(5,20,5,20);
                           button.setLayoutParams(layoutParams);
                           button.setTypeface(typeface);
                           button.setText(entry.getKey());
                           button.setTextSize(20.0f);

                           group.addView(button);
                       }
                   }
                   }

               }
           }
       );




    }
    private void showDialog()
    {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        dialog.setCancelable(false);
        dialog.show();
        window.setAttributes(lp);
    }
}
