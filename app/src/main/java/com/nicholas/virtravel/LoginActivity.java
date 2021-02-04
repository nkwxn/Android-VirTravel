package com.nicholas.virtravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputLayout tilEmail, tilPwd;
    Button btnLogin, btnSignUp;
    private FirebaseAuth mAuth;

    LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilEmail = findViewById(R.id.tilEmail);
        tilPwd = findViewById(R.id.tilPwd);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();

        loading = new LoadingDialog(LoginActivity.this);

        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private boolean fieldIsFilled(TextInputLayout til, String text) {
        if (text.equals("")) {
            til.setError("must be filled");
            return false;
        } else {
            til.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                // Verification for a login screen
                String email, password;
                email = tilEmail.getEditText().getText().toString();
                password = tilPwd.getEditText().getText().toString();
                fieldIsFilled(tilEmail, email);
                fieldIsFilled(tilPwd, password);
                if (fieldIsFilled(tilEmail, email) && fieldIsFilled(tilPwd, password)) {
                    loading.startLoadingDialog("Logging you in\nPlease wait");
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    loading.dismissDialog();
                                    finish();
                                } else {
                                    if (task.getException().toString().contains("The email address is badly formatted")) {
                                        tilEmail.setError("is badly formatted");
                                        tilPwd.setError("is invalid");
                                    } else if (task.getException().toString().contains("There is no user record")) {
                                        tilEmail.setError("is not recorded in our database");
                                        tilPwd.setError("is invalid");
                                    } else if (task.getException().toString().contains("The password is invalid")) {
                                        tilPwd.setError("is invalid");
                                    }
                                    loading.dismissDialog();
                                }
                            });
                } else {
                    Toast.makeText(this, "Please check all the required fields", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnSignUp:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }
}