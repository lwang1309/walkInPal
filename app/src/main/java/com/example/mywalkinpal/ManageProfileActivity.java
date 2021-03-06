package com.example.mywalkinpal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mywalkinpal.ui.login.Employee;
import com.example.mywalkinpal.ui.login.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.telephony.PhoneNumberUtils.isGlobalPhoneNumber;

public class ManageProfileActivity extends AppCompatActivity {
    EditText updateAddress, updatePhoneNumber,updateName;
    CheckBox publicInsurance, privateInsurance, credit, debit, cash;
    DatabaseReference dbref;
    Button updateButton;
    public ArrayList<String> insuranceTypesAccepted;
    public ArrayList<String> paymentTypesAccepted;
    FirebaseAuth fbAuth;
    DatabaseReference dbUsers;
    FirebaseUser mUser;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);
        fbAuth = FirebaseAuth.getInstance();
        mUser = fbAuth.getCurrentUser();
        dbUsers = FirebaseDatabase.getInstance().getReference("Users");

        updateAddress = (EditText) findViewById(R.id.updateAddress);
        updatePhoneNumber = (EditText) findViewById(R.id.updatePhoneNumber);
        updateName = (EditText) findViewById(R.id.updateName);
        updateButton = (Button) findViewById(R.id.modProfile);
        privateInsurance = (CheckBox) findViewById(R.id.checkBoxPrivateIns);
        publicInsurance = (CheckBox) findViewById(R.id.checkBoxPublicIns);
        cash = (CheckBox) findViewById(R.id.checkBoxCash);
        credit = (CheckBox) findViewById(R.id.checkBoxCredit);
        debit = (CheckBox) findViewById(R.id.checkBoxDebit);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    update();
                    startActivity(new Intent(ManageProfileActivity.this, ViewProfileActivity.class));

                    //finish();
                }


            }
        });
    }
    private void update(){

        if(mUser == null){
            finish();
        }

        else{
            insuranceTypesAccepted = new ArrayList<>();
            if(privateInsurance.isChecked()){
                insuranceTypesAccepted.add("Private Insurance");
            }
            if(publicInsurance.isChecked()){
                insuranceTypesAccepted.add("Public Insurance");
            }
            paymentTypesAccepted = new ArrayList<>();
            if(cash.isChecked()){
                paymentTypesAccepted.add("Cash");
            }
            if(debit.isChecked()){
                paymentTypesAccepted.add("Debit");
            }
            if(credit.isChecked()){
                paymentTypesAccepted.add("Credit");
            }

            dbUsers.child(mUser.getUid()).child("phoneNumber").setValue(updatePhoneNumber.getText().toString());
            dbUsers.child(mUser.getUid()).child("address").setValue(updateAddress.getText().toString());
            dbUsers.child(mUser.getUid()).child("clinicName").setValue(updateName.getText().toString());
            dbUsers.child(mUser.getUid()).child("insuranceTypesAccepted").setValue(insuranceTypesAccepted);
            dbUsers.child(mUser.getUid()).child("paymentTypesAccepted").setValue(paymentTypesAccepted);

        }
    }



    private Boolean validate(){
        Boolean ret = false;
        String addressText = updateAddress.getText().toString();
        String phoneNumberText = updatePhoneNumber.getText().toString();
        String nameText = updateName.getText().toString();

        if(!validateEmptyName(nameText)){
            Toast.makeText(ManageProfileActivity.this, "Please fill out a name.", Toast.LENGTH_SHORT).show();
        }else if(!validateEmptyAddress(addressText) ) {
            Toast.makeText(ManageProfileActivity.this, "Please fill out an address.", Toast.LENGTH_SHORT).show();
        }else if(!isGlobalPhoneNumber(phoneNumberText)){
            Toast.makeText(ManageProfileActivity.this, "Please enter a valid global phone number.", Toast.LENGTH_SHORT).show();
        }else if(!privateInsurance.isChecked() && !publicInsurance.isChecked()){
            Toast.makeText(ManageProfileActivity.this, "Please select at least one insurance type accepted.", Toast.LENGTH_SHORT).show();
        }else if(!credit.isChecked() && !debit.isChecked() && !cash.isChecked()){
            Toast.makeText(ManageProfileActivity.this, "Please select at least one payment type accepted.", Toast.LENGTH_SHORT).show();
        }
        else{
            ret = true;
            Toast.makeText(ManageProfileActivity.this, "Valid", Toast.LENGTH_SHORT).show();
        }

        return ret;

    }

    protected static boolean validateEmptyAddress(String address){
        boolean result = true;

        if(address.isEmpty()){
            result = false;
        }
        return result;
    }

    protected static boolean validateEmptyName(String name){
        boolean result = true;

        if(name.isEmpty()){
            result = false;
        }
        return result;
    }
}
