package com.example.contactlist4;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.List;

public class ContactMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
        contactlistButton();
        mapButton();

        settingsButton();
        initGetLocationButton();

    }
    private void initGetLocationButton() {
        Button locationButton = (Button) findViewById(R.id.buttonGetLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editAddress = (EditText) findViewById(R.id.editTextAddress);
                EditText editCity = (EditText) findViewById(R.id.editCityText);
                EditText editState = (EditText) findViewById(R.id.editStateText);
                EditText editZipCode = (EditText) findViewById(R.id.editTextZipcode);

                String address = editAddress.getText().toString() + ", " +
                        editCity.getText().toString() + ", " +
                        editState.getText().toString() + ", " +
                        editZipCode.getText().toString();

                List<Address> addresses = null;
                Geocoder geo = new Geocoder(ContactMapActivity.this);
                TextView txtLatitude = (TextView) findViewById(R.id.txtLatitude);
                TextView txtLongitude = (TextView) findViewById(R.id.txtLongitude);

                try {
                    addresses = geo.getFromLocationName(address, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        txtLatitude.setText(String.valueOf(addresses.get(0).getLatitude()));
                        txtLongitude.setText(String.valueOf(addresses.get(0).getLongitude()));
                    } else {
                        txtLatitude.setText("Not Found");
                        txtLongitude.setText("Not Found");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    txtLatitude.setText("Error");
                    txtLongitude.setText("Error");
                }



            }
        });
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
}


