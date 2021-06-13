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
import com.tamer.alna99.watertabclient.model.SharedPrefs;
import com.tamer.alna99.watertabclient.model.findDriver.MySocket;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
    private final Emitter.Listener driverDecisionListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String data = (String) args[0];
            Log.d("ddd", data);
        }
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
    String id;
    private Socket socket;
    LatLng origin;
    LatLng destination;
    private Group group;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        networkUtils = NetworkUtils.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        id = SharedPrefs.getUserId(getContext());
        checkSettingsAndRequestLocationUpdates();
        createLocationRequest();
        createLocationCallback();
        socket = MySocket.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        Button findDriver = view.findViewById(R.id.findDriverBtn);
        group = view.findViewById(R.id.group);
        progressBar = view.findViewById(R.id.map_progressBar);
        findDriver.setOnClickListener(view1 -> {
            ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                    getString(R.string.search_driver), true);

            Call<ResponseBody> responseBodyCall = networkUtils.getApiInterface().findDriver(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));

            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    dialog.dismiss();
                    Log.d("ddd", "onResponse");
                    if (response.body() != null) {
                        try {
                            String result = response.body().string();
                            JSONObject jsonObject = new JSONObject(result);
                            Log.d("dddd", result);
                            boolean success = jsonObject.getBoolean("success");
                            JSONArray array = jsonObject.getJSONArray("driver");
                            Log.d("dddd", array.toString());

                            if (success) {
                                JSONObject driver = array.getJSONObject(0);
                                String driverID = driver.getString("_id");
                                JSONObject locationJSON = driver.getJSONObject("location");
                                JSONArray coordinates = locationJSON.getJSONArray("coordinates");
                                String name = driver.getString("name");
                                String email = driver.getString("email");

                                Log.d("ddd", driverID);
                                Log.d("ddd", name);
                                Log.d("ddd", email);
                                Log.d("ddd", String.valueOf(coordinates.getDouble(0)));
                                Log.d("ddd", String.valueOf(coordinates.getDouble(1)));
                                destination = new LatLng(coordinates.getDouble(1), coordinates.getDouble(0));

                                Log.d("dddd", String.valueOf(destination.latitude));
                                BottomSheetFragment sheetFragment = new BottomSheetFragment(driverID, String.valueOf(coordinates.getDouble(0)), String.valueOf(coordinates.getDouble(1)));
                                sheetFragment.show(getChildFragmentManager(), "Tag");


                                drawRouting();


                                socket.connect();
//                                JSONObject data = new JSONObject();
//                                try {
//
//                                    data.put("id", id);
//                                    data.put("isDriver", "false");
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                socket.emit("join", data);

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
                    Log.d("dddd", t.getMessage() + "-----" + t.getLocalizedMessage());
                    dialog.dismiss();
                }
            });
        });
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        mMap = googleMap;
        origin = new LatLng(location.getLatitude(), location.getLongitude());

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
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Create location settings request
            LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest).build();
            SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
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
            });
        }
    }

    private void requestLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                Log.d("dddd", String.valueOf(location.getLongitude()));
                Log.d("dddd", String.valueOf(location.getLatitude()));
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
//        MarkerOptions markerOptions2 = new MarkerOptions()
//                .position(destination);
//        mMap.addMarker(markerOptions2);
//        mMap.addPolyline(new PolylineOptions()
//                .add(origin, destination)
//                .width(5)
//                .color(Color.RED)
//                .geodesic(true));

//        DrawRouteMaps.getInstance(getContext())
//                .draw(origin, destination, mMap);
//        DrawMarker.getInstance(getContext()).draw(mMap, origin, R.drawable.ic_location, "Origin Location");
//        DrawMarker.getInstance(getContext()).draw(mMap, destination, R.drawable.ic_location, "Destination Location");
//
//        LatLngBounds bounds = new LatLngBounds.Builder()
//                .include(origin)
//                .include(destination).build();
//
//        Point displaySize = new Point();
//        getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
//        supportMapFragment.getMapAsync(HomepageFragment.this);

    }


}