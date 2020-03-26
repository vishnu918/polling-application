package com.PollBuzz.pollbuzz;

import com.PollBuzz.pollbuzz.navFragments.HomeFeed;
import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.PollBuzz.pollbuzz.navFragments.VotedFeed;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    SmoothBottomBar bottomBar;
    FrameLayout container;
    Fragment active;
    Boolean flag2 = false, flag3 = false;
    Fragment fragment1, fragment2, fragment3;
    FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setGlobals();
        setBottomBar();
    }

    private void setGlobals() {
        bottomBar = findViewById(R.id.bottom);
        container = findViewById(R.id.container);
        fragment1 = new HomeFeed();
        fragment2 = new VotedFeed();
        fragment3 = new ProfileFeed();
        fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.container, fragment1, "1").commit();
        active = fragment1;
    }

    private void setBottomBar() {
        bottomBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case 0:
                    replaceFragment(fragment1);
                    active = fragment1;
                    break;
                case 1:
                    if (!flag2) {
                        createFragment(fragment2, "2");
                        flag2 = true;
                    } else {
                        replaceFragment(fragment2);
                    }
                    active = fragment2;
                    break;
                case 2:
                    if (!flag3) {
                        createFragment(fragment3, "3");
                        flag3 = true;
                    } else {
                        replaceFragment(fragment3);
                    }
                    active = fragment3;
                    break;
            }
        });
    }

    private void replaceFragment(Fragment fragment1) {
        fm.beginTransaction().hide(active).show(fragment1).commit();
    }

    private void createFragment(Fragment fragment, String id) {
        fm.beginTransaction().add(R.id.container, fragment, id).hide(active).commit();
    }

}