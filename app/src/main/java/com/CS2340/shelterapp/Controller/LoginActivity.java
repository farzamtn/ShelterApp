package com.CS2340.shelterapp.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.CS2340.shelterapp.Model.Login;
import com.CS2340.shelterapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A login screen that offers login via email/password.
 *
 * @author Farzam
 * @version 1.2
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mLoginDatabase;
    private DatabaseReference conditionRef;

    // UI references.
    private EditText email;
    private EditText password;
    private Button signInButton;
    private Button registerButton;
    private View progressBar;
    private View LoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //Get Firebase DB instance
        mLoginDatabase = FirebaseDatabase.getInstance().getReference();

        //Check if user is already signed on (persistence)
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getBaseContext(), MapsMasterActivity.class));
            finish();
        }

        // Set up the login form.
        email = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signInButton = (Button) findViewById(R.id.sign_in_button);
        registerButton = (Button) findViewById(R.id.register_button);
        progressBar = findViewById(R.id.login_progress);
        LoginFormView = findViewById(R.id.login_form);
    }

    /**
     * Event handler for Login Button - uses FireBase email/password authentication to validate
     * the email and password entered by user.
     *
     * @param view the current view
     */
    public void newLogin(View view) {

        // Reset errors.
        email.setError(null);
        password.setError(null);

        String user = email.getText().toString();
        final String pass = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //In Reverse order of the UI to display error for the first EditText first

        // Check for a valid password.
        if (TextUtils.isEmpty(pass)) {
            password.setError(getString(R.string.error_field_required));
            focusView = password;
            cancel = true;
        } else if (!Login.isPasswordValid(pass)) {
            password.setError(getString(R.string.error_minimum_password));
            focusView = password;
            cancel = true;
        }

        // Check for a valid email.
        if (TextUtils.isEmpty(user)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!Login.isUsernameValid(user)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt registration and ask for focus.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressBar.setVisibility(View.VISIBLE);

            //authenticate user
            mAuth.signInWithEmailAndPassword(user, pass)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            progressBar.setVisibility(View.GONE);

                            if (!task.isSuccessful()) {
                                Log.d("Firebase Auth error: ", task.getException().toString());
                                // there was an error
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            } else {
                                //Getting the correct type of user based on their login info
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                String RegisteredUserID = currentUser.getUid();
                                conditionRef = mLoginDatabase.child("Users").child(RegisteredUserID);

                                conditionRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String userType = dataSnapshot.child("User Type").getValue(String.class);
                                        if(userType.equals("Admin")){
                                            //TODO: Start Admin Activity
                                        } else if (userType.equals("Shelter Employee")) {
                                            //TODO: Start Shelter Employee Activity
                                        } else if (userType.equals("Shelter Seeker")) {
                                            Intent intentUser = new Intent(LoginActivity.this, MapsMasterActivity.class);
                                            intentUser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intentUser);
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Failed Login. Please Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    });
        }
    }

    /**
     * Event handler for new Registration Button - Navigates to RegistrationActivity.
     *
     * @param view the current view
     */
    public void newRegistrationIntent(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }
}

