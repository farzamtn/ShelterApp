package com.CS2340.shelterapp.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.CS2340.shelterapp.R;

/**
 * A registration screen that offers Shelter App registration.
 *
 * @author Farzam
 * @version 1.0
 */
public class RegistrationActivity extends AppCompatActivity {

    // UI references.
    private EditText name;
    private EditText username;
    private EditText password;
    private EditText phoneNumber;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        name = findViewById(R.id.new_register_name);
        username = findViewById(R.id.new_register_email);
        password = findViewById(R.id.new_register_password);
        phoneNumber = findViewById(R.id.new_register_phone);
        registerButton = findViewById(R.id.new_register_button);
    }

    public void newRegistration(View view) {

    }
}
