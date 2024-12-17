package com.example.ecomegrove;

public class HelperClass {

    private String name;
    private String email;
    private String username;
    private String password;
    private String phoneNumber; // Added phone number
    private String address;     // Added address
    private String photo;       // Added photo URL
    private String dateOfJoin;  // Added date of joining

    // Constructor with parameters
    public HelperClass(String name, String email, String username, String password,
                       String phoneNumber, String address, String photo, String dateOfJoin) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.photo = photo;
        this.dateOfJoin = dateOfJoin;
    }

    // Default constructor required for calls to DataSnapshot.getValue(HelperClass.class)
    public HelperClass() {
        // Required empty constructor
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDateOfJoin() {
        return dateOfJoin;
    }

    public void setDateOfJoin(String dateOfJoin) {
        this.dateOfJoin = dateOfJoin;
    }
}
