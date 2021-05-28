package com.tamer.alna99.watertabclient.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.CameraPosition;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.tamer.alna99.watertabclient.DriverInfoActivity;
import com.tamer.alna99.watertabclient.NetworkUtils;
import com.tamer.alna99.watertabclient.R;
import com.tamer.alna99.watertabclient.model.findDriver.FindDriverResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomepageFragment extends Fragment implements OnMapReadyCallback {
    private final int REQUEST_LOCATION_PERMISSION = 100;
    private final int REQUEST_LOCATION_SETTINGS = 10;
    private NetworkUtils networkUtils;
    private Button findDriver;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location location;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("dddd", "onCreateView");
        networkUtils = NetworkUtils.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        checkSettingsAndRequestLocationUpdates();
        createLocationRequest();
        createLocationCallback();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        findDriver = view.findViewById(R.id.findDriverBtn);
        findDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ddd", "onClick");
                Call<FindDriverResponse> responseBodyCall = networkUtils.getApiInterface().findDriver("-112.4724356", "37.7672544");
                responseBodyCall.enqueue(new Callback<FindDriverResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<FindDriverResponse> call, @NotNull Response<FindDriverResponse> response) {
                        Log.d("ddd", "onResponse");

                        try {
                            if (response.body() != null) {
                                FindDriverResponse driver = response.body();
                                Intent intent = new Intent(getContext(), DriverInfoActivity.class);
                                intent.putExtra("driver", driver);
                                Log.d("ddd", driver.getDriver().getName());
                                startActivity(intent);
//                                Log.d("ddd", "true");
//                                JsonObject root = new JsonParser().parse(response.body().string()).getAsJsonObject();
//                                if (root != null){
//                                    Log.d("dddd", root.toString());
//                                }
////                                String name = root.get("name").getAsString();
//                                Log.d("dddd", response.body().string()+"ll");
//                                Log.d("dddd", "done");
                            } else {
                                Log.d("dddd", "null    " + response.errorBody().string());

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<FindDriverResponse> call, @NotNull Throwable t) {

                    }
                });
            }
        });
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        Log.d("dddd", "onMapReady");


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_SETTINGS && resultCode == Activity.RESULT_OK) {
            requestLocationUpdates();
        } else {
            Toast.makeText(getContext(), "location service is not enabled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Get Location
                checkSettingsAndRequestLocationUpdates();
            } else {
                Toast.makeText(getContext(), "location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkSettingsAndRequestLocationUpdates() {
        // Check permission
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Create location settings request
            LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest).build();
            SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequest);

            // Success
            task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    // request location updates
                    requestLocationUpdates();
                }
            });

            // Failure
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // if resolvable, ask the user  to enable location settings
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(getActivity(), REQUEST_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                            // Location is not available in this device
                            Toast.makeText(getContext(), "Location Service unavailable", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Location is not available in this device
                        Toast.makeText(getContext(), "Location Service unavailable", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void requestLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5);
        locationRequest.setFastestInterval(3);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                location = locationResult.getLastLocation();
                if (location != null) {
                    Log.d("dddd", location.getLongitude() + " Longitude " + location.getAltitude() + " Altitude");
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getAltitude(), location.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng);
                        googleMap.addMarker(markerOptions);
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .zoom(18)
                                .bearing(30)
                                .target(latLng)
                                .build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        removeLocationUpdates();
                    }
                }

            }
        };
    }

    private void removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}