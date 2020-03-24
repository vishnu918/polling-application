package Utils;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.PollBuzz.pollbuzz.LogIn_SignUp.Login_Signup_Activity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.LogIn_SignUp.ProfileSetUp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AuthCheck extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_auth_check);
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseAuth auth= FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null)
        {
            Utils.helper.removeProfileSetUpPref(getApplicationContext());
            Intent i=new Intent(AuthCheck.this, Login_Signup_Activity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        else{
            if (Utils.helper.getProfileSetUpPref(getApplicationContext())) {
                Intent i = new Intent(AuthCheck.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } else {
                Intent i = new Intent(AuthCheck.this, ProfileSetUp.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }
    }
}
