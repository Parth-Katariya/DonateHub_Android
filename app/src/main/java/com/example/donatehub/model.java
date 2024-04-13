package com.example.donatehub;

public class model {

    String name, dntdetail, image;

    public model() {
        // Default constructor required for Firebase
    }

    public model(String image, String name, String dntdetail) {
        this.image = image;
        this.name = name;
        this.dntdetail = dntdetail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDntdetail() {
        return dntdetail;
    }

    public void setDntdetail(String dntdetail) {
        this.dntdetail = dntdetail;
    }
}
