package com.CS2340.shelterapp.Controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.CS2340.shelterapp.Model.Login;
import com.CS2340.shelterapp.Model.ShelterData;
import com.CS2340.shelterapp.Model.Shelters;
import com.CS2340.shelterapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A login screen that offers login via email/password + Email/Password recovery (added after M9 by
 * Farzam)
 *
 * @author Farzam
 * @version 2.0
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mLoginDatabase;
    private DatabaseReference conditionRef;
//    private DatabaseReference lockoutRef;

    private DatabaseReference shelterDB;

    private Shelters shelterModel;


    // UI references.
    private EditText email;
    private EditText password;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); //For splash screen - Farzam
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //Get Firebase DB instance
        mLoginDatabase = FirebaseDatabase.getInstance().getReference();

        //Getting the Shelters Table from DB and creating a new Model Instance for Shelters
        shelterDB = FirebaseDatabase.getInstance().getReference().child("Shelters");
        shelterModel = Shelters.INSTANCE;

        //Check if user is already signed on (persistence)
        if (mAuth.getCurrentUser() != null) {
            //Populating shelter shelterModel class after successful login attempt
            if (shelterModel.getItems().isEmpty()) {
                populateShelterInfo();
            }

            startActivity(new Intent(getBaseContext(), MapsMasterActivity.class));
            finish();
        }

        // Set up the login form.
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button signInButton = findViewById(R.id.sign_in_button);
        Button registerButton = findViewById(R.id.register_button);
        Button resetPassword_button = findViewById(R.id.resetPassword_button);
        progressBar = findViewById(R.id.login_progress);
        View loginFormView = findViewById(R.id.login_form);
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
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
//                            try {
//                                throw task.getException();
//                            } catch (FirebaseAuthInvalidCredentialsException existEmail) {
//                                lockoutRef = mLoginDatabase.child("Users");
//                                Query queryEmail = lockoutRef.orderByChild("Email").equalTo(user);
//                                queryEmail.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        Toast.makeText(LoginActivity.this, queryEmail.toString(),
//                                          Toast.LENGTH_LONG).show();
//                                        Log.d("QUERY : ", dataSnapshot.toString());
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                            Log.d("Firebase Auth error: ", task.getException().toString());
                            // there was an error
                            Toast.makeText(LoginActivity.this,
                                    getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                        } else {
                            //Populating shelter shelterModel class after successful login attempt
                            if (shelterModel.getItems().isEmpty()) {
                                populateShelterInfo();
                            }

                            //Getting the correct type of user based on their login info
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String RegisteredUserID = currentUser.getUid();
                            conditionRef = mLoginDatabase.child("Users").child(RegisteredUserID);

                            conditionRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String disabled = dataSnapshot.child("Disabled")
                                            .getValue(String.class);

                                    if ("true".equals(disabled)) {
                                        View focusView1 = email;
                                        email.setError("User is Banned");
                                        Toast.makeText(LoginActivity.this,
                                                "This account has been banned. " +
                                                        "Please contact the administrator.",
                                                Toast.LENGTH_LONG).show();
                                        FirebaseAuth.getInstance().signOut();
                                        return;
                                    }

                                    String userType = dataSnapshot.child("User Type")
                                            .getValue(String.class);
                                    switch (userType) {
                                        case "Admin": {
                                            Intent intentUser = new Intent(
                                                    LoginActivity.this,
                                                    AdminActivity.class);
                                            intentUser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intentUser);
                                            finish();
                                            break;
                                        }
                                        case "Shelter Employee": {
                                            Intent intentUser = new Intent(
                                                    LoginActivity.this,
                                                    MapsMasterActivity.class);
                                            intentUser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intentUser);
                                            finish();
                                            break;
                                        }
                                        case "Shelter Seeker": {
                                            Intent intentUser = new Intent(
                                                    LoginActivity.this,
                                                    MapsMasterActivity.class);
                                            intentUser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intentUser);
                                            finish();
                                            break;
                                        }
                                        default:
                                            Toast.makeText(LoginActivity.this,
                                                    "Failed Login. Please Try Again",
                                                    Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d("User DB Error (login)", databaseError.getMessage());
                                }
                            });
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

    /**
     * Method that gets fired when Forgot Password button is pressed.
     * @param view the current view
     */
    public void passwordReset(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Enter Email");
        builder.setMessage("Please enter the email you used to register for an account in order" +
                " to receive a password reset email.");

        // Set up the input
        final EditText input = new EditText(LoginActivity.this);
        // Specify the type of input expected; this, for example, sets the input as a password,
        // and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Reset Password", (dialogInterface, i) -> {
            String emailAddress = input.getText().toString();
            if (TextUtils.isEmpty(emailAddress)) {
                Toast.makeText(LoginActivity.this, "No email was entered. Try again.",
                        Toast.LENGTH_LONG).show();
            } else {
                if (!Login.isValidEmail(emailAddress)) {
                    Toast.makeText(LoginActivity.this, "Invalid email entered. " +
                                    "Please enter a valid email address.",
                            Toast.LENGTH_LONG).show();
                } else {
                    mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Password reset email " +
                                            "sent. Please follow the instructions in the email" +
                                            " to reset your password.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("Pass Reset", task.getException().toString());
                            Toast.makeText(LoginActivity.this, "There is no account " +
                                            "associated with this email. Try a different email.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            //Do nothing
        });

        builder.show();
    }

    /**
     * Method for getting all the shelter info from the FireBase DB and adding it to the local
     * shelterModel.
     * Remember both the .csv file (for local bufferReading) and .json file (For FireBase DB)
     * has been
     * added to the res/raw just in case. - Farzam
     */
    private void populateShelterInfo() {
        shelterDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shelters : dataSnapshot.getChildren()) {
                    shelterModel.addItem(new ShelterData(shelters.child("Unique Key")
                            .getValue(Integer.class),
                            shelters.child("Shelter Name").getValue(String.class),
                            shelters.child("Capacity").getValue(String.class),
                            shelters.child("Restrictions").getValue(String.class),
                            shelters.child("Longitude").getValue(Double.class),
                            shelters.child("Latitude").getValue(Double.class),
                            shelters.child("Address").getValue(String.class),
                            shelters.child("Special Notes").getValue(String.class),
                            shelters.child("Phone Number").getValue(String.class)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Shelter DB Error", databaseError.getMessage());
            }
        });
    }
}

