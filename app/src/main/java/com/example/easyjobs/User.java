package com.example.easyjobs;

public class User {
    //String User_ID;
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
    boolean isPremium;
    String password; //Think if we need here?
    // PROF PIC // THINK HOW?
    float rating;

    public User()
    {

    }

    public User(String firstName, String lastName, String phoneNumber, String email, boolean isPremium, String password, float rating) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isPremium = isPremium;
        this.password = password;
        this.rating = rating;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public User(String firstName, String lastName, String phoneNumber, String email, boolean isPremium, String password, float rating) {
//       // User_ID = user_ID;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.phoneNumber = phoneNumber;
//        this.email = email;
//        this.isPremium = isPremium;
//        this.password = password;
//        this.rating = rating;
//    }


    public User(String firstName, String lastName, String phoneNumber, String email, boolean isPremium, float rating) {
       // User_ID = user_ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isPremium = isPremium;
        this.rating = rating;
    }

    public User(String firstName, String lastName, String phoneNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

//    public void setUser_ID(String user_ID) {
//        User_ID = user_ID;
//    }
//
//    public String getUser_ID() {
//        return User_ID;
//    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public float getRating() {
        return rating;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", isPremium=" + isPremium +
                ", password='" + password + '\'' +
                ", rating=" + rating +
                '}';
    }

}
