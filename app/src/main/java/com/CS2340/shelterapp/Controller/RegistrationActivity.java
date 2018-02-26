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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.CS2340.shelterapp.Model.Login;
import com.CS2340.shelterapp.Model.Registration;
import com.CS2340.shelterapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A registration screen that offers Shelter App registration.
 *
 * @author Farzam
 * @version 1.2
 */
public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRegisterDatabase;
    private DatabaseReference conditionRef;

    // UI references.
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText phoneNumber;
    private Button registerButton;
    private ProgressBar progressBar;
    private RadioGroup userTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //Get Firebase DB instance - The Users Table
        mRegisterDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        //Set up the registration form
        name = (EditText) findViewById(R.id.new_register_name);
        email = (EditText) findViewById(R.id.new_register_email);
        password = (EditText) findViewById(R.id.new_register_password);
        phoneNumber = (EditText) findViewById(R.id.new_register_phone);
        registerButton = (Button) findViewById(R.id.new_register_button);
        progressBar = (ProgressBar) findViewById(R.id.registration_progress);
        userTypes = (RadioGroup) findViewById(R.id.userTypesRbutton);
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

        final String fullName = name.getText().toString();
        final String user = email.getText().toString().trim();
        final String pass = password.getText().toString().trim();
        final String phoneNum = phoneNumber.getText().toString().trim();

        //Retrieving the selected user type
        int selectedButtonId = userTypes.getCheckedRadioButtonId();
        RadioButton selectedUser = (RadioButton) findViewById(selectedButtonId);
        final String userType = selectedUser.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //In Reverse order of the UI to display error for the first EditText first

        if (TextUtils.isEmpty(phoneNum)) {
            phoneNumber.setError(getString(R.string.error_field_required));
            focusView = phoneNumber;
            cancel = true;
        } else if (!Registration.isPhoneNumberValid(phoneNum)) {
            phoneNumber.setError(getString(R.string.error_invalid_phoneNumber));
            focusView = phoneNumber;
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

        if (TextUtils.isEmpty(user)) {
            this.email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!Login.isUsernameValid(user)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        if (TextUtils.isEmpty(fullName)) {
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        } else if (!Registration.isNameValid(fullName)) {
            name.setError(getString(R.string.error_invalid_name));
            focusView = password;
            cancel = true;
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

                                //Adding new user information to DB with Uid being their Unique Key
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = mRegisterDatabase.child(user_id);
                                current_user_db.child("Name").setValue(fullName);
                                current_user_db.child("Email").setValue(user);
                                current_user_db.child("Phone Number").setValue(phoneNum);
                                current_user_db.child("User Type").setValue(userType);

                                Toast.makeText(RegistrationActivity.this, "Your new account has been created. Welcome!",
                                        Toast.LENGTH_LONG).show();

                                //Start the correct activity based on usertype
                                if (userType.equals("Admin")){
                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                } else if (userType.equals("Shelter Employee")) {
                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                    finish();
                                } else if (userType.equals("Shelter Seeker")) {
                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                    finish();
                                }
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
