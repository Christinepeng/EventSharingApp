package com.google.firebase.quickstart.database.java.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.quickstart.database.R;
import com.google.firebase.quickstart.database.java.PostDetailActivity;
import com.google.maps.android.ui.IconGenerator;

import java.util.Random;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";

    private DatabaseReference mDatabase;
    private Random randomGenerator;
    private IconGenerator iconFactory;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        randomGenerator = new Random(0);
        iconFactory = new IconGenerator(getActivity());
        iconFactory.setStyle(IconGenerator.STYLE_BLUE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //mMap.setMyLocationEnabled(true); // TODO
                //mMap.getUiSettings().setMyLocationButtonEnabled(true); // TODO
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.getUiSettings().setIndoorLevelPickerEnabled(true);

                mMap.clear(); //clear old markers

                mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
                mDatabase.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                        String title = (String) dataSnapshot.child("title").getValue();
                        LatLng position = generateRandomPosition();
                        Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(title));
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(title)));
                        marker.setTag(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(37.4629101, -122.2449094))
                        .zoom(9)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, ((String) marker.getTag()));
                        startActivity(intent);
                        return false;
                    }
                });
            }
        });
    }

    private LatLng generateRandomPosition() {
        int region_id = randomGenerator.nextInt() % 3;

        double lat_eps = randomGenerator.nextDouble() / 20;
        double lng_eps = randomGenerator.nextDouble() / 20;

        if (region_id == 0) { // North Bay Area
            return new LatLng(37.758804 + lat_eps, -122.444735 + lng_eps);
        } else if (region_id == 1) { // South Bay Area
            return new LatLng(37.367012 + lat_eps, -122.073223 + lng_eps);
        } else { // East Bay Area
            return new LatLng(37.824656 + lat_eps, -122.240738 + lng_eps);
        }
    }

}