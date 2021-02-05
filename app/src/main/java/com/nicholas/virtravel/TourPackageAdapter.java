package com.nicholas.virtravel;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.local.ReferenceSet;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TourPackageAdapter extends RecyclerView.Adapter<TourPackageAdapter.TourPackageVH> {
    List<TourPackage> tourPackageList;
    Context c;
    Bundle savedInstanceState;

    public TourPackageAdapter(Context c, List<TourPackage> tourPackageList, Bundle savedInstanceState) {
        this.tourPackageList = tourPackageList;
        this.c = c;
        this.savedInstanceState = savedInstanceState;
    }

    @NonNull
    @Override
    public TourPackageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tours, parent, false);
        return new TourPackageVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TourPackageVH holder, int position) {
        TourPackage pack = tourPackageList.get(position);
        holder.tourTitle.setText(pack.getTitle());
        holder.tourDesc.setText(pack.getDesc());
        holder.tourPrice.setText("$" + pack.getPrice());

        loadMaps(holder.maps, pack.getLat(), pack.getLon());

        LoadImage loadImage = new LoadImage(holder.imgTours);
        loadImage.execute(pack.getImgLink());

        boolean isExpanded = pack.isExpanded();
        holder.collapsingView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.btnExpCol.setText(isExpanded ? "Collapse" : "Expand");

        // When book tour pressed
        holder.btnBuyPack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String uID = mAuth.getCurrentUser().getUid();
                String tourID = pack.getTourID();
                openDialog(tourID, uID, pack.getTitle(), pack.getPrice(), pack.getVideoLink());
            }
        });
    }

    public void loadMaps(
            MapView gMapsFrame,
            double lat,
            double lon
    ) {
        gMapsFrame.onCreate(savedInstanceState);
        gMapsFrame.getMapAsync(googleMap -> {
            LatLng location = new LatLng(lat, lon);
            googleMap.addMarker(
                    new MarkerOptions()
                            .position(location)
                            .title("The tour location")
            );
            googleMap.moveCamera(
                    CameraUpdateFactory
                            .newLatLng(location)
            );

            MapsInitializer.initialize(c);
            CameraPosition zoom =
                    new CameraPosition
                            .Builder()
                            .target(location)
                            .zoom(15)
                            .bearing(0)
                            .tilt(0)
                            .build();
            googleMap.animateCamera(
                    CameraUpdateFactory
                            .newCameraPosition(zoom)
            );
        });
        gMapsFrame.onResume();
    }

    AlertDialog ad;

    public void openDialog(String tourID, String userID, String title, double price, String link) {
        String t = title;
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Booking Confirmation")
                .setMessage("You are about to book " + title + " and $" + price + " is going to be deducted from your balance. Continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        DocumentReference profileRef = db.collection("userProfiles").document(userID);
                        DocumentReference tourRef = db.collection("virtualTours").document(tourID);

                        Map<String, Object> booking = new HashMap<>();
                        booking.put("profile", profileRef);
                        booking.put("purchaseDate", new Timestamp(new Date()));
                        booking.put("tourBooked", tourRef);
                        booking.put("watched", false);

                        db.collection("userTransactions")
                                .add(booking)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        // Notification service
                                        String title = "Booking Success";
                                        String message = "Please tap this notification to access " + t;
                                        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                                        Intent intent = new Intent(c, VirtualTourVideo.class);
                                        intent.putExtra("VideoURL", link);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent, 0);
                                        if(Build.VERSION.SDK_INT >= 26) {// Ketika versi SDK diatas sama dengan 26 (Android 8.0 Oreo)
                                            String id = "dm_1";
                                            String desc = "Transaction";
                                            int importance = NotificationManager.IMPORTANCE_HIGH;
                                            NotificationChannel channel = new NotificationChannel(id, desc, importance);
                                            channel.enableVibration(true);
                                            manager.createNotificationChannel(channel);
                                            Notification notification = new Notification.Builder(c, id)
                                                    .setCategory(Notification.CATEGORY_MESSAGE)
                                                    .setSmallIcon(R.drawable.ic_buy_tours)
                                                    .setContentTitle(title)
                                                    .setContentText(message)
                                                    .setStyle(new Notification.BigTextStyle().bigText(message + ""))
                                                    .setContentIntent(pendingIntent)
                                                    .build();
                                            manager.notify(1, notification);
                                        } else {
                                            // Ketika versi SDK di bawah 26 (Android 8.0 Oreo)
                                            Notification notification = new NotificationCompat.Builder(c)
                                                    .setCategory(Notification.CATEGORY_MESSAGE)
                                                    .setSmallIcon(R.drawable.ic_buy_tours)
                                                    .setContentTitle(title)
                                                    .setContentText(message)
                                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message + ""))
                                                    .setContentIntent(pendingIntent)
                                                    .build();
                                            manager.notify(1, notification);
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(c, "Error transaction: " + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.dismiss();
                    }
                })
                .setCancelable(true);
        ad = builder.create();
        ad.show();
    }

    @Override
    public int getItemCount() {
        return tourPackageList.size();
    }

    class TourPackageVH extends RecyclerView.ViewHolder {
        TextView tourTitle, tourDesc, tourPrice;
        Button btnExpCol, btnBuyPack;
        ImageView imgTours;
        MapView maps;
        LinearLayout collapsingView;

        public TourPackageVH(@NonNull View itemView) {
            super(itemView);

            tourTitle = itemView.findViewById(R.id.tvTourName);
            tourDesc = itemView.findViewById(R.id.tourDesc);
            tourPrice = itemView.findViewById(R.id.tvPrice);
            btnExpCol = itemView.findViewById(R.id.btnExpandCollapse);
            btnBuyPack = itemView.findViewById(R.id.btnBookNow);
            imgTours = itemView.findViewById(R.id.tourImage);
            maps = itemView.findViewById(R.id.maps);
            collapsingView = itemView.findViewById(R.id.collapsingView);

            // Expand and Collapse
            btnExpCol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TourPackage pack = tourPackageList.get(getAdapterPosition());
                    pack.setExpanded(!pack.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}

