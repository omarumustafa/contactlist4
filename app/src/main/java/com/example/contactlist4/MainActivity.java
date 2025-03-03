package com.example.contactlist4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ContactDataSource dataSource;
    private ArrayList<String> contactNames;
    private ContactAdapter contactAdapter;
//    Contact currentContact;
//    final int PERMISSION_REQUEST_PHONE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                double levelScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int batteryPercent = (int) Math.floor(batteryLevel / levelScale * 100);
                TextView textBatteryState = (TextView) findViewById(R.id.textBatteryLevel);
                textBatteryState.setText(batteryPercent + "%");
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);

        contactlistButton();
        mapButton();
        settingsButton();
        initAddContactButton();
        initDeleteSwitch();
//        initDeleteButton();
//        initCallFunction();




    }

    @Override
    public void onResume() {
        super.onResume();
        String sortBy = getSharedPreferences("MyContactListPreferences",

                Context.MODE_PRIVATE).getString("sortfield", "contactname");

        String sortOrder = getSharedPreferences("MyContactListPreferences",

                Context.MODE_PRIVATE).getString("sortorder", "ASC");
        ContactDataSource ds = new ContactDataSource(this);
        ArrayList<Contact> contacts;

        try {
            ds.open();
            contacts = ds.getContacts(sortBy, sortOrder);

            ds.getContacts(sortBy, sortOrder);
            ds.close();

            Log.d("DB_CHECK", "Number of Contacts: " + contacts.size());
            for (Contact c : contacts) {
                Log.d("DB_CHECK", "Contact: " + contacts);
            }

            RecyclerView contactList = findViewById(R.id.rvContacts);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            contactList.setLayoutManager(layoutManager);

            contactAdapter = new ContactAdapter(this, contacts, contact -> {
                Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                intent.putExtra("contact_id", contact.getContactID());
                startActivity(intent);
            });

            contactList.setAdapter(contactAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving contact", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dataSource.close(); // Close DB when activity is destroyed
        super.onDestroy();
    }


    private void initDeleteSwitch() {
        Switch switchDelete = findViewById(R.id.switchDelete);
        switchDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (contactAdapter != null) {
                contactAdapter.setDelete(isChecked);
                contactAdapter.notifyDataSetChanged(); // Refresh the adapter to update UI
            }
        });


    }

//    private void initCallFunction() {
//        TextView editPhone = (TextView) findViewById(R.id.textPhoneNumber);
//        editPhone.setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View arg0) {
//                checkPhonePermission(currentContact.getPhoneNumber());
//                return false;
//            }
//        });

//        EditText editCell = (EditText) findViewById(R.id.editCell);
//        editCell.setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View arg0) {
//                checkPhonePermission(currentContact.getCellNumber());
//                return false;
//            }
//        });

// }
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String permissions[], @NonNull int[] grantResults) {
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PERMISSION_REQUEST_PHONE: {
//                if (grantResults.length > 0 && grantResults[0] ==
//                        PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(MainActivity.this, "You may now call from this app.",
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "You will not be able to make calls " +
//                            "from this app", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }

//    private void callContact(String phoneNumber) {
//        Intent intent = new Intent(Intent.ACTION_CALL);
//        intent.setData(Uri.parse("tel:" + phoneNumber));
//
//        if (Build.VERSION.SDK_INT >= 23 &&
//                ContextCompat.checkSelfPermission(getBaseContext(),
//                        android.Manifest.permission.CALL_PHONE) !=
//                        PackageManager.PERMISSION_GRANTED) {
//            return;
//        } else {
//            startActivity(intent);
//        }
//    }


//    private void checkPhonePermission(String phoneNumber) {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (ContextCompat.checkSelfPermission(MainActivity.this,
//                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//
//                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                        android.Manifest.permission.CALL_PHONE)) {
//
//                    Snackbar.make(findViewById(R.id.activity_main),
//                            "MyContactList requires this permission to place a call from the app.",
//                            Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View view) {
//                            ActivityCompat.requestPermissions(
//                                    MainActivity.this,
//                                    new String[]{android.Manifest.permission.CALL_PHONE},
//                                    PERMISSION_REQUEST_PHONE);
//                        }
//                    }).show();
//
//                } else {
//                    ActivityCompat.requestPermissions(MainActivity.this,
//                            new String[]{android.Manifest.permission.CALL_PHONE},
//                            PERMISSION_REQUEST_PHONE);
//                }
//            } else {
//                callContact(phoneNumber);
//            }
//        } else {
//            callContact(phoneNumber);
//        }
//    }


    private void initAddContactButton() {
        Button buttonAddContact = findViewById(R.id.buttonAddContact);
        buttonAddContact.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
            startActivity(intent);
        });
    }
    private void contactlistButton() {
        ImageButton imageButtonList = findViewById(R.id.imageButtonList);
        imageButtonList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void mapButton() {
        ImageButton imageButtonMap = findViewById(R.id.imageButtonMap);
        imageButtonMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactMapActivity.class);
            startActivity(intent);
        });
    }
    private void settingsButton() {
        ImageButton imageButtonSettings = findViewById(R.id.imageButtonSettings);
        imageButtonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactSettingsActivity.class);
            startActivity(intent);
        });
    }

}



