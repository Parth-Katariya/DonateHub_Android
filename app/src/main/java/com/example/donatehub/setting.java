package com.example.donatehub;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class setting extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, settings, share, about, logout;
    TextView navDrawerName, navDrawerEmail; // Add TextViews for name and email
    ImageView profileImage; // ImageView for profile image

    Switch themeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);
        settings = findViewById(R.id.settings);
        share = findViewById(R.id.share);

        // Add TextViews for name and email in the navigation drawer
        navDrawerName = findViewById(R.id.prf_name);
        navDrawerEmail = findViewById(R.id.prf_email);

        // Initialize the ImageView for the profile image
        profileImage = findViewById(R.id.default_profile_image);

        // Initialize the theme switch
        themeSwitch = findViewById(R.id.themeSwitch);

        // Load the theme preference
        SharedPreferences settingsPrefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkMode = settingsPrefs.getBoolean("isDarkMode", false);
        themeSwitch.setChecked(isDarkMode);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the theme preference
            SharedPreferences.Editor editor = settingsPrefs.edit();
            editor.putBoolean("isDarkMode", isChecked);
            editor.apply();

            // Apply the selected theme to the whole application
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

            // You may need to restart the activity or recreate other activities to apply the new theme immediately
            recreate();
        });

        menu.setOnClickListener(v -> openDrawer(drawerLayout));

        home.setOnClickListener(v -> redirectActivity(setting.this, MainActivity.class));

        settings.setOnClickListener(v -> recreate());

        share.setOnClickListener(v -> redirectActivity(setting.this, share.class));

        about.setOnClickListener(v -> redirectActivity(setting.this, about.class));

        logout.setOnClickListener(v -> {
            // Log out the user from Firebase Authentication
            FirebaseAuth.getInstance().signOut();

            // Redirect the user to the login screen or any other desired activity
            redirectActivity(setting.this, MainActivity.class);
        });

        // Retrieve and set the user's name, email, and profile image from Firebase Realtime Database
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("registration/users");

            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        String userEmail = dataSnapshot.child("email").getValue(String.class);
                        String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class); // Replace with the correct field name in your database
                        loadProfileImageFromStorage(profileImageUrl);

                        // Set the name and email in the navigation drawer TextViews
                        navDrawerName.setText(userName);
                        navDrawerEmail.setText(userEmail);

                        // You can also load and set the user's profile image here if you have an image URL stored in the database
                        // For example:
                        // String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                        // Glide.with(setting.this).load(profileImageUrl).into(profileImage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }
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
