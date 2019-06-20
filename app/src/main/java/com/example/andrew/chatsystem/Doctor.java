package com.example.andrew.chatsystem;

public class Doctor {

    private String uName , uStatus , image , doctorRate , numOfCases;

    public Doctor() { }

    public Doctor(String uName, String uStatus, String image, String doctorRate) {
        this.uName = uName;
        this.uStatus = uStatus;
        this.image = image;
        this.doctorRate = doctorRate;

    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuStatus() {
        return uStatus;
    }

    public void setuStatus(String uStatus) {
        this.uStatus = uStatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDoctorRate() {
        return doctorRate;
    }

    public void setDoctorRate(String doctorRate) {
        this.doctorRate = doctorRate;
    }

    public String getNumOfCases() { return numOfCases; }

    public void setNumOfCases(String numOfCases) { this.numOfCases = numOfCases; }
}
