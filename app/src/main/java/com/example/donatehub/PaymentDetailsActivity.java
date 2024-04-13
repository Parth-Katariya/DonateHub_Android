package com.example.donatehub;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentDetailsActivity extends AppCompatActivity {

    Button generateReceiptButton;
    TextView txtPid, txtAmount, txtTrustName, txtEmailId, txtStatus, txtDate, titleTrustname;
    ImageView donationImage;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        txtPid = findViewById(R.id.paymentIdTextView);
        txtAmount = findViewById(R.id.amountTextView);
        txtTrustName = findViewById(R.id.trustNameTextView);
        txtEmailId = findViewById(R.id.emailIdTextView);
        txtStatus = findViewById(R.id.paymentStatusTextView);
        txtDate = findViewById(R.id.dateTextView);
        titleTrustname = findViewById(R.id.tv1);
        donationImage = findViewById(R.id.img1);
        progressBar=findViewById(R.id.progressBar);

        // Retrieve payment details from the Intent
        String paymentId = getIntent().getStringExtra("paymentId");
        String paymentAmount = getIntent().getStringExtra("paymentAmount");
        String username = getIntent().getStringExtra("username");
        String userEmail = getIntent().getStringExtra("userEmail");
        String trustname = getIntent().getStringExtra("trustname");
        String status = getIntent().getStringExtra("status");
        String trustImage = getIntent().getStringExtra("image");

        Glide.with(this)
                .load(trustImage) // Load the image from the URL
                .into(donationImage);

        // Display payment details
        String details = "Payment ID: " + paymentId + "\n"
                + "Payment Amount: " + paymentAmount + "\n"
                + "Donater Name: " + username + "\n"
                + "Donater Email: " + userEmail + "\n"
                + "Trust Name: " + trustname + "\n"
                + "Status: " + status;

        titleTrustname.setText(trustname);
        txtPid.setText(paymentId);
        txtAmount.setText(paymentAmount);
        txtTrustName.setText(trustname);
        txtEmailId.setText(userEmail);
        txtStatus.setText(status);
        txtDate.setText(getCurrentDateTime());

        generateReceiptButton = findViewById(R.id.generateReceiptButton);
        generateReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                generateReceiptPDF(details);
            }
        });
    }

    private void generateReceiptPDF(String details) {
        PdfDocument pdfDocument = new PdfDocument();

        // Create a PageInfo
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create();

        // Start a new page
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Create a Canvas to draw on the page
        android.graphics.Canvas canvas = page.getCanvas();

        // Define your text, fonts, and layout
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(14);

        // Define the position and text for each piece of information
        int x = 50;
        int y = 50;

        // Add the company logo in the top-left corner
        android.graphics.Bitmap companyLogo = BitmapFactory.decodeResource(getResources(), R.drawable.simplelogo1);
        int logoWidth = 100; // Adjust the logo width as needed
        int logoHeight = 100; // Adjust the logo height as needed
        canvas.drawBitmap(companyLogo, 20, 20, paint); // Adjust the position as needed

        // Add the company name at the top
        String companyName = "DonateHub";
        paint.setTextSize(30); // Increase the text size
        paint.setFakeBoldText(true); // Make the text bold
        paint.setColor(Color.BLACK); // Set the text color to black
        // Calculate the width of the company name text to center it horizontally
        float companyNameWidth = paint.measureText(companyName);
        float companyNameX = (pageInfo.getPageWidth() - companyNameWidth) / 2;
        canvas.drawText(companyName, 150, 60, paint);

        // Add the "Foundation" text with the color #919191
        String tagName = "Foundation";
        paint.setTextSize(20); // Increase the text size
        paint.setFakeBoldText(true);
        paint.setColor(Color.parseColor("#919191")); // Set the text color to #919191
        // Calculate the width of the "Foundation" text to center it horizontally
        float companyTagWidth = paint.measureText(tagName);
        float companyTagX = (pageInfo.getPageWidth() - companyTagWidth) / 2;
        canvas.drawText(tagName, 150, 90, paint);

        // Reset the text color back to black
        paint.setColor(Color.BLACK);

        // Set custom title and formatting
        paint.setTextSize(20);
        paint.setUnderlineText(true);

        // Calculate the width of the title text to center it horizontally
        String titleText = "Payment Receipt";
        float titleWidth = paint.measureText(titleText);
        float titleX = (pageInfo.getPageWidth() - titleWidth) / 2;
        canvas.drawText(titleText, titleX, y + logoHeight + 10 + 25, paint);

        // Print current date and time in the top right corner
        String dateTimeText = getCurrentDateTime();
        float dateTimeWidth = paint.measureText(dateTimeText);
        float dateTimeX = pageInfo.getPageWidth() - dateTimeWidth - 20; // Adjust the position from the right edge
        canvas.drawText(dateTimeText, dateTimeX, 50, paint);

        // Restore standard formatting
        paint.setTextSize(14);
        paint.setUnderlineText(false);

        y += logoHeight + 10 + 25 + 40; // Adjust the vertical position for the table

        // Split the details into separate lines
        String[] detailLines = details.split("\n");

        // Add "Thanks For Help The Humanity" at the bottom
        String thankYouText = "Your support means the world to us. Thank you!";
        paint.setTextSize(14);
        float thankYouWidth = paint.measureText(thankYouText);
        float thankYouX = (pageInfo.getPageWidth() - thankYouWidth) / 2;
        float thankYouY = pageInfo.getPageHeight() - 40; // Adjust the vertical position as needed
        canvas.drawText(thankYouText, thankYouX, thankYouY, paint);

        // Define table properties
        int numRows = detailLines.length;
        int numCols = 2; // Assuming two columns (key and value)
        float cellWidth = 250;
        float cellHeight = 30;
        float tableWidth = numCols * cellWidth;
        float tableHeight = numRows * cellHeight;
        float startX = (pageInfo.getPageWidth() - tableWidth) / 2;

        // Draw table borders
        for (int row = 0; row <= numRows; row++) {
            float yPos = y + (row * cellHeight);
            canvas.drawLine(startX, yPos, startX + tableWidth, yPos, paint);
        }
        for (int col = 0; col <= numCols; col++) {
            float xPos = startX + (col * cellWidth);
            canvas.drawLine(xPos, y, xPos, y + tableHeight, paint);
        }

        // Populate the table with data
        for (int row = 0; row < numRows; row++) {
            String[] rowData = detailLines[row].split(": ");
            String key = rowData[0];
            String value = rowData[1];
            float keyX = startX + 10;
            float valueX = startX + cellWidth + 10;
            float cellY = y + (row * cellHeight) + cellHeight - 10;
            canvas.drawText(key, keyX, cellY, paint);
            canvas.drawText(value, valueX, cellY, paint);
        }

        // Finish the page
        pdfDocument.finishPage(page);

        try {

            progressBar.setVisibility(View.GONE);
            // Generate a unique PDF file name based on the current timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String timestamp = sdf.format(new Date());
            String pdfFileName = "receipt_" + timestamp + ".pdf";

            // Build the full file path
            String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            String pdfFilePath = directoryPath + File.separator + pdfFileName;

            // Save the PDF to the generated file path
            File pdfFile = new File(pdfFilePath);
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(outputStream);
            pdfDocument.close();
            outputStream.close();

            Toast.makeText(this, "PDF receipt saved at: " + pdfFilePath, Toast.LENGTH_LONG).show();

            // After successfully generating the PDF, you can implement your desired action here.
            Intent intent = new Intent(PaymentDetailsActivity.this,thankpage.class);
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF receipt", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return sdf.format(date);
    }
}
