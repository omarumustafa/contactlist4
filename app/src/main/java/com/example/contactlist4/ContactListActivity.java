package com.example.contactlist4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ContactListActivity extends AppCompatActivity implements DatePickerDialog.SaveDateListener {
    private Contact currentContact;
    private ArrayList<Contact> contacts;
    private ArrayList<String> names;
    private ListView listView;
    private ContactAdapter contactAdapter;
    private RecyclerView contactList;

    final int PERMISSION_REQUEST_PHONE = 1;
    final int PERMISSION_REQUEST_CAMERA = 103;
    final int CAMERA_REQUEST = 1888;
//    private Bitmap picture;

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

        int contactId = getIntent().getIntExtra("contact_id", -1);
        if (contactId == -1) {
            Log.e("ContactListActivity", "Error: No contact ID received");
        } else {
            Log.d("ContactListActivity", "Opening contact ID: " + contactId);
        }

        if (contactId != -1) {
            initContact(contactId);
        } else {
            currentContact = new Contact();
        }


        setForEditing(false);
        buttonChange();
        initTextChangedEvents();
        initSaveButton();
        initCallFunction();
        initImageButton();




//


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ContactListActivity.this, "You may now call from this app.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ContactListActivity.this, "You will not be able to make calls " +
                            "from this app", Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(ContactListActivity.this,
                            "You will not be able to save contact pictures from this app",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
    public void takePhoto() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 144, 144, true);
                ImageButton imageContact = (ImageButton) findViewById(R.id.imageContact);
                imageContact.setImageBitmap(scaledPhoto);
                currentContact.setPicture(scaledPhoto);
            }
        }
    }




    private void initImageButton() {
        ImageButton ib = findViewById(R.id.imageContact);
        ib.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(ContactListActivity.this,
                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale
                                (ContactListActivity.this, android.Manifest.permission.CAMERA)) {

                            Snackbar.make(findViewById(R.id.activity_main),
                                            "The app needs permission to take pictures.",
                                            Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ActivityCompat.requestPermissions(
                                                    ContactListActivity.this,
                                                    new String[]{android.Manifest.permission.CAMERA},
                                                    PERMISSION_REQUEST_CAMERA);
                                        }
                                    })
                                    .show();
                        } else {
                            ActivityCompat.requestPermissions(
                                    ContactListActivity.this,
                                    new String[]{android.Manifest.permission.CAMERA},
                                    PERMISSION_REQUEST_CAMERA);
                        }
                    } else {
                        takePhoto();
                    }
                } else {
                    takePhoto();
                }
            }
        });
    }


    private void checkPhonePermission(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ContactListActivity.this,
                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(ContactListActivity.this,
                        android.Manifest.permission.CALL_PHONE)) {

                    Snackbar.make(findViewById(R.id.activity_main),
                            "MyContactList requires this permission to place a call from the app.",
                            Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(
                                    ContactListActivity.this,
                                    new String[]{android.Manifest.permission.CALL_PHONE},
                                    PERMISSION_REQUEST_PHONE);
                        }
                    }).show();

                } else {
                    ActivityCompat.requestPermissions(ContactListActivity.this,
                            new String[]{android.Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST_PHONE);
                }
            } else {
                callContact(phoneNumber);
            }
        } else {
            callContact(phoneNumber);
        }
    }

    private void initCallFunction() {
        EditText editPhone = (EditText) findViewById(R.id.editTextHomePhone);
        editPhone.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                checkPhonePermission(currentContact.getPhoneNumber());
                return false;
            }
        });
    }
    private void callContact(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            startActivity(intent);
        }
    }



    private void initContact(int id) {
        ContactDataSource ds = new ContactDataSource(ContactListActivity.this);
        try {
            ds.open();
            currentContact = ds.getSpecificContact(id);
            ds.close();
        }
        catch (Exception e) {
            Toast.makeText(this, "Load Contact Failed", Toast.LENGTH_LONG).show();
        }

        EditText editName = findViewById(R.id.editTextName);
        EditText editAddress = findViewById(R.id.editTextStreet);
        EditText editCity = findViewById(R.id.editTextCity);
        EditText editState = findViewById(R.id.editTextState);
        EditText editZipCode = findViewById(R.id.editTextZip);
        EditText editPhone = findViewById(R.id.editTextHomePhone);
        EditText editCell = findViewById(R.id.editTextCellPhone);
        EditText editEmail = findViewById(R.id.editTextEmail);
        TextView birthDay = findViewById(R.id.textBirthday);

        editName.setText(currentContact.getContactName());
        editAddress.setText(currentContact.getStreetAddress());
        editCity.setText(currentContact.getCity());
        editState.setText(currentContact.getState());
        editZipCode.setText(currentContact.getZipCode());

        editPhone.setText(currentContact.getPhoneNumber());
        editCell.setText(currentContact.getCellNumber());
        editEmail.setText(currentContact.getEMail());
        birthDay.setText(DateFormat.format("MM/dd/yyyy", currentContact.getBirthday().getTimeInMillis()).toString());
        ImageButton picture = (ImageButton) findViewById(R.id.imageContact);
        if (currentContact.getPicture() != null) {
            picture.setImageBitmap(currentContact.getPicture());
        }
        else {
            picture.setImageResource(R.drawable.ic_launcher_background);
        }

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
        ImageButton picture = findViewById(R.id.imageContact);

        picture.setEnabled(enabled);

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
//                    updateListView();
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
//    private void buttonSave() {
//        Button buttonSaveClk = findViewById(R.id.buttonSave);
//        buttonSaveClk.setOnClickListener(v -> {
//            Intent intent = new Intent(ContactListActivity.this, ContactSettingsActivity.class);
//            startActivity(intent);
//        });
//



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
            Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
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
