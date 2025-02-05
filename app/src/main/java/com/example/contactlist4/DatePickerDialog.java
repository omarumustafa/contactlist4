package com.example.contactlist4;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;  // Updated to AndroidX
import java.util.Calendar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

public class DatePickerDialog extends DialogFragment {

    private Calendar selectedDate;

    // Interface for passing the selected date back
    public interface SaveDateListener {
        void didFinishDatePickerDialog(Calendar selectedTime);

    }

    // Default constructor
    public DatePickerDialog() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.select_date, container, false);

        // Initialize selectedDate to current date
        selectedDate = Calendar.getInstance();

        // Set up CalendarView listener
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                selectedDate.set(year, month, day);
            }
        });

        // Handle save button click
        Button saveButton = view.findViewById(R.id.buttonSelect);
        saveButton.setOnClickListener(v -> saveItem(selectedDate));

        // Handle cancel button click
        Button cancelButton = view.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(v -> dismiss());


        return view;
    }

    // Method to save the selected date
    private void saveItem(Calendar selectedTime) {
        if (getActivity() instanceof SaveDateListener) {
            SaveDateListener activity = (SaveDateListener) getActivity();
            activity.didFinishDatePickerDialog(selectedTime);
        } else {
            Log.e("DatePickerDialog", "Activity does not implement SaveDateListener");
        }
        dismiss();
    }
}
