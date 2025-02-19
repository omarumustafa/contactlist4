package com.example.contactlist4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ContactDataSource dataSource;
    private ArrayList<String> contactNames;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        contactlistButton();
        mapButton();
        settingsButton();
//        listView = findViewById(R.id.listViewContacts);
//        dataSource = new ContactDataSource(this);
//        dataSource.open();
//
//        contactNames = getContactNames(); // Retrieve all contact names
//        displayContacts(); // Display in ListView

        ContactDataSource ds = new ContactDataSource(this);
        ArrayList<Contact> contacts;

        try {
            ds.open();
            contacts = ds.getContacts();

            ds.getContacts();
            ds.close();

            Log.d("DB_CHECK", "Number of Contacts: " + contacts.size());
            for (Contact c : contacts) {
                Log.d("DB_CHECK", "Contact: " + contacts);
            }

            RecyclerView contactList = findViewById(R.id.rvContacts);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            contactList.setLayoutManager(layoutManager);

            ContactAdapter contactAdapter = new ContactAdapter(contacts, contact -> {
                Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                intent.putExtra("contact_name", contacts);
                intent.putExtra("contact_phone", contacts);
                startActivity(intent);
            });

            contactList.setAdapter(contactAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving contact", Toast.LENGTH_SHORT).show();
        }
    }
//    private ArrayList<String> getContactNames() {
//        ArrayList<String> names = new ArrayList<>();
//        ArrayList<Contact> contacts = dataSource.getAllContacts(); // Retrieve all contacts
//        for (Contact c : contacts) {
//            names.add(c.getContactName()); // Add only names
//        }
//        return names;
//    }

//    private void displayContacts() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this, android.R.layout.simple_list_item_1, contactNames);
//        listView.setAdapter(adapter);
//    }

    @Override
    protected void onDestroy() {
        dataSource.close(); // Close DB when activity is destroyed
        super.onDestroy();
    }

    private void contactlistButton() {
        ImageButton imageButtonList = findViewById(R.id.imageButtonList);
        imageButtonList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
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



