package com.tamer.alna99.watertabclient.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.tamer.alna99.watertabclient.NetworkUtils;
import com.tamer.alna99.watertabclient.R;
import com.tamer.alna99.watertabclient.model.MySocket;
import com.tamer.alna99.watertabclient.model.SharedPrefs;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomepageFragment extends Fragment implements OnMapReadyCallback {
    private final Emitter.Listener driverDecisionListener = args -> {
        boolean data = (boolean) args[0];
        Log.d("ddd", String.valueOf(data));
    };
    GoogleMap mMap;
    private final int REQUEST_LOCATION_PERMISSION = 100;
    private final int REQUEST_LOCATION_SETTINGS = 10;
    private NetworkUtils networkUtils;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location location;
    private SupportMapFragment supportMapFragment;
    private Socket socket;
    private LatLng destination;
    private Group group;
    private ProgressBar progressBar;
    private double lon;
    private double lat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        networkUtils = NetworkUtils.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        checkSettingsAndRequestLocationUpdates();
        createLocationRequest();
        createLocationCallback();
        socket = MySocket.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        Button findDriver = view.findViewById(R.id.findDriverBtn);
        group = view.findViewById(R.id.group);
        progressBar = view.findViewById(R.id.map_progressBar);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        findDriver.setOnClickListener(view1 -> {
            ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                    getString(R.string.search_driver), true);

            Call<ResponseBody> responseBodyCall = networkUtils.getApiInterface().findDriver(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));

            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    dialog.dismiss();
                    if (response.body() != null) {
                        try {
                            String result = response.body().string();
                            JSONObject jsonObject = new JSONObject(result);
                            Log.d("dddd", result);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {
                                JSONObject driver = jsonObject.getJSONObject("driver");
                                String id = driver.getString("id");
                                String name = driver.getString("name");
                                String email = driver.getString("email");
                                //String phone = driver.getString("phone");
                                double driverLat = driver.getDouble("lat");
                                double driverLon = driver.getDouble("lon");
                                double rate = driver.getDouble("rate");
                                destination = new LatLng(driverLat, driverLon);

                                BottomSheetFragment sheetFragment = new
                                        BottomSheetFragment(name, email, "phone", rate, () -> orderDriver(id, lat, lon));
                                sheetFragment.show(getChildFragmentManager(), "Tag");

                                drawRouting();

                                socket.connect();

                                JSONObject data = new JSONObject();
                                try {
                                    String clintId = SharedPrefs.getUserId(requireContext());
                                    Log.d("dddd", clintId);
                                    data.put("id", clintId);
                                    data.put("isDriver", "false");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                socket.emit("join", data);

                                socket.on("driverDecision", driverDecisionListener);

                            } else {
                                Log.d("dddd", "No Driver");
                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    dialog.dismiss();
                }
            });
        });
        return view;
    }

    private void orderDriver(String driverID, double lat, double lon) {
        String clintId = SharedPrefs.getUserId(requireContext());
        String name = SharedPrefs.getUserName(requireContext());
        Call<ResponseBody> orderDriverResponse = networkUtils.getApiInterface().orderDriver(
                clintId,
                driverID,
                name,
                lat,
                lon);
        orderDriverResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    Log.d("dddd", "onResponse");
                    assert response.body() != null;
                    Log.d("dddd", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(origin);
        mMap.addMarker(markerOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .zoom(18)
                .bearing(30)
                .target(origin)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
                createLocationRequest();
                createLocationCallback();
            } else {
                Toast.makeText(getContext(), "location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkSettingsAndRequestLocationUpdates() {
        // Check permission
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Create location settings request
            LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest).build();
            SettingsClient settingsClient = LocationServices.getSettingsClient(requireContext());
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequest);

            // Success
            task.addOnSuccessListener(locationSettingsResponse -> {
                // request location updates
                requestLocationUpdates();
            });

            // Failure
            task.addOnFailureListener(e -> {
                if (e instanceof ResolvableApiException) {
                    // if resolvable, ask the user  to enable location settings
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(requireActivity(), REQUEST_LOCATION_SETTINGS);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                        // Location is not available in this device
                        Toast.makeText(getContext(), "Location Service unavailable", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Location is not available in this device
                    Toast.makeText(getContext(), "Location Service unavailable", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void requestLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
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
                lat = location.getLatitude();
                lon = location.getLongitude();
                removeLocationUpdates();
                supportMapFragment.getMapAsync(HomepageFragment.this);
                progressBar.setVisibility(View.GONE);
                group.setVisibility(View.VISIBLE);

            }
        };
    }

    private void removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void drawRouting() {
    }
}