package com.example.tutorial6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial6.ui.home.DBOperations;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private  static int DELAY_TIME=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        HashMap<String,Object> data = new HashMap<>();

        EditText email = findViewById(R.id.login_email);
        EditText password = findViewById(R.id.login_password);
        Button login = findViewById(R.id.login_but);
        TextView signupRedirect = findViewById(R.id.signup_redirected);


        signupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "redirecting to Sign Up page ", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                        startActivity(intent);
                        finish();


                    }
                },DELAY_TIME);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBOperations db = new DBOperations();
                String emailField = email.getText().toString().toLowerCase();
                System.out.println("emailField = " + emailField);
                String passwordField = password.getText().toString();
                db.getDocumentsByEmailAndPassword(emailField, passwordField, new DBOperations.FirestoreCallback() {
                    @Override
                    public void onSuccess(HashMap<String, Object> documents) {
                        // Process the retrieved documents
                        for (HashMap.Entry<String, Object> entry : documents.entrySet()) {
                            String documentId = entry.getKey();
                            HashMap<String, Object> documentData = (HashMap<String, Object>) entry.getValue();
                            // Do something with the document ID and data
                        }

                        // Call another function or return the HashMap as needed
                        checkLogin(documents);
                    }


                });
            }

            private void checkLogin(HashMap<String, Object> documents) {
                // Process the retrieved documents or return them to the calling function
                data.putAll(documents);
                System.out.println("dad = " + data);
                if (data.isEmpty()){
                    Toast.makeText(LoginActivity.this, "wrong email or password", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Logged in successfully ", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bundle args = new Bundle();
                            String deviceAdd ="3C:61:05:14:97:AA";
                            args.putString("device", deviceAdd);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    },DELAY_TIME);
                }

            }





//
//                else{
//                    Toast.makeText(LoginActivity.this, "Logged in successfully ", Toast.LENGTH_SHORT).show();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//
//                        }
//                    },DELAY_TIME);
//                }


        });
    }
}