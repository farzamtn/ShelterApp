package com.CS2340.shelterapp.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.CS2340.shelterapp.Model.Login;
import com.CS2340.shelterapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A registration screen that offers Shelter App registration.
 *
 * @author Farzam
 * @version 1.1
 */
public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // UI references.
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText phoneNumber;
    private Button registerButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        name = (EditText) findViewById(R.id.new_register_name);
        email = (EditText) findViewById(R.id.new_register_email);
        password = (EditText) findViewById(R.id.new_register_password);
        phoneNumber = (EditText) findViewById(R.id.new_register_phone);
        registerButton = (Button) findViewById(R.id.new_register_button);
        progressBar = (ProgressBar) findViewById(R.id.registration_progress);
    }

    /**
     * Button event handler for the register button - uses FireBase email/password authentication
     * to validate the email and password entered by user.
     *
     * @param view the current view
     */
    public void newRegistration(View view) {
        // Reset errors.
        email.setError(null);
        password.setError(null);

        String user = this.email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        //If both inputs are empty, display message and put focus on email
        if (TextUtils.isEmpty(user) && TextUtils.isEmpty(pass)) {
            email.setError(getString(R.string.error_field_required));
            password.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else {
            if (TextUtils.isEmpty(user)) {
                this.email.setError(getString(R.string.error_field_required));
                focusView = email;
                cancel = true;
            } else if (!Login.isUsernameValid(user)) {
                email.setError(getString(R.string.error_invalid_email));
                focusView = email;
                cancel = true;
            }

            if (TextUtils.isEmpty(pass)) {
                password.setError(getString(R.string.error_field_required));
                focusView = password;
                cancel = true;
            } else if (!Login.isPasswordValid(pass)) {
                password.setError(getString(R.string.error_minimum_password));
                focusView = password;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt registration and ask for focus.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the new user registration attempt.
            progressBar.setVisibility(View.VISIBLE);

            //create user using FireBase email/password authentication server
            mAuth.createUserWithEmailAndPassword(user, pass)
                    .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Your new account has been created. Welcome!",
                                        Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
