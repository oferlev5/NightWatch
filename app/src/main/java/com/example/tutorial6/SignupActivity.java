package com.example.tutorial6;
import com.example.tutorial6.ui.home.DBOperations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.DefaultDatabaseErrorHandler;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    private  static int DELAY_TIME=1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);



        EditText email = findViewById(R.id.signup_email);
        EditText password = findViewById(R.id.signup_password);
        EditText confPass = findViewById(R.id.signup_confirm);
        Button signup = findViewById(R.id.signup_but);
        TextView redirectLogin = findViewById(R.id.login_redirected);

        redirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignupActivity.this, "redirecting to Login page ", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();


                    }
                },DELAY_TIME);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DBOperations DB = new DBOperations();
                String emailField = email.getText().toString().toLowerCase();
                String passwordField = password.getText().toString();
                String confPassField = confPass.getText().toString();

                if (!passwordField.equals(confPassField)) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();

                } else if(emailField.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Enter an Email!", Toast.LENGTH_SHORT).show();

                }
                else {
                    HashMap<String,String> cred = new HashMap<>();
                    cred.put("email",emailField);
                    cred.put("password",passwordField);
                    DB.insertUsernameData(cred);
                    Toast.makeText(SignupActivity.this, "Signed up successfully ", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();


                        }
                    },DELAY_TIME);





                }

            }
        });



    }
}