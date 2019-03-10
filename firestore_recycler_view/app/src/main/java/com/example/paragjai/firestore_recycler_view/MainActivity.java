package com.example.paragjai.firestore_recycler_view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity  extends AppCompatActivity {

    private final int REQUEST_SIGNIN = 1000;
    Button loginButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        auth = FirebaseAuth.getInstance();
        if(isUserLogin())//if the user is logged in already
        {
            loginUser(); // then take the user to next activity
        }
        loginButton = (Button) findViewById(R.id.login_button); //otherwise show the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Configure which kind of login page you want to show [ we dont create the layout
                //of this page, it is created by Firebase itself ]
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.PhoneBuilder().build()
                );

                //Create the signIn activity provided by Firebase UI depending
                //on the provider list that we have created.
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        REQUEST_SIGNIN
                );
            }
        });
    }


    private boolean isUserLogin() {

        if(auth.getCurrentUser() != null)
        {
            return  true;
        }
        return false; //user is not logged in already
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SIGNIN)
        {

            if(resultCode == RESULT_OK)
            {
                loginUser();
            }
            else {
                Toast.makeText(this, "Problem in authentication occurred. Try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginUser() {
        Intent loginIntent = new Intent(MainActivity.this, SignIn.class);
        startActivity(loginIntent);
        finish();
    }


    public void signOut()
    {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }
}


