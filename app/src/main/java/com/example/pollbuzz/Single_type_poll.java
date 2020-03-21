package com.example.pollbuzz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class Single_type_poll extends AppCompatActivity {
    Button add;
    RadioGroup group;
    String name;

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

        if(group.getChildCount()==0)
            group.setVisibility(View.INVISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton button=new RadioButton(getApplicationContext());
                button.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,RadioGroup.LayoutParams.WRAP_CONTENT));
                String t="Option"+(group.getChildCount()+1);
                //
               showDialog(Single_type_poll.this, button);

                button.setTag(t.toLowerCase());
                group.removeAllViews();
                //button.setText(name);
                group.addView(button);
                group.setVisibility(View.VISIBLE);

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
                button.setText(name);
                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });



    }
}
