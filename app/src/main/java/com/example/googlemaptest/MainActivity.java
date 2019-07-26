package com.example.googlemaptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FloatingActionButton floatingActionButton;
    private TextView showAddressTV;
    private ImageView pickerIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        init();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

    }

    private void init() {
        floatingActionButton = findViewById(R.id.floatingActionBtn);
        pickerIV = findViewById(R.id.pickerIV);
        showAddressTV = findViewById(R.id.showAddressTV);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng BITM = new LatLng(23.7104, 90.40744);

        mMap.addMarker(new MarkerOptions().position(BITM).title("Marker in BITM"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BITM, 12));

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        getPermission();
        getCurrentLocation();
        getPickerLocation(googleMap);



    }



    private void getPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(permission, 0);

            } else {

            }


        }
    }
    private  void getPickerLocation(final GoogleMap googleMap){
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng pickerLatLng = googleMap.getCameraPosition().target;
                showAddressTV.setText(getAddress(pickerLatLng.latitude,pickerLatLng.longitude));
               

            }
        });
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(this);


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Task location = fusedLocationProviderClient.getLastLocation();

        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();


                    if (currentLocation != null) {

                        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                        String address = getAddress(currentLocation.getLatitude(), currentLocation.getLongitude());


                        mMap.addMarker(new MarkerOptions().position(current).title(address));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
                    } else {

                    }

                }

            }
        });
    }

    public String getAddress(double lat, double lng) {
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return address;
    }
}
