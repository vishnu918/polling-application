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
    public static SmoothBottomBar bottomBar;
    FrameLayout container;
    public static Fragment active;
    Boolean flag2 = false, flag3 = false;
    public static Fragment fragment1, fragment2, fragment3;
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
        fm.beginTransaction().addToBackStack("1").add(R.id.container, fragment1, "1").commit();
        active = fragment1;
    }

    private void setBottomBar() {
        bottomBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case 0:
                    if (fm.findFragmentByTag("1") == null) {
                        createFragment(fragment1, "1");
                    } else
                        replaceFragment(fragment1);
                    active = fragment1;
                    break;
                case 1:
                    if (fm.findFragmentByTag("2") == null) {
                        createFragment(fragment2, "2");
                    } else {
                        replaceFragment(fragment2);
                    }
                    active = fragment2;
                    break;
                case 2:
                    if (fm.findFragmentByTag("3") == null) {
                        createFragment(fragment3, "3");
                    } else {
                        replaceFragment(fragment3);
                    }
                    active = fragment3;
                    break;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment.getTag().equals("3"))
            fm.beginTransaction().hide(active).show(fragment).commit();
        else
            fm.beginTransaction().addToBackStack(fragment.getTag()).hide(active).show(fragment).commit();
    }

    private void createFragment(Fragment fragment, String id) {
        if (id.equals("3"))
            fm.beginTransaction().add(R.id.container, fragment, id).hide(active).commit();
        else
            fm.beginTransaction().addToBackStack(id).add(R.id.container, fragment, id).hide(active).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}