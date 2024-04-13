package com.example.donatehub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity3 extends AppCompatActivity {

    private Button sendVerificationButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mAuth = FirebaseAuth.getInstance();

        sendVerificationButton = findViewById(R.id.otpbtn);

        sendVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    user.sendEmailVerification()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity3.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity3.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}
