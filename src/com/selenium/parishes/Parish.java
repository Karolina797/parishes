package com.selenium.parishes;


public class Parish {

    private String name;
    private String address;
    private String phoneNumber;
    private String url;
    private String diocese;
    private String decanate;
    private String notes;

    public Parish(String name, String address, String phoneNumber, String url, String diocese, String decanate, String notes) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.url = url;
        this.diocese = diocese;
        this.decanate = decanate;
        this.notes = notes;
    }


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUrl() {
        return url;
    }

    public String getDiocese() {
        return diocese;
    }

    public String getDecanate() {
        return decanate;
    }

    public String getNotes() {
        return notes;
    }

    public void parishPresentation(){
        System.out.println("Name: " + getName());
        System.out.println("Address: " + getAddress());
        System.out.println("Phone: " + getPhoneNumber());
        System.out.println("Url: " + getUrl());
        System.out.println("Diocese: " + getDiocese());
        System.out.println("Decanate: " + getDecanate());
        System.out.println("Notes: " + getNotes());
    }



}
