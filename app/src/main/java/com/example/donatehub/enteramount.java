package com.example.donatehub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.Date;

public class enteramount extends AppCompatActivity implements PaymentResultListener {
    DrawerLayout drawerLayout;
    ImageView menu, trustImage, navDrawerProfileImage;
    TextView title, description, navDrawerName, navDrawerEmail;
    TextView onehundred, twohundred, fivehundred, onethousand, twothousand, twothousandfivehundred, threethousand, fivethousand;
    LinearLayout home, settings, share, about, logout;

    EditText amount;
    Button donatebtn;

    ImageView profileImage;

    String email,mobilenumber,name,trustname,doantnationIamge;

    Date date = new Date();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enteramount);

        Checkout.preload(getApplicationContext());

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);
        settings = findViewById(R.id.settings);
        share = findViewById(R.id.share);
        amount = findViewById(R.id.edt1);
        profileImage=findViewById(R.id.default_profile_image);

        onehundred = findViewById(R.id.onehundred);
        twohundred = findViewById(R.id.twohundred);
        fivehundred = findViewById(R.id.fivehundred);
        onethousand = findViewById(R.id.onethousand);
        twothousand = findViewById(R.id.twothousand);
        twothousandfivehundred = findViewById(R.id.twothousandfivehundred);
        threethousand = findViewById(R.id.threethousand);
        fivethousand = findViewById(R.id.fivethousand);


        onehundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount.setText("100");
            }
        });

        twohundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount.setText("200");
            }
        });
        fivehundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount.setText("500");
            }
        });
        onethousand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount.setText("1000");
            }
        });
        twothousand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount.setText("2000");
            }
        });
        twothousandfivehundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount.setText("2500");
            }
        });
        threethousand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount.setText("3000");
            }
        });
        fivethousand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount.setText("5000");
            }
        });

        String titleText = getIntent().getStringExtra("name");
        trustname = titleText;
        String doanteIamge = getIntent().getStringExtra("image");
        doantnationIamge = doanteIamge;



        title = findViewById(R.id.tv1);
        description = findViewById(R.id.tv2);
        trustImage = findViewById(R.id.img1);
        donatebtn = findViewById(R.id.btn1);

        title.setText(titleText);
        Glide.with(this)
                .load(doanteIamge) // Load the image from the URL
                .into(trustImage);


        // Fetch user's name, email, and profile image from Firebase Realtime Database
        navDrawerName = findViewById(R.id.prf_name);
        navDrawerEmail = findViewById(R.id.prf_email);
        navDrawerProfileImage = findViewById(R.id.default_profile_image);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("registration/users");

            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        name = userName;
                        String userEmail = dataSnapshot.child("email").getValue(String.class);
                        email = userEmail;
                        String userImage = dataSnapshot.child("profileImageUrl").getValue(String.class);
                        loadProfileImageFromStorage(userImage);
                        String mobile = dataSnapshot.child("mobile").getValue(String.class);
                        mobilenumber = mobile;

                        // String userProfileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                        // Set name and email in the navigation drawer TextViews
                        navDrawerName.setText(userName);
                        navDrawerEmail.setText(userEmail);

                        // Load and set the user's profile image using Glide or any other image loading library
                        // Example: Glide.with(enteramount.this).load(userProfileImageUrl).into(navDrawerProfileImage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }

        donatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountString = amount.getText().toString();

                // Check if the amount is not empty and is a valid numeric value
                if (!TextUtils.isEmpty(amountString) && isNumeric(amountString) && Float.parseFloat(amountString) > 0) {
                    // Amount is valid, proceed with the payment
                    makePayment(amountString);
                } else {
                    // Invalid amount, show an error message to the user
                    Toast.makeText(enteramount.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            }
        });






        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(enteramount.this, MainActivity.class);
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
                redirectActivity(enteramount.this, share.class);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(enteramount.this, about.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out the user from Firebase Authentication
                FirebaseAuth.getInstance().signOut();

                // Redirect the user to the login screen or any other desired activity
                redirectActivity(enteramount.this, MainActivity.class);
            }
        });
    }

    private boolean isNumeric(String amountString) {
        try {
            Float.parseFloat(amountString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void makePayment(String amount) {

        final Activity activity  = this;

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_IsJsix4K1puY6N");
        checkout.setImage(R.drawable.logo);

        double finalamount = Float.parseFloat(amount)*100;

        try {
            JSONObject options = new JSONObject();
            options.put("name","DonateHub");
            options.put("description","Make Payment");
            options.put("image","C:\\Users\\darsh\\donatehub\\app\\src\\main\\res\\drawable\\logo.png");
            options.put("theme.color","#537EC5");
            options.put("currency","INR");
            options.put("amount",finalamount+"");
            options.put("prefill.email",email);
            options.put("prefill.contact",mobilenumber);

            checkout.open(activity,options);

        }
        catch (Exception e){
            Log.e("TAG","Error in Starting Razorpay Checkout",e);
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

    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }


    @Override
    public void onPaymentSuccess(String s) {
        // Payment was successful, you can log it in Firebase Realtime Database here

        // First, get a reference to your Firebase Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Assuming you have a "payments" node in your database to store payment information
        DatabaseReference paymentsRef = databaseReference.child("payments/transaction/SuccessTransaction");

        // Create a unique key for the payment entry, for example, using push()
        DatabaseReference paymentEntryRef = paymentsRef.push();

        // Get the payment details such as the payment ID, amount, email, etc.
        String paymentId = s; // This should be the Razorpay payment ID
        String paymentAmount = "₹"+ amount.getText().toString();
        String username = name;
        String tname = trustname;
        String statuss = "Success";
        String userEmail = email; // Assuming you have stored the user's email


        // Create a Payment object to store the details
        Payment payment = new Payment(paymentId, paymentAmount,username, userEmail,tname,statuss,date.toString());

        // Set the payment details in the Firebase Realtime Database
        paymentEntryRef.setValue(payment);

        Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, PaymentDetailsActivity.class);
        intent.putExtra("paymentId", paymentId);
        intent.putExtra("paymentAmount", paymentAmount);
        intent.putExtra("username", username);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("trustname", tname);
        intent.putExtra("status", statuss);
        intent.putExtra("date",date.toString());
        intent.putExtra("image",doantnationIamge);

        startActivity(intent);
    }

    private void loadProfileImageFromStorage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl) // Load the image from the URL
                .into(profileImage);
    }

    @Override
    public void onPaymentError(int i, String s) {
        // Payment was successful, you can log it in Firebase Realtime Database here

        // First, get a reference to your Firebase Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Assuming you have a "payments" node in your database to store payment information
        DatabaseReference paymentsRef = databaseReference.child("payments/transaction/FailedTransaction");

        // Create a unique key for the payment entry, for example, using push()
        DatabaseReference paymentEntryRef = paymentsRef.push();

        // Get the payment details such as the payment ID, amount, email, etc.
        String paymentId = s; // This should be the Razorpay payment ID
        String paymentAmount = "₹"+ amount.getText().toString();
        String username = name;
        String tname = trustname;
        String statuss = "Failed";
        String userEmail = email; // Assuming you have stored the user's email

        // Create a Payment object to store the details
        Payment payment = new Payment(paymentId, paymentAmount,username, userEmail,tname,statuss,date.toString());

        // Set the payment details in the Firebase Realtime Database
        paymentEntryRef.setValue(payment);

        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }
}
