package com.PollBuzz.pollbuzz;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.PollBuzz.pollbuzz.helper.Utils;

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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login_Fragment extends Fragment {

    private static final String ARG_PARAM1 = "Login";

    private String title;

    TextInputLayout email,password;
    Button login;
    SignInButton gsignin;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;



    public Login_Fragment() {
        // Required empty public constructor
    }


    public static Login_Fragment newInstance(String param1) {
        Login_Fragment fragment = new Login_Fragment();
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


        return inflater.inflate(R.layout.fragment_login, container, false);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        email = (TextInputLayout) view.findViewById(R.id.email);
        password = (TextInputLayout) view.findViewById(R.id.password);
        login=view.findViewById(R.id.login);
        gsignin=view.findViewById(R.id.gsignin);
        auth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("896875392739-m41n3o1qrde27chcfh883avrhp1tvd7t.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        googleSignInClient.signOut();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=email.getEditText().getText().toString();
                String pass=password.getEditText().getText().toString();
                log_in(mail,pass);
            }
        });
        gsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google_sign_in();
            }
        });


    }
    private void log_in(String emailS,String passwordS)
    {
        if(emailS.isEmpty())
        {
            Toast.makeText(getContext(), "Email can't be empty", Toast.LENGTH_SHORT).show();
            this.email.requestFocus();
        }
        else if(passwordS.isEmpty()){
            Toast.makeText(getContext(), "Password can't be empty", Toast.LENGTH_SHORT).show();
            this.password.requestFocus();
        }
        else
        {
            auth.signInWithEmailAndPassword(emailS,passwordS).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        if(!auth.getCurrentUser().isEmailVerified()){
                            Toast.makeText(getContext(), "Please verify your mail.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            auth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                @Override
                                public void onSuccess(GetTokenResult getTokenResult) {
                                    String token = getTokenResult.getToken();
                                    Utils.setLoginAuthToken(getContext(), token);
                                    Toast.makeText(getActivity(), "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }
                            });
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Log In Failed!", Toast.LENGTH_SHORT).show();
                        password.getEditText().getText().clear();
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
                if (account != null) {
                    String token = account.getIdToken();
                    Utils.setLoginAuthToken(getContext(), token);
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
        // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Google Sign In Successful!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } else {
                            Toast.makeText(getContext(), "Google Sign In Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
