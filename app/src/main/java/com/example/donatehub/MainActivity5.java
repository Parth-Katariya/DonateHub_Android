package com.example.donatehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity5 extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetButton;
    private FirebaseAuth mAuth;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.edt1);
        resetButton = findViewById(R.id.btn1);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE); // Initially hide the progress bar

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (!email.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE); // Show the progress bar

                    sendPasswordResetEmail(email);
                } else {
                    Toast.makeText(MainActivity5.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar

                        if (task.isSuccessful()) {
                            // Password reset email sent successfully
                            Toast.makeText(MainActivity5.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity5.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity5.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
