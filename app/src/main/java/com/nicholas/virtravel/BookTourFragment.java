package com.nicholas.virtravel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookTourFragment extends Fragment {
    RecyclerView recyclerView;
    List<TourPackage> tourPackages;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

        @Nullable
        @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();

        View v = inflater.inflate(R.layout.fragment_book_tour, container, false);
        recyclerView = v.findViewById(R.id.rvTravels);
        initDataRV(savedInstanceState);

        return v;
    }

    private void initDataRV(Bundle savedInstanceState) {
        tourPackages = new ArrayList<>();

        // Disini diganti dengan Fetch data from Firebase
        CollectionReference colRef = db.collection("virtualTourPackages");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Get the data
                    for (QueryDocumentSnapshot docs : task.getResult()) {
                        GeoPoint location = docs.getGeoPoint("location");
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        tourPackages.add(new TourPackage(
                                docs.getString("title"),
                                docs.getString("desc"),
                                docs.getString("imageLink"),
                                docs.getDouble("price"),
                                lat, lon
                        ));
                    }

                    // Set adapter for the RecyclerView
                    TourPackageAdapter tourPackageAdapter = new TourPackageAdapter(getContext(), tourPackages, savedInstanceState);
                    recyclerView.setAdapter(tourPackageAdapter);
                }
            }
        });
    }
}
