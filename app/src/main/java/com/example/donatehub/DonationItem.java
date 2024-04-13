package com.example.donatehub;

import android.graphics.Bitmap;

public class DonationItem {
    private Bitmap imageBitmap;
    private String donationName;
    private String donationDetail;

    public DonationItem(Bitmap imageBitmap, String donationName, String donationDetail) {
        this.imageBitmap = imageBitmap;
        this.donationName = donationName;
        this.donationDetail = donationDetail;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public String getDonationName() {
        return donationName;
    }

    public String getDonationDetail() {
        return donationDetail;
    }
}
