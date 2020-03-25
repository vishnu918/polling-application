package com.PollBuzz.pollbuzz.LoginSignup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import Utils.firebase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    TextInputLayout email,password;
    Button login;
    SignInButton gsignin;
    GoogleSignInClient googleSignInClient;
    firebase fb;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setGlobals(view);
        login.setOnClickListener(v -> {
            login();
        });
        gsignin.setOnClickListener(v -> googleSignin());
    }

    void setGlobals(View view) {
        email = (TextInputLayout) view.findViewById(R.id.email);
        password = (TextInputLayout) view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);
        gsignin = view.findViewById(R.id.gsignin);
        fb = new firebase();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("896875392739-m41n3o1qrde27chcfh883avrhp1tvd7t.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        googleSignInClient.signOut();
    }

    private void login() {
        String emailS = email.getEditText().getText().toString();
        String passwordS = password.getEditText().getText().toString();
        if (emailS.isEmpty()) {
            Toast.makeText(getContext(), "Email can't be empty", Toast.LENGTH_SHORT).show();
            this.email.requestFocus();
        } else if (passwordS.isEmpty()) {
            Toast.makeText(getContext(), "Password can't be empty", Toast.LENGTH_SHORT).show();
            this.password.requestFocus();
        } else {
            fb.getAuth().signInWithEmailAndPassword(emailS, passwordS).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!fb.getUser().isEmailVerified()) {
                        Toast.makeText(getContext(), "Please verify your mail.", Toast.LENGTH_SHORT).show();
                        fb.signOut();
                    } else {
                        fb.getUserDocument().get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getActivity(), "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                DocumentSnapshot dS = task1.getResult();
                                isProfileSet(dS);
                            } else {
                                Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                password.getEditText().getText().clear();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    password.getEditText().getText().clear();
                }
            });
        }
    }

    private void isProfileSet(DocumentSnapshot dS) {
        Intent i = new Intent(getActivity(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (dS != null && dS.exists()) {
            setSharedPreference(dS);
        } else {
            removeSharedPreference();
            i = new Intent(getActivity(), ProfileSetUp.class);
        }
        startActivity(i);
    }

    private void removeSharedPreference() {
        Utils.helper.setProfileSetUpPref(getContext(), false);
    }

    private void setSharedPreference(DocumentSnapshot dS) {
        Utils.helper.setProfileSetUpPref(getContext(), true);
        Utils.helper.setpPicPref(getContext(), String.valueOf(dS.get("pic")));
        Utils.helper.setusernamePref(getContext(), String.valueOf(dS.get("username")));
    }

    private void googleSignin() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                if (e.getMessage() != null) {
                    Log.d("error", e.getMessage());
                    FirebaseCrashlytics.getInstance().log(e.getMessage());
                }
                Toast.makeText(getContext(), "Google Sign In failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fb.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fb.getUserDocument().get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getActivity(), "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                DocumentSnapshot dS = task1.getResult();
                                isProfileSet(dS);
                            } else {
                                Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("UID", task1.getException().toString());
                                password.getEditText().getText().clear();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Google Sign In Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
