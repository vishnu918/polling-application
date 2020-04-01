package com.PollBuzz.pollbuzz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.PollBuzz.pollbuzz.navFragments.HomeFeed;
import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.PollBuzz.pollbuzz.navFragments.VotedFeed;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.List;

import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    public static SmoothBottomBar bottomBar;
    public static Fragment active;
    public static Fragment fragment1, fragment2, fragment3;
    private FragmentManager fm;
    private FloatingActionButton fab;
    private List<Fragment> fList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setGlobals();
        setBottomBar();
        setListeners();
    }

    private void setGlobals() {
        bottomBar = findViewById(R.id.bottom);
        fab = findViewById(R.id.fab);
        try {
            YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(fab);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
        fragment1 = new HomeFeed();
        fragment2 = new VotedFeed();
        fragment3 = new ProfileFeed();
        fList = new ArrayList<>();
        fm = getSupportFragmentManager();
        newFragment(fragment1, "0");
        fList.add(fragment1);
    }

    private void setListeners() {
        fab.setOnClickListener(view1 -> {
            Intent i = new Intent(MainActivity.this, PollList.class);
            startActivity(i);
        });
    }

    private void setBottomBar() {
        bottomBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case 0:
                    newFragment(fragment1, "0");
                    fList.remove(fragment1);
                    fList.add(fragment1);
                    break;
                case 1:
                    newFragment(fragment2, "1");
                    fList.remove(fragment2);
                    fList.add(fragment2);
                    break;
                case 2:
                    newFragment(fragment3, "2");
                    fList.remove(fragment3);
                    fList.add(fragment3);
                    break;
            }
        });
    }

    private void newFragment(Fragment fragment, String id) {
        try {
            if (fList.contains(fragment)) {
                Log.d("fragment", fragment.getTag() + " " + active.getTag());
                fm.beginTransaction().show(fragment).hide(active).commit();
            } else {
                if (fm.getFragments().contains(fragment))
                    fm.beginTransaction().show(fragment).hide(active).commit();
                else
                    fm.beginTransaction().add(R.id.container, fragment, id).commit();
            }
            active = fragment;
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (fList.size() == 1) {
            if (fList.get(0) == fragment1)
                super.onBackPressed();
            else {
                fList.clear();
                newFragment(fragment1, "0");
                fList.add(fragment1);
            }
        } else {
            fList.remove(active);
            fm.beginTransaction().remove(active).show(fList.get(fList.size() - 1)).commit();
            active = fList.get(fList.size() - 1);
        }
        if (active.getTag() != null) {
            switch (active.getTag()) {
                case "0":
                    bottomBar.setActiveItem(0);
                    break;
                case "1":
                    bottomBar.setActiveItem(1);
                    break;
                case "2":
                    bottomBar.setActiveItem(2);
                    break;
            }
        }
    }
}