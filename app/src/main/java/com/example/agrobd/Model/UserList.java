package com.example.agrobd.Model;

public class UserList {

    String username,profession,contactNumber,email,password,address,bio,user_id,url,profile;

    public UserList(String username, String profession, String contactNumber, String email, String password, String address, String bio, String user_id, String url,String profile) {
        this.username = username;
        this.profession = profession;
        this.contactNumber = contactNumber;
        this.email = email;
        this.password = password;
        this.address = address;
        this.bio = bio;
        this.user_id = user_id;
        this.url = url;
        this.profile = profile;
    }

    public UserList() {
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
