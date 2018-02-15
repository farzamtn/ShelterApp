package com.CS2340.shelterapp.Controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.CS2340.shelterapp.Model.Login;
import com.CS2340.shelterapp.R;

import java.util.HashMap;

/**
 * A login screen that offers login via email/password.
 *
 * @author Farzam
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     */
    private static final HashMap<String, String> DUMMY_CREDENTIALS = Login.DUMMY_CREDENTIALS;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText username;
    private EditText password;
    private Button signInButton;
    private Button registerButton;
    private View ProgressView;
    private View LoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in_button);
        registerButton = findViewById(R.id.register_button);
        ProgressView = findViewById(R.id.login_progress);
        LoginFormView = findViewById(R.id.login_form);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        username.setError(null);
        password.setError(null);

        // Store values at the time of the login attempt.
        String user = username.getText().toString();
        String pass = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //If both inputs are empty, display message and put focus on username
        if (TextUtils.isEmpty(user) && TextUtils.isEmpty(pass)) {
            username.setError(getString(R.string.error_field_required));
            password.setError(getString(R.string.error_field_required));
            focusView = username;
            cancel = true;
        } else {
            // Check for a valid username.
            if (TextUtils.isEmpty(user)) {
                username.setError(getString(R.string.error_field_required));
                focusView = username;
                cancel = true;
            } else if (!Login.isUsernameValid(user)) {
                username.setError(getString(R.string.error_invalid_username));
                focusView = username;
                cancel = true;
            }

            // Check for a valid password.
            if (TextUtils.isEmpty(pass)) {
                password.setError(getString(R.string.error_field_required));
                focusView = password;
                cancel = true;
            } else if (!Login.isPasswordValid(pass)) {
                password.setError(getString(R.string.error_invalid_password));
                focusView = password;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(user, pass);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String user;
        private final String pass;

        UserLoginTask(String username, String password) {
            user = username;
            pass = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service - SQLite in future (Farzam)

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credentialUser : DUMMY_CREDENTIALS.keySet()) {
                String credentialPassword = DUMMY_CREDENTIALS.get(credentialUser);
                if (credentialUser.equals(user)) {
                    // Account exists, return true if the password matches.
                    return credentialPassword.equals(pass);
                }
            }

            // TODO: register the new account here.
            return false; //If no valid combination of username/password is found (hard-coded for now)
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //If username and password was valid, create the new MapIntent and transition over
                Intent myIntent = new Intent(getBaseContext(), MapsMasterActivity.class);
                startActivity(myIntent);
                finish();
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(R.string.invalid_login_alert_message)
                        .setTitle(R.string.invalid_login_alert_title);
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                });
                builder.show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     * (Our app has been set at API 19 for now so there's really no need to check for device SDK,
     * the conditionals have been added in case we target a higher API) - Farzam
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            LoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            LoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            ProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            LoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /*Register button caller method - new registration activity starts*/
    public void newRegistrationIntent(View view) {
        Intent myIntent = new Intent(getBaseContext(), RegistrationActivity.class);
        startActivity(myIntent);
    }

    /*Login button caller method - new MapsActivity is initialized if user/pass is valid*/
    public void newLogin(View view) {
        attemptLogin();
    }
}

