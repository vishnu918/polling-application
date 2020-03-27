package com.PollBuzz.pollbuzz.LoginSignup;

import com.google.android.material.textfield.TextInputLayout;

import com.PollBuzz.pollbuzz.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import Utils.firebase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SignupFragment extends Fragment {

    TextInputLayout emailL, passwordL, password2L;
    Button signup;
    firebase fb;

    public SignupFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setGlobals(view);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    private void signup() {
        String email = emailL.getEditText().getText().toString();
        String password = passwordL.getEditText().getText().toString();
        String password2 = password2L.getEditText().getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Email can't be empty", Toast.LENGTH_SHORT).show();
            emailL.requestFocus();
        } else if (password.isEmpty()) {
            Toast.makeText(getContext(), "Password can't be empty", Toast.LENGTH_SHORT).show();
            passwordL.requestFocus();
        } else if (password2.isEmpty()) {
            Toast.makeText(getContext(), "Password can't be empty", Toast.LENGTH_SHORT).show();
            password2L.requestFocus();
        } else if (!password.equals(password2)) {
            Toast.makeText(getContext(), "Passwords must be same", Toast.LENGTH_SHORT).show();
            passwordL.getEditText().getText().clear();
            password2L.getEditText().getText().clear();
            passwordL.requestFocus();
        } else {
            fb.getAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fb.getUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                        Toast.makeText(getContext(), "Signup successful.\nPlease verify your mail.", Toast.LENGTH_LONG).show();
                        fb.signOut();
                    });
                } else {
                    Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                    passwordL.getEditText().getText().clear();
                    password2L.getEditText().getText().clear();
                }
            });
        }
    }

    void setGlobals(View view) {
        emailL = (TextInputLayout) view.findViewById(R.id.email);
        passwordL = (TextInputLayout) view.findViewById(R.id.password);
        password2L = (TextInputLayout) view.findViewById(R.id.password2);
        signup = view.findViewById(R.id.signup);
        fb = new firebase();
    }
}