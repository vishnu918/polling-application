package com.PollBuzz.pollbuzz;

import com.google.firebase.auth.FirebaseAuth;

import com.PollBuzz.pollbuzz.navFragments.HomeFeed;
import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.PollBuzz.pollbuzz.navFragments.VotedFeed;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    SmoothBottomBar bottomBar;
    FrameLayout container;
    Fragment active;
    Boolean flag2=false, flag3=false;
    Fragment fragment1, fragment2, fragment3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        bottomBar = findViewById(R.id.bottom);
        container=findViewById(R.id.container);
        fragment1 = new HomeFeed();
        final FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.container, fragment1, "1").commit();
        active = fragment1;
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelect(int i) {
                switch (i) {
                    case 0:
                        fm.beginTransaction().hide(active).show(fragment1).commit();
                        active = fragment1;
                        break;
                    case 1:
                        if (!flag2) {
                            fragment2 = new VotedFeed();
                            fm.beginTransaction().add(R.id.container, fragment2, "2").hide(active).commit();
                            flag2 = true;
                        }
                        else {
                            fm.beginTransaction().hide(active).show(fragment2).commit();
                        }
                        active = fragment2;
                        break;
                    case 2:
                        if (!flag3) {
                            fragment3 = new ProfileFeed();
                            fm.beginTransaction().add(R.id.container, fragment3, "3").hide(active).commit();
                            flag3 = true;
                        }
                        else{
                            fm.beginTransaction().hide(active).show(fragment3).commit();
                        }
                        active = fragment3;
                        break;
                }
            }
        });
    }
}