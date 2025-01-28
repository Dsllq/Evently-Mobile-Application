package com.evently.eventlyapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.evently.eventlyapp.R;

public class LoginActivity extends Activity {


    private Button btnLogin;
    TextView tvRegister,etRegEmail,etRegPass,tvResetPassword;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String userName;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        btnLogin = findViewById(R.id.btnLogin);
        tvResetPassword = findViewById(R.id.tvResetPassword);
        tvRegister = findViewById(R.id.tvRegisterHere);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPass = findViewById(R.id.etRegPass);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser != null){

            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
            startActivity(i);
            finish();
        }

        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = "e-mail";
                if(etRegEmail.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter your e-mail address!", Toast.LENGTH_SHORT).show();
                    return;

                }

                emailAddress=etRegEmail.getText().toString();
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Password recovery mail has been sent!", Toast.LENGTH_SHORT).show();
                                }
                                else {

                                }
                            }
                        });

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = etRegEmail.getText().toString();
                userPassword = etRegPass.getText().toString();
                if(userName.isEmpty() || userPassword.isEmpty()){

                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.blank_error),
                            Toast.LENGTH_LONG).show();

                }else{

                    loginFunc();
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
    }

    private void loginFunc() {

        mAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(LoginActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,
                                    getResources().getString(R.string.succes_login),
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
                            startActivity(i);
                            finish();

                        }
                        else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

}