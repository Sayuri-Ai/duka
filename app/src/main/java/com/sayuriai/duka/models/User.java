package com.sayuriai.duka.models;

public class User {
    String firstname;
    String surname;
    String fullname;
    String date;
    String phone;
    String uid;
    String email;
    String address;


    public User(String uid) {
        this.uid = uid;
    }

    public User(String fullname, String phone, String uid, String address) {
        this.fullname = fullname;
        this.phone = phone;
        this.uid = uid;
        this.address = address;
    }
    public User(String fullname, String phone, String uid) {
        this.fullname = fullname;
        this.phone = phone;
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
