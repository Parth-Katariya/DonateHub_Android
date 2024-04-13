package com.example.donatehub;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    TextView forgotpassword;

    ProgressBar progressBar;

    private Button loginButton, regButton;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.edt1);
        passwordEditText = findViewById(R.id.edt2);
        loginButton = findViewById(R.id.btn2);
        forgotpassword = findViewById(R.id.edt3);

        progressBar = findViewById(R.id.progressBar);



        regButton = findViewById(R.id.btn1);

        // Check if a user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // If user is logged in and verified, go directly to MainActivity4
            startActivity(new Intent(MainActivity.this, homepage.class));
            finish();
        }

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity5.class);
                startActivity(intent);
            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String emailstring = emailEditText.getText().toString();
                String passwordstring = passwordEditText.getText().toString();

                // Perform input validation
                if (TextUtils.isEmpty(emailstring) || TextUtils.isEmpty(passwordstring)) {
                    Toast.makeText(MainActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                    return;
                }


                progressBar.bringToFront();
                progressBar.setVisibility(View.VISIBLE);


                mAuth.signInWithEmailAndPassword(emailstring, passwordstring)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    if (user.isEmailVerified()) {
                                        // Email is verified, proceed to MainActivity4
                                        startActivity(new Intent(MainActivity.this, homepage.class));
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });





        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.bringToFront();
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
                progressBar.setVisibility(View.GONE);


            }
        });
    }
}
