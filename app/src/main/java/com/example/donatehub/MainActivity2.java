package com.example.donatehub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class MainActivity2 extends AppCompatActivity {

    private final int GALLERY_REQ_CODE = 1000;
    EditText name, email, password, mobile;
    Button submit;
    LinearLayout uploadImageLayout;
    ProgressBar progressBar;
    ImageView dp;
    private FirebaseAuth mAuth;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        name = findViewById(R.id.edt_name);
        email = findViewById(R.id.edt1);
        password = findViewById(R.id.edt2);
        mobile = findViewById(R.id.edt_mobile);
        submit = findViewById(R.id.btn1);
        progressBar = findViewById(R.id.progressBar);
        uploadImageLayout = findViewById(R.id.uploadimage);
        dp = findViewById(R.id.img);

        mAuth = FirebaseAuth.getInstance();

        uploadImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQ_CODE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameString = name.getText().toString();
                final String emailString = email.getText().toString();
                final String passwordString = password.getText().toString();
                final String mobileString = mobile.getText().toString();

                progressBar.bringToFront();
                progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(emailString) || TextUtils.isEmpty(passwordString) || TextUtils.isEmpty(mobileString)) {
                    Toast.makeText(MainActivity2.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                // Mobile number validation using regular expression

                String mobilePattern = "^[0-9]{10}$"; // Validates 10-digit numeric mobile number
                if (!mobileString.matches(mobilePattern)) {
                    Toast.makeText(MainActivity2.this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;}


                // Check if an image is selected
                if (selectedImageUri != null) {
                    // Create a random UUID for the image file name
                    final String imageFileName = UUID.randomUUID().toString();

                    // Get a reference to the Firebase Storage location
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_images/" + imageFileName);

                    // Upload the image to Firebase Storage
                    storageReference.putFile(selectedImageUri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        // Image upload successful, get the download URL
                                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    // Get the download URL
                                                    Uri downloadUri = task.getResult();

                                                    // Inside your registerUser() method
                                                    registerUser(nameString, emailString, passwordString, mobileString, downloadUri.toString());
                                                } else {
                                                    // Handle image upload failure
                                                    Toast.makeText(MainActivity2.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    } else {
                                        // Handle image upload failure
                                        Toast.makeText(MainActivity2.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    // If no image is selected, proceed without uploading an image
                    // Inside your registerUser() method
                    registerUser(nameString, emailString, passwordString, mobileString, "");
                }
            }
        });
    }

    private void registerUser(final String nameString, final String emailString, final String passwordString, final String mobileString, final String profileImageUrl) {
        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(MainActivity2.this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                User user = new User(nameString, emailString, mobileString, profileImageUrl);

                                DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("registration/users");
                                usersReference.child(userId).setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> databaseTask) {
                                                if (databaseTask.isSuccessful()) {
                                                    // Send verification email
                                                    sendVerificationEmail(firebaseUser);
                                                } else {
                                                    // Handle database failure
                                                    Toast.makeText(MainActivity2.this, "Database storage failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // Handle authentication failure with specific error message
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(MainActivity2.this, "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                selectedImageUri = data.getData();
                dp.setImageURI(selectedImageUri);
            }
        }
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity2.this, "Verification email sent", Toast.LENGTH_SHORT).show();

                            // Navigate to another activity upon successful verification
                            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Optionally, finish this activity to prevent the user from going back
                        } else {
                            Toast.makeText(MainActivity2.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
