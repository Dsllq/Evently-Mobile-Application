package com.evently.eventlyapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.evently.eventlyapp.Model.UserModel;
import com.evently.eventlyapp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class RegisterActivity extends Activity {

    private TextInputEditText etName, etPass, etMail;

    private Button btnSignUp;
    private TextView tvAlreadyHaveAAccount;
    private FirebaseAuth mAuth;
    private String mailAdress;
    private String userPassword;
    FirebaseDatabase database;
    FirebaseUser user;
    String userId;
    DatabaseReference mRef;
    Spinner spinner;
    CheckBox checkBoxTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etRegName);
        etPass = findViewById(R.id.etRegPass);
        etMail = findViewById(R.id.etRegEmail);
        btnSignUp = findViewById(R.id.btnRegister);
        checkBoxTerms = findViewById(R.id.checkBoxTerms);
        tvAlreadyHaveAAccount = findViewById(R.id.tvAlreadyHaveAAccount);
        spinner = findViewById(R.id.spinner);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        checkBoxTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Terms of Use");
                builder.setMessage("By using this social media application, you agree to the following terms and conditions:\n\n" +
                        "- You must use the app in compliance with applicable laws and regulations.\n" +
                        "- You are responsible for any content you post on the platform.\n" +
                        "- You must respect the privacy of other users and not engage in harassment or abuse.\n" +
                        "- The app reserves the right to terminate or suspend your account for violations.\n\n" +
                        "Please read the complete terms of use on our website.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        ArrayList<String> spinnerValues = new ArrayList<>();

        String[] values = getResources().getStringArray(R.array.spinner_values);
        spinnerValues.addAll(Arrays.asList(values));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Admin Approval");
                    builder.setMessage("To use the application as an editor, you need to be approved by the admin. Until then, you will continue with your visitor account.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMail.getText().toString().isEmpty() || etName.getText().toString().isEmpty() || etPass.getText().toString().isEmpty()||!checkBoxTerms.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please fill in the required fields!", Toast.LENGTH_SHORT).show();

                } else {

                    registerFunc();
                }

            }
        });
        tvAlreadyHaveAAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }
    private void registerFunc() {

        mAuth.createUserWithEmailAndPassword(etMail.getText().toString(), etPass.getText().toString())
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            userId = user.getUid();
                            mRef =
                                    database.getReference().child("EVENTLYDB/Users").child(userId+"/UID");
                            Log.i("MARDUK USER ID ", userId);
                            mRef.setValue(new UserModel(etName.getText().toString(),
                                    etMail.getText().toString(),
                                    spinner.getSelectedItem().toString()));
                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}