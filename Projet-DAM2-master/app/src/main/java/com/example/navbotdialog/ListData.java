package com.example.navbotdialog;

public class ListData {
    String dateOfBirth, email, numberPhone, firstname, address, lastname, mat, job, image;

    public ListData(String firstname, String lastname, String dateOfBirth, String email, String image,
                    String numberPhone, String address, String job, String mat) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.image = image;
        this.numberPhone = numberPhone;
        this.address = address;
        this.job = job;
        this.mat = mat;
    }
}
