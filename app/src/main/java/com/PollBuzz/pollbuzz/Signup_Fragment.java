package com.PollBuzz.pollbuzz;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Signup_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Signup_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Signup";
    //private static final String TAG = ;
    // private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String title;
    TextInputLayout emailL, passwordL, password2L;
    Button signup;
    FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;


    public Signup_Fragment() {
        // Required empty public constructor
    }


    public static Signup_Fragment newInstance(String param1) {
        Signup_Fragment fragment = new Signup_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_signup, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        emailL = (TextInputLayout) view.findViewById(R.id.email);
        passwordL = (TextInputLayout) view.findViewById(R.id.password);
        password2L = (TextInputLayout) view.findViewById(R.id.password2);
        signup=view.findViewById(R.id.signup);
        auth=FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=emailL.getEditText().getText().toString();
                String pass=passwordL.getEditText().getText().toString();
                String pass2 = password2L.getEditText().getText().toString();
                sign_up(mail, pass, pass2);
            }
        });
    }
    private void sign_up(String email, final String password, String password2)
    {
        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Email can't be empty", Toast.LENGTH_SHORT).show();
            emailL.requestFocus();
        } else if (password.isEmpty()) {
            Toast.makeText(getContext(), "Password can't be empty", Toast.LENGTH_SHORT).show();
            passwordL.requestFocus();
        }
        else if(password2.isEmpty()){
            Toast.makeText(getContext(), "Password can't be empty", Toast.LENGTH_SHORT).show();
            password2L.requestFocus();
        }
        else if(!password.equals(password2)){
            Toast.makeText(getContext(), "Passwords must be same", Toast.LENGTH_SHORT).show();
            passwordL.getEditText().getText().clear();
            password2L.getEditText().getText().clear();
            passwordL.requestFocus();
        }
        else {
              auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful())
                      {
                          auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  Toast.makeText(getContext(), "Signup successful.\nPlease verify your mail.", Toast.LENGTH_LONG).show();
                                  auth.signOut();
                              }
                          });
                      }
                      else
                      {
                          Toast.makeText(getContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                          passwordL.getEditText().getText().clear();
                          password2L.getEditText().getText().clear();
                      }
                  }
              });
          }

    }
}
