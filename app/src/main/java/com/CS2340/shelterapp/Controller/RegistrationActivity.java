package com.CS2340.shelterapp.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        name = findViewById(R.id.name_input);
        username = findViewById(R.id.username_input);
        password = findViewById(R.id.password_input);
        registerButton = findViewById(R.id.newRegister_button);
    }
}
