package com.example.donatehub;


import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class nav_drawer extends AppCompatActivity {
    TextView name, email;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        name = findViewById(R.id.prf_name);
        email = findViewById(R.id.prf_email);

        // Initialize the Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("registration/users");

        // Get the current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Add a ValueEventListener to retrieve the user data using the UID
            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Check if the data exists
                    if (dataSnapshot.exists()) {
                        // Retrieve the name and email from the dataSnapshot
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        String userEmail = dataSnapshot.child("email").getValue(String.class);

                        // Set the retrieved name and email in the TextViews
                        name.setText(userName);
                        email.setText(userEmail);
                    } else {
                        // Handle the case where the data doesn't exist
                        name.setText("No Name Found");
                        email.setText("No Email Found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors that may occur
                    name.setText("Error: " + databaseError.getMessage());
                    email.setText("Error: " + databaseError.getMessage());
                }
            });
        } else {
            // Handle the case where the user is not authenticated
            name.setText("Not Authenticated");
            email.setText("Not Authenticated");
        }
    }
}
