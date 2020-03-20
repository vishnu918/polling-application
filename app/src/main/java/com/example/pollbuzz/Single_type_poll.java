package com.example.pollbuzz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Single_type_poll extends AppCompatActivity {
    Button add;
    RadioGroup group;

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
                String t="option"+(group.getChildCount()+1);
                button.setText(t);
                button.setClickable(false);
                group.addView(button);
                group.setVisibility(View.VISIBLE);

            }
        });

    }
}
