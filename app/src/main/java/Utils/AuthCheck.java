package Utils;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.LoginSignup.ProfileSetUp;
import com.PollBuzz.pollbuzz.MainActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AuthCheck extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebase fb = new firebase();
        Intent i = getIntent(fb);
        startActivity(i);
    }

    private Intent getIntent(firebase fb) {
        Intent i = new Intent(AuthCheck.this, LoginSignupActivity.class);
        if (!isUserLoggedIn(fb)) {
            helper.removeProfileSetUpPref(getApplicationContext());
        }
        else {
            i = isProfileSetUp() ? new Intent(AuthCheck.this, MainActivity.class) :
                    new Intent(AuthCheck.this, ProfileSetUp.class);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return i;
    }

    Boolean isUserLoggedIn(firebase fb) {
        return fb.getUser() != null;
    }

    Boolean isProfileSetUp() {
        return helper.getProfileSetUpPref(getApplicationContext());
    }
}
