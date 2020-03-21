package com.example.pollbuzz;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


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
    TextInputLayout email,password;
    Button signup;
    SignInButton gsignin;
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

        email = (TextInputLayout) view.findViewById(R.id.email);
        password = (TextInputLayout) view.findViewById(R.id.password);
        signup=view.findViewById(R.id.signup);
        gsignin=view.findViewById(R.id.gsignin);
        auth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient=GoogleSignIn.getClient(getActivity(),gso);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=email.getEditText().getText().toString();
                String pass=password.getEditText().getText().toString();
                Toast.makeText(getContext(),mail,Toast.LENGTH_LONG).show();

                sign_up(mail,pass);
            }
        });
        gsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            google_sign_in();
            }
        });


    }
    private void sign_up(String email,String password)
    {
          if(email.isEmpty() || password.isEmpty())
          {
              Toast.makeText(getContext(), "Email and Password can't be empty", Toast.LENGTH_LONG).show();
          }
          else
          {

              auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful())
                      {
                          auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  Toast.makeText(getContext(), "Signup successful.\nPlease verify your mail.", Toast.LENGTH_LONG).show();
                              }
                          });
                      }
                      else
                      {
                          Toast.makeText(getContext(),"Signup failed",Toast.LENGTH_LONG).show();
                      }
                  }
              });
          }

    }
    private void google_sign_in()
    {

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Toast.makeText(getContext(),"Registered Successfully",Toast.LENGTH_LONG).show();
            } catch (ApiException e) {
                      Log.d("error",e.getStackTrace().toString());
                Toast.makeText(getContext(),"GSignup failed",Toast.LENGTH_LONG).show();

            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
       // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           // Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getContext(),"GSignup successful",Toast.LENGTH_LONG).show();
                            FirebaseUser user = auth.getCurrentUser();

                        } else {


                        }


                    }
                });
    }
}
