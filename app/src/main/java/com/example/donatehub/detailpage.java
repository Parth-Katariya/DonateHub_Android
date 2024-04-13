package com.example.donatehub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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

public class detailpage extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, settings, share, about, logout;
    ImageView img;
    TextView tv1, tv2;
    Button btn;
    TextView navDrawerName, navDrawerEmail; // Add TextViews for name and email
    ImageView profileImage; // ImageView for profile image


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailpage);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);
        settings = findViewById(R.id.settings);
        share = findViewById(R.id.share);

        img = findViewById(R.id.img1);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        btn = findViewById(R.id.btn1);

        navDrawerName = findViewById(R.id.prf_name); // Add TextView for name in nav drawer
        navDrawerEmail = findViewById(R.id.prf_email); // Add TextView for email in nav drawer
        profileImage = findViewById(R.id.default_profile_image); // Add ImageView for profile image in nav drawer

        Intent intent = getIntent();
        String name = intent.getExtras().getString("name");
        String dntdetail = intent.getExtras().getString("dntdetail");
        String imageOne = intent.getExtras().getString("imagePath");

        tv1.setText(name);
        tv2.setText(dntdetail);

        Glide.with(this)
                .load(imageOne) // Load the image from the URL
                .into(img);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(detailpage.this, MainActivity.class);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(detailpage.this, share.class);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(detailpage.this, about.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out the user from Firebase Authentication
                FirebaseAuth.getInstance().signOut();

                // Redirect the user to the login screen or any other desired activity
                redirectActivity(detailpage.this, MainActivity.class);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(detailpage.this, enteramount.class);
                intent.putExtra("name", name);
                intent.putExtra("donate", imageOne);
                intent.putExtra("image",imageOne);
                startActivity(intent);
            }
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
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors
                }
            });

            // You can also load and set the user's profile image here if you have an image URL stored in the database
            // For example:
            // String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
            // Glide.with(detailpage.this).load(profileImageUrl).into(profileImage);
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

    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}
