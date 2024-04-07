package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Domain.User;
import com.example.finalproject.encryption.Decrypt;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    com.example.finalproject.UserDAO userDAO;
    private EditText userNameInput;
    private EditText userPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        setupListeners();
    }

    private void initializeUI() {
        userNameInput = findViewById(R.id.userNameInput);
        userPasswordInput = findViewById(R.id.userPasswordInput);
    }

    private void setupListeners() {
        Button submit = findViewById(R.id.submitButton);
        Button register = findViewById(R.id.register);

        submit.setOnClickListener(v -> loginUser());
        register.setOnClickListener(v -> navigateToRegisterPage());
    }

    private void loginUser() {
        String name = userNameInput.getText().toString().trim();
        String password = userPasswordInput.getText().toString().trim();

        if (name.isEmpty()) {
            userNameInput.setError("Name cannot be empty");
            return;
        }

        new Thread(() -> {
            try {
                User user = userDAO.getUser(name);
                if (user != null) {
                    Decrypt decrypt = new Decrypt();
                    String decryptedPassword = decrypt.decrypt(this, user.Password);
                    if (password.equals(decryptedPassword)) {
                        navigateToHomePage(user.id,user.name);
                    } else {
                        showLoginError("Invalid credentials.");
                    }
                } else {
                    showLoginError("Invalid credentials.");
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Error during decryption", e);
                showLoginError("Login failed.");
            }
        }).start();
    }

    private void navigateToRegisterPage() {
        Intent intent = new Intent(MainActivity.this, RegisterPage.class);
        startActivity(intent);
    }

    private void navigateToHomePage(int Id, String Name) {
        Log.d("TimeTest", "Homepage: ");
        runOnUiThread(() -> {
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            intent.putExtra( "userId", Id);
            intent.putExtra("userName", Name);
            startActivity(intent);
        });
    }

    private void showLoginError(String errorMessage) {
        runOnUiThread(() -> userPasswordInput.setError(errorMessage));
    }
}
