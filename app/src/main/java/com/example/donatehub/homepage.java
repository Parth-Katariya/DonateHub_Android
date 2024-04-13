package com.example.donatehub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class homepage extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, settings, share, about, logout;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<model> arrayList = new ArrayList<>();
    TextView navDrawerName, navDrawerEmail; // Add TextViews for name and email
    ImageView profileImage; // ImageView for profile image
    ViewFlipper imageFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);
        settings = findViewById(R.id.settings);
        share = findViewById(R.id.share);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.rcylview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageFlipper = findViewById(R.id.view);

        // Add TextViews for name and email in the navigation drawer
        navDrawerName = findViewById(R.id.prf_name);
        navDrawerEmail = findViewById(R.id.prf_email);

        // Initialize the ImageView for the profile image
        profileImage = findViewById(R.id.default_profile_image);

        // Set up the RecyclerView
        modelrecycleview modelrecycleview = new modelrecycleview(this, arrayList);
        recyclerView.setAdapter(modelrecycleview);

        // Click listeners
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(homepage.this, setting.class);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(homepage.this, share.class);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(homepage.this, about.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out the user from Firebase Authentication
                FirebaseAuth.getInstance().signOut();

                // Redirect the user to the login screen or any other desired activity
                redirectActivity(homepage.this, MainActivity.class);
            }
        });

        // Retrieve data from the database and populate the arrayList
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference().child("trusts/alltrust");
        donationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear(); // Clear the existing data
                progressBar.setVisibility(View.VISIBLE); // Show ProgressBar

                for (DataSnapshot donationSnapshot : dataSnapshot.getChildren()) {
                    String donationName = donationSnapshot.child("name").getValue(String.class);
                    String donationDetail = donationSnapshot.child("dntdetail").getValue(String.class);
                    String donateImage = donationSnapshot.child("imagePath").getValue(String.class);

                    // Create a new model with the loaded image
                    model donationModel = new model(donateImage, donationName, donationDetail);
                    arrayList.add(donationModel);
                }

                // Notify the RecyclerView adapter of data changes
                recyclerView.getAdapter().notifyDataSetChanged();

                // Hide ProgressBar when data loading is complete
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        // Retrieve and set the user's name, email, and profile image URL from Firebase Realtime Database
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("registration/users");

            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        String userEmail = dataSnapshot.child("email").getValue(String.class);
                        String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class); // Replace with the correct field name in your database

                        // Set the name and email in the navigation drawer TextViews
                        navDrawerName.setText(userName);
                        navDrawerEmail.setText(userEmail);

                        // Load the profile image using Glide from Firebase Storage
                        loadProfileImageFromStorage(profileImageUrl);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }

        // Load the last 4 images into the ViewFlipper
        loadLastFourImagesIntoFlipper();
    }

    private void loadLastFourImagesIntoFlipper() {
        // Initialize Firebase references
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("trusts/alltrust");

        // Query to get the last 4 image URLs
        Query lastFourImagesQuery = databaseReference.orderByKey().limitToLast(4);

        lastFourImagesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imageUrl = snapshot.child("imagePath").getValue(String.class);

                    // Create an ImageView for each image URL
                    ImageView imageView = new ImageView(homepage.this);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    // Use Glide to load the image into the ImageView
                    Glide.with(homepage.this)
                            .load(imageUrl)
                            .into(imageView);

                    // Add the ImageView to the ViewFlipper
                    imageFlipper.addView(imageView);
                }

                // Start flipping images
                imageFlipper.setInAnimation(homepage.this, android.R.anim.slide_in_left);
                imageFlipper.setOutAnimation(homepage.this, android.R.anim.slide_out_right);
                imageFlipper.startFlipping();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    // Helper method to load the profile image from Firebase Storage (you can implement this)
    private void loadProfileImageFromStorage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl) // Load the image from the URL
                .into(profileImage);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}
