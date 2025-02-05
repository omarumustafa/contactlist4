package com.example.contactlist4;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ContactListActivity extends AppCompatActivity implements DatePickerDialog.SaveDateListener {
    private Contact currentContact;

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
        buttonChange();
        initTextChangedEvents();
        initSaveButton();

        currentContact = new Contact();



    }


    private void initToggleButton() {
        ToggleButton toggleButtonEdit = findViewById(R.id.toggleButtonEdit);
        toggleButtonEdit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setForEditing(isChecked);
        });

    }
    private void initTextChangedEvents(){
        final EditText etContactName = findViewById(R.id.editTextName);
        etContactName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setContactName(etContactName.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        final EditText etStreetAddress = findViewById(R.id.editTextStreet);
        etStreetAddress.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setStreetAddress(etStreetAddress.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        final EditText etCity = findViewById(R.id.editTextCity);
        etCity.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setCity(etCity.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        final EditText etState = findViewById(R.id.editTextState);
        etState.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setState(etState.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        final EditText etZip = findViewById(R.id.editTextZip);
        etZip.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setZipCode(etZip.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        final EditText etHomePhone = findViewById(R.id.editTextHomePhone);
        etHomePhone.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setPhoneNumber(etHomePhone.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        final EditText etCellPhone = findViewById(R.id.editTextCellPhone);
        etCellPhone.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setCellNumber(etCellPhone.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        final EditText etEmail = findViewById(R.id.editTextEmail);
        etEmail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setEMail(etEmail.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    boolean enabled =true;
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

        if (enabled) {
            editTextName.requestFocus(); // Focus on EditText when editing is enabled
        } else {
            ScrollView s = findViewById(R.id.scrollView);
            s.fullScroll(ScrollView.FOCUS_UP); // Move focus to the top of the ScrollView
        }
    }

        private void initSaveButton() {
        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                boolean wasSuccessful;
                ContactDataSource ds = new ContactDataSource(ContactListActivity.this);
                try {
                    ds.open();

                    if (currentContact.getContactID() == -1) {
                        wasSuccessful = ds.insertContact(currentContact);
                        if (wasSuccessful) {

                            int newId = ds.getLastContactId();

                            currentContact.setContactID(newId);
                            Log.d("DB", "Fetching Last Contact ID");

                        }

                    } else {
                        wasSuccessful = ds.updateContact(currentContact);
                    }

                    ds.close();
                }
                catch (Exception e) {
                    wasSuccessful = false;
                }

                if (wasSuccessful) {
                    ToggleButton editToggle = findViewById(R.id.toggleButtonEdit);
                    editToggle.toggle();
                    setForEditing(false);
                }
            }
        });
    }
    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        // Handle the selected date here (e.g., update UI, save to a database, etc.)
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(selectedTime.getTime());

        Toast.makeText(this, "Selected Date: " + formattedDate, Toast.LENGTH_SHORT).show();
        TextView textBirthday = findViewById(R.id.textBirthday);
        textBirthday.setText("Birthday: " + formattedDate);


        currentContact.setBirthday(selectedTime);
    }

    private void buttonChange() {
        Button buttonChange = findViewById(R.id.buttonChange);
        buttonChange.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog();
            dialog.show(getSupportFragmentManager(), "datePicker");
        });

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


}
