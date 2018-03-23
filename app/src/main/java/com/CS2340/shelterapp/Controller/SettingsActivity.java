package com.CS2340.shelterapp.Controller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.CS2340.shelterapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button resendVerificationEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resendVerificationEmail = (Button) findViewById(R.id.resendVerificationEmailBtn);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            //Disabling resend ver-email button if user is already verified
            if (mAuth.getCurrentUser().isEmailVerified()) {
//                resendVerificationEmail.setEnabled(false);
//                resendVerificationEmail.setClickable(false);
//                resendVerificationEmail.setBackgroundColor(Color.parseColor("#808080"));
                resendVerificationEmail.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void resendVerificationEmail(View view) {
        if (mAuth.getCurrentUser() != null) {
            if (!mAuth.getCurrentUser().isEmailVerified()) {
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Verification Email:", "Email sent.");
                        Toast.makeText(SettingsActivity.this, "Verification email has been sent again. Please check your email.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Cannot send verification email. Please try again later." + task.getException(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(SettingsActivity.this, "You are already verified! :-D",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(SettingsActivity.this, "(For Dev) Null user.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
