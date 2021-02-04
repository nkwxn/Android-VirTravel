package com.nicholas.virtravel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    TextView tvFullName, tvEmail, tvMemberSince, tvBalance;
    MaterialCardView cvEditProfile, cvTopUpBalance, cvSignOut;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        tvFullName = v.findViewById(R.id.userFullName);
        tvEmail = v.findViewById(R.id.userEmail);
        tvMemberSince = v.findViewById(R.id.usrRegisteredDate);
        tvBalance = v.findViewById(R.id.usrBalance);

        // Set text on each fields
        db.collection("userProfiles")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                String JSONData = documentSnapshot.getData() + "";
                                try {
                                    LanguageDetector langDetect = new LanguageDetector();

                                    JSONObject jsonObject = new JSONObject(JSONData);
                                    String firstName = jsonObject.getString("firstName"),
                                            lastName = jsonObject.getString("lastName");
                                    if (langDetect.hasKorean(lastName) || langDetect.hasJapanese(lastName) || langDetect.hasChinese(lastName)) {
                                        tvFullName.setText(lastName + " " + firstName);
                                    } else {
                                        tvFullName.setText(firstName + " " + lastName);
                                    }
                                    tvEmail.setText(currentUser.getEmail());

                                    // Bikin registered date jadi tanggal beneran
                                    Date date = new Date((long) jsonObject.getDouble("registerDate"));
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");

                                    tvMemberSince.setText("Member since " + formatter.format(date));
                                    tvBalance.setText("$" + jsonObject.getDouble("money"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Toast.makeText(getContext(), "No document", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed with: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        cvEditProfile = v.findViewById(R.id.btnEditProfile);
        cvTopUpBalance = v.findViewById(R.id.btnTopUp);
        cvSignOut = v.findViewById(R.id.btnLogOut);

        cvEditProfile.setOnClickListener(this);
        cvTopUpBalance.setOnClickListener(this);
        cvSignOut.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogOut:
                mAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
                break;
            default:
                Toast.makeText(getContext(), "Function not yet available", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
