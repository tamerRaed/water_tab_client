package com.tamer.alna99.watertabclient.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.tamer.alna99.watertabclient.R;
import com.tamer.alna99.watertabclient.model.MySocket;
import com.tamer.alna99.watertabclient.model.Result;
import com.tamer.alna99.watertabclient.model.SharedPrefs;
import com.tamer.alna99.watertabclient.viewmodel.HomepageViewModel;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class HomepageFragment extends Fragment implements OnMapReadyCallback {

    private final int REQUEST_LOCATION_PERMISSION = 100;
    private final int REQUEST_LOCATION_SETTINGS = 10;
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
    JSONObject data2;
    boolean answer;
    String driverId;
    private HomepageViewModel viewModel;
    private Button findDriverBtn;
    private Button rateBtn;
    private GoogleMap googleMap;
    private LatLng origin;
    private Marker marker;
    private Polyline polyline;
    private CameraPosition cameraPosition;
    private final Emitter.Listener driverDecisionListener = args -> {
        answer = (boolean) args[0];
        Log.d("dddd", "Answer Listener: " + answer);
        Alerter.hide();
        if (answer) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findDriverBtn.setVisibility(View.GONE);
                    rateBtn.setVisibility(View.VISIBLE);
                }
            });

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new HomepageViewModel();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        checkSettingsAndRequestLocationUpdates();
        createLocationRequest();
        createLocationCallback();
        socket = MySocket.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        findDriverBtn = view.findViewById(R.id.findDriverBtn);
        group = view.findViewById(R.id.group);
        rateBtn = view.findViewById(R.id.rateBtn);
        progressBar = view.findViewById(R.id.map_progressBar);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        socket.connect();

        data2 = new JSONObject();
        try {
            String clintId = SharedPrefs.getUserId(requireContext());
            data2.put("id", clintId);
            data2.put("isDriver", "false");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("join", data2);

        rateBtn.setOnClickListener(view12 -> {
            BottomSheetRating rating = new BottomSheetRating(new BottomSheetRating.OnRateAnswerClick() {
                @Override
                public void onRateClick(double rate) {
                    marker.remove();
                    polyline.remove();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    Log.d("dddd", "Rate : " + rate);
                    rateBtn.setVisibility(View.GONE);
                    findDriverBtn.setVisibility(View.VISIBLE);
                    viewModel.getInfo().addObserver((observable, o) -> {
                        Result result = (Result) o;
                        switch (result.status) {
                            case SUCCESS:
                                String data = (String) result.data;
                                Log.d("dddd", data);
                                break;
                            case ERROR:
                                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    });
                    viewModel.requestRateDriver(driverId, (int) rate);
                }

                @Override
                public void onLaterClick() {
                    rateBtn.setVisibility(View.GONE);
                    findDriverBtn.setVisibility(View.VISIBLE);
                    marker.remove();
                }
            });
            rating.show(getChildFragmentManager(), "Tag2");
        });

        findDriverBtn.setOnClickListener(view1 -> {
            ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                    getString(R.string.search_driver), true);

            viewModel.getInfo().addObserver((observable, o) -> {
                Result<String> result = (Result) o;
                switch (result.status) {
                    case SUCCESS:
                        dialog.dismiss();
                        String data = result.data;
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {
                                JSONObject driver = jsonObject.getJSONObject("driver");
                                driverId = driver.getString("id");
                                String name = driver.getString("name");
                                String email = driver.getString("email");
                                String phone = driver.getString("phone");
                                double driverLat = driver.getDouble("lat");
                                double driverLon = driver.getDouble("lon");
                                double rate = driver.getDouble("rate");

                                destination = new LatLng(driverLon, driverLat);

                                BottomSheetFragment sheetFragment = new
                                        BottomSheetFragment(name, email, phone, rate, () -> orderDriver(driverId, lat, lon));
                                sheetFragment.show(getChildFragmentManager(), "Tag");

                            } else {
                                Log.d("dddd", "No Driver");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case ERROR:
                        dialog.dismiss();
                        break;
                }
            });
            viewModel.requestFindDriver(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
        });
        return view;
    }

    private void orderDriver(String driverID, double lat, double lon) {
        Toast.makeText(getContext(), R.string.order_send, Toast.LENGTH_SHORT).show();
        String clintId = SharedPrefs.getUserId(requireContext());
        String name = SharedPrefs.getUserName(requireContext());
        viewModel.getInfo().addObserver((observable, o) -> {
            Result<String> result = (Result) o;
            switch (result.status) {
                case SUCCESS:
                    if (answer) {
                        Alerter.create(requireActivity())
                                .setText(getString(R.string.driver_on_way))
                                .setTitle(R.string.order_accepted)
                                .setDuration(5000)
                                .setBackgroundColorRes(R.color.teal_200)
                                .show();

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(destination);
                        marker = googleMap.addMarker(markerOptions);
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_delivery_truck));
//                        googleMap.addMarker(markerOptions);

                        polyline = googleMap.addPolyline((new PolylineOptions()).add(destination, origin).
                                // below line is use to specify the width of poly line.
                                        width(5)
                                // below line is use to add color to our poly line.
                                .color(Color.RED)
                                // below line is to make our poly line geodesic.
                                .geodesic(true));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .zoom(18)
                                .bearing(30)
                                .target(destination)
                                .build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } else {
                        Alerter.create(requireActivity())
                                .setText(getString(R.string.find_another_driver))
                                .setTitle(R.string.order_rejection)
                                .setDuration(5000)
                                .setBackgroundColorRes(R.color.teal_200)
                                .show();
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        viewModel.requestOrderDriver(clintId, driverID, name, lat, lon);
        socket.on("driverDecision", driverDecisionListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        origin = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(origin);
        googleMap.addMarker(markerOptions);
        cameraPosition = new CameraPosition.Builder()
                .zoom(18)
                .bearing(30)
                .target(origin)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
}