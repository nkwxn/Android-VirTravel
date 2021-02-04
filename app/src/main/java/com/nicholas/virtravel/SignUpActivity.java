package com.nicholas.virtravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    ActionBar actionBar;
    Button btnSignUp;
    TextInputLayout tilFName, tilLName, tilEmail, tilPwd, tilCPwd;
    RadioGroup rgGender;
    RadioButton rbGender, rbMale, rbFemale;
    TextView errGender;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        actionBar = getSupportActionBar();
        btnSignUp = findViewById(R.id.btnSignUp);
        tilFName = findViewById(R.id.tilFirstName);
        tilLName = findViewById(R.id.tilLastName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPwd = findViewById(R.id.tilPwd);
        tilCPwd = findViewById(R.id.tilCPwd);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        errGender = findViewById(R.id.genderErr);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loading = new LoadingDialog(SignUpActivity.this);

        btnSignUp.setOnClickListener(this);
        rbMale.setOnClickListener(this);
        rbFemale.setOnClickListener(this);

        tilFName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String firstName = tilFName.getEditText().getText().toString();
                fieldIsFilled(tilFName, firstName);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tilLName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String lastName = tilLName.getEditText().getText().toString();
                fieldIsFilled(tilLName, lastName);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tilEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = tilEmail.getEditText().getText().toString();
                fieldIsFilled(tilEmail, email);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tilPwd.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = tilPwd.getEditText().getText().toString();
                fieldIsFilled(tilPwd, password);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tilCPwd.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cpassword = tilCPwd.getEditText().getText().toString();
                fieldIsFilled(tilCPwd, cpassword);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        actionBar.setTitle(R.string.sign_up);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean fieldIsFilled(TextInputLayout til, String text) {
        String password = tilPwd.getEditText().getText().toString();
        if (text.equals("")) {
            til.setError("must be filled");
            return false;
        } else if (til.equals(tilCPwd) && !text.equals(password)) {
            til.setError("must be same as password");
            return false;
        } else {
            til.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                String firstName, lastName, gender, email, password, cpassword;
                firstName = tilFName.getEditText().getText().toString();
                lastName = tilLName.getEditText().getText().toString();
                email = tilEmail.getEditText().getText().toString();
                password = tilPwd.getEditText().getText().toString();
                cpassword = tilCPwd.getEditText().getText().toString();

                if (rgGender.getCheckedRadioButtonId() != -1) {
                    rbGender = findViewById(rgGender.getCheckedRadioButtonId());
                    gender = rbGender.getText().toString();
                } else {
                    gender = "";
                }

                if (gender.equals("")) {
                    errGender.setVisibility(View.VISIBLE);
                }

                fieldIsFilled(tilFName, firstName);
                fieldIsFilled(tilLName, lastName);
                fieldIsFilled(tilEmail, email);
                fieldIsFilled(tilPwd, password);
                fieldIsFilled(tilCPwd, cpassword);

                boolean allFieldsFilled = fieldIsFilled(tilFName, firstName) &&
                        fieldIsFilled(tilLName, lastName) &&
                        fieldIsFilled(tilEmail, email) &&
                        fieldIsFilled(tilPwd, password) &&
                        fieldIsFilled(tilCPwd, cpassword);
                if (allFieldsFilled) {
                    loading.startLoadingDialog("Signing you up\nPlease wait");
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign up and sign in success. Insert the user profile and go to the main activity
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        db = FirebaseFirestore.getInstance();
                                        Map<String, Object> userProfile = new HashMap<>();
                                        userProfile.put("firstName", firstName);
                                        userProfile.put("lastName", lastName);
                                        userProfile.put("gender", gender);
                                        userProfile.put("money", 0.0);
                                        userProfile.put("registerDate", new Date().getTime()); // Time since 1 jan 1970 00.00 utc

                                        db.collection("userProfiles")
                                                .document(user.getUid())
                                                .set(userProfile);
                                        loading.dismissDialog();

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finishAffinity();
                                    } else {
                                        // If sign in failed, display this message
                                        if (task.getException().toString().contains("already in use")) {
                                            tilEmail.setError("is already used by another account");
                                        }
                                        loading.dismissDialog();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(this, "Please check all fields!", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.rbMale:
            case R.id.rbFemale:
            case R.id.rgGender:
                errGender.setVisibility(View.GONE);
                break;
            default:
                Toast.makeText(this, "Error finding text field", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}