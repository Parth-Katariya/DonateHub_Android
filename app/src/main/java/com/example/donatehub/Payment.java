package com.example.donatehub;

public class Payment {
    private String paymentId;
    private String amount;
    private String username;
    private String userEmail;
    private String trustname;
    private String status;

    private String date;




    public Payment(String paymentId, String amount, String username, String userEmail, String trustname, String status,String date) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.username = username;
        this.userEmail = userEmail;
        this.trustname = trustname;
        this.status = status;
        this.date = date;


    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getAmount() {
        return amount;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public String getUsername() {
        return username;
    }

    public String getTrustname() {
        return trustname;
    }

    public String getStatus() {
        return status;
    }

    public String getDate (){return date;}
}

