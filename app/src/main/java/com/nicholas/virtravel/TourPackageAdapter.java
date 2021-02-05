package com.nicholas.virtravel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class TourPackageAdapter extends RecyclerView.Adapter<TourPackageAdapter.TourPackageVH> {
    List<TourPackage> tourPackageList;
    Context c;
    Bundle instance;

    public TourPackageAdapter(Context c, List<TourPackage> tourPackageList, Bundle savedInstanceState) {
        this.tourPackageList = tourPackageList;
        this.c = c;
        instance = savedInstanceState;
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
        holder.tourPrice.setText(pack.getPrice() + "");

        holder.maps.onCreate(instance);
        holder.maps.getMapAsync(googleMap -> {
            double lat = pack.getLat();
            double lon = pack.getLon();
            LatLng location = new LatLng(lat, lon);
            googleMap.addMarker(new MarkerOptions().position(location));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

            MapsInitializer.initialize(c);
            CameraPosition zoom = new CameraPosition.Builder().target(location).zoom(15).bearing(0).tilt(0).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(zoom));
        });
        holder.maps.onResume();

        boolean isExpanded = pack.isExpanded();
        holder.collapsingView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.btnExpCol.setText(isExpanded ? "Collapse" : "Expand");
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
