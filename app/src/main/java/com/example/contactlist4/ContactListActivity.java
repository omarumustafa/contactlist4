package com.example.contactlist4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        contactlistButton();
        mapButton();
        settingsButton();
        initToggleButton();

        setForEditing(false);
    }

    private void contactlistButton() {
        ImageButton imageButtonList = findViewById(R.id.imageButtonList);
        imageButtonList.setOnClickListener(v -> {
            Intent intent = new Intent(ContactListActivity.this, ContactListActivity.class);
            startActivity(intent);
        });
    }

    private void mapButton() {
        ImageButton imageButtonMap = findViewById(R.id.imageButtonMap);
        imageButtonMap.setOnClickListener(v -> {
            Intent intent = new Intent(ContactListActivity.this, ContactMapActivity.class);
            startActivity(intent);
        });
    }
    private void settingsButton() {
        ImageButton imageButtonSettings = findViewById(R.id.imageButtonSettings);
        imageButtonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ContactListActivity.this, ContactSettingsActivity.class);
            startActivity(intent);
        });
    }
    private void initToggleButton() {
        ToggleButton toggleButtonEdit = findViewById(R.id.toggleButtonEdit);
        toggleButtonEdit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setForEditing(isChecked);
        });

    }

    private void setForEditing(boolean isChecked) {
        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextStreet = findViewById(R.id.editTextStreet);
        EditText editTextCity = findViewById(R.id.editTextCity);
        EditText editTextState = findViewById(R.id.editTextState);
        EditText editTextZip = findViewById(R.id.editTextZip);
        EditText editTextHomePhone = findViewById(R.id.editTextHomePhone);
        EditText editTextCellPhone = findViewById(R.id.editTextCellPhone);
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        Button buttonChange = findViewById(R.id.buttonChange);
        Button buttonSave = findViewById(R.id.buttonSave);

        editTextHomePhone.setEnabled(isChecked);
        editTextCellPhone.setEnabled(isChecked);
        editTextEmail.setEnabled(isChecked);
        buttonChange.setEnabled(isChecked);
        buttonSave.setEnabled(isChecked);


        editTextName.setEnabled(isChecked);
        editTextStreet.setEnabled(isChecked);
        editTextCity.setEnabled(isChecked);
        editTextState.setEnabled(isChecked);
        editTextZip.setEnabled(isChecked);


    }
}
