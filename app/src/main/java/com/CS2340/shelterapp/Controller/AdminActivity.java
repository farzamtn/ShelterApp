package com.CS2340.shelterapp.Controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.CS2340.shelterapp.Model.Login;
import com.CS2340.shelterapp.Model.User;
import com.CS2340.shelterapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Admin Activity page that will contain the tasks an admin can do
 *
 * @author chandler
 * @version 1.2
 */
public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference usersDatabase;

    private EditText emailBox;
    private String email;
    private String disabled;
    private DatabaseReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        emailBox = findViewById(R.id.emailInput);
        Button ban = findViewById(R.id.ban);
        email = emailBox.getText().toString();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_shelters: {
                Intent intent = new Intent(this, ShelterItemListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_map: {
                Intent intent = new Intent(this, MapsMasterActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_share:

                break;
            case R.id.nav_signout:
            /*  Prompt the user to sign out
                If yes, clear cache and go back to the login screen. - Farzam
             */
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setMessage("Are you sure you want to sign out?");

                builder.setPositiveButton("YES", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut(); //Ending FireBase session for this user
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                });

                builder.setNegativeButton("NO", (dialog, which) -> {
                    //Do nothing
                });
                builder.show();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * banUser method that will get fired when admin clicks on the Ban/Un-ban button.
     *
     * @param view the current view
     */
    public void banUser(View view) {
        View focusView = null;

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        emailBox = findViewById(R.id.emailInput);
        email = emailBox.getText().toString();
        boolean cancel = false;

        if (TextUtils.isEmpty(email)) {
            this.emailBox.setError(getString(R.string.error_field_required));
            focusView = emailBox;
            cancel = true;
        } else if (Login.isUsernameValid(email)) {
            emailBox.setError(getString(R.string.error_invalid_email));
            focusView = emailBox;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt registration and ask for focus.
            focusView.requestFocus();
        } else {
            getUser();
        }
    }

    private void banUserAttempt() {
        if (user == null) {
            emailBox.setError("Email not in database");
            View focusView = emailBox;
            focusView.requestFocus();
            return;
        }
        if ("false".equals(disabled)) {
            disabled = "true";
            User.updateDBUserDisabled(disabled, user);
            emailBox.setError("User Banned");
        } else {
            disabled = "false";
            User.updateDBUserDisabled(disabled, user);
            emailBox.setError("User Unbanned");
        }
    }

    private void getUser() {
        usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String userEmail = ds.child("Email").getValue(String.class);

                    assert userEmail != null;
                    if (userEmail.equals(email)) {
                        user = ds.getRef();
                        disabled = ds.child("Disabled").getValue(String.class);
                    }
                }
                banUserAttempt();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //do nothing
            }
        });

    }
}
