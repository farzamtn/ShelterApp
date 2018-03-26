package com.CS2340.shelterapp.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.CS2340.shelterapp.Model.Login;
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
 * @version 1.1
 */
public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference usersDatabase;

    private EditText emailBox;
    private Button ban;
    private String email;
    private String disabled;
    private DatabaseReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        emailBox = (EditText) findViewById(R.id.emailInput);
        ban = (Button) findViewById(R.id.ban);
        email = emailBox.getText().toString();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shelters) {
            Intent intent = new Intent(this, ShelterItemListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapsMasterActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id ==  R.id.nav_signout) {
            /*  Prompt the user to sign out
                If yes, clear cache and go back to the login screen. - Farzam
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
            builder.setMessage("Are you sure you want to sign out?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut(); //Ending FireBase session for this user
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Do nothing
                }
            });
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void banUser(View view) {
        View focusView = null;

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        emailBox = (EditText) findViewById(R.id.emailInput);
        email = emailBox.getText().toString();
        boolean cancel = false;

        if (email.equals("")) {
            this.emailBox.setError(getString(R.string.error_field_required));
            focusView = emailBox;
            cancel = true;
        } else if (!Login.isUsernameValid(email)) {
            emailBox.setError(getString(R.string.error_invalid_email));
            focusView = emailBox;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt registration and ask for focus.
            focusView.requestFocus();
        } else {
            getUser(focusView);
        }
    }

    private void banUserAttempt(View focusView) {
        if (user == null) {
            emailBox.setError("Email not in database");
            focusView = emailBox;
            focusView.requestFocus();
            return;
        }
        if (disabled.equals("false")) {
            disabled = "true";
            updateDBUserBeds(disabled);
            focusView = null;
            emailBox.setError("User Banned");
        } else {
            disabled = "false";
            updateDBUserBeds(disabled);
            focusView = null;
            emailBox.setError("User Unbanned");
        }
    }

    private void getUser(View focusView) {
        usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String userEmail = ds.child("Email").getValue(String.class);

                    if (userEmail.equals(email)) {
                        user = ds.getRef();
                        disabled = ds.child("Disabled").getValue(String.class);
                    }
                }
                banUserAttempt(focusView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //do nothing
            }
        });

    }

    private void updateDBUserBeds(String newDisabled) {

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.child("Disabled").setValue(newDisabled);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.print(databaseError.getMessage());
            }
        });
    }
}
