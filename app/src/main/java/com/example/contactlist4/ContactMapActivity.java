package com.example.contactlist4;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
//import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.location.LocationRequest;  // ✅ Correct

//import com.google.android.gms.location.Priority;  // ✅ Add this import


public class ContactMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    final int PERMISSION_REQUEST_LOCATION = 101;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_map);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_contact_map), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error loading map", Toast.LENGTH_SHORT).show();
        }

        createLocationRequest();
        createLocationCallback();

        contactlistButton();
        mapButton();
        settingsButton();
//        initGetLocationButton();
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);  // ✅ Correct
    }
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Toast.makeText(getBaseContext(), "Lat: " + location.getLatitude() +
                            " Long: " + location.getLongitude() +
                            " Accuracy: " + location.getAccuracy(), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        if (gMap != null) {
            gMap.setMyLocationEnabled(true);
        }
    }

    private void stopLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Button locationButton = findViewById(R.id.buttonGetLocation);
        locationButton.setOnClickListener(v -> {
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(ContactMapActivity.this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                ContactMapActivity.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                            Snackbar.make(findViewById(R.id.activity_contact_map),
                                            "MyContactList requires this permission to locate your contacts",
                                            Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK", view -> ActivityCompat.requestPermissions(
                                            ContactMapActivity.this,
                                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                            PERMISSION_REQUEST_LOCATION))
                                    .show();
                        } else {
                            ActivityCompat.requestPermissions(ContactMapActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSION_REQUEST_LOCATION);
                        }
                    } else {
                        startLocationUpdates();
                    }
                } else {
                    startLocationUpdates();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Error requesting permission", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "MyContactList will not locate your contacts.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void contactlistButton() {
        ImageButton imageButtonList = findViewById(R.id.imageButtonList);
        imageButtonList.setOnClickListener(v -> {
            Intent intent = new Intent(ContactMapActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void mapButton() {
        ImageButton imageButtonMap = findViewById(R.id.imageButtonMap);
        imageButtonMap.setOnClickListener(v -> {
            Intent intent = new Intent(ContactMapActivity.this, ContactMapActivity.class);
            startActivity(intent);
        });
    }

    private void settingsButton() {
        ImageButton imageButtonSettings = findViewById(R.id.imageButtonSettings);
        imageButtonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ContactMapActivity.this, ContactSettingsActivity.class);
            startActivity(intent);
        });
    }

//    private void initGetLocationButton() {
//        Button locationButton = findViewById(R.id.buttonGetLocation);
//        locationButton.setOnClickListener(v -> startLocationUpdates());
//    }
}

//public class ContactMapActivity extends AppCompatActivity implements
////    LocationManager locationManager;
////
////    LocationListener gpsListener;
////    LocationListener networkListener;
//
////    Location currentBestLocation;
//
//        OnMapReadyCallback {
//
//         final int PERMISSION_REQUEST_LOCATION = 101;
//         GoogleMap gMap;
//         FusedLocationProviderClient fusedLocationProviderClient;
//         LocationRequest locationRequest;
//         LocationCallback locationCallback;
//
//
//         @Override
//         protected void onCreate (Bundle savedInstanceState){
//             super.onCreate(savedInstanceState);
//             EdgeToEdge.enable(this);
//             setContentView(R.layout.activity_contact_map);
//             ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_contact_map), (v, insets) -> {
//                 Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//                 v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//                 return insets;
//
//
//             });
//             fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//             SupportMapFragment mapFragment = (SupportMapFragment)
//                     getSupportFragmentManager().findFragmentById(R.id.map);
//             mapFragment.getMapAsync(this);
//
//             createLocationRequest();
//             createLocationCallback();
//
//             contactlistButton();
//             mapButton();
//
//             settingsButton();
//             initGetLocationButton();
//             startLocationUpdates();
//         }
//     }
//    private void createLocationRequest() {
//        locationRequest = LocationRequest.create();
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(5000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//    private void createLocationCallback() {
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    Toast.makeText(getBaseContext(), "Lat: " + location.getLatitude() +
//                            " Long: " + location.getLongitude() +
//                            " Accuracy: " + location.getAccuracy(), Toast.LENGTH_LONG).show();
//                }
//            }
//        };
//    }
//    private void startLocationUpdates() {
//        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ContextCompat.checkSelfPermission(getBaseContext(),
//                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
//        gMap.setMyLocationEnabled(true);
//    }
//
//    private void stopLocationUpdates() {
//        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ContextCompat.checkSelfPermission(getBaseContext(),
//                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        gMap = googleMap;
//        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        // Paste permission request code from initGetLocationButton here (Listing 7.6)
//        locationButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                try {
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        if (ContextCompat.checkSelfPermission(ContactMapActivity.this,
//                                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                                != PackageManager.PERMISSION_GRANTED) {
//
//                            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                                    ContactMapActivity.this,
//                                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                                Snackbar.make(findViewById(R.id.activity_contact_map),
//                                                "MyContactList requires this permission to locate " +
//                                                        "your contacts", Snackbar.LENGTH_INDEFINITE)
//                                        .setAction("OK", new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                ActivityCompat.requestPermissions(
//                                                        ContactMapActivity.this,
//                                                        new String[]{
//                                                                android.Manifest.permission.ACCESS_FINE_LOCATION
//                                                        },
//                                                        PERMISSION_REQUEST_LOCATION);
//                                            }
//                                        })
//                                        .show();
//                            } else {
//                                ActivityCompat.requestPermissions(ContactMapActivity.this, new
//                                                String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                                        PERMISSION_REQUEST_LOCATION);
//                            }
//                        } else {
//                            startLocationUpdates();
//                        }
//                    } else {
//                        startLocationUpdates();
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(getBaseContext(), "Error requesting permission",
//                            Toast.LENGTH_LONG).show();
//                }
//
//
//            }
//        });
//
//    }
//
//
//
//
////    private void initGetLocationButton() {
////        Button locationButton = (Button) findViewById(R.id.buttonGetLocation);
////        locationButton.setOnClickListener(new View.OnClickListener() {
////
////            @Override
////            public void onClick(View v) {
////                try {
////                    if (Build.VERSION.SDK_INT >= 23) {
////                        if (ContextCompat.checkSelfPermission(ContactMapActivity.this,
////                                android.Manifest.permission.ACCESS_FINE_LOCATION)
////                                != PackageManager.PERMISSION_GRANTED) {
////
////                            if (ActivityCompat.shouldShowRequestPermissionRationale(
////                                    ContactMapActivity.this,
////                                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
////
////                                Snackbar.make(findViewById(R.id.activity_contact_map),
////                                                "MyContactList requires this permission to locate " +
////                                                        "your contacts", Snackbar.LENGTH_INDEFINITE)
////                                        .setAction("OK", new View.OnClickListener() {
////                                            @Override
////                                            public void onClick(View view) {
////                                                ActivityCompat.requestPermissions(
////                                                        ContactMapActivity.this,
////                                                        new String[]{
////                                                                android.Manifest.permission.ACCESS_FINE_LOCATION
////                                                        },
////                                                        PERMISSION_REQUEST_LOCATION);
////                                            }
////                                        })
////                                        .show();
////                            } else {
////                                ActivityCompat.requestPermissions(ContactMapActivity.this, new
////                                                String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
////                                        PERMISSION_REQUEST_LOCATION);
////                            }
////                        } else {
////                            startLocationUpdates();
////                        }
////                    } else {
////                        startLocationUpdates();
////                    }
////                } catch (Exception e) {
////                    Toast.makeText(getBaseContext(), "Error requesting permission",
////                            Toast.LENGTH_LONG).show();
////                }
////
////
////            }
////        });
////    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        if ( Build.VERSION.SDK_INT >= 23 &&
//                ContextCompat.checkSelfPermission(getBaseContext(),
//                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
//                        PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(getBaseContext(),
//                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                        PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//        try {
//            locationManager.removeUpdates(gpsListener);
//            locationManager.removeUpdates(networkListener);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
////    private boolean isBetterLocation(Location location) {
////        boolean isBetter = false;
////        if (currentBestLocation == null) {
////            isBetter = true;
////        }
////        else if (location.getAccuracy() <= currentBestLocation.getAccuracy()) {
////            isBetter = true;
////        }
////        else if (location.getTime() - currentBestLocation.getTime() > 5 * 60 * 1000) {
////            isBetter = true;
////        }
////        return isBetter;
////    }
//
////    private void startLocationUpdates() {
////        if ( Build.VERSION.SDK_INT >= 23 &&
////                ContextCompat.checkSelfPermission(getBaseContext(),
////                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
////                        PackageManager.PERMISSION_GRANTED &&
////                ContextCompat.checkSelfPermission(getBaseContext(),
////                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
////                        PackageManager.PERMISSION_GRANTED) {
////
////            return;
////        }
////        try {
////            locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
////
////            gpsListener = new LocationListener() {
////                public void onLocationChanged(Location location) {
////                    if (isBetterLocation(location)) {
////                        currentBestLocation = location;//display in location in TextViews.
////                    }//no else block.if not better, just ig
////                    TextView txtLatitude = (TextView) findViewById(R.id.txtLatitude);
////                    TextView txtLongitude = (TextView) findViewById(R.id.txtLongitude);
////                    TextView txtAccuracy = (TextView) findViewById(R.id.textAccuracy);
////
////                    txtLatitude.setText(String.valueOf(location.getLatitude()));
////                    txtLongitude.setText(String.valueOf(location.getLongitude()));
////                    txtAccuracy.setText(String.valueOf(location.getAccuracy()));
////                }
////
////                public void onStatusChanged(String provider, int status, Bundle extras) {}
////                public void onProviderEnabled(String provider) {}
////                public void onProviderDisabled(String provider) {}
////            };
////            locationManager.requestLocationUpdates(
////                    LocationManager.GPS_PROVIDER, 0, 0, gpsListener
////            );
////            networkListener = new LocationListener() {
////                public void onLocationChanged(Location location) {
////                    TextView txtLatitude = (TextView) findViewById(R.id.txtLatitude);
////                    TextView txtLongitude = (TextView) findViewById(R.id.txtLongitude);
////                    TextView txtAccuracy = (TextView) findViewById(R.id.textAccuracy);
////
////                    txtLatitude.setText(String.valueOf(location.getLatitude()));
////                    txtLongitude.setText(String.valueOf(location.getLongitude()));
////                    txtAccuracy.setText(String.valueOf(location.getAccuracy()));
////                }
////
////                public void onStatusChanged(String provider, int status, Bundle extras) {}
////                public void onProviderEnabled(String provider) {}
////                public void onProviderDisabled(String provider) {}
////            };
////            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0, networkListener);
////
////
////        }
////        catch (Exception e) {
////            Toast.makeText(getBaseContext(), "Error, Location not available", Toast.LENGTH_LONG).show();
////        }
////
////    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[],
//                                           int[] grantResults) {
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PERMISSION_REQUEST_LOCATION: {
//                if (grantResults.length > 0 &&
//                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    startLocationUpdates();
//                } else {
//                    Toast.makeText(ContactMapActivity.this,
//                            "MyContactList will not locate your contacts.",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }
//
//
//
//    private void contactlistButton() {
//        ImageButton imageButtonList = findViewById(R.id.imageButtonList);
//        imageButtonList.setOnClickListener(v -> {
//            Intent intent = new Intent(ContactMapActivity.this, MainActivity.class);
//            startActivity(intent);
//        });
//    }
//
//    private void mapButton() {
//        ImageButton imageButtonMap = findViewById(R.id.imageButtonMap);
//        imageButtonMap.setOnClickListener(v -> {
//            Intent intent = new Intent(ContactMapActivity.this, ContactMapActivity.class);
//            startActivity(intent);
//        });
//    }
//
//    private void settingsButton() {
//        ImageButton imageButtonSettings = findViewById(R.id.imageButtonSettings);
//        imageButtonSettings.setOnClickListener(v -> {
//            Intent intent = new Intent(ContactMapActivity.this, ContactSettingsActivity.class);
//            startActivity(intent);
//        });
//    }
//}
//
//
