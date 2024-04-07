package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Domain.User;
import com.example.finalproject.encryption.Encrypt;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterPage extends AppCompatActivity {

    @Inject
    com.example.finalproject.UserDAO userDAO;

    private EditText userNameInput;
    private EditText userPasswordInput1;
    private EditText userPasswordInput2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        initializeViews();
        setupSubmitButton();
        setupGoBackButton();
    }

    private void initializeViews() {
        userNameInput = findViewById(R.id.RegisterUserNameInput);
        userPasswordInput1 = findViewById(R.id.RegisterUserPasswordInput1);
        userPasswordInput2 = findViewById(R.id.RegisterUserPasswordInput2);
    }

    private void setupSubmitButton() {
        Button submit = findViewById(R.id.RegisterUser);
        submit.setOnClickListener(v -> registerUser());
    }
    private void setupGoBackButton() {
        Button submit = findViewById(R.id.GoBack);
        submit.setOnClickListener(v -> navigateToMainActivity());
    }

    private void registerUser() {
        String name = userNameInput.getText().toString().trim();
        String password1 = userPasswordInput1.getText().toString();
        String password2 = userPasswordInput2.getText().toString();

        if (name.isEmpty()) {
            userNameInput.setError("Name cannot be empty");
            return;
        }

        new Thread(() -> {
            // Check if user already exists
            User existingUser = userDAO.getUser(name);
            if (existingUser != null) {
                runOnUiThread(() -> userNameInput.setError("Already in use"));
                return;
            }

            if (!password1.equals(password2)) {
                runOnUiThread(() -> userPasswordInput2.setError("Passwords do not match"));
                return;
            }

            createUser(name, password2);
        }).start();
    }

    private void createUser(String name, String password) {
        try {
            Encrypt encrypt = new Encrypt();
            String encryptedPassword = encrypt.encrypt(this, password);

            User newUser = new User();
            newUser.setName(name);
            newUser.setPassword(encryptedPassword);
            userDAO.insertAll(newUser);

            Log.d("RegisterPage", "User registered: " + name);
            navigateToMainActivity();
        } catch (Exception e) {
            Log.e("RegisterPage", "Encryption error", e);
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterPage.this, MainActivity.class);
        startActivity(intent);
    }
}
