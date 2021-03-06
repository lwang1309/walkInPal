package com.example.mywalkinpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mywalkinpal.ui.login.Employee;
import com.example.mywalkinpal.ui.login.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppointmentBookingActivity extends AppCompatActivity {

    CalendarView calendarView;
    Button selectDate;
    String date;
    //auth of the actual patient currently using the thing
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    //array list of maps
    //each map was a booking, key was date, value was start time/end time
    //keep clinic name/email stored locally
    //then call it using the find child function in firebase?

    //uID of the employee/clinic that was selected
    //^^NEED to get this instead of the current fbAuth in the dbref

    DatabaseReference dbAppointment = FirebaseDatabase.getInstance().getReference("Users")
            .child(fbAuth.getUid()).child("Appointment Dates");

    //instance of patient that is accessing this booking thing !!!!!!!
    Patient patient;
    BookingQueue bookingQueue;
    Employee employee;

    ArrayList<Map<String, List<Double>>> bookingList;
    Map<String, List<Double>> dateTime;
    List<Double> apptTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking);


        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setMinDate(System.currentTimeMillis()-1000);
        selectDate = (Button) findViewById(R.id.selectDate);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = Integer.toString(year) + Integer.toString(month) + Integer.toString(dayOfMonth);
            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (date != null){
                    //now check if the date exists in the database
                    //call the database uID of the employee/clinic they have selected

                    //apptTime.add(startTime);
                    //apptTime.add(endTime);
                    dateTime.put(date, apptTime);

                    dbAppointment.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(AppointmentBookingActivity.this, "This time is not available", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                bookingQueue.addPatient(patient);
                             //   dbAppointment.setValue(patient.getUid());
                            }
                        }

                        //then call the instance of the clinic's booking queue
                        BookingQueue currentBookingQueue = employee.getBookingQueue();
                        //currentBookingQueue.addPatient(patient);
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                else{
                    Toast.makeText(AppointmentBookingActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
