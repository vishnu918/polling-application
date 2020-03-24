package com.PollBuzz.pollbuzz;

import com.google.firebase.auth.FirebaseAuth;

import com.PollBuzz.pollbuzz.navFragments.HomeFeed;
import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.PollBuzz.pollbuzz.navFragments.VotedFeed;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    SmoothBottomBar bottomBar;
    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        bottomBar = findViewById(R.id.bottom);
        container=findViewById(R.id.container);
        openFragment(new HomeFeed());
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelect(int i) {
                switch (i) {
                    case 0:
                        openFragment(new HomeFeed());
                        break;
                    case 1:
                        openFragment(new VotedFeed());
                        break;
                    case 2:
                        openFragment(new ProfileFeed());
                        break;
                }
            }
        });

    }
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}